package com.boubei.tss.framework.sso.online;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * <p> 远程在线用户管理服务 </p>
 * <pre>
 * 将本地配置的在线用户管理服务转换为远程服务。
 * 
 * 注：TSS框架里本class将做为在线用户库service发布在基于TSS开发的应用里。
 * 它将读取TSS的application.properties里：
 *   bean.onlineManager = CacheOnlineUserManager
 * 配置项来初始化并操作在线用户库（上面指定的是"基于缓存的在线用户库"）.
 *  
 * 本来可以直接将OnlineUserService配置为CacheOnlineUserManager，但通过RemoteOnlineUserManager来配置
 * 则使得在线用户库的配置更加灵活，当需要将CacheOnlineUserManager换成DBOnlineUserManager时，
 * 只需要改动application.properties里的bean.onlineManager配置。
 * 
 * </pre>
 */
@Service("OnlineUserService")
public class OnlineUserService implements IOnlineUserManager {
	
	private IOnlineUserManager getOnlineUserManager() {
		return OnlineUserManagerFactory.getManager();
	}

    public String logout(String appCode, String sessionId) {
        return getOnlineUserManager().logout(appCode, sessionId);
    }

    public boolean isOnline(String token) {
        return getOnlineUserManager().isOnline(token);
    }

    public void register(String token, String appCode, String sessionId, Long userId, String userName) {
        getOnlineUserManager().register(token, appCode, sessionId, userId, userName);
    }

    public Set<OnlineUser> getOnlineUsersByToken(String token) {
        return getOnlineUserManager().getOnlineUsersByToken(token);
    }

    public Collection<?> getOnlineUsers() {
        return getOnlineUserManager().getOnlineUsers();
    }

	public void logout(Long userId) {
		getOnlineUserManager().logout(userId);
	}

}
