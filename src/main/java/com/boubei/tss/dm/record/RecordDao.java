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
