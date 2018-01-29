package com.boubei.tss.framework.sso;

/** 
 * <p> 登录操作自定义器接口类：用户登录成功后自定义操作。 </p> 
 * <p>
 * 例如：用户登陆请求验证通过后需要做以下两步操作：
 * 1.获取登陆用户的权限（拥有的角色）
 * 2.保存到用户权限（拥有的角色）对应表
 * </p>
 */
public interface ILoginCustomizer {
    
    /**
     * <p>
     * 登录自定义操作执行函数
     * </p>
     */
    void execute();
}
