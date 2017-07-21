package com.boubei.tss.dm.ext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;


public class ImportReport implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());
	
	ReportService reportService = (ReportService) Global.getBean("ReportService");

	public String processUploadFile(HttpServletRequest request,
			String filepath, String oldfileName) throws Exception {
		
		File targetFile = new File(filepath);
		String json = FileHelper.readFile(targetFile);
            
		String dataSource = request.getParameter("dataSource");
        Long groupId;
        try { 
        	groupId = Long.parseLong(request.getParameter("groupId"));
        } catch(Exception e) {
        	groupId = Report.DEFAULT_PARENT_ID;
        }
        
        int count = createReports(json, dataSource, groupId);
        
		return "parent.alert('成功导入" +count+ "个报表.');parent.loadInitData();";
	}

	// 参考Param模块的【复制】操作
	public int createReports(String json, String dataSource, Long groupId)
			throws IOException, JsonParseException, JsonMappingException {
		
        int count = 0;
    	Map<Long, Long> idMapping = new HashMap<Long, Long>();
    	
    	List<?> list = new ObjectMapper().readValue(json, List.class);
        for (int i = 0; i < list.size(); i++) {
        	Object obj = list.get(i);  // Map
            Report report = new ObjectMapper().readValue(EasyUtils.obj2Json(obj), Report.class);
            Long oldId = report.getId();
            
            report.setId(null);
            if ( i == 0 ) {
                report.setParentId(groupId);
            } else {
                report.setParentId(idMapping.get(report.getParentId()));
            }
            
            if( !report.isGroup() ) {
            	count ++;
            	report.setDatasource(dataSource);
            }
            
            Integer status = report.getDisabled();
            reportService.createReport(report);
            
            report.setDisabled(status); // 因默认创建分组都是停用状态，但导入分组不需要，保留原来状态
            reportService.updateReport(report);
            
            idMapping.put(oldId, report.getId());
        }
		return count;
	}
}
