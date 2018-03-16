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

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.framework.web.display.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.MathUtil;

/**
 * <p>
 * 通过用户登录名，获取用户认证方式及用户名<br>
 * </p>
 */
@WebServlet(urlPatterns="/getLoginInfo.in")
public class GetLoginInfo extends HttpServlet {
    private static final long serialVersionUID = 8680769606094382553L;
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ILoginService service = (ILoginService) Global.getBean("LoginService");
        
        String loginName = request.getParameter(SSOConstants.LOGINNAME_IN_SESSION);
        try {
        	String[] info = service.getLoginInfoByLoginName(loginName);
            XmlHttpEncoder encoder = new XmlHttpEncoder(); 
            encoder.put("UserName",  info[0]);  // 返回用户姓名
            encoder.put("identifier", info[1]); // 返回身份认证器类名：全路径
            
            // 产生一个登录随机数给客户端，客户端使用该随机数对账号和密码进行加密后再传回后台
            int randomKey = MathUtil.randomInt(10000);
            encoder.put(SSOConstants.RANDOM_KEY, randomKey);
			request.getSession(true).setAttribute(SSOConstants.RANDOM_KEY, randomKey);
			
            response.setCharacterEncoding("utf-8");
            encoder.print(new XmlPrintWriter(response.getWriter()));
        } 
        catch(BusinessException e) {
        	throw new BusinessException(e.getMessage(), false); // 无需打印登录异常
        }
    }

}
