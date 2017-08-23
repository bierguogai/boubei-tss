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
            
		String dataSource = request.getParameter("dataSource");
        Long groupId;
        try { 
        	groupId = Long.parseLong(request.getParameter("groupId"));
        } catch(Exception e) {
        	groupId = Record.DEFAULT_PARENT_ID;
        }
        
        int count = createRecords(json, dataSource, groupId);
        
		return "parent.alert('成功导入" +count+ "个录入表.');parent.loadInitData();";
	}

	// 参考Param模块的【复制】操作
	public int createRecords(String json, String dataSource, Long groupId)
			throws IOException, JsonParseException, JsonMappingException {
		
        int count = 0;
    	Map<Long, Long> idMapping = new HashMap<Long, Long>();
    	
    	List<?> list = new ObjectMapper().readValue(json, List.class);
        for (int i = 0; i < list.size(); i++) {
        	Object obj = list.get(i);  // Map
            Record record = new ObjectMapper().readValue(EasyUtils.obj2Json(obj), Record.class);
            Long oldId = record.getId();
            
            record.setId(null);
            if ( i == 0 ) {
                record.setParentId(groupId);
            } else {
                Long parentId = idMapping.get(record.getParentId());
                parentId = (Long) EasyUtils.checkNull(parentId, groupId);
				record.setParentId(parentId);
            }
            
            if( Record.TYPE1 == record.getType() ) {
            	count ++;
            	record.setDatasource(dataSource);
            	
            	String table = record.getTable();
                record.setTable( table.substring(table.indexOf(".") + 1) ); // 去掉表空间|schema
            }
            
            Integer status = record.getDisabled();
            recordService.createRecord(record);
            
            record.setDisabled(status);
            recordService.updateRecord(record);
            
            idMapping.put(oldId, record.getId());
        }
		return count;
	}
}
