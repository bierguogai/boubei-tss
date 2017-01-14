package com.boubei.tss.dm.record;

import java.util.List;

import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.modules.log.Logable;
import com.boubei.tss.um.permission.filter.PermissionFilter4Create;
import com.boubei.tss.um.permission.filter.PermissionFilter4Sort;
import com.boubei.tss.um.permission.filter.PermissionTag;

public interface RecordService {

    Record getRecord(Long id);
    
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

    @PermissionTag(
            resourceType = Record.RESOURCE_TYPE,
            operation = Record.OPERATION_EDIT , 
            filter = PermissionFilter4Create.class)
    @Logable(operateObject="数据录入",  operateInfo="新增/更新了：${args[0]?default(\"\")}")
    Record saveRecord(Record record);
    
    @Logable(operateObject="数据录入", operateInfo="删除了：${returnVal?default(\"\")}")
    Record delete(Long id);
    
    @Logable(operateObject="数据录入", operateInfo="<#if args[1]=1>停用<#else>启用</#if>了报表(ID = ${args[0]?default(\"\")}) ")
    void startOrStop(Long recordId, Integer disabled);
 
    @PermissionTag(
            resourceType = Record.RESOURCE_TYPE,
            operation = Record.OPERATION_EDIT, 
            filter = PermissionFilter4Sort.class)
    @Logable(operateObject="数据录入", operateInfo="(ID: ${args[0]})节点移动到了(ID: ${args[1]})节点<#if args[2]=1>之下<#else>之上</#if>")
    void sort(Long startId, Long targetId, int direction);
 
    @Logable(operateObject="数据录入", operateInfo="移动(ID: ${args[0]}) 节点至 (ID: ${args[1]}) 组下。")
    void move(Long id, Long groupId);

    
    Integer getAttachSeqNo(Long recordId, Long itemId);
    
	List<?> getAttachList(Long recordId, Long itemId);
	
	void deleteAttach(Long id);

	RecordAttach createAttach(RecordAttach attach);
	
	RecordAttach getAttach(Long id);
}
