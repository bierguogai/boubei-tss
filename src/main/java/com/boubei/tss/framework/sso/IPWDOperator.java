package com.boubei.tss.framework.sso;

/**
 * <p>
 * 密码用户对象（区别于匿名用户）
 * </p>
 */
public interface IPWDOperator extends IOperator {
    
    /**
     * 获取用户密码
     * @return
     */
    public String getPassword();

}
