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
import com.boubei.tss.framework.sso.Anonymous;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.context.Context;

/**
 * <p>
 * 匿名用户身份认证器
 * </p>
 */
public class AnonymousUserIdentifier extends BaseUserIdentifier {

    protected IOperator validate() throws UserIdentificationException {
        if (Context.getRequestContext().canAnonymous()) {
            return Anonymous.one; 
        } 
        else {
            throw new UserIdentificationException("系统要求身份认证，请重新登录！");
        }
    }

}
