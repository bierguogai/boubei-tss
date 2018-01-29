package com.boubei.tss.um.permission.filter;

import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 权限过滤器接口。
 */
public interface IPermissionFilter {
	
	/**
	 * 执行过滤。
	 * 
	 * @param args
	 *              执行查询时的参数
	 * @param resources 
	 * 				过滤前的资源节点(即直接查询返回的结果列表)
	 * @param tag
	 * 				配置信息
	 * @param helper
	 */
	void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper);

}
