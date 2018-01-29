package com.boubei.tss.um.permission;

import java.util.List;

/**
 * 角色－资源 权限设置操作service
 */
public interface PermissionService {

	/**
	 * <p>
	 * 删除授权信息。<br>
     * 导入资源注册文件时保存资源类型前将会先调用本方法清除之前数据。<br>
	 * </p>
	 * @param tableName
	 */
	void clearPermissionData(String tableName);
	
	/**
	 * 清除某个角色对某类资源的授权信息  或 清除某个资源对所有角色的授权信息
	 * 
	 * @param applicationId
	 * @param resourceType
	 * @param permissionRank
	 * @param roleId
	 * @param isRole2Resource
	 */
	void clearPermissionByRole(String applicationId, String resourceType,
			String permissionRank, Long roleId, Integer isRole2Resource);

	/**
	 * <p>
     * 角色对资源：一组资源授予一个角色。<br>
     * 保存角色对资源的权限选项设置<br>
	 * 注：因为角色对资源授权时是先删除角色对该资源的老的授权信息，<br>
	 *    然后再按页面传入的数据生成新的授权信息。<br>
     *    所以如果页面上对某个资源没有任何授权信息，则该节点可不必传到后台。<br>
	 * </p>
     * @see com.boubei.tss.um.permission.PermissionHelper.deletePermissionByRole()
     * 
	 * @param appId
	 * @param resourceTypeId
	 * @param roleId 被授权的角色
	 * @param permissionRank
	 * @param permissions 
	 *         授权内容, 当多个资源对一个角色授权时:  resource1|2224, resource2|4022
	 *         竖线后面为各个权限选项的打勾情况【0: 没打勾, 1: 仅此节点，虚勾 2: 此节点及所有子节点，实勾 3:禁用未选中 4:禁用已选中】
	 */
	void saveResources2Role(String appId, String resourceTypeId, Long roleId, String permissionRank, String permissions);
	
	/**
	 * <p>
     * 资源对角色：一个资源授予一组角色。<br>
	 * 保存资源对角色的权限选项设置.<br>
	 * 注：资源对角色授权时是先删除某个角色对该资源的授权信息，然后再生成该角色和该资源间新的授权信息。<br>
     *    所以页面上所有的角色节点不管有没有授权信息都要传回后台，否则如果某个角色之前有授权信息，<br>
     *    而现在全部去掉了，则后台会因为该角色没传回来而无法删除该角色的授权信息。<br>
	 * </p>
	 * @param appId
	 * @param resourceTypeId
	 * @param resourceId  被授权的资源
	 * @param permissionRank
	 * @param permissions
	 *         授权内容, 当单个资源对多个角色授权时:  roleId1|2224, roleId2|4022
     *         竖线后面为各个权限选项的打勾情况【0: 没打勾, 1: 仅此节点，虚勾 2: 此节点及所有子节点，实勾 3:禁用未选中 4:禁用已选中】
	 */
	void saveResource2Roles(String appId, String resourceTypeId, Long resourceId, String permissionRank, String permissions);

	/**
	 * <p>
	 * 角色对资源授权（“角色维护”菜单）时，调用本方法获取 资源－操作选项 矩阵
	 * </p>
	 * @param appId
	 * @param resourceTypeId
	 * @param roleId
	 * @param permissionRank 下拉选择传入（普通、可授权、可传递授权）
     * @param roleUsers 当前用户的角色信息, 为展示资源提供过滤条件
	 * @return
	 */
	Object[] genResource2OperationMatrix(String appId, String resourceTypeId, Long roleId, String permissionRank, List<Long[]> roleUsers);

	/**
	 * <p>
	 * 资源对角色授权（“资源授予角色”菜单）时，调用本方法获取 角色－操作选项 矩阵
	 * </p>
	 * @param appId
	 * @param resourceTypeId
	 * @param roleId
	 * @param permissionRank  下拉选择传入（普通、可授权、可传递授权）
	 * @param roleUsers
	 * @return
	 */
	Object[] genRole2OperationMatrix(String appId, String resourceTypeId, Long roleId, String permissionRank, List<Long[]> roleUsers);
	
	/**
	 * <p>
	 * 创建角色、资源、权限选项关联关系。
	 * 添加权限选项，给管理员添加最大权限时使用
	 * </p>
	 * @param roleId 角色
	 * @param resourceId 资源
	 * @param operationId 权限选项
	 * @param permissionState 权限关联状态 1-仅仅当前节点 2-包含所有的子节点
	 * @param permissionTable
	 * @param resourceTable
	 */
	void saveRoleResourceOperation(Long roleId, Long resourceId, String operationId, Integer permissionState, 
	        String permissionTable, String resourceTable);

}
