package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.exception.UserIdentificationException;

/** 
 * <p>
 * 身份认证器接口
 * </p>
 * 
 */
public interface IUserIdentifier {

    /**
     * 根据用户通行证识别身份，如果合法返回用户身份信息，否则抛出UserIdentificationExcepiton异常
     * @param passport
     * @return
     * @throws UserIdentificationException
     */
    IdentityCard identify() throws UserIdentificationException;
    
}