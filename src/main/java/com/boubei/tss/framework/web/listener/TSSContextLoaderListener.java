package com.boubei.tss.framework.web.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.boubei.tss.framework.Global;

/**
 * <p>
 * 主要实现非Spring Context中对象能直接访问Spring Context中对象。
 * 使用时需要在web.xml中加入一个配置项，如下：
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
 *  
 *  加载applicationContext.xml文件后，直接将context对象本身设置到Global中，如此调用Global可以起直接读取Spring IOC池里的对象。
 * </p>
 */
public class TSSContextLoaderListener extends ContextLoaderListener {
	
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		
		Global.setContext(super.getCurrentWebApplicationContext()); 
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
		
		Global.destroyContext();
	}

}
