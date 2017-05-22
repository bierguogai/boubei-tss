package com.boubei.cloud;

import org.springframework.stereotype.Service;

import com.boubei.tss.um.entity.User;

@Service("RegiterService")
public class RegiterService implements IRegiterService {

	/* 
	 * 1、注册用户XX（带domain?）
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
	public boolean register(User user, String domain) {
		
		return false;
	}

}
