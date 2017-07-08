package com.boubei.tss.matrix;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.etl.ByDayETLJob;
import com.boubei.tss.dm.etl.Task;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * 按天抽取Job，可以配置多个，在不同时间点触发
 */
@DisallowConcurrentExecution
public class WsDayETLJob extends ByDayETLJob {
	
	protected String etlType() {
		return "wsDay";
	}
 
	/* 按天ETL */
	protected String etlByDay(Task task, Date day, boolean repeat) {
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
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 0);
		if(ex.count == 0) {
			return "total=" + 0;
		}
        
		StringBuffer data = new StringBuffer();
		data.append( EasyUtils.list2Str(ex.selectFields) ).append("\r\n");
    	for (Map<String, Object> row : ex.result) {
        	Collection<Object> values = row.values();
        	for(String field : ex.selectFields) {
        		Object value = row.get(field);
        		values.add( DataExport.preCheatVal(value) ); 
        	}
        	data.append(EasyUtils.list2Str(values) ).append("\r\n");
        }
    	
    	try {
			MatrixUtil.remoteRecordBatch(task.getTargetScript(), data.toString());
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
        
        return "total=" + ex.count;
	}
}
