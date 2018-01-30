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

import java.util.Iterator;
import java.util.List;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 某一树枝类型（包括所有子节点 或 包括所有父节点）的过滤拦截器。
 * 如果发现对该树枝某一节点没有权限，则抛出异常。
 * 一般用于停用、启用、删除等操作时的权限判断。
 */
public class PermissionFilter4Branch implements IPermissionFilter {

	@Override
    public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {

	    if(args.length < 2 || !(args[1] instanceof String)) return;
	    
	    List<?> resources = (List<?>) returnValue;
	    String operation = (String) args[1];
	    
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(tag.application(), tag.resourceType(), operation);
 
        for( Iterator<?> it = resources.iterator(); it.hasNext(); ) {
            IEntity resource = (IEntity)it.next();
            if( !permitedResourceIds.contains(resource.getPK()) ) {
                throw new BusinessException(EX.U_12);
            }
        }
	}

}
