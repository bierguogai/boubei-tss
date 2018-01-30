/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.service;

import java.io.File;

import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.modules.log.Logable;

public interface IArticleService {

    /**
     * 新增/修改文章
     * 
     * @param article
     * @param channelId
     * @param attachList
     * @param tempArticleId
     *            新增的时候上传的附件对象以new Date()为主键，此处的tempArticleId就是这个值
     */
    @Logable(operateObject="文章", operateInfo="新增文章：${args[0]} 到(ID: ${args[1]}) 栏目下")
    void createArticle(Article article, Long channelId, String attachList, Long tempArticleId);

    @Logable(operateObject="文章", operateInfo="修改文章：${args[0]}，附件列表：${args[2]}")
    void updateArticle(Article article, Long channelId, String attachList);

    /**
     * 删除文章
     * 
     * @param articleId
     */
    @Logable(operateObject="文章", operateInfo="删除了文章: ${returnVal} ")
    Article deleteArticle(Long articleId);

    /**
     * 获取文章
     * 
     * @param articleId
     * @return
     */
    Article getArticleById(Long articleId);
    
    /**
     * 文章附件上传后做进一步处理，包括转移目录、重命名及制作缩略图等。
     * 
     * @param file
     * @param articleId
     * @param channelId
     * @param type
     * @param kidName
     * @return
     */
    Attachment processFile(File file, Long articleId, Long channelId, int type, String kidName);

    /**
     * 移动文章
     * 
     * @param articleId
     * @param oldChannelId
     * @param channelId
     */
    @Logable(operateObject="文章", operateInfo="将(ID: ${args[0]}) 文章移动到(ID: ${args[1]}) 栏目下")
    void moveArticle(Long articleId, Long channelId);

    /**
     * 获取栏目下属所有文章列表
     * 
     * @param channelId
     * @param page
     * @return
     */
    PageInfo getChannelArticles(Long channelId, Integer page);
    
    /**
     * <p>
     * 搜索栏目下的文章
     * </p>
     * 
     * @param condition
     * @return
     */
    Object[] searchArticleList(ArticleQueryCondition condition);
 
    /**
     * 置顶文章
     * 
     * @param articleId
     */
    @Logable(operateObject="文章", operateInfo="置顶/取消置顶文章(${returnVal})")
    Article doTopArticle(Long articleId); 
}