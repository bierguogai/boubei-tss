package com.boubei.tss.um.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.web.display.ErrorMessageEncoder;
import com.boubei.tss.framework.web.display.SuccessMessageEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;
import com.boubei.tss.util.EasyUtils;

/**
 * <p> 用户注册Servlet </p>
 * <p>
 * 因普通的Action会被要求登录用户才能访问，所以这里采用Servlet来实现注册功能。
 * </p>
 */
@WebServlet(urlPatterns="/reg.in")
public class Register extends HttpServlet {

    private static final long serialVersionUID = -740569423483772472L;
    
    private IUserService userService;
    
	public void init() {
		userService = (IUserService) Global.getBean("UserService");
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
    	
    	response.setContentType("text/html;charset=UTF-8");
    	
    	// 验证码，防止机器自动注册
        HttpSession session = request.getSession();
        Object randomKey1 = request.getParameter(SSOConstants.RANDOM_KEY);
        Object randomKey2 = session.getAttribute(SSOConstants.RANDOM_KEY);
        if( randomKey2 == null || randomKey1 == null 
        		|| !randomKey2.toString().equals(randomKey1.toString()) ) {
        	
        	ErrorMessageEncoder encoder = new ErrorMessageEncoder(EX.REG_TIMEOUT_MSG);
            encoder.print(new XmlPrintWriter(response.getWriter()));
        	return;
        }
    	
        String account = request.getParameter("loginName");
        String password = request.getParameter("password");
        String domain = request.getParameter("domain");
        String isDev = request.getParameter("isDev");
        
        User user = new User();
		user.setLoginName(account);
		user.setPassword(password);
		user.setUserName(request.getParameter("userName"));
        user.setEmail(request.getParameter("email"));
        user.setTelephone(request.getParameter("telephone"));
        user.setPasswordQuestion(request.getParameter("passwordQuestion"));
        user.setPasswordAnswer(request.getParameter("passwordAnswer"));
        
        String regex = "\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}";
        if( account.matches(regex) && EasyUtils.isNullOrEmpty(user.getEmail()) ) {
        	user.setEmail(account);
        } 
        
        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(account);  
        if( m.find() && EasyUtils.isNullOrEmpty(user.getTelephone()) ) {
        	user.setTelephone(account);
        }
        
        if( !EasyUtils.isNullOrEmpty(isDev) ) {
        	userService.regDeveloper(user);
        } 
        else if( !EasyUtils.isNullOrEmpty(domain) ) {
        	userService.regBusiness(user, domain);
        } 
        else {
        	userService.regUser(user);
        }

        SuccessMessageEncoder encoder = new SuccessMessageEncoder(EX.REG_SUCCESS_MSG, "");
        encoder.print(new XmlPrintWriter(response.getWriter()));
    }
    
	/* 
	 * 一、企业商家注册：生成domain，其所有子组都属同一个域，用以SAAS隔离不同企业的数据（通过判断资源/录入记录的创建人是否为域下组的用户）
	 * 
	 * 1、注册用户XX，域用户注册完成登陆后可以选定一个或多个行业场景的模块（比如洗车店记账模块等，选定模块后，自动把模块角色集授予域用户）
	 * 2、生成组域
	 * 3、授予角色（域管理员）：维护自己的域组（新增、修改用户和子组）、公告栏（只能查看编辑自己域下用户创建的公告）
	 * 4、注册成功
	 * 5、登陆进入tssbi.html、开始使用既有功能。
	 * 
	 * 注：只有单机部署的BI允许无域（百世快运这类）；SAAS部署必须每个组都要有域，每个人必属于某个域。
	 *    自注册用户和开发者分属于各自的组域（自注册域、开发者域），其只允许开自己个人的数据。Admin不属于任何域。
	 *    
	 * 注2：域管理员拥有的权限
	 * 1、维护自己的域组（新增、修改用户和子组）,DOMAIN_ROLE已经对DOMAIN_ROOT拥有完全权限
	 * 2、维护公告栏（ID=2）,DOMAIN_ROLE已经对【公告栏】的新建和发布文章权限
	 */
    
    /* 
     * 二、开发者注册
     * 
	 * 1、注册开发者XX
	 * 2、加入开发者域（自动继承此域共有的"$开发者"角色）
	 * 3、生成一个XX角色组、XX的报表、XX的数据表
	 * 4、生成一个XX管理员角色，管理XX的一切
	 * 5、注册成功，等待审核通过后账号启用才可正式使用
	 * 
	 * 登陆：
	 * 1、进入开发者首页：“角色授权”、“数据报表”、“数据表” 、“数据源 ”这四个菜单快捷入口 + 各类在线开发案例和手册指南
	 * 2、开始开发： 权限（人员 + 岗位）、流程（录入表单 + 权限控制）、数据（可视化、分发、共享）、API
	 * 
	 * 注：禁止开发者对“数据源本地”进行访问，只能访问其自己创建的数据源
	 */
}
