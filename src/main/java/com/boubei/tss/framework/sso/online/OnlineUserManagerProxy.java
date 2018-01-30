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

import com.boubei.tss.framework.Global;

/**
 * <p> 远程在线用户库 </p>
 * <pre>
 * 用于TSS以外的基于TSS框架的其它应用的配置
 * 
 * OnlineUserManagerFactory先读取application.properties里的bean.onlineManager配置，
 * 如果是RemoteOnlineUserManager，则初始化一个RemoteOnlineUserManager实例，并通过该实例来调用
 * applicationContext.xml里配置的远程在线用户库。
 * 
 * TODO 远程在线用户管理实现中解藕远程Service定义名称(Global.getBean("RemoteOnlineUserManager"))
 * </pre>
 */
public class OnlineUserManagerProxy implements IOnlineUserManager {
 
    public String logout(String appCode, String sessionId) {
        return Global.getRemoteOnlineUserManager().logout(appCode, sessionId);
    }
    
	public void logout(Long userId) {
		Global.getRemoteOnlineUserManager().logout(userId);
	}
 
    public boolean isOnline(String token) {
        return Global.getRemoteOnlineUserManager().isOnline(token);
    }
 
    public void register(String token, String appCode, String sessionId, Long userId, String userName) {
        Global.getRemoteOnlineUserManager().register(token, appCode, sessionId, userId, userName);
    }
 
    public Set<OnlineUser> getOnlineUsersByToken(String token) {
        return Global.getRemoteOnlineUserManager().getOnlineUsersByToken(token);
    }

    public Collection<?> getOnlineUsers() {
        return Global.getRemoteOnlineUserManager().getOnlineUsers();
    }
}
