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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IRoleDao;
import com.boubei.tss.um.entity.RoleGroup;
import com.boubei.tss.um.entity.RoleUser;
import com.boubei.tss.um.entity.SubAuthorize;
import com.boubei.tss.um.service.ISubAuthorizeService;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

@Service("SubAuthorizeService")
public class SubAuthorizeService implements ISubAuthorizeService {

	@Autowired private IRoleDao  roleDao;
	@Autowired private IGroupDao groupDao;	

	public void deleteSubauth(Long id) {
		roleDao.deleteStrategy((SubAuthorize) roleDao.getEntity(SubAuthorize.class, id));
	}

	public void disable(Long id, Integer disabled) {
		SubAuthorize strategy = (SubAuthorize) roleDao.getEntity(SubAuthorize.class, id);
		strategy.setEndDate( DateUtil.addDays(new Date(), 1) );
		strategy.setDisabled(disabled);
		roleDao.update(strategy);
	}
 
	public Map<String, Object> getSubauthInfo4Create() {
	    Long operatorId = Environment.getUserId();
	    
		List<?> groups = groupDao.getMainAndAssistantGroups(operatorId); // 用户组
		
        Map<String, Object> map = new HashMap<String, Object>();
		map.put("Rule2GroupTree", groups);
		map.put("Rule2UserTree", groupDao.getVisibleMainUsers(operatorId));
		map.put("Rule2RoleTree", roleDao.getSubAuthorizeableRoles(operatorId));
		return map;
	}

	public Map<String, Object> getSubauthInfo4Update(Long strategyId) {
	    Long operatorId = Environment.getUserId();
	    
        Map<String, Object> map = new HashMap<String, Object>();
		map.put("RuleInfo", roleDao.getEntity(SubAuthorize.class, strategyId));
		map.put("Rule2RoleTree", roleDao.getSubAuthorizeableRoles(operatorId));
		map.put("Rule2GroupTree", groupDao.getMainAndAssistantGroups(operatorId)); // 主用户组
		map.put("Rule2GroupExistTree", roleDao.getGroupsByStrategy(strategyId));
		map.put("Rule2UserExistTree",  roleDao.getUsersByStrategy(strategyId));
		map.put("Rule2RoleExistTree",  roleDao.getRolesByStrategy(strategyId));
		
		return map;
	}

	public List<?> getStrategyByCreator() {
	    return roleDao.getEntities("from SubAuthorize o where o.creatorId = ?" , Environment.getUserId());
	}

	public void saveSubauth(SubAuthorize strategy, String userIds, String groupIds, String roleIds) {
		if(strategy.getId() == null) {
		    roleDao.createObject(strategy);
		} 
		else {
		    roleDao.update(strategy);
		}
        
		// 在角色用户关系表中保存 策略对用户，策略对角色的信息 在角色用户组关系表中保存 策略对用户组，策略对角色的信息
        saveRule2Group(strategy, roleIds, groupIds);
        saveRule2User(strategy, roleIds, userIds);
	}
    
    /**
     * <p>
     * 在角色用户关系表中保存 策略对用户，策略对角色的信息。
     * 策略可以授予用户、用户组、也可以授予角色，或者三者兼有。
     * </p>
     * @param strategy
     * @param roleIdsStr
     * @param userIdsStr
     */
    private void saveRule2User(SubAuthorize strategy, String roleIdsStr, String userIdsStr) {
        Long subauthId = strategy.getId();
		List<?> roleUsers = roleDao.getRoleUserByStrategy(subauthId);
        Map<String, RoleUser> historyMap = new HashMap<String, RoleUser>(); // 把老的转授记录放入一个map, 以"roleId_userId"为key
        for (Object temp : roleUsers) { 
            RoleUser roleUser = (RoleUser) temp;
            historyMap.put(roleUser.getRoleId() + "_" + roleUser.getUserId(), roleUser);
        }
        
        if ( !EasyUtils.isNullOrEmpty(roleIdsStr) && !EasyUtils.isNullOrEmpty(userIdsStr)) {
        	String[] roleIds = roleIdsStr.split(",");
            String[] userIds = userIdsStr.split(",");
            for (String roleId : roleIds) {
                for (String userId : userIds) {
                    saveRoleUser(historyMap, roleId, userId, subauthId);
                }
            }
        } 
        
        //老的转授记录中剩下的就是该删除的了
        roleDao.deleteAll(historyMap.values());
    }
    
    private void saveRoleUser(Map<String, RoleUser> historyMap, String roleId, String userId, Long subauthId){
        // 如果老的转授记录里面有，则从历史记录中移出
        RoleUser roleUser = historyMap.remove(roleId + "_" + userId); 
        
        //如果老的转授记录里面没有，则新增
        if (roleUser == null) { 
            roleUser = new RoleUser();
            roleUser.setRoleId(Long.valueOf(roleId));
            roleUser.setUserId(Long.valueOf(userId));
            roleUser.setStrategyId(subauthId);
            roleDao.createObject(roleUser);
        } 
    }

	/**
	 * <p>
	 * 在角色用户组关系表中保存 策略对用户组，策略对角色的信息
	 * </p>
	 * @param strategy
	 * @param roleIdsStr
	 * @param groupIdsStr
	 */
	private void saveRule2Group(SubAuthorize strategy, String roleIdsStr, String groupIdsStr) {
		List<?> roleGroups = roleDao.getRoleGroupByStrategy(strategy.getId());
		Map<String, RoleGroup> historyMap = new HashMap<String, RoleGroup>(); // 把老的转授记录做成一个map， 以"roleId_groupId"为key
		 for (Object temp : roleGroups) { 
			RoleGroup roleGroup = (RoleGroup) temp;
			historyMap.put(roleGroup.getRoleId() + "_" + roleGroup.getGroupId(), roleGroup);
		}
		 
        if ( !EasyUtils.isNullOrEmpty(roleIdsStr) && !EasyUtils.isNullOrEmpty(groupIdsStr)) {
        	String[] roleIds  = roleIdsStr.split(",");
            String[] groupIds = groupIdsStr.split(",");
            for (String roleId : roleIds) {
                for (String groupId : groupIds) {
                    saveRoleGroup(historyMap, roleId, groupId, strategy);
                }
            }
        } 
 
        // 老的转授记录中剩下的就是该删除的了
        roleDao.deleteAll(historyMap.values());
	}
    
    /** roleId, groupId 之一有可能为null */
    private void saveRoleGroup(Map<String, RoleGroup> historyMap, String roleId, String groupId, SubAuthorize strategy){
        // 如果老的转授记录里面有，则从老的转授记录中移出
        RoleGroup roleGroup = historyMap.remove(roleId + "_" + groupId); 
        
        //如果老的转授记录里面没有，则新增
        if (roleGroup == null) { 
            roleGroup = new RoleGroup();
            roleGroup.setRoleId(Long.valueOf(roleId));
            roleGroup.setGroupId(Long.valueOf(groupId));
            roleGroup.setStrategyId(strategy.getId());
            roleDao.createObject(roleGroup);
        } 
    }
}
