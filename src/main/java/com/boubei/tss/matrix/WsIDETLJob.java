/* ==================================================================   
 * Created [2017-10-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.etl.ByIDETLJob;
import com.boubei.tss.dm.etl.Task;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
import com.boubei.tss.util.EasyUtils;

/**
 * 按ID抽取Job，数据通过WebService接口被抽取到母体BI系统的数据表
 */
@DisallowConcurrentExecution
public class WsIDETLJob extends ByIDETLJob {
	
	protected String etlType() {
		return "wsID";
	}

	protected Long[] etlByID(Task task, Long startID) {
		Report report = new Report();
		report.setName(task.getName());
		report.setDatasource(task.getSourceDS());
		report.setScript(task.getSourceScript());
		report.setParam("[{'label':'maxID', 'type':'number'}]");
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("param1", String.valueOf(startID));
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 0);
		if(ex.count == 0) {
			return new Long[] { 0L, startID};
		}
		
		Long maxID = startID;
		StringBuffer data = new StringBuffer();
		data.append( EasyUtils.list2Str(ex.selectFields) + ",licenseowner").append("\n");
    	for (Map<String, Object> row : ex.result) {
    		Collection<Object> values = new ArrayList<Object>();
        	for(String field : ex.selectFields) {
        		Object value = row.get(field);
        		if(field.equals("id")) {
        			maxID = Math.max(maxID, EasyUtils.obj2Long(value));
        			value = ""; // id不要
        		}
        		values.add( DMUtil.preCheatVal(value) ); 
        	}
        	
        	values.add( InstallListener.licenseOwner() );
        	data.append(EasyUtils.list2Str(values) ).append("\n");
        }
    	
		MatrixUtil.remoteRecordBatch(task.getTargetScript(), data.toString());
        
        return new Long[] { (long) ex.count, maxID};
	}
}
