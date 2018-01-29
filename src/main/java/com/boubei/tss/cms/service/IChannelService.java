package com.boubei.tss.cms.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.lucene.ArticleContent;
import com.boubei.tss.modules.log.Logable;
import com.boubei.tss.um.permission.filter.PermissionFilter4Create;
import com.boubei.tss.um.permission.filter.PermissionFilter4Move;
import com.boubei.tss.um.permission.filter.PermissionFilter4Sort;
import com.boubei.tss.um.permission.filter.PermissionFilter4Update;
import com.boubei.tss.um.permission.filter.PermissionTag;

/**
 * Channel的Service层接口，定义Channel相关的所有业务处理接口
 */
public interface IChannelService {
    
    /**
     * <p>
     * 得到所有站点栏目列表
     * </p>
     * @return
     */
    List<?> getAllSiteChannelList();
	
	/**
	 * 新增或更新普通栏目
	 * @param channel
	 * @return
	 */
    @Logable(operateObject="站点栏目", operateInfo="新增了 ${args[0]} 节点")
    @PermissionTag(
            operation = CMSConstants.OPERATION_ADD_CHANNEL, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Create.class
    )
	Channel createChannel(Channel channel);
    
    @Logable(operateObject="站点栏目", operateInfo="修改了 ${args[0]} 节点")
    @PermissionTag(
            operation = CMSConstants.OPERATION_EDIT, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Update.class
    )
	Channel updateChannel(Channel channel);
    
    /**
     * 新增站点
     * @param channel
     * @return
     */
    @Logable(operateObject="站点栏目", operateInfo="新增了 ${args[0]} 节点")
    @PermissionTag(
            operation = CMSConstants.OPERATION_ADD_CHANNEL, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Create.class
    )
    Channel createSite(Channel site);
    
    /**
     * 更新站点
     * @param channel
     * @return
     */
    @Logable(operateObject="站点栏目", operateInfo="修改了 ${args[0]} 节点")
    @PermissionTag(
            operation = CMSConstants.OPERATION_EDIT, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Update.class
    )
    Channel updateSite(Channel site);

	/**
	 * <p>
	 * 逻辑删除某栏目下的所有未删除Channel
	 * </p>
	 * @param channelId
	 */
    @Logable(operateObject="站点栏目", operateInfo="删除了 ID为 ${args[0]} 的节点。")
	void deleteChannel(Long channelId);

	/**
	 * <p>
	 * 移动Channel到站点或栏目下
	 * </p>
	 */
    @Logable(operateObject="站点栏目", operateInfo="移动(ID: ${args[0]})节点到(ID: ${args[1]})节点下")
    @PermissionTag(
            operation = CMSConstants.OPERATION_ADD_CHANNEL + "," + CMSConstants.OPERATION_DELETE, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Move.class
    )
	void moveChannel(Long channelId, Long targetId);

	/**
	 * 排序同一级的栏目
	 * @param channelId
	 * @param toChannelId
	 * @param direction
	 */
    @Logable(operateObject="站点栏目", operateInfo="(ID: ${args[0]})节点移动到了(ID: ${args[1]})节点<#if args[2]=1>的下方<#else>的上方</#if>")
    @PermissionTag(
            operation = CMSConstants.OPERATION_ORDER, 
            resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL,
            filter = PermissionFilter4Sort.class
    )
	void sortChannel(Long channelId, Long toChannelId, Integer direction);
 
	/**
	 * <p>
	 * 通过ID得到单个栏目信息
	 * </p>
	 * @param id
	 * @return
	 */
	Channel getChannelById(Long id);
 
    /**
     * <p>
     * 停用站点
     * </p>
     * @param id
     */
    @Logable(operateObject="站点栏目", operateInfo="停用了 （ID ：${args[0]}） 站点")
    void disable(Long id);
    
    /**
     * <p>
     * 启用站点及所有子栏目
     * <p>
     * @param siteId
     */
    @Logable(operateObject="站点栏目", operateInfo="启用了 （ID ：${args[0]}） 站点及其所有子栏目")
    void enableSite(Long siteId);
    
    /**
     * <p>
     * 启用栏目
     * </p>
     * @param id
     */
    @Logable(operateObject="站点栏目",  operateInfo="启用了 （ID ：${args[0]}） 栏目")
    void enableChannel(Long id);
 
	/**
	 * <p>
	 * 通过栏目id取栏目下所有可发布的文章总数
	 * </p>
	 * @param channelId
	 * @return
	 */
	Integer getPublishableArticlesCount(Long channelId);

	/**
	 * <p>
	 * 通过栏目id取栏目下所有可发布文章id
	 * </p>
	 * @param channelId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<Article> getPagePublishableArticles(Long channelId, int page, int pageSize);
	
	/**
	 * <p>
	 * 发布文章.
	 * 供PublishManager调用，没法直接在Manager里实现，因其没有事务，需要借助service的事务。
	 * </p>
	 * @param articleIdList
	 * @param siteId 
	 */
	@Logable(operateObject="站点栏目", operateInfo="发布文章")
	void publishArticle(List<Article> articleList);

    /**
     * 获取需要发布的总文章数
     * @param channelId
     * @param category   1:增量发布 2: 完全发布
     * @return
     */
    int getPublishableArticlesDeeplyCount(Long channelId, String category);

    /**
     * 根据页码获取当前页需要发布的文章列表
     * @param channelId
     * @param page
     * @param page_site
     * @param category
     * @return
     */
    List<Article> getPagePublishableArticlesDeeply(Long channelId, int page, int pageSize, String category);
    
    /**
     * <p>
     * 获取过期文章列表。
     * 比较过期时间是早于当前时间，以及文章是否为”已发布“状态。其他状态没必要设置为过期。
     * </p>
     * @param now
     * @param channelId
     * @return
     */
    List<Article> getExpireArticlePuburlList(Date now, Long channelId);
    
    List<Long> getAllEnabledChannelIds(Long siteId);
    
    Set<ArticleContent> getIndexableArticles(List<Long> channelIds, boolean isIncrement);
}
