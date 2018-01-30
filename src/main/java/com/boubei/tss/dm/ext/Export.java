/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.ext;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.DataExport;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/**
 * report/record 导入 & 导出，以json格式，支持单个及按组批量导出导入
 *
 */
@Controller
@RequestMapping( {"/auth/export"} )
public class Export {
	
	@Autowired ReportService reportService;
	@Autowired RecordService recordService;
	
	@RequestMapping("/report/{reportId}")
	public void exportReport(HttpServletResponse response, 
            @PathVariable("reportId") Long reportId) {
		
		Report report = reportService.getReport(reportId);
		List<Report> list = reportService.getReportsByGroup(reportId, Environment.getUserId());
		String json = EasyUtils.obj2Json(list);
		
		String fileName = report.getName() + ".json";
        String exportPath = DataExport.getExportPath() + "/" + fileName;
 
		// 先输出内容到服务端的导出文件中
        FileHelper.writeFile(exportPath, json, false);
        DataExport.downloadFileByHttp(response, exportPath);
	}
	
	@RequestMapping("/record/{recordId}")
	public void exportRecord(HttpServletResponse response, 
            @PathVariable("recordId") Long recordId) {
		
		Record record = recordService.getRecord(recordId);
		List<Record> list = recordService.getRecordsByPID(recordId, Environment.getUserId());
		String json = EasyUtils.obj2Json(list);
		
		String fileName = record.getName() + ".json";
        String exportPath = DataExport.getExportPath() + "/" + fileName;
 
		// 先输出内容到服务端的导出文件中
        FileHelper.writeFile(exportPath, json, false);
        DataExport.downloadFileByHttp(response, exportPath);
	}
	
	@RequestMapping("/record2report")
    @ResponseBody
	public Object recordAsReport(Long reportGroup, String recordIds) {
		String[] _ids = recordIds.split(",");
		for(String _id : _ids) {
			Record rc = recordService.getRecord( EasyUtils.obj2Long(_id) );
			if( Record.TYPE0 == rc.getType() ) continue;
			
			Report rp = new Report();
			rp.setName(rc.getName());
			rp.setType(Report.TYPE1);
			rp.setDisabled(ParamConstants.FALSE);
			rp.setParentId(reportGroup);
			rp.setDisplayUri(DMConstants.RECORD_PORTLET_HTML + rc.getId());
			
			reportService.createReport(rp);
		}
		
		return "success";
	}
}
