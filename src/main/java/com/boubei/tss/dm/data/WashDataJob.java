package com.boubei.tss.dm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.timer.AbstractJob;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MailUtil;

/**
 * 用于清洗数据的JOB，利用report和record一进一出的功能，将数据清洗到指定的数据表（支持跨DB）
 * 
 * com.boubei.tss.dm.data.WashDataJob | 0 36 10 * * ? | 101:122:param1=today-1
 * 102:123:param1=0,param3=today-1,fullUpdate
 * 
 * 支持分页查询，批量插入
 * 
 */
public class WashDataJob extends AbstractJob {
	
	ReportService reportService = (ReportService) Global.getBean("ReportService");
	RecordService recordService = (RecordService) Global.getBean("RecordService");
	
	public static final String FULL_UPDATE = "fullUpdate";  // 全量更新
	
	/* 
	 * jobConfig的格式为: recordId:reportId:param1=a,param2=b
	 * 支持在有一个Job里配置多个清洗任务，每个任务单独一行
	 */
	protected void excuteJob(String jobConfig) {
		
		String[] jobConfigs = EasyUtils.split(jobConfig, "\n");
		
		for(int i = 0; i < jobConfigs.length; i++) {
			if(EasyUtils.isNullOrEmpty(jobConfigs[i])) continue;
			
			String reportInfo[] = EasyUtils.split(jobConfigs[i], ":");
			if(reportInfo.length < 2) continue;
 		
			Long recordId = EasyUtils.obj2Long(reportInfo[0]);
	        Long reportId = EasyUtils.obj2Long(reportInfo[1]);
	        boolean fullUpdate = false;
	        
	        // 读取配置的参数
	    	Map<String, String> paramsMap = new HashMap<String, String>();
	    	if(reportInfo.length >= 3) {
	    		String[] params = reportInfo[2].split(",");
	    		for(String param : params) {
	    			String[] keyValue = param.split("=");
	    			if(keyValue.length == 2) {
	    				paramsMap.put(keyValue[0].trim(), keyValue[1].trim());
	    			} 
	    			else if( FULL_UPDATE.equalsIgnoreCase(param) ) {
	    				// 全量更新，插入数据前先清空表
	    				fullUpdate = true;
	    			}
	    		}
	    	}
	    	
	    	// 读取配置的分页
	    	int pagesize = 100;
	    	if(reportInfo.length >= 4) {
	    		try {
	    			pagesize = EasyUtils.obj2Int(reportInfo[3]);
	    		} catch(Exception e) { }
	    	}
	    	pagesize = Math.max(100, pagesize);
	    	
	        SQLExcutor ex = reportService.queryReport(reportId, paramsMap, 1, 1, -1);  
	        int total = ex.count;
	        int totalPages = total / pagesize;
	        if( total % pagesize > 0) {
	        	totalPages = totalPages + 1;
	        }
	        
	        _Database db = getDB(recordId);
	        if(fullUpdate) {
	        	truncate(db);
	        }
	        
	        int startCount = fullUpdate ? 0 : getCount(db);
	        long startId = fullUpdate ? 0 : getMaxId(db);
	        try {
	        	// 分页查询，批量插入
		        for(int pageNum = 1; pageNum <= totalPages; pageNum++) {
		        	ex = reportService.queryReport(reportId, paramsMap, pageNum, pagesize, -1);
		        	
		        	List<Map<String, String>> valuesMaps = new ArrayList<Map<String, String>>();
		        	for (Map<String, Object> row : ex.result) {
			        	Map<String, String> valuesMap = new HashMap<String, String>();
			        	for(String key : row.keySet()) {
			        		Object value = row.get(key);
			        		if(value != null) {
			        			valuesMap.put(key, value.toString());
			        		}
			        	}
			        	
			        	valuesMaps.add(valuesMap);
			        }
		        	db.insertBatch(valuesMaps);
		        }
	        } 
	        catch(Exception e) {
        		rollback(db, startId);
	        	log.error("insert washed data error: ", ExceptionEncoder.getFirstCause(e)); 
	        } 
	        finally {
	        	// 检查插入的数据记录数和查询出来的是否一致，如不一致，则回滚数据，并发送邮件提醒
	        	int currentRows = ex.result.size();
	        	int endCount = getCount(db);
	        	int deltaCount = endCount - startCount;
				if( deltaCount < currentRows){
					rollback(db, startId);
					log.error("washed data error, " + "expectRows:" + currentRows + ", actualRows:" + deltaCount);
					MailUtil.send("washed data error", "expectRows:" + currentRows + ", actualRows:" + deltaCount);
				}
	        }
		}
	}
	
	int getCount(_Database db) {
		String sql = "select count(*) as num from " + db.table;
		Object result = SQLExcutor.query(db.datasource, sql).get(0).get("num");
		return EasyUtils.obj2Int(result);
	}
	
	int getMaxId(_Database db) {
		String sql = "select max(id) as maxid from " + db.table;
		List<Map<String, Object>> list = SQLExcutor.query(db.datasource, sql);
		if( EasyUtils.isNullOrEmpty(list) ) {
			return 0;
		}
		return EasyUtils.obj2Int(list.get(0).get("maxid"));
	}
	
	void rollback(_Database db, Long startId) {
		String sql = "delete from " + db.table + " where id > ? ";
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		paramsMap.put(1, startId);
		SQLExcutor.excute(sql, paramsMap, db.datasource);
	}
	
	void truncate(_Database db) {
		String sql = "truncate table " + db.table;
		SQLExcutor.excute(sql, new HashMap<Integer, Object>(), db.datasource);
	}
	
	_Database getDB(Long recordId) {
		Record record = recordService.getRecord(recordId);
		return _Database.getDB(record);
	}
}
