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
 * 删除时候检查用户是否对删除资源有删除权限。
 */
public class PermissionFilter4Check implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
	    if(args.length < 1 ) return;
	    
	    Long resourceId = null;
 
	    //  eg: Structure getStructure(Long id)
	    if( args[0] instanceof Long ) {
	        resourceId = (Long) args[0];
	    }
	    
	    if(resourceId == null) return;
        
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(tag.application(), tag.resourceType(), tag.operation());
 
        if(!permitedResourceIds.contains(resourceId)) {
            throw new BusinessException(EX.parse(EX.U_13, resourceId));
        }
	}
}
