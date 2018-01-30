/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 资源移动时候的权限检查： 对自己有删除权限，对移动到的节点有新增权限的可以移动。
 * 
 * void move(Long id, Long targetId, ......)
 * 
 */
public class PermissionFilter4Move implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
        String application = tag.application();
		String resourceType = tag.resourceType();
		String[] operation = tag.operation().split(",");
		
		String addOperation = operation[0];
		String delOperation = operation[1];
 
		Long resourceId = (Long)args[0];
        Long parentResourceId = (Long)args[1];
        
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(application, resourceType, addOperation);
        if( !permitedResourceIds.contains(parentResourceId) ) {
            throw new BusinessException( EX.parse(EX.U_15, parentResourceId) );
        }
        
        permitedResourceIds = helper.getResourceIdsByOperation(application, resourceType, delOperation);
        if( !permitedResourceIds.contains(resourceId) ) {
            throw new BusinessException( EX.parse(EX.U_16, resourceId) );
        }
	}

}
