package com.boubei.tss.dm.ext;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.sso.Environment;
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
}
