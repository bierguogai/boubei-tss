package com.boubei.tss.matrix;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.etl.ByIDETLJob;
import com.boubei.tss.dm.etl.Task;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;

/**
 * 按ID抽取Job，数据通过WebService接口被抽取到母体BI系统的录入表
 */
@DisallowConcurrentExecution
public class WsIDETLJob extends ByIDETLJob {
	
	protected String etlType() {
		return "wsID";
	}

	protected Long[] etlByID(Task task, Long startID) {
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
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("param1", String.valueOf(startID));
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 0);
		if(ex.count == 0) {
			return new Long[] { 0L, startID};
		}
		
		Long maxID = startID;
		StringBuffer data = new StringBuffer();
		data.append( EasyUtils.list2Str(ex.selectFields) ).append("\r\n");
    	for (Map<String, Object> row : ex.result) {
    		Collection<Object> values = row.values();
        	for(String field : ex.selectFields) {
        		Object value = row.get(field);
        		if(field.equals("id")) {
        			maxID = Math.max(maxID, EasyUtils.obj2Long(value));
        			value = ""; // id不要
        		}
        		values.add( DataExport.preCheatVal(value) ); 
        	}
        	data.append(EasyUtils.list2Str(values) ).append("\r\n");
        }
    	
    	try {
			MatrixUtil.remoteRecordBatch(task.getTargetScript(), data.toString());
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
        
        return new Long[] { (long) ex.count, maxID};
	}
}
