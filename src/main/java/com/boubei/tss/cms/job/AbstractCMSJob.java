package com.boubei.tss.cms.job;

import com.boubei.tss.cms.service.IArticleService;
import com.boubei.tss.cms.service.IChannelService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.util.EasyUtils;

public abstract class AbstractCMSJob extends AbstractJob {
	
	// jobConfig 为 siteIds
	protected void excuteJob(String jobConfig, Long jobID) {
		
		JobStrategy strategy = getJobStrategy();
        log.info("开始执行定时策略【" + strategy.name + "】");
        
    	String[] jobConfigs = EasyUtils.split(jobConfig, ","); 
    	for(int i = 0; i < jobConfigs.length; i++) {
    		excuteCMSJob(jobConfigs[i]);
		}
	}
 
	protected IChannelService getChannelService() {
		return (IChannelService) Global.getBean("ChannelService");
	}
	
	protected IArticleService getArticleService() {
		return (IArticleService) Global.getBean("ArticleService");
	}
	
	protected abstract void excuteCMSJob(String jobConfig);
	
	protected abstract JobStrategy getJobStrategy();
}
