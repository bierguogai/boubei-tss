package com.boubei.tss.modules.search;

import java.util.List;

import com.boubei.tss.um.entity.User;

public interface GeneralSearchService {
 
	/**
	 * <p>
	 * 一个组下面所有用户的因转授而获得的角色的情况
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<?> searchUserSubauthByGroupId(Long groupId);
	
	/**
	 * 根据用户组查询组下用户（需是登陆用户可见的用户）的角色授予情况
	 * @param groupId
	 * @return
	 */
	List<UserRoleDTO> searchUserRolesMapping(Long groupId);

	/** 
     * 查询角色授予的用户列表（包括直接授予用户的和授予组时组下的所有用户）
	 * @param roleId
	 * @return
	 */
	List<User> searchUsersByRole(Long roleId); 
}
