package com.boubei.tss.dm.etl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * 按天抽取Job，可以配置多个，在不同时间点触发
 */
@DisallowConcurrentExecution
public class ByDayETLJob extends AbstractETLJob {
	
	protected String etlType() {
		return "byDay";
	}
 
	protected  List<String> getExsitDays(Long taskId) {
		String hql = "select distinct dataDay from TaskLog where taskId = ? and exception='no'";
		List<String> exsitDays = new ArrayList<String>();
		List<?> list = commonService.getList(hql, taskId);
		for(Object obj : list) {
			exsitDays.add((String) obj);
		}
		
		return exsitDays;
	}
	
	protected void excuteTask(Task task) {
		// 获取已经存在的日结日期 exsitDays
		List<String> exsitDays = getExsitDays(task.getId());
		Date currDay = DateUtil.noHMS(task.getStartDay());

		Set<Date> dateList = new LinkedHashSet<Date>();
		List<Date> repeatList = new ArrayList<Date>();
		Date today = DateUtil.today();
		while (currDay.before(today)) {
			if ( !exsitDays.contains( DateUtil.format(currDay) ) ) {
				dateList.add(currDay); // 缺失的天
			}
			currDay = DateUtil.addDays(currDay, 1);
		}
		
		int repeats = task.getRepeatDays();
		while ( repeats > 0 ) {
			currDay = DateUtil.subDays(today, repeats);
			dateList.add(currDay);
			repeatList.add(currDay);
			repeats --;
		}
				
		log.info(task.getName() + " is starting! 共【" +dateList.size()+ "】天" );
		
		long start = System.currentTimeMillis();
		for (final Date day : dateList) {
			TaskLog tLog = new TaskLog(task);
			tLog.setDataDay( DateUtil.format(day) );
	        
			try {
				long startTime = System.currentTimeMillis();
				
				String result = etlByDay(task, day, repeatList.contains(day));
				
				tLog.setException("no");
				tLog.setDetail(result);
				tLog.setRunningMS(System.currentTimeMillis() - startTime);
			} 
			catch(Exception e) {
				setException(tLog, task, e);
				break;
			}
			finally {
				 // 记录任务日志，不管是否成功
		        commonService.create(tLog);
			}
		}

		log.info("Done! 共计用时: " + (System.currentTimeMillis() - start));
	}
	
	/* 按天ETL */
	private String etlByDay(Task task, Date day, boolean repeat) {
		// 判断是否重新抽取以更新当前日期的数据，是的话先清除已存在的改天数据
		String preRepeatSQL = task.getPreRepeatSQL();
		if(repeat && !EasyUtils.isNullOrEmpty(preRepeatSQL) ) {
			Map<Integer, Object> params = new HashMap<Integer, Object>();
			params.put(1, day);
			SQLExcutor.excute(preRepeatSQL, params , task.getTargetDS());
		}
		
		Report report;
		String source = task.getSourceScript();
		try { 
    		Long reportId = EasyUtils.obj2Long(source);
    		report = reportService.getReport(reportId, false);
    	} catch(Exception e) {
    		report = new Report();
    		report.setName(task.getName());
    		report.setDatasource(task.getSourceDS());
    		report.setScript(task.getSourceScript());
    		report.setParam("[{'label':'从日期', 'type':'date'}, {'label':'到日期', 'type':'date'}]");
    	}
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("param1", DateUtil.format(day));
		paramsMap.put("param2", DateUtil.format(DateUtil.addDays(day, 1)));
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 1);
		int total = ex.count, pagesize = 10*10000;
        int totalPages = PageInfo.calTotalPages(total, pagesize);
        
        // 分页查询，批量插入
        String target = task.getTargetScript();
        for(int pageNum = 1; pageNum <= totalPages; pageNum++) {
        	ex = ReportQuery.excute(report, paramsMap, pageNum, pagesize);
        	
        	List<Map<Integer, Object>> list1 = new ArrayList<Map<Integer, Object>>();
        	List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
        	for (Map<String, Object> row : ex.result) {
	        	Map<Integer, Object> item1 = new HashMap<Integer, Object>();
	        	Map<String, String> item2 = new HashMap<String, String>();
	        	for(int i=0; i < ex.selectFields.size(); i++) {
	        		String field = ex.selectFields.get(i); 

	        		Object value = row.get(field);
	        		item1.put(i+1, value); 
	        		item2.put(field, EasyUtils.obj2String(value));
	        	}
	        	
	        	list1.add(item1);
	        	list2.add(item2);
	        }
        	
        	try { 
        		Long recordId = EasyUtils.obj2Long(target);
        		_Database db = recordService.getDB(recordId);
				db.insertBatch(list2);
				
        	} catch(Exception e) {
        		SQLExcutor.excuteBatch(target, list1, task.getTargetDS());
        	}
        }
        
        return "total=" + total;
	}
}
