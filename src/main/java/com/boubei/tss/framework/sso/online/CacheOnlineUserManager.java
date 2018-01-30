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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * <p> Cache在线用户库 </p>
 * <pre>
 *  包含了 3个Map：
 *  	1、tokenMap     ： key:token            , value:Set[OnlineUser] 
 *  	2、sessionIdMap ： key:appCode_sessionId, value:OnlineUser（含有token）
 *  	3、usersMap     ： key:userId           , value:userName  用来统计在线用户信息
 *
 *  一个令牌可以在多个应用中使用，所以会有多个在线用户信息。
 *  而一个session（对应一个打开状态的页面）在一个应用里只有一个在线用户信息
 * （但有可能多个用户在这页面登陆过， 如此sessionIdMap只保存最后一个登陆的用户），
 *  页面刷新时sessionId不变，但如果超时后刷新重新登陆后（或者换个用户登陆），token将会发生变化（将重新生成）。
 * </pre>
 */
@Component("CacheOnlineUserManager")
public class CacheOnlineUserManager implements IOnlineUserManager {

    protected Map<String, Set<OnlineUser>> tokenMap = new HashMap<String, Set<OnlineUser>>();
    protected Map<String, OnlineUser> sessionIdMap = new HashMap<String, OnlineUser>(); 
    protected Map<Long, String> usersMap = new HashMap<Long, String>();
 
    public String logout(String appCode, String sessionId) {
        // 删除当前sessionId在当前应用中登陆后产生的在线用户信息
        OnlineUser userInfo = (OnlineUser) sessionIdMap.remove(getSessionIdMapKey(appCode, sessionId));
        
        if (userInfo == null) return null;
        
        String token = userInfo.getToken();
        Set<OnlineUser> userInfos = tokenMap.get(token);
        userInfos.remove(userInfo); //删除令牌在当前应用下的 在线用户信息，不包含其它的应用。
        
        // 判断Token是否已经退出了所有应用，是的话移除此Token
        if (userInfos.size() == 0) {
            tokenMap.remove(token);
            usersMap.remove(userInfo.getUserId());
        }
        userInfo = null;
        userInfos = null;
        
        return token;
    }
    
	public void logout(Long userId) {
		List<OnlineUser> list = new ArrayList<OnlineUser>();
		for(OnlineUser ou : sessionIdMap.values()) {
			if(userId.equals( ou.getUserId() )) {
				list.add(ou);
			}
		}
		
		for(OnlineUser ou : list) {
			sessionIdMap.remove(getSessionIdMapKey(ou.getAppCode(), ou.getSessionId()));
			tokenMap.remove(ou.getToken());
		}
		
		usersMap.remove(userId);
	}
 
    public  Set<OnlineUser> getOnlineUsersByToken(String token) {
        return tokenMap.get(token);
    }
 
    public boolean isOnline(String token) {
        return tokenMap.containsKey(token);
    }

    public void register(String token, String appCode, String sessionId, Long userId, String userName) {
        OnlineUser user = new OnlineUser(userId, appCode, sessionId, token);
        
        /* onlineUsers 用来退出时判断当前Token是不是已经退出了所有应用，是的话将token从tokenMap中移除。 */
        Set<OnlineUser> onlineUsers = tokenMap.get(token); 
        
        /* 判断同一个令牌是否已经注册过，如果没有则初始化一个 onlineUsers */
        if (onlineUsers == null) { 
            tokenMap.put(token, onlineUsers = new HashSet<OnlineUser>());
        }
        /* 
         * 注意此处用的是Set：如果是同一个令牌在同一个应用里再次注册，则user相同，add到Set将会覆盖原来的；
         * 否则将令牌在当前应用注册。
         */
        onlineUsers.add(user); 
        
        sessionIdMap.put(getSessionIdMapKey(appCode, sessionId), user);
        usersMap.put(userId, userName);
    }

    /**
     * <p>
     * 根据当前应用Code和当前SessionId生成SessionIdMap的key
     * </p>
     * @param appCode
     * @param sessionId
     * @return
     */
    protected String getSessionIdMapKey(String appCode, String sessionId) {
        return appCode + "_" + sessionId;
    }

    public Collection<String> getOnlineUsers() {
        return usersMap.values();
    }
}
