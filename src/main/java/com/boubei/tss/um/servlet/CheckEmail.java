package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.web.display.ErrorMessageEncoder;
import com.boubei.tss.framework.web.display.SuccessMessageEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MailUtil;
import com.boubei.tss.util.MathUtil;

/**
 * <p>
 * 密码忘记时通过注册邮箱重置密码：
 * 发送一个随机数到邮箱，用户获取随机数后修改密码。
 * </p>
 */
@WebServlet(urlPatterns="/checkEmail.in")
public class CheckEmail extends HttpServlet {

	private static final long serialVersionUID = 3958707576748004012L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accout = request.getParameter("loginName");
		String email  = request.getParameter("email");
		
		IUserService service = (IUserService) Global.getBean("UserService");
		User user = service.getUserByLoginName(accout);
		
		response.setContentType("text/html;charset=UTF-8");
		if ( user == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder( EX.parse(EX.U_41, accout) );
			encoder.print(new XmlPrintWriter(response.getWriter()));
		} 
		else {
            String _email = user.getEmail();
            if ( EasyUtils.isNullOrEmpty(_email) || !_email.equals(email)  ) {
                ErrorMessageEncoder encoder = new ErrorMessageEncoder( EX.U_43 );
                encoder.print(new XmlPrintWriter(response.getWriter()));
            } 
            else {
            	// 产生一个登录随机数，发到用户的邮箱里
                int randomKey = MathUtil.randomInt(1000000);
    			request.getSession(true).setAttribute(SSOConstants.RANDOM_KEY, randomKey);
            	String info = "凭此随机数修改您的密码，打死不要告诉其它人。随机数 = " + randomKey;
            	MailUtil.sendHTML("reset password", info, new String[] { email }, MailUtil.DEFAULT_MS);
            	
            	SuccessMessageEncoder encoder = new SuccessMessageEncoder("请速去您的邮箱查取修改密码的验证码。");
                encoder.print(new XmlPrintWriter(response.getWriter()));
			} 
		}
	}
}

	