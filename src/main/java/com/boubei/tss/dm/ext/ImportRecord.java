package com.boubei.tss.dm.ext;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;


public class ImportRecord implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());
	
	RecordService recordService = (RecordService) Global.getBean("RecordService");

	public String processUploadFile(HttpServletRequest request,
			String filepath, String oldfileName) throws Exception {
		
		File targetFile = new File(filepath);
		String json = FileHelper.readFile(targetFile);
		List<?> list = new ObjectMapper().readValue(json, List.class);
            
		String dataSource = request.getParameter("dataSource");
        Long groupId;
        try { 
        	groupId = Long.parseLong(request.getParameter("groupId"));
        } catch(Exception e) {
        	groupId = Record.DEFAULT_PARENT_ID;
        }
        
        // 参考Param模块的【复制】操作
        int count = 0;
    	Map<Long, Long> idMapping = new HashMap<Long, Long>();
        for (int i = 0; i < list.size(); i++) {
        	Object obj = list.get(i);  // Map
            Record record = new ObjectMapper().readValue(EasyUtils.obj2Json(obj), Record.class);
            Long oldId = record.getId();
            
            record.setId(null);
            if ( i == 0 ) {
                record.setParentId(groupId);
            } else {
                record.setParentId(idMapping.get(record.getParentId()));
            }
            
            if( Record.TYPE1 == record.getType() ) {
            	count ++;
            	record.setDatasource(dataSource);
            }
            
            String remark = "导入前原ID = " + oldId + " .\n " + EasyUtils.obj2String(record.getRemark());
            record.setRemark(remark);
            Integer status = record.getDisabled();
            recordService.saveRecord(record);
            record.setDisabled(status);
            recordService.saveRecord(record);
            
            idMapping.put(oldId, record.getId());
        }
        
		return "parent.alert('成功导入" +count+ "个录入表.');parent.loadInitData();";
	}
}
