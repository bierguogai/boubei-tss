package com.boubei.tss.framework.sso;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.EasyUtils;

/**
 * <p>
 * 环境变量对象：利用线程变量，保存运行时用户信息等参数
 * </p>
 */
public class Environment {
	
	public static long threadID() {
		return Thread.currentThread().getId();
	}
	
	public static boolean isAnonymous() {
		Long userId = getUserId();
		return Anonymous.one.getId().equals( userId ) || userId == null;
	}
	
	public static boolean isAdmin() {
		return UMConstants.ADMIN_USER_ID.equals( Environment.getUserId() );
	}
	
	public static boolean isRobot() {
		return UMConstants.ROBOT_USER_ID.equals( Environment.getUserId() );
	}
	
	public static boolean isDomainUser() {
		return getOwnRoles().contains(UMConstants.DOMAIN_ROLE_ID);
	}
	
	public static boolean isDeveloper() {
		return getOwnRoles().contains(UMConstants.DEV_ROLE_ID);
	}
	
	public static boolean isFirstTimeLogon() {
		return getUserInfo("lastLogonTime") == null;
	}
	
    /**
     * 获取用户ID信息
     */
    public static Long getUserId() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getOperator().getId();
    }

    /**
     * 获取用户账号（LoginName）
     */
    public static String getUserCode() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getLoginName();
    }
    
    /**
     * 获取用户姓名
     */
    public static String getUserName() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getUserName();
    }
    
    public static Object getUserInfo(String field) {
        IdentityCard card = Context.getIdentityCard();
        if ( card != null && card.getOperator() != null ) {
        	return card.getOperator().getAttributesMap().get(field);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public static List<Long> getOwnRoles() {
    	List<Long> list = new ArrayList<Long>();
    	list.add(UMConstants.ANONYMOUS_ROLE_ID);
    	
    	return (List<Long>) EasyUtils.checkNull(getInSession(SSOConstants.USER_RIGHTS), list);
    }
    
    @SuppressWarnings("unchecked")
	public static List<String> getOwnRoleNames() {
    	List<String> list = new ArrayList<String>();
    	list.add(Anonymous._NAME);
    	
    	return (List<String>) EasyUtils.checkNull(getInSession(SSOConstants.USER_ROLES_), list);
    }
    
    public static String getDomain() {
    	return (String) getInSession(SSOConstants.USER_DOAMIN);
    }
    
    private static Object getInSession(String attrName) {
    	RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        
        Object result = null;
        try {
	    	HttpSession session = requestContext.getSession();
	    	result = session.getAttribute(attrName);
        } 
        catch(Exception e) { }
        
        return result;
    }

    /**
     * 获取当前SessionID
     */
    public static String getSessionId() {
        RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        return requestContext.getSessionId();
    }

    /**
     * 获取用户客户端IP
     */
    public static String getClientIp() {
        RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        return requestContext.getClientIp();
    }
    
    /**
     * 获取应用系统上下文根路径(即发布路径)，一般为"/tss"
     */
    public static String getContextPath(){
        return Context.getApplicationContext().getCurrentAppServer().getPath();
    }
}
