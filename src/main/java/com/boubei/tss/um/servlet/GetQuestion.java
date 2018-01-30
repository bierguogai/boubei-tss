/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.display.ErrorMessageEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.framework.web.display.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;

/**
 * 获取用户的密码提示问题
 */
@WebServlet(urlPatterns="/getQuestion.in")
public class GetQuestion extends HttpServlet {
 
	private static final long serialVersionUID = -740569423483772472L;
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html;charset=UTF-8");
    	
    	String loginName = request.getParameter("loginName");
		IUserService service = (IUserService) Global.getBean("UserService");
		User user = service.getUserByLoginName(loginName);
		if ( user == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder( EX.parse(EX.U_41, loginName) );
			encoder.print(new XmlPrintWriter(response.getWriter()));
			return;
		} 
		
		String question = user.getPasswordQuestion();
		if ( question == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder( EX.parse(EX.U_42, loginName) );
			encoder.print(new XmlPrintWriter(response.getWriter()));
		} 
		else {
            XmlHttpEncoder encoder = new XmlHttpEncoder();
			encoder.put("Question", question);
			encoder.print(new XmlPrintWriter(response.getWriter()));
		}
	}
}

	