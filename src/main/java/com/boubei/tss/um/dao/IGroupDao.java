package com.boubei.tss.um.dao;

import java.util.Collection;
import java.util.List;

import com.boubei.tss.framework.persistence.ITreeSupportDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.UMQueryCondition;
 
public interface IGroupDao extends ITreeSupportDao<Group>{
    
    /**
     * 创建或修改Group信息
     * @param group
     */
    Group saveGroup(Group group);
    
    /**
     * 删除主（其他）用户组的所有子组，且包含用户
     * @param group
     * @return
     */
    Group removeGroup(Group group);
    /**
     * 删除辅助用户组的所有子组，但不包含用户 
     * @param group
     * @return
     */
    Group removeAssistmentGroup(Group group);
	
	/**
	 * <p>
	 * 根据用户组的id获取此用户组拥有的角色
	 * (不包括转授策略,转授策略只能在权限转授中删除对应关系)
	 * </p>
	 * @param groupId
	 * @return List
	 */
	List<?> findRolesByGroupId(Long groupId);
	
	/**
	 * 根据用户组的id获取用户组对用户的信息
	 * @param groupId
	 * @return List
	 */
	List<?> findGroup2UserByGroupId(Long groupId);
	
	/**
	 * 根据用户组的id获取用户组对角色的信息
	 * @param groupId
	 * @return List
	 */
	List<?> findGroup2RoleByGroupId(Long groupId);
 
	/**
	 * <p>
	 * 根据用户id找到该用户所在的主用户组.
     * 启用用户的时候如果需要启用其所属的主用户组，则取出该指定ID的启用User所在的主用户组。
	 * </p>
	 * @param userId
	 * @return
	 * 			未找到返回null
	 */
	Group findMainGroupByUserId(Long userId);

	/**
	 * <p>
	 * 根据groupId获得该组下可查看的子节点
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<?> getVisibleSubGroups(Long groupId);

	/**
	 * <p>
	 * 取一批组下的用户。用于复制、导入等操作。
	 * </p>
	 * @param groupIds
	 * @return
	 */
	List<User> getUsersByGroupIds(Collection<Long> groupIds);

	/**
	 * <p>
	 * 判断操作用户是否在操作的组或者该组的子组中
	 * </p>
	 * @param groupId
	 * @param operatorId
	 * @return
	 */
	boolean isOperatorInGroup(Long groupId, Long operatorId);

	/**
	 * <p>
	 * 根据组下的用户
	 * 包括子组.
     *    gu.decode like group.getDecode()，有可能删除【组的decode值】和【与该组平级的用户】的docode（在GroupUser中）一样，所以这里还要加个条件
     *    gu.decode <> group.getDecode() 
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<User> getUsersByGroupIdDeeply(Long groupId);
	
	/**
	 * 获取指定类型的用户组，并按“查看组”操作权限Id进行过滤
	 * @param operatorId
	 * @return
	 */
	List<?> getMainAndAssistantGroups(Long operatorId);
	
	/**
	 * 获取指定类型的用户组，并按指定的操作权限Id进行过滤
	 * @param operatorId
	 * @param operationId
	 * @param groupType
	 * @return
	 */
	List<Group> getGroupsByType(Long operatorId, String operationId, Integer groupType);
	
	/**
	 * 按操作选项获取一系列组的所有父组
	 * @param groupId
	 * @param operatorId
	 * @param operationId
	 * @return
	 */
	List<?> getParentGroupByGroupIds(List<Long> groupIds, Long operatorId, String operationId);
	
	/**
	 * <p>
	 * 获取所有可见的用户(即有浏览权限的组下的用户)
	 * </p>
	 * @param operatorId
	 * @return
	 */
	List<?> getVisibleMainUsers(Long operatorId);

	/**
	 * <p>
	 * 根据用户组获取用户。用于展示。
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<User> getUsersByGroupId(Long groupId);
    
    /**
     * <p>
     * 根据用户的id获取所在拥有此用户所在的用户组。
     * 如果是在辅助用户组，则可能对应多个辅助组。
     * </p>
     * 
     * @param userId
     * @return List
     */
    List<?> findGroupsByUserId(Long userId);

	/**
	 * <p>
	 * 根据用户id找到用户所在组所有父节点
	 * </p>
	 * @param userId
	 * @return
	 */
	List<?> getFatherGroupsByUserId(Long userId);
    
	
    //******************************************* 按组或按查询条件查询用户 *******************************************
	
    PageInfo searchUser(UMQueryCondition condition);
    
    /**
     * 根据用户组获取用户
     * 分页
     * @param groupId
     * @param pageNum
     * @param orderBy
     * @return
     */
    PageInfo getUsersByGroup(Long groupId, Integer pageNum, String...orderBy);
    
}
