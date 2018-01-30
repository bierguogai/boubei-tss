/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.sso.identifier;

import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.IUserIdentifier;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.IdentityGetter;
import com.boubei.tss.framework.sso.IdentityGetterFactory;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.online.IOnlineUserManager;
import com.boubei.tss.framework.sso.online.OnlineUserManagerFactory;

/**
 * <p> 在线用户库方式用户签定器。 </p>
 * 
 * <p>
 * 通过根据令牌来判断该令牌是否已经在在线用户库中注册过了。
 * 如果已经注册则验证通过返回用户IdentityCard（类似身份证）。
 * </p>
 */
public class OnlineUserIdentifier implements IUserIdentifier {

    public IdentityCard identify() throws UserIdentificationException {
        String token = Context.getRequestContext().getUserToken();
        
        // 如果在线，则说明令牌合法，获取用户当前系统相关信息并且重新注册到在线用户库（可能跟原先在线的应用不同,appCode不同）
        IOnlineUserManager onlineUserManager = OnlineUserManagerFactory.getManager();
		if (onlineUserManager.isOnline(token)) {
			String appCode   = Context.getApplicationContext().getCurrentAppCode();
            String sessionId = Context.getRequestContext().getSessionId();
            
			Long userId = TokenUtil.getUserIdFromToken(token);
			IdentityGetter ig = IdentityGetterFactory.create();
			IOperator operator = ig.getOperator(userId);
			
            onlineUserManager.register(token, appCode, sessionId, userId, operator.getUserName());
            return new IdentityCard(token, operator);
        }
		
        // 用户不在线说明token令牌是伪造的或是已经过期的，返回null，表示验证不通过。
        return null;
    }
}
