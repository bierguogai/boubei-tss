package com.boubei.tss.cms.dao;

import java.util.List;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.helper.PermissionFilter4ChannelBranch;
import com.boubei.tss.framework.persistence.ITreeSupportDao;
import com.boubei.tss.um.permission.filter.PermissionFilter4Branch;
import com.boubei.tss.um.permission.filter.PermissionTag;

/** 
 * Channel的Dao层接口，定义所有Channel相关的数据库操作接口
 */
public interface IChannelDao extends ITreeSupportDao<Channel> {
	
	Channel updateChannel(Channel channel);
    
    /**
     * 根据栏目Id获取到站点对象
     * @param channelId
     * @return
     */
    Channel getSiteByChannel(Long channelId);
    
    /**
     * <p>
     * 得到所有站点栏目列表
     * </p>
     * @return
     */
    @PermissionTag(
	        operation = CMSConstants.OPERATION_VIEW, 
	        resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL
	)
    List<?> getAllSiteChannelList();
    
    /**
     * <p>
     *  获得某站点下的栏目列表,不含站点本身（没有权限过滤）
     * </p>
     * @param siteId
     * @return
     */
    List<?> getChannelsBySiteIdNoPermission(Long siteId);

	/**
	 * <p>
	 * 获得栏目向下的所有栏目列表
	 * </p>
	 * @param channelId
	 * @param operationId
	 * @return
	 */
    @PermissionTag(
            filter = PermissionFilter4ChannelBranch.class
    )
	List<Channel> getChildrenById(Long channelId, String operationId);
    
    /**
     * <p>
     * 获得栏目向上的所有栏目列表
     * </p>
     * @param channelId
     * @param operationId
     * @return
     */
    @PermissionTag(
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Branch.class
    )
    List<Channel> getParentsById(Long channelId, String operationId);
	
    /**
     * 检查对channelId有没有浏览权限
     * @param channelId
     * @return
     */
    boolean checkBrowsePermission(Long channelId);
    
    /**
     * <p>
     * 通过栏目ID取栏目下所有可发布的文章ID
     * </p>
     * @param channelId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Article> getPagePublishableArticles(Long channelId, int pageNum, int pageSize);
    
    /**
     * 获取需要发布的总文章数
     * @param channelId
     * @param category   1:增量发布 2: 完全发布
     * @return
     */
    int getPublishableArticlesDeeplyCount(Long channelId, String category );

    /**
     * 根据页码获取当前页需要发布的文章列表
     * @param category
     * 				发布类型： 增量发布 or 完全发布
     * @param pageNum
     * @param pageSize
     * @param decode
     * @return
     */
    List<Article> getPagePublishableArticlesDeeply(Long channelId, String category, int pageNum, int pageSize);
}
