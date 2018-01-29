package com.boubei.tss.framework.web.rmi;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.appserver.AppServerStorerFactory;

/**
 * <p>
 * 客户端配置远程接口用到本对象，配置时将appCode（如：CMS）以及serviceUrl（如：/remote/PermissionService）设置进来。<br>
 * 如此则可以根据appCode值直接取到该应用的BaseURL（如：http://10.100.1.5/cms）值，
 * 然后BaseURL + serviceUrl ==> http://10.100.1.5/cms/remote/PermissionService 就是远程接口真实地址。<br>
 * 
 * 本对象主要目的是避免每次应用IP改变时候都要重新改远程接口的配置。<br>
 * 
 * eg:
 *      HttpInvokerProxyFactory factory = new HttpInvokerProxyFactory();
        factory.setServiceUrl("/remote/PermissionService");
        factory.setServiceInterface(IPermissionService.class);
        factory.setAppCode("CMS");
        
        return (IPermissionService)factory.getObject();
        
 * 即调用远程接口时，在前台发来的请求（request1）的里开启一个新的请求（request2），在JAVA端实现远程调用其他应用里的接口
 * </p>
 *
 */
public class HttpInvokerProxyFactory extends HttpInvokerProxyFactoryBean {

	private String targetAppCode;
	private String serviceUrl;
	
 	/**
	 * <p> 设置应用系统编码 </p>
	 */
	public void setAppCode(String appCode) {
		this.targetAppCode = appCode;
	}
	
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
 
	public void afterPropertiesSet() {
		AppServer targetAppServer = AppServerStorerFactory.newInstance().getAppServerStorer().getAppServer(targetAppCode);
		super.setServiceUrl(targetAppServer.getBaseURL() + serviceUrl);
		setHttpInvokerRequestExecutor(new AutoLoginHttpInvokerRequestExecutor(targetAppServer));
		
		super.afterPropertiesSet();
	}
	
	public Object getObject(){
	    afterPropertiesSet();
		return super.getObject();
	}
}
