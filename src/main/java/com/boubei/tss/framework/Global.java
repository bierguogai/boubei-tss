package com.boubei.tss.framework;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.boubei.tss.PX;
import com.boubei.tss.framework.sso.online.IOnlineUserManager;
import com.boubei.tss.modules.param.ParamListener;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;

/**
 * <p>
 * 加载spring配置文件，以调用配置文件中配置的对象。
 * </p>
 * 
 * @author Jon.King 2006-6-19
 */
public class Global {
	
	private static ApplicationContext _ctx;

	private static String defaultContextPath = "META-INF/spring.xml";
 
	public static synchronized ApplicationContext getContext() {
		if (_ctx == null) {
			String contextPath = Config.getAttribute(PX.SPRING_CONTEXT_PATH);
			if (contextPath != null) {
				defaultContextPath = contextPath;
			}
			_ctx = new ClassPathXmlApplicationContext(defaultContextPath.split(","));
		}
		return _ctx;
	}
	
	public static Object getBean(String beanId) {
		return getContext().getBean(beanId);
	}
 
	// 配置在um-remote.xml里
	public static IOnlineUserManager getRemoteOnlineUserManager() {
		return (IOnlineUserManager) getBean("RemoteOnlineUserManager");
	}
	
    public static ParamService getParamService() {
        return (ParamService) Global.getBean("ParamService");
    }

	public static synchronized void setContext(ApplicationContext context) {
		_ctx = context;
		
		// param缓存刷新监听器需要第一个执行，其它监听器里需要读取刷新后的Param信息
    	ParamManager.listeners.add(0, (ParamListener) ParamManager.getService());
    	getParamService().fireListener(null); // 系统启动时，自动触发一次所有的监听器，以完成缓存池、定时器等初始化。
	}

	public static synchronized void destroyContext() {
		_ctx = null;
	}
}
