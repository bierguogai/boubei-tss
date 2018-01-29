package com.boubei.tss.framework.sso;

/**
 * <p>
 * 用户身份转换器，根据不同应用间用户映射关系实现用户身份转换
 * </p>
 */
public interface IdentityGetter {

    /**
     * <p>
     * 根据标准用户Id获取当前系统对应用户相关信息
     * </p>
     * @param standardUserId    TSS平台用户ID
     * @return
     */
    IOperator getOperator(Long standardUserId);
    
    /**
     * 自定义身份认证方法，如果用本地的账号密码验证不能通过，则会继续采用本处方法再次进行认证。
     * 
     * @param operator
     * @param password
     * @return
     */
    boolean indentify(IPWDOperator operator, String password);

}
