package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.framework.web.display.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.um.helper.PasswordRule;

/** 
 * 获取密码强度
 */
@WebServlet(urlPatterns="/getPasswordStrength.in")
public class GetPasswordStrength extends HttpServlet {

	private static final long serialVersionUID = -192831928301L;
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String loginName = request.getParameter("loginName");
		String password  = request.getParameter("password");
		
		Object level = PasswordRule.getStrengthLevel(password, loginName);
		
		response.setContentType("text/html;charset=UTF-8");
		XmlHttpEncoder encoder = new XmlHttpEncoder();
		encoder.put("SecurityLevel", level);
		encoder.print(new XmlPrintWriter(response.getWriter()));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
}
