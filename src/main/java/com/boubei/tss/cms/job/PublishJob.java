package com.boubei.tss.cms.job;

import java.util.List;

import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.service.IChannelService;
import com.boubei.tss.util.EasyUtils;

/**
 * 0 *\/30 * * * ?   每半小时发布一次
 * com.boubei.tss.cms.job.PublishJob | 0 07 * * * ? | 12
 */
public class PublishJob extends AbstractCMSJob {

	private static final int PAGE_SIZE = PublishManger.PAGE_SIZE;

	protected void excuteCMSJob(String jobConfig) {
		
        Long siteId = EasyUtils.obj2Long(jobConfig.trim());
  
        IChannelService channelService = getChannelService();
        
        List<Long> channelIds = channelService.getAllEnabledChannelIds(siteId);
        for ( Long channelId : channelIds ) {
            int totalRows = channelService.getPublishableArticlesCount(channelId);       
            int totalPageNum = totalRows / PAGE_SIZE ;
            if( totalRows % PAGE_SIZE > 0 ) {
                totalPageNum = totalPageNum + 1;
            }
            
            for (int page = 1; page <= totalPageNum; page++) { // 逐页发布文章
                List<Article> list = channelService.getPagePublishableArticles(channelId, page, PAGE_SIZE);
                channelService.publishArticle(list);
            }
        }
	}

	protected JobStrategy getJobStrategy() {
		return JobStrategy.getPublishStrategy();
	}
}