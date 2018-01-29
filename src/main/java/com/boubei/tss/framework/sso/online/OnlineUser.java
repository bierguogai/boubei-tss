package com.boubei.tss.framework.sso.online;

import javax.persistence.MappedSuperclass;

/**
 * <p> 在线用户库里的用户 </p>
 * 
 * 同一用户对应到不同的应用、不同的SessionID，在在线用户库里都被视为不同的在线用户，
 * 但它们用可能共享一个相同的token（比如同一用户、不同的应用的情况下）。
 */
@MappedSuperclass
public class OnlineUser {
    
    /** 用户编号 */
    protected Long userId;

    /** 应用Code  */
    protected String appCode;

    /** Session编号  */
    protected String sessionId;

    /** 令牌：包含标准用户ID、平台SessionID和令牌的生成时间 */
    protected String token;
    
    public OnlineUser() { }
 
    public OnlineUser(Long userId, String appCode, String sessionId, String token) {
        this.setUserId(userId);
        this.setAppCode(appCode);
        this.setSessionId(sessionId);
        this.setToken(token);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }
    
    public String toString() {
        return getUserId() + ":" + getAppCode() + ":" + getSessionId() + ":" + getToken();
    }

    public String getAppCode() {
        return appCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getToken() {
        return token;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}