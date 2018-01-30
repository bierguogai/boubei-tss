/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.dao;

import java.util.List;

import com.boubei.tss.framework.persistence.ITreeSupportDao;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.SubAuthorize;
import com.boubei.tss.um.permission.filter.PermissionTag;
 
public interface IRoleDao extends ITreeSupportDao<Role> {
    
    /**
     * 如果是组的话删除所有子节点
     * 删除其他和 Role 相关的数据信息:roleGroup, roleUser, 转授的。
     * @param role
     */
	List<Role> removeRole(Role role);

	/**
	 * 根据角色的id获取所在拥有此角色的用户
     * 编辑角色的时候用。注：需要过滤掉转授关联起来的RoleUser。
	 * @param roleId
	 * @return List
	 */
	List<?> getUsersByRoleId(Long roleId);

	/**
	 * 根据角色的id获取所在拥有此角色的用户组。
     * 编辑角色的时候用。注：需要过滤掉转授关联起来的RoleGroup。
	 * @param roleId
	 * @return List
	 */
	List<?> getGroupsByRoleId(Long roleId);
	
   /**
     * <p>
     * 获取用户有编辑权限的角色。用于编辑组或用户时，可选的角色列表。
     * </p>
     * @param userId
     * @return
     */
    @PermissionTag(
            operation = UMConstants.ROLE_EDIT_OPERRATION, 
            resourceType = UMConstants.ROLE_RESOURCE_TYPE_ID
    )
	List<?> getEditableRoles();	
 
	// ===========================================================================================================
    // 转授相关的数据库操作
    // ===========================================================================================================
    
    /**
     * <p>
     * 删除实体对象
     * </p>
     * @param entity
     */
    void deleteStrategy(SubAuthorize strategy);

    /**
     * <p>
     * 根据策略的id获取该策略拥有的用户
     * </p>
     * @param strategyId
     * @return List
     */
    List<?> getUsersByStrategy(Long strategyId);

    /**
     * <p>
     * 根据策略的id获取该策略拥有的用户组
     * </p>
     * @param strategyId
     * @return List
     */
    List<?> getGroupsByStrategy(Long strategyId);

    /**
     * <p>
     * 根据策略id查找该策略被赋予的角色列表
     * </p>
     * @param strategyId
     * @return
     */
    List<?> getRolesByStrategy(Long strategyId);

    /**
     * <p>
     * 根据策略id找到其关联的角色用户的信息
     * </p>
     * @param strategyId
     * @return
     */
    List<?> getRoleUserByStrategy(Long strategyId);
    
    /**
     * <p>
     * 根据策略id找到其关联的角色用户组的信息
     * </p>
     * @param strategyId
     * @return
     */
    List<?> getRoleGroupByStrategy(Long strategyId);
    
    /**
     * <pre>
     * 返回用户被转授予的角色列表：用户拥有的角色以及用户所在组拥有的角色（供转授使用）<br/>
     * 用户只能对自身拥有的角色进行转授，如果是因为转授而获得的角色不能再转授。 <br/>
     * 停用的角色虽然可以转授出去，但是使用时会过滤掉的。 <br/>
     * </pre>
     * @param userId
     * @return
     */
    List<?> getSubAuthorizeableRoles(Long userId);
    
    // ===========================================================================================================
    // 用户的授权信息变动时，拦截器需要调用来收回转授权限的方法
    // ===========================================================================================================
    
    /**
     * 当组不再拥有的某个角色，则收回这个组下用户转授出去的授权信息
     * @param groupId
     * @param roleId
     */
    void deleteGroupSubAuthorizeInfo(Long groupId, Long roleId);
    
    /**
     * 当用户不再拥有的某个角色，则收回这个用户转授出去的授权信息
     * @param groupId
     * @param roleId
     */
    void deleteUserSubAuthorizeInfo(Long userId, Long roleId);
}
