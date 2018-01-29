package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 通用权限过滤器：把用户没有指定操作权限的资源给过滤点，资源列表里只留下有指定操作权限的。
 */
public class PermissionFilter implements IPermissionFilter {

	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
	    List<?> resources = (List<?>) returnValue;
	    
        helper.filtrateResourcesByPermission(tag.application(), tag.resourceType(), tag.operation(), resources);
	}

}
