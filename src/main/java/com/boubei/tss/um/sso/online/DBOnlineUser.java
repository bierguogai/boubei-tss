package com.boubei.tss.um.sso.online;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.online.OnlineUser;

/** 
 * <p> 在线用户信息 </p> 
 */
@Entity
@Table(name="online_user")
@SequenceGenerator(name = "online_user_sequence", sequenceName = "online_user_sequence", initialValue = 1000, allocationSize = 10)
public class DBOnlineUser extends OnlineUser implements IEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "online_user_sequence")
	private Long id;
    
    private Date   loginTime;
    private String clientIp;
    private String userName;
    
    public DBOnlineUser() { }
 
    public DBOnlineUser(Long userId, String sessionId, String appCode, String token, String userName) {
        super(userId, appCode, sessionId, token);
        
        this.setLoginTime( new Date() );
        this.setClientIp( Environment.getClientIp() );
        this.setUserName( userName );
    }
    
	public Serializable getPK() {
		return this.id;
	}
 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLoginTime() {
        return loginTime;
    }
 
    public String getClientIp() {
        return clientIp;
    }

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
