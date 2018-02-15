/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.cloud.ModuleDef;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IRoleDao;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.GroupUser;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.RoleGroup;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.ResourcePermission;
import com.boubei.tss.um.service.IGroupService;
import com.boubei.tss.util.EasyUtils;
 
@Service("GroupService")
public class GroupService implements IGroupService {

	@Autowired private IGroupDao groupDao;
	@Autowired private IRoleDao  roleDao;
	@Autowired private ResourcePermission resourcePermission;

    public Group getGroupById(Long id) {
        Group entity = groupDao.getEntity(id);
        groupDao.evict(entity);
		return entity;
    }

    public List<User> getUsersByGroupId(Long groupId) {
        return groupDao.getUsersByGroupId(groupId);
    }

    public List<?> findRolesByGroupId(Long groupId) {
        return groupDao.findRolesByGroupId(groupId);
    }

    @SuppressWarnings("unchecked")
	public List<?> findEditableRoles() {
        List<Role> list = (List<Role>) roleDao.getEditableRoles();
        Set<Role> set = new LinkedHashSet<Role>(list);
        
        // 加上因域管理员选用功能模块而得来的模块角色，以用来给域内用户定岗
        String hql = "select o from ModuleDef o, ModuleUser mu where mu.moduleId = o.id and mu.userId = ? and o.status in ('opened') ";
        List<ModuleDef> modules = (List<ModuleDef>) roleDao.getEntities(hql, Environment.getUserId());
        for(ModuleDef module : modules ) {
        	String[] roles = module.getRoles().split(",");
    		for(String role : roles) {
    			Long roleId = EasyUtils.obj2Long(role);
				Role r = roleDao.getEntity(roleId);
				if(r != null) {
					roleDao.evict(r);
    				r.setName( r.getName() + "(" + module.getModule() + ")" );
    				set.add( r );
				}
    		}
        }
        
		return new ArrayList<Role>(set);
    }

    public List<?> findGroups() {
        return groupDao.getMainAndAssistantGroups( Environment.getUserId() );
    }
    
    public Object[] getAssistGroupsByOperationId(String operationId) {
        return getGroupsByGroupTypeAndOperationId(Group.ASSISTANT_GROUP_TYPE, operationId);
    }
    
    public Object[] getMainGroupsByOperationId(String operationId) {
        return getGroupsByGroupTypeAndOperationId(Group.MAIN_GROUP_TYPE, operationId);
    }
    
    private Object[] getGroupsByGroupTypeAndOperationId(Integer groupType, String operationId) {
    	Long operatorId = Environment.getUserId();
    	
        List<?> groups = groupDao.getGroupsByType(operatorId, operationId, groupType);  
        List<Long> groupIds = new ArrayList<Long>();
        for( Object temp : groups ){
            Group group = (Group) temp;
            groupIds.add(group.getId());
        }
        List<?> parentGroups = groupDao.getParentGroupByGroupIds(groupIds, operatorId, UMConstants.GROUP_VIEW_OPERRATION);
        return new Object[] { groupIds, parentGroups };
    }
 
    public Group createDomainGroup(String domain) {
		List<?> list = groupDao.getEntities("from Group where ? in (domain, name)", domain);
		if(list.size() > 0) {
			throw new BusinessException(EX.parse(EX.U_19, domain));
		}
		Group group = new Group();
		group.setName(domain);
		group.setParentId(UMConstants.DOMAIN_ROOT_ID);
		group.setDomain(domain);
		group.setGroupType(Group.MAIN_GROUP_TYPE);
		group.setSeqNo(groupDao.getNextSeqNo(UMConstants.DOMAIN_ROOT_ID));
		groupDao.saveGroup(group);
		
		return group;
    }
    
