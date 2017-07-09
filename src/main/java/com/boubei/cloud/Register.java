package com.boubei.cloud;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.display.SuccessMessageEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.util.EasyUtils;

@WebServlet(urlPatterns="/reg.in")
public class Register extends HttpServlet {
	
	private static final long serialVersionUID = -740569423483772472L;
    
    private IRegiterService service;
 
	public void init() {
		service = (IRegiterService) Global.getBean("RegiterService");
	}
	
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
    	
        User user = new User();
        String account = request.getParameter("loginName");
		user.setLoginName(account);
        user.setPassword(request.getParameter("password"));
        user.setUserName(request.getParameter("userName"));
        user.setEmail(request.getParameter("email"));
        user.setTelephone(request.getParameter("telephone"));
        
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
        
        String domain = request.getParameter("domain"); // 域由后台统一生成
        String roles = request.getParameter("roles");  // 
        
        service.regBusiness(user, domain, roles);

        response.setContentType("text/html;charset=UTF-8");
        SuccessMessageEncoder encoder = new SuccessMessageEncoder("用户注册成功！", "用户注册成功！");
        encoder.print(new XmlPrintWriter(response.getWriter()));
    }
}
