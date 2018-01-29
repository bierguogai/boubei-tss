package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.permission.IResource;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 资源修改时的权限检查： 判断用户是否对当前节点有编辑权限
 * 
 * IResource update***( IResource obj )
 */
public class PermissionFilter4Update implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
        String application = tag.application();
		String resourceType = tag.resourceType();
		String updateOperation = tag.operation();
 
		IResource resource = (IResource) args[0];
		
		List<Long> permitedResourceIds = helper.getResourceIdsByOperation(application, resourceType, updateOperation);
        if( !permitedResourceIds.contains(resource.getId()) ) {
            throw new BusinessException( EX.parse(EX.U_18, resource.getName()) );
        }
	}

}