	public void createNewGroup(Group group, String userIdsStr, String roleIdsStr) {
		Long parentId = group.getParentId();
		Group parent = groupDao.getEntity(parentId);
		
		group.setDomain(parent.getDomain());
		group.setSeqNo(groupDao.getNextSeqNo(parentId));
		group.setDisabled(parent.getDisabled());
		groupDao.saveGroup(group);
        
		saveGroupToUser(group.getId(), userIdsStr);
		saveGroupToRole(group.getId(), roleIdsStr);
	}
    
    public void editExistGroup(Group group, String userIdsStr, String roleIdsStr) {
        groupDao.refreshEntity(group);
        
        saveGroupToRole(group.getId(), roleIdsStr);
        
        /* 主用户组中组对用户的关系不能随便更改，所以不能调用saveGroupToUser
         * 只有辅助用户组可以选择组对应的用户(group2UserExistTree有效)，可以调用saveGroupToUser方法 。
         */
        Integer groupType = group.getGroupType();
		if (Group.ASSISTANT_GROUP_TYPE.equals(groupType)) {
			saveGroupToUser(group.getId(), userIdsStr);
		}
    }
    
    /** 组对用户：先把该组对应的用户都找到，再把提交上来的用户和找到的数据比较，多的做增加操作。 */
    private void saveGroupToUser(Long groupId, String userIdsStr) {
        List<?> group2Users = groupDao.findGroup2UserByGroupId(groupId);
        
        Map<Long, Object> historyMap = new HashMap<Long, Object>(); //把老的组对用户记录做成一个map，以"userId"为key
        for (Object temp : group2Users) {
            GroupUser groupUser = (GroupUser) temp;
            historyMap.put(groupUser.getUserId(), groupUser);
        }
        
        if ( !EasyUtils.isNullOrEmpty(userIdsStr) ) {
            String[] userIds = userIdsStr.split(",");
            for (String temp : userIds) {
                // 如果historyMap里面没有，则新增用户组对用户的关系；如果historyMap里面有，则从历史记录中移出；剩下的将被删除
                Long userId = Long.valueOf(temp);
                if (historyMap.remove(userId) == null) { 
                    GroupUser group2User = new GroupUser(userId, groupId);
                    groupDao.createObject(group2User);
                } 
            }
        }
        
        // historyMap中剩下的就是该删除的了
        groupDao.deleteAll(historyMap.values());
    }
    
    /** 组对角色。先把该组对应的角色都找到，再把提交上来的用户和找到的数据比较，多的做增加操作 */
    private void saveGroupToRole(Long groupId, String roleIdsStr) {
        List<?> group2Roles = groupDao.findGroup2RoleByGroupId(groupId);
        
        Map<Long, Object> historyMap = new HashMap<Long, Object>();// 把老的组对角色记录做成一个map，以"roleId"为key
        for (Object temp : group2Roles) {
            RoleGroup roleGroup = (RoleGroup) temp;
            historyMap.put(roleGroup.getRoleId(), roleGroup);
        }
        
        if ( !EasyUtils.isNullOrEmpty(roleIdsStr) ) {
            String[] roleIds = roleIdsStr.split(",");
            for (String temp : roleIds) {
                // 如果historyMap里面没有，则新增用户组对角色的关系; 如果historyMap里面有，则从历史记录中移出；剩下的将被删除
                Long roleId = Long.valueOf(temp);
                if (historyMap.remove(roleId) == null) {
                    RoleGroup role2Group = new RoleGroup();
                    role2Group.setRoleId(roleId);
                    role2Group.setGroupId(groupId);
                    groupDao.createObject(role2Group);
                } 
            }
        }
        
        // historyMap中剩下的就是该删除的了
        groupDao.deleteAll(historyMap.values());
    }

    public void sortGroup(Long groupId, Long toGroupId, int direction) {
        groupDao.sort(groupId, toGroupId, direction);
    }
    
    public void move(Long id, Long toGroupId) {
        Group group = groupDao.getEntity(id);
        Group target = groupDao.getEntity(toGroupId);
        if( !group.getGroupType().equals(target.getGroupType()) ) {
        	throw new BusinessException(EX.U_24);
        }
        
        group.setParentId(toGroupId);
        group.setSeqNo(groupDao.getNextSeqNo(toGroupId));
                   
        groupDao.moveEntity(group);
    }

