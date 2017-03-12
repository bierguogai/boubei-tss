package com.boubei.tss.framework.web.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.online.IOnlineUserManager;
import com.boubei.tss.framework.sso.online.OnlineUserManagerFactory;
import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.util.EasyUtils;

/**
 * <p> Session超时监听程序 </p>
 *
 * 相关Session超时时，注销在线用户库中对应记录信息
 *
 */
@WebListener
public class SessionDestroyedListener implements HttpSessionListener {

    private Logger log = Logger.getLogger(this.getClass());
 
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        
        // 设置 session 的过期时间
        if(session.isNew()){
            String configValue = ParamConfig.getAttribute(PX.SESSION_CYCLELIFE_CONFIG);
            if ( !EasyUtils.isNullOrEmpty(configValue) ) {
                session.setMaxInactiveInterval(Integer.parseInt(configValue)); // 以秒为单位
            }
        }
        String sessionId = session.getId();
        String appCode = Context.getApplicationContext().getCurrentAppCode();
        log.debug("应用【" + appCode + "】里 sessionId为：" + sessionId
                + " 的session创建完成，有效期为：" + session.getMaxInactiveInterval() + " 秒 ");
    }
 
    public void sessionDestroyed(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        String appCode = Context.getApplicationContext().getCurrentAppCode();
        
        log.debug("应用【" + appCode + "】里 sessionId为：" + sessionId + " 的session已经过期，" +
                "有效期为：" + event.getSession().getMaxInactiveInterval() + " 秒 ");
        
        // 注销在线用户库中对应记录信息，去除登陆用户身份证card信息
    	IOnlineUserManager ouManager = OnlineUserManagerFactory.getManager();
    	if(ouManager != null) {
    		try {
    			String token = ouManager.logout(appCode, sessionId);
    			if(token != null) {
                	Context.destroyIdentityCard(token);
                }
    		} catch(Exception e) {
    			log.error("注销过期session时出错了：" + e.getMessage());
    		}
    	}
    }
}
