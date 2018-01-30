/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.record;

import java.util.List;

import com.boubei.tss.dm.ddl._Database;
import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.modules.log.Logable;
import com.boubei.tss.um.permission.filter.PermissionFilter4Create;
import com.boubei.tss.um.permission.filter.PermissionFilter4Sort;
import com.boubei.tss.um.permission.filter.PermissionFilter4Update;
import com.boubei.tss.um.permission.filter.PermissionTag;

public interface RecordService {

    Record getRecord(Long id);
    
    Long getRecordID(String recordName, int type);
    
    _Database getDB(Long recordId);
    
    List<Record> getAllRecords();
    
    @PermissionTag(
    		resourceType = Record.RESOURCE_TYPE,
	        operation = Record.OPERATION_CDATA
	)
    List<Record> getRecordables();
    
    @PermissionTag(
    		resourceType = Record.RESOURCE_TYPE,
	        operation = Record.OPERATION_VDATA
	)
    List<Record> getVisiables();

    @PermissionTag(
    		resourceType = Record.RESOURCE_TYPE,
	        operation = Record.OPERATION_CDATA
	)
    List<Record> getAllRecordGroups();
    
    List<Record> getRecordsByPID(Long recordId, Long userId);

    @PermissionTag(
            resourceType = Record.RESOURCE_TYPE,
            operation = Record.OPERATION_EDIT , 
            filter = PermissionFilter4Create.class)
    @Logable(operateObject="数据表",  operateInfo="新增了：${args[0]?default(\"\")}")
    Record createRecord(Record record);
    
    @PermissionTag(
            resourceType = Record.RESOURCE_TYPE,
            operation = Record.OPERATION_EDIT , 
            filter = PermissionFilter4Update.class)
    @Logable(operateObject="数据表",  operateInfo="修改了：${args[0]?default(\"\")}")
    void updateRecord(Record record);
    
    @Logable(operateObject="数据表", operateInfo="删除了：${returnVal?default(\"\")}")
    Record delete(Long id);
    
    @Logable(operateObject="数据表", operateInfo="<#if args[1]=1>停用<#else>启用</#if>了报表(ID = ${args[0]?default(\"\")}) ")
    void startOrStop(Long recordId, Integer disabled);
 
    @PermissionTag(
            resourceType = Record.RESOURCE_TYPE,
            operation = Record.OPERATION_EDIT, 
            filter = PermissionFilter4Sort.class)
    @Logable(operateObject="数据表", operateInfo="(ID: ${args[0]})节点移动到了(ID: ${args[1]})节点<#if args[2]=1>之下<#else>之上</#if>")
    void sort(Long startId, Long targetId, int direction);
 
    @Logable(operateObject="数据表", operateInfo="移动(ID: ${args[0]}) 节点至 (ID: ${args[1]}) 组下。")
    void move(Long id, Long groupId);

    
    Integer getAttachSeqNo(Long recordId, Long itemId);
    
	List<?> getAttachList(Long recordId, Long itemId);
	
	@Logable(operateObject="附件表", operateInfo="删除了：${returnVal?default(\"\")}")
	RecordAttach deleteAttach(Long id);

	@Logable(operateObject="附件表",  operateInfo="新增了：${args[0]?default(\"\")}")
	RecordAttach createAttach(RecordAttach attach);
	
	RecordAttach getAttach(Long id);
}
