/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.sso.context;

import java.util.Collection;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.appserver.AppServerStorerFactory;
import com.boubei.tss.framework.sso.appserver.IAppServerStorer;

/**
 * 应用系统上下文信息对象
 */
public class ApplicationContext {
	
    protected IAppServerStorer storer;

    /**
     * 默认构造函数
     */
    public ApplicationContext() {
        storer = AppServerStorerFactory.newInstance().getAppServerStorer();
    }

    /**
     * 获取当前系统编号
     */
    public String getCurrentAppCode() {
        return Config.getAttribute(PX.APPLICATION_CODE);
    }

    /**
     * 根据应用系统编号获取应用系统配置信息
     * @param appCode
     * @return
     */
    public AppServer getAppServer(String appCode) {
        return storer.getAppServer(appCode);
    }

    /**
     * 获取当前系统访问配置信息
     * @return
     */
    public AppServer getCurrentAppServer() {
        return getAppServer(getCurrentAppCode());
    }
    
    public Collection<AppServer> getAppServers() {
        return storer.getAppServers();
    }
}
