package com.boubei.tss.dm.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.util.EasyUtils;

/**
 * 按ID抽取Job，可以配置多个，对应不同的间隔时间
 */
@DisallowConcurrentExecution
public class ByIDETLJob extends AbstractETLJob {
	
	protected String etlType() {
		return "byID";
	}
	
	private Object getMaxID(Long taskId) {
		String hql = "select max(maxID) from TaskLog where taskId = ? and exception='no'";
		List<?> list = commonService.getList(hql, taskId);
		Object rt = null;
		try {
			rt = list.get(0);
		} catch(Exception e) { }
		
		return rt;
	}

	protected void excuteTask(Task task) {
		Long taskId = task.getId();
		Long maxID = EasyUtils.obj2Long( EasyUtils.checkNull(getMaxID(taskId), task.getStartID() ) );
		
		log.info(task.getName() + " is starting! 【 " + maxID + "】" );
		
		long start = System.currentTimeMillis();
		TaskLog tLog = new TaskLog(task);
        
		try {
			Long[] result = etlByID(task, maxID);
			
			tLog.setException("no");
			tLog.setDetail("total=" + result[0]);
			tLog.setMaxID(result[1]);
		} 
		catch(Exception e) {
			setException(tLog, task, e);
		}
		finally {
			 // 记录任务日志，不管是否成功
			tLog.setRunningMS(System.currentTimeMillis() - start);
	        commonService.create(tLog);
		}

		log.info("Done! 共计用时: " + (System.currentTimeMillis() - start));
	}
	
	private Long[] etlByID(Task task, Long startID) {
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
    		report.setParam("[{'label':'maxID', 'type':'number'}]");
    	}
		// 始终按ID排序
		report.setScript( "select * from (" +report.getScript()+") t order by id asc" );
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("param1", String.valueOf(startID));
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 1);
		int total = ex.count, pagesize = 1*10000;
        int totalPages = PageInfo.calTotalPages(total, pagesize);
        
        // 分页查询，批量插入
        Long maxID = startID;
        String target = task.getTargetScript();
        for(int pageNum = 1; pageNum <= totalPages; pageNum++) {
        	ex = ReportQuery.excute(report, paramsMap, pageNum, pagesize);
        	
        	List<Map<Integer, Object>> list = new ArrayList<Map<Integer, Object>>();
        	for (Map<String, Object> row : ex.result) {
	        	Map<Integer, Object> item = new HashMap<Integer, Object>();
	        	for(String field : ex.selectFields) {
	        		Object value = row.get(field);
	        		item.put(item.size()+1, value); 
	        	}
	        	
	        	list.add(item);
	        	maxID = Math.max(maxID, EasyUtils.obj2Long(row.get("id")));
	        }
        	
        	SQLExcutor.excuteBatch(target, list, task.getTargetDS());
        }
        
        return new Long[] { (long) total, maxID};
	}
}
