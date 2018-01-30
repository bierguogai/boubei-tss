/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.persistence.BaseDao;
import com.boubei.tss.um.dao.IUserDao;
import com.boubei.tss.um.entity.GroupUser;
import com.boubei.tss.um.entity.User;

@Repository("UserDao")
public class UserDao extends BaseDao<User> implements IUserDao {

    public UserDao() {
		super(User.class);
	}
    
    public User initUser(User obj) {
		return create(obj);
	}
	
	public User removeUser(User user) {
		delete(user);

        Long deletedUserId = user.getId();
        deleteAll(findUser2GroupByUserId(deletedUserId)); // 用户对组
        deleteAll(findUser2RoleByUserId(deletedUserId));  // 用户对角色
		
		return user;
	}

	public User getUserByLoginName(String account) {
	    List<?> users = getEntities("from User o where ? in (o.loginName, o.telephone, o.email) ", account);
        return users.size() > 0 ? (User) users.get(0) : null;
	}

	public List<?> findUser2GroupByUserId(Long userId) {
		return getEntities("from GroupUser o where o.userId = ? ", userId);
	}

	public List<?> findUser2RoleByUserId(Long userId) {
		return getEntities("from RoleUser o where o.id.userId = ? and o.strategyId is null", userId);
	}

	public List<?> findRolesByUserId(Long userId) {
		String hql = "select distinct r from RoleUser ru, Role r where ru.id.roleId = r.id and ru.id.userId = ? and ru.strategyId is null ";
		return getEntities(hql, userId);
	}
 
	public GroupUser getGroup2User(Long groupId, Long userId) {
        List<?> list = getEntities("from GroupUser o where o.groupId = ? and o.userId = ?", groupId, userId);
		return !list.isEmpty() ? (GroupUser)list.get(0) : null;
	}
}