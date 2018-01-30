/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.sso.online;

import java.util.Collection;
import java.util.Set;

/**
 * 在线用户管理类接口
 * 
 * 在线用户库和用户令牌都是用户单点登陆的重要组成：前者记录用户的登陆信息，后者则是表明用户身份的唯一凭证。
 * 在线用户库的工作原理是：
 * （1）当用户第一次成功登陆平台应用时，将会用登陆生成的Token针对当前应用在在线用户库中注册一条信息。
 * （2）当访问其它应用时，先检测携带的token是否已经在在线用户库中注册过，如果有，则说明用户已经在其它应用登陆过，同时也用Token为该应用也注册一条信息。
 * （3）当用户退出某个应用时，删除Token在这个应用的注册信息，同时检测是否还有其它应用在线，如果没有了，则从在线用户库移除本Token。
 * （4）当session过期时，操作和第三步一样。
 */
public interface IOnlineUserManager {

    /**
     * <p>
     * 根据token判断用户是否已经登录其他系统，如果登录则返回True，否则返回false
     * </p>
     * @param token
     * @return
     */
    boolean isOnline(String token);

    /**
     * <p>
     * 注册用户登录当前系统
     * </p>
     * @param token 令牌
     * @param appCode 当前系统Code
     * @param sessionId 当前SessionID
     * @param userId 当前系统用户ID
     * @param userName 当前用户名称
     */
    void register(String token, String appCode, String sessionId, Long userId, String userName);

    /**
     * <p>
     * 销毁在线用户或访问应用：根据应用Code，SessionId销毁相应的记录。
     * 用于session超时时自动注销。
     * 注：一次只注销一个应用，即一次登录、多次登出，因为在每个应用都生成了一个不同的token（给了多把钥匙，一把把归还）。
     * SessionDestroyedListener，此监听器会在session超时时自动销毁在线用户信息。
     * </p>
     *
     * @param appCode
     * @param sessionId
     */
    String logout(String appCode, String sessionId);
    
    /**
     * 根据用户一次性注销在所有应用的全部登陆信息。
     * 用于用户手动退出。
     * 
     * @param userId
     * @return
     */
    void logout(Long userId);

    /**
     * <p>
     * 根据Token获取当前用户登录的所有系统的相关信息
     * </p>
     * @param token
     */
    Set<OnlineUser> getOnlineUsersByToken(String token);
    
    /**
     * 获取所有在线用户
     * @return
     */
    Collection<?> getOnlineUsers();
}
