package com.boubei.tss.cms.dao;

import java.util.List;

import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.framework.persistence.IDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;

/** 
 * Article的Dao层接口，定义所有Article相关的数据库操作接口
 */
public interface IArticleDao extends IDao<Article> {
 
    /**
     * <p>
     *  根据栏目id获取发布成xml文件(真实发布状态)的文章列表
     * </p>
     * @param channelId
     * @return
     */
    List<?> getPublishedArticleByChannel(Long channelId);

	/**
	 * <p>
	 * 根据文章ID获取文章附件列表
	 * </p>
	 * @param articleId
	 * @return
	 */
    List<Attachment> getArticleAttachments(Long articleId);

    /**
     * <p>
     * 根据文章ID获取此文章附件最大的序号
     * </p>
     * @param id
     * @return
     */
    Integer getAttachmentIndex(Long id);
    
    /**
     * 获取文章下的指定附件
     * 
     * @param articleId
     * @param seqNo
     * @return
     */
    Attachment getAttachment(Long articleId, Integer seqNo);
 
    /**
     * <p>
     *  获得栏目的文章列表
     * </p>
     * @param channelId
     * @param pageNum
     * @return
     */
    PageInfo getPageList(Long channelId, Integer pageNum);
 
    /**
     * <p>
     *  条件搜索栏目及其子栏目的文章列表
     * </p>
     * @param condition
     * @return
     */
    PageInfo getArticlePageList(ArticleQueryCondition condition);
 
    /**
     * <p>
     *  获取栏目的分页文章列表，Portlet远程调用时使用
     * </p>
     * @param condition
     * @return
     */
    PageInfo getChannelPageArticleList(ArticleQueryCondition condition);
    
    /**
     * 根据栏目ids，获取这些栏目下的所有文章列表
     * @param condition
     * @param isArchives
     * @return
     */
    PageInfo getArticlesByChannelIds(ArticleQueryCondition condition);
    
    /**
     * <p> 保存文章 </p>
     * @param article
     * @return
     */
    Article saveArticle(Article article);
    
    /**
     * <p>
     * 删除文章
     * </p>
     * @param article
     */
    void deleteArticle(Article article);
}