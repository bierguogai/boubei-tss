package com.boubei.tss.dm.record;

import java.util.List;

import com.boubei.tss.framework.persistence.ITreeSupportDao;
import com.boubei.tss.um.permission.filter.PermissionFilter4Branch;
import com.boubei.tss.um.permission.filter.PermissionTag;
 
public interface RecordDao extends ITreeSupportDao<Record> {
    
    @PermissionTag(
    		resourceType = Record.RESOURCE_TYPE,
            filter = PermissionFilter4Branch.class)
    List<Record> getChildrenById(Long id, String operationId);

    Record deleteRecord(Record Record);
    
}
