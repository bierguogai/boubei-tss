/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boubei.tss.EX;
import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.service.IChannelService;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.progress.Progress;
import com.boubei.tss.modules.progress.ProgressManager;
import com.boubei.tss.modules.progress.Progressable;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 文章发布（带进度条实现）
 */
@Component("PublishManger")
public class PublishManger implements Progressable {

    public static final int PAGE_SIZE = 100; // 按页发布，每页记录数

    @Autowired private IChannelService channelService;
    
	/**
	 * 判断是否对该栏目有发布权限
	 */
	void checkPublishPermission(Long channelId) {
	    String appId = UMConstants.TSS_APPLICATION_ID;
	    String resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL;
	    String operation = CMSConstants.OPERATION_PUBLISH;
	    List<Long> permitedList = PermissionHelper.getInstance().getResourceIdsByOperation(appId, resourceType, operation);
        if ( !permitedList.contains(channelId) ) {
        	Channel channel = channelService.getChannelById(channelId);
            throw new BusinessException( EX.parse(EX.CMS_4, channelId, channel.getName()));
        }
	}

	/**
     * 发布文章（注：完全发布的话已经发布的也重新发布，增量发布则只发布流程为“待发布”的文章）
     * 
     * @param channelId 栏目ID
     * @param category  1:增量发布 2:完全发布 
     */
	public String publishArticle(Long channelId, String category) {
		// 判断是否对该栏目有发布权限
		checkPublishPermission(channelId);

		int totalRows = channelService.getPublishableArticlesDeeplyCount(channelId, category);       
        
		Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("channelId", channelId);
        paramsMap.put("category", category);
        paramsMap.put("totalRows", totalRows);
            
        ProgressManager manager = new ProgressManager(this, totalRows, paramsMap);
        return manager.execute();
	}
    
    public void execute(Map<String, Object> params, final Progress progress) {
        Long    channelId = (Long) params.get("channelId");
        String  category = (String) params.get("category");
        Integer totalRows = (Integer) params.get("totalRows");
        
        // 分页发布文章
        int totalPageNum = totalRows / PAGE_SIZE ;
        if( totalRows % PAGE_SIZE > 0 ) {
            totalPageNum = totalPageNum + 1;
        }
        for (int page = 1; page <= totalPageNum; page++) {
            List<Article> pageArticleList = channelService.getPagePublishableArticlesDeeply(channelId, page, PAGE_SIZE, category);
            
            channelService.publishArticle(pageArticleList);
            progress.add(pageArticleList.size());
        }
        // 如果循环结束了进度还没有完成，则取消进度（不取消会导致页面一直在请求进度信息）
        if( !progress.isCompleted() ) {
            progress.add(8888888); // 通过设置一个大数（远大于总数）来使进度完成
        }
    }
}
