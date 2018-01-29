package com.boubei.tss.um.sso.othersystem;

import java.security.Principal;
import java.util.List;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.IPWDOperator;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.sso.identifier.BaseUserIdentifier;
import com.boubei.tss.framework.web.XHttpServletRequest;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.Escape;

/**
 * <pre>
 *  LtpaToken身份认证器 
 *  通过验证是否存在LtpaToken以及username来判断用户是否已经登录其它系统（比如：OA），如果是，则让其在平台登录。 
 *  
 *  用户在其它系统中登录以后，通过以下地址转入到TSS中： 
 *  http://localhost:9000/tss/auth/login.do?identifier=com.boubei.tss.um.sso.othersystem.LtpaTokenIdentifier
 *  	&username=Admin&sso=true&LtpaToken=0eddd3641df0f1994f28b6e990149671&fromApp=TSSBI 
 *  
 *  配置认证成功后调整页面的方法有：
 *  1、TSS的application.properties文件中设置SSO成功后调整的页面地址，例如
 *     sso.index.page = /tss/index.portal 
 *     默认login.do只返回成功信息，但如果有sso=true和sso.index.page的配置同时存在，则会自动sendRedirect至sso.index.page页面。
 *  2、在跳转地址里直接拼接 sso=true&target=/tss/xxx.html,如：
 *  http://localhost:9000/tss/auth/login.do?identifier=com.boubei.tss.um.sso.othersystem.LtpaTokenIdentifier
 *  &username=JXX&sso=true&LtpaToken=C4E5EC65C90C73A0B7B6D413CEADA9E5&fromApp=OT&sso=true&target=/tss/modules/dm/recorder.html?id=12
 * </pre>
 */
public class LtpaTokenIdentifier extends BaseUserIdentifier {
    
    public final static String LTPA_TOKEN_NAME = "LtpaToken";
    public final static String LOGIN_NAME = "username";
    public final static String FROM_APP = "fromApp";
    
    ILoginService loginSerivce = (ILoginService) Global.getBean("LoginService");
 
    protected IOperator validate() throws UserIdentificationException {
        RequestContext requestContext = Context.getRequestContext();
        XHttpServletRequest request = requestContext.getRequest();

        String ltpaToken = requestContext.getValueFromRequest(LTPA_TOKEN_NAME);
        String fromApp = requestContext.getValueFromRequest(FROM_APP);
        if( EasyUtils.isNullOrEmpty(ltpaToken) || EasyUtils.isNullOrEmpty(fromApp) ) {
            throw new UserIdentificationException("LtpaToken和fromApp不能为空，跳转登录失败。");
        }
        
        String uName;
        Principal userPrincipal = request.getUserPrincipal();
        if(userPrincipal != null) {
            uName = userPrincipal.getName();
        } 
        else {
            uName = requestContext.getValueFromRequest(LOGIN_NAME);
        }
        
        if(uName == null) {
        	throw new UserIdentificationException("取不到用户，请确认已经配置好SSO！");
        }
        
        // 检查用户是否存在
        uName = Escape.unescape(uName); // maybe is GBK
        IPWDOperator operator;
        try {
        	operator = loginSerivce.getOperatorDTOByLoginName(uName);
		}  catch (BusinessException e) {
			throw new UserIdentificationException(e.getMessage());
		}
        
        // 检查令牌是否合法
        List<String> tokenList = loginSerivce.searchTokes(uName, fromApp, "SSO"); 
        if( !tokenList.contains(ltpaToken) ) {
        	throw new UserIdentificationException("非法LtpaToken!");
        }
        
        return operator;
    }
}
