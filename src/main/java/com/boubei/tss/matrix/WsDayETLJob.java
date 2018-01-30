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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.etl.ByDayETLJob;
import com.boubei.tss.dm.etl.Task;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportQuery;
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
	protected String etlByDay(Task task, Date day, List<Date> repeatList, boolean isFirstDay) {
		Report report = new Report();
		report.setName(task.getName());
		report.setDatasource(task.getSourceDS());
		report.setScript(task.getSourceScript());
		report.setParam("[{'label':'fromDay', 'type':'date'}, {'label':'toDay', 'type':'date'}]");
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("param1", DateUtil.format(day));
		paramsMap.put("param2", DateUtil.format(DateUtil.addDays(day, 1)));
		
		SQLExcutor ex = ReportQuery.excute(report, paramsMap , 1, 0);
		if(ex.count == 0) {
			return "total=" + 0;
		}
        
		StringBuffer data = new StringBuffer();
		data.append( EasyUtils.list2Str(ex.selectFields) + ",licenseowner" ).append("\n");
    	for (Map<String, Object> row : ex.result) {
        	Collection<Object> values = new ArrayList<Object>();
        	for(String field : ex.selectFields) {
        		Object value = row.get(field);
        		values.add( DMUtil.preCheatVal(value) ); 
        	}
        	
        	values.add( InstallListener.licenseOwner() );
        	data.append(EasyUtils.list2Str(values) ).append("\n");
        }
    	
		MatrixUtil.remoteRecordBatch(task.getTargetScript(), data.toString());
        
        return "total=" + ex.count;
	}
}
