package com.boubei.cloud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IRoleDao;
import com.boubei.tss.um.dao.IUserDao;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.GroupUser;
import com.boubei.tss.um.entity.RoleUser;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.util.EasyUtils;

/**
 * TODO
 * 1、一个月不登录，清除。
 * 2、体验完成后，分享到微博微信等。
 * 3、支持用“社交账号登录”
 */
@Service("RegiterService")
public class RegiterService implements IRegiterService {
	
	static Long DOMAIN_ROOT_ID = 102L;
	static Long DOMAIN_ROLE_ID = 67L;
	static Long NOTICE_CHANNEL_ID = 26L;
	
	@Autowired IUserDao userDao;
	@Autowired IRoleDao roleDao;
	@Autowired IGroupDao groupDao;

	/* 
	 * 企业商家注册：生成domain，其所有子组都属同一个域，用以SAAS隔离不同企业的数据（通过判断资源/录入记录的创建人是否为域下组的用户）
	 * 
	 * 1、注册用户XX（带domain?按普通用户注册：按域用户注册），作为域用户注册时可以选定一个或多个行业场景的角色集（XX店老板等）
	 * 2、生成组域
	 * 3、授予角色（域管理员）：维护自己的域组（新增、修改用户和子组）、公告栏（只能查看编辑自己域下用户创建的公告）
	 * 4、注册成功
	 * 
	 * 登陆进入tssbi.html、开始使用既有功能
	 */
	public boolean regBusiness(User user, String domain, String roles) {
		// create group
		List<?> list = groupDao.getEntities("from Group where ? in (domain, name)", domain);
		if(list.size() > 0) {
			throw new BusinessException("组织名【" +domain+ "】已被注册，请更换一个名称");
		}
		
		Group group = new Group();
		group.setName(domain);
		group.setParentId(DOMAIN_ROOT_ID);
		group.setDomain(domain);
		group.setGroupType(Group.MAIN_GROUP_TYPE);
		groupDao.saveGroup(group);
		
		// create user
		userDao.create(user);
		
		// save groupuser
		GroupUser gu = new GroupUser(user.getId(), group.getId());
		userDao.createObject(gu);
	
		// roleuser
		String[] _roles = EasyUtils.obj2String(roles).split(",");
		for(String _role : _roles) {
			RoleUser ru = new RoleUser();
			ru.setRoleId( EasyUtils.obj2Long(_role) );
			ru.setUserId(user.getId());
			roleDao.createObject(ru);
		}

		RoleUser ru = new RoleUser();
		ru.setRoleId(DOMAIN_ROLE_ID);
		ru.setUserId(user.getId());
		roleDao.createObject(ru);
		
		// permission：维护自己的域组（新增、修改用户和子组）,DOMAIN_ROLE已经对DOMAIN_ROOT拥有完全权限 
		// permission：维护公告栏（ID=26）,DOMAIN_ROLE已经对【公告栏】的新建和发布文章权限
		
		return false;
	}
 
	/* 
	 * 1、注册开发者XX
	 * 2、生成开发者私有域（XX用户组）
	 * 3、生成一个XX角色组、XX的报表、XX的录入表
	 * 4、生成一个XX管理员角色，管理XX的一切
	 * 5、注册成功，等待审核通过后账号启用才可正式使用
	 * 
	 * 登陆：
	 * 1、进入开发者首页：“角色授权”、“用户管理”、“数据报表”、“录入表” 、“数据源 ”这四个菜单快捷入口 + 各类在线开发案例和手册指南
	 * 2、开始开发： 权限（人员 + 岗位）、门户（栏目文章）、流程（录入表单 + 权限控制）、数据（可视化、分发、共享）、API
	 * 
	 * 注：禁止开发者对“数据源本地”进行访问，只能访问其自己创建的录入表
	 */
	public boolean regDeveloper(User user) {
		// TODO Auto-generated method stub
		
		/* 
		 * 将新增的私人资源授权给私人管理员角色
		GroupPermission newPermission = new GroupPermission();   
        newPermission.setOperationId(operationId);
        newPermission.setRoleId(roleId);
        newPermission.setPermissionState(permissionState);
        newPermission.setIsGrant(isGrant);
        newPermission.setIsPass(isPass);
        newPermission.setResourceId(resourceId);
        newPermission.setResourceName(resource.getName());
        roleDao.createObjectWithoutFlush(newPermission);
        */
		
		return false;
	}

}
