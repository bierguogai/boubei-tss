package com.boubei.tss.um.service;

import java.util.List;

import com.boubei.tss.modules.log.Logable;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;

public interface IGroupService {
	
	/** 根据ID查询用户组 */
	Group getGroupById(Long id);

	/**
	 * <p>
	 * 根据用户组的id获取所在用户组的用户
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<User> getUsersByGroupId(Long groupId);

	/**
	 * <p>
	 * 根据用户组的id获取此用户组拥有的角色
	 * </p>
	 * @param groupId
	 * @return List
	 */
	List<?> findRolesByGroupId(Long groupId);
	
	/**
	 * <p>
	 * 查询操作用户拥有编辑权限所有角色
	 * </p>
	 * @return List
	 */
	List<?> findEditableRoles();
	
    /**
     * 获取有操作权限的主用户组（多用于移动时取目标用户组）
     * @param operationId
     * @return
     */
    Object[] getMainGroupsByOperationId(String operationId);
    
    /**
     * 获取有操作权限的辅助用户组
     * @param operationId
     * @return
     */
    Object[] getAssistGroupsByOperationId(String operationId);

	/**
	 * <p>
	 * 编辑一个Group对象的明细信息、用户组对用户信息、用户组对角色的信息.
	 * </p>
	 * @param group
	 * @param userIdsStr
	 * @param roleIdsStr
	 */
    @Logable(operateObject="用户组织",  
            operateInfo="编辑 ${args[0]} 用户组的明细信息（用户组对用户信息: ${args[1]} 、用户组对角色的信息: ${args[2]}）"
        )
	void editExistGroup(Group group, String userIdsStr, String roleIdsStr);
	
	/**
	 * <p>
	 * 新建一个Group对象的明细信息、用户组对用户信息、用户组对角色的信息
	 * </p>
	 * @param group
	 * @param userIdsStr
	 * @param roleIdsStr
	 */
    @Logable(operateObject="用户组织", 
            operateInfo="新建 ${args[0]} 用户组 （用户组对用户信息: ${args[1]} 、用户组对角色的信息: ${args[2]}）"
        )
	void createNewGroup(Group group, String userIdsStr, String roleIdsStr);
	
    @Logable(operateObject="用户组织", 
            operateInfo="新建了【 ${args[0]} 】域"
        )
    Group createDomainGroup(String domain);
    
	/**
	 * <p>
	 * 启用或者停用用户组
	 * </p>
	 * @param groupId
	 * @param disabled
	 */
    @Logable(operateObject="用户组织", 
            operateInfo="启用/停用用户组 (ID: ${args[0]})，(State: ${args[1]})"
        )
	void startOrStopGroup(Long groupId, Integer disabled);
	
	/**
	 * <p>
	 * 用户组的排序
	 * </p>
	 * @param groupId
	 * @param toGroupId
	 * @param direction
	 * 			+1/向下
	 * 			-1/向上
	 */
    @Logable(operateObject="用户组织", 
            operateInfo="排序(ID: ${args[0]}) 用户组至 (ID: ${args[1]}) 用户组"
        )
	void sortGroup(Long groupId, Long toGroupId, int direction);
	
	/**
	 * 获取用户组
	 */
    List<?> findGroups();

	/**
	 * 删除用户组
	 * @param groupId
	 */
    @Logable(operateObject="用户组织",  
            operateInfo="删除 (ID: ${args[0]}) 用户组" )
	void deleteGroup(Long groupId);
    
	/**
	 * <p>
	 * 根据groupId获得该组下可查看的子节点
	 * </p>
	 * @param groupId
	 * @return
	 */
	List<?> getVisibleSubGroups(Long groupId);

	void move(Long id, Long toGroupId);

}