    public void startOrStopGroup(Long groupId, Integer disabled) {
        String applicationId = UMConstants.TSS_APPLICATION_ID;
        if (ParamConstants.TRUE.equals(disabled)) { // 停用            
            String operationId = UMConstants.GROUP_EDIT_OPERRATION;
            checkSubGroupsPermission(groupId, operationId, EX.U_22);
            
            stopGroup(groupId);
        } 
        else { // 启用一个组,该组的父节点也得全部启用
            List<?> groups = groupDao.getParentsById(groupId);
            String operationId = UMConstants.GROUP_EDIT_OPERRATION;
            List<?> canDoGroups = resourcePermission.getParentResourceIds(applicationId, UMConstants.GROUP_RESOURCE_TYPE_ID, groupId, operationId, 
                    Environment.getUserId());
 
            PermissionHelper.vsSize(canDoGroups, groups, EX.U_23);
            
            for(Iterator<?> it = groups.iterator();it.hasNext();){
                Group group = (Group) it.next();
                if(group.getDisabled().equals(ParamConstants.TRUE)) {
                    group.setDisabled(ParamConstants.FALSE);
                    groupDao.update(group); 
                }
            }
        }
    }
    
    // 停用组以及组下的子组和所有的用户
    private void stopGroup(Long groupId) {
        Group group = groupDao.getEntity(groupId);
        groupDao.executeHQL("update Group set disabled = ? where decode like ?", ParamConstants.TRUE, group.getDecode() + "%");
       
        /* 
         * 停用主用户组，需要停用组下的用户。
         * 停用辅助用户组，不停用用户，因为辅助用户组当中的用户是从主用户组中选取的.
         */
        Integer groupType = group.getGroupType();
        if ( Group.MAIN_GROUP_TYPE.equals(groupType) ) {
            List<User> users = groupDao.getUsersByGroupIdDeeply(groupId);
            for( User user : users) {
                if(!ParamConstants.TRUE.equals(user.getDisabled())){
                    user.setDisabled(ParamConstants.TRUE);
                }
            }       
        }
    }

    public void deleteGroup(Long groupId) {
        if( groupDao.isOperatorInGroup(groupId, Environment.getUserId()) ) {
            throw new BusinessException(EX.U_20);
        }
        
        Group group = groupDao.getEntity(groupId);
        String operationId = UMConstants.GROUP_EDIT_OPERRATION;
        checkSubGroupsPermission(groupId, operationId, EX.U_21);
        
        // 辅助用户组里面的用户都是从主用户组选过来的,所以删除的时候只是删除辅助用户组的结构，里面的用户是不删的
        if ( Group.ASSISTANT_GROUP_TYPE.equals(group.getGroupType()) ) { // 辅助用户组
            groupDao.removeAssistmentGroup(group);
        } 
        else {// 删除主用户组和其他用户组
            groupDao.removeGroup(group);
        }
    }
    
    // 判断对所有子节点是否都拥有指定的操作权限
    private void checkSubGroupsPermission(Long groupId, String operationId, String msg) {
        String applicationId = UMConstants.TSS_APPLICATION_ID;
        List<?> allGroups = groupDao.getChildrenById(groupId);
        List<?> canDoGroups = resourcePermission.getSubResourceIds(applicationId, UMConstants.GROUP_RESOURCE_TYPE_ID, 
                groupId, operationId, Environment.getUserId());
        
        //如果将要操作的数量==能够操作的数量,说明对所有组都有操作权限,则返回true
        PermissionHelper.vsSize(canDoGroups, allGroups, msg);
    }
  
	public List<?> getVisibleSubGroups(Long groupId){
		return groupDao.getVisibleSubGroups(groupId);
	}
}
