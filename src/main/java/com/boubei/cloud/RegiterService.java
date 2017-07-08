package com.boubei.cloud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IRoleDao;
import com.boubei.tss.um.dao.IUserDao;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;

@Service("RegiterService")
public class RegiterService implements IRegiterService {
	
	@Autowired IUserDao userDao;
	@Autowired IRoleDao roleDao;
	@Autowired IGroupDao groupDao;

	/* 
	 * 1、注册用户XX（带domain?按普通用户注册：按域用户注册），作为域用户注册时可以选定一个或多个行业场景的角色（XX店老板等）
	 * 2、生成组域 （domain = loginName）
	 * 3、生成一个XX角色组、XX的报表、XX的录入、XX的栏目（默认含：公告、新闻、文档）
	 * 4、生成一个XX管理员角色，管理XX的一切
	 * 5、注册成功
	 * 
	 * 登陆：
	 * 1、进入XX的门户
	 * 2、各种引导说明（step by step）、查看报表、录入的示例
	 * 3、开始试用： 权限（人员 + 岗位）、门户（栏目文章）、流程（录入表单 + 权限控制）、数据（可视化、分发、共享）、API
	 */
	public boolean register(User user, String domain, String roles) {
		
		// 检查域名是否已经被注册
		List<?> list = groupDao.getEntities("from Group where ? in (domain, name)", domain);
		if(list.size() > 0) {
			throw new BusinessException("组织名【" +domain+ "】已被注册，请更换一个名称");
		}
		
		Group group = new Group();
		group.setName(domain);
		group.setDomain(domain);
		group.setGroupType(Group.MAIN_GROUP_TYPE);
		groupDao.saveGroup(group);
		
		userDao.create(user);
		
		return false;
	}

}
