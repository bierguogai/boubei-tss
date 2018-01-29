package com.boubei.tss.um.permission;

import java.util.List;

/**
 * 操作资源表时相关的补全操作和删除操作
 */
public interface ResourcePermission {
    
    /**
     * <pre>
     * 添加资源时的根据其父亲节点的权限补全当前新增资源节点的权限信息，如果父亲节点打的是全勾（即包含所有子节点），那么当前新增节点也设置为全勾。
     * 
     * 自动授权过程是这样的：
     * 1.找到资源的授权状态为全勾的父节点
     * 2.存在的话设置当前新增节点也为全勾。
     * </pre>
     * @param resourceId     资源
     * @param resourceTypeId 资源类型
     */
    void addResource(Long resourceId, String resourceTypeId);
    
	/**
	 * <pre>
	 * 删除资源时的清除其相关授权信息。
     * 只需处理删除节点本身的授权信息即可
     *（通常应用在删除节点的时候会一块删除其子节点，每删一次都要调用本方法一次的）
	 * </pre>
	 * @param resourceId     资源
	 * @param resourceTypeId 资源类型
	 */
	void delResource(Long resourceId, String resourceTypeId);
	
	/**
	 * <pre>
	 * 资源被移动后的权限信息设置。
	 * 如果移动后的父亲节点授权状态为全勾，则移动过去整枝节点的授权状态都为全勾。
	 * </pre>
	 * @param resourceId     资源
	 * @param resourceTypeId 资源类型
	 */
	void moveResource(Long resourceId, String resourceTypeId);
 
	/**
	 * <p>
	 * 获取用户对一个应用中的一种资源类型的一个资源的所有“父节点”的某个权限选项所拥有的资源ID集合
	 * </p>
	 * @param applicationId  应用
	 * @param resourceTypeId 资源类型
	 * @param resourceId     资源ID
	 * @param operationId    权限选项
	 * @param operatorId     登录用户
	 * @return
	 */
	List<?> getParentResourceIds(String applicationId, String resourceTypeId, Long resourceId, String operationId, Long operatorId);

	/**
	 * <p>
	 * 获取用户对一个应用中的一种资源类型的一个资源下的“子节点”的某个权限选项所拥有的资源ID集合
	 * </p>
     * @param applicationId  应用
     * @param resourceTypeId 资源类型
     * @param resourceId     资源ID
     * @param operationId    权限选项
     * @param operatorId     登录用户
	 * @return
	 */
	List<?> getSubResourceIds(String applicationId, String resourceTypeId, Long resourceId, String operationId, Long operatorId);
}
