package com.boubei.tss.cms.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;

import com.boubei.tss.cms.dao.IArticleDao;
import com.boubei.tss.cms.dao.IChannelDao;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.cms.job.JobStrategy;
import com.boubei.tss.cms.lucene.executor.IndexExecutor;
import com.boubei.tss.cms.lucene.executor.IndexExecutorFactory;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.progress.Progress;
import com.boubei.tss.util.FileHelper;

public class IndexHelper {
    
    static Logger log = Logger.getLogger(IndexHelper.class);
 
    /**
     * 获取索引的所有文章地址列表
     * 
     * @param channelIds
     * @param isIncrement
     * @param channelDao
     * @param articleDao
     * @return
     */
    public static Set<ArticleContent> getIndexableArticles(List<Long> channelIds, boolean isIncrement, 
            IChannelDao channelDao, IArticleDao articleDao) {
     
        Set<ArticleContent> articleContentSet = new HashSet<ArticleContent>();
        
        for ( Long channelId : channelIds ) {
            Channel channel = channelDao.getEntity(channelId);
            Channel site = channelDao.getSiteByChannel(channel.getId()); 
            
            // 过滤栏目下未生成xml文件的文章(并且过滤掉已经过期的文章)
            List<?> publishedArticles = articleDao.getPublishedArticleByChannel(channel.getId());
            for ( Object temp : publishedArticles ) {
                Object[] objs = (Object[]) temp; // 数组形式{articleId, pubUrl, issueDate}
                Long articleId = (Long)objs[0];
                
                // 只为当天发布的文章创建索引
                if( isIncrement ){
                    java.util.Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    Date yesterday = calendar.getTime();
                    
                    // 如果是今天之前发布的文章，则不再为其重建索引。
                    Date issueDate = (Date) objs[2];
                    if(issueDate.before(yesterday)){
                        continue;
                    }
                }
                
                List<Attachment> attachList = articleDao.getArticleAttachments(articleId);
                StringBuffer buffer = new StringBuffer();
                for( Attachment attachment : attachList ){
                    File attachmentPath = new File(ArticleHelper.getAttachUploadPath(site, attachment)[0]);
                    String attachContent = AttachmentIndex.getInstance().disposeAttachment(attachmentPath);
                    buffer.append(attachContent); // 放入附件内容
                }
                
                articleContentSet.add(new ArticleContent((String)objs[1], buffer.toString()));
            }
        }
        
        return articleContentSet;
    }

    public static void createIndex(JobStrategy strategy, Set<ArticleContent> articleContentSet, Progress progress) {
        String indexExecutorClass = strategy.executorClass;
        IndexExecutor executor = IndexExecutorFactory.create(indexExecutorClass);
        
        //创建索引文件存放路径
        String indexPath = strategy.getIndexPath();
        File indexDir = new File(indexPath); 
        if( !indexDir.exists() ) {
            FileHelper.createDir(indexPath);
        }
        
        File tempIndexDir = FileHelper.createDir(indexPath + "/temp"); // 先把新建的索引文件放在临时文件里，创建成功再覆盖原先的
        Analyzer analyzer = AnalyzerFactory.createAnalyzer();
        IndexWriter indexWriter = null;
        int count = 0;
        try {
            // 如果 不是增量创建索引 或者 tempIndexDir目录为空， 则重新创建索引目录
            boolean isRecreateIndex = !strategy.isIncrement || FileHelper.listFiles(tempIndexDir).isEmpty();
            
            try {
				indexWriter = new IndexWriter(tempIndexDir, analyzer, isRecreateIndex);
            } catch(FileNotFoundException e){
            	indexWriter = new IndexWriter(tempIndexDir, analyzer, !isRecreateIndex);
            }
            
            indexWriter.setMaxBufferedDocs(10); // 设置强制索引document对象后flush
            
            for ( Iterator<ArticleContent> it = articleContentSet.iterator(); it.hasNext(); ) {
            	ArticleContent articleContent = it.next();
                try{
                    executor.createIndex(articleContent, indexWriter);
                } catch(Exception e){
                    log.error("创建发布路径为:" + articleContent.getPubUrl() + "的文章索引时出错", e);
                    // TODO　将创建索引失败的文章记录下来
                    continue;
                }
                
                count ++;
                // 进度条的信息更新，每一百个更新一次
                if ((count > 0 && count % 100 == 0) || progress.isCompleted()) {
                    progress.add(count);
                    count = 0;
                    indexWriter.optimize();
                }
                it.remove(); // 把已经创建完索引的文章从articleContentSet中去掉，好让垃圾回收站及节约内存时回收
            }
        } catch (Exception e) {
            throw new BusinessException("创建索引文件出错！tempPath=" + tempIndexDir, e);
        } finally {
            progress.add(count); // 确保最后一次进度信息更新
            try {
            	indexWriter.close();
            } catch (IOException e) {
                // log.error("关闭索引文件错误！", e);
            }
        }

        // 先删除老的索引文件
        List<String> list = FileHelper.listFiles(indexDir);
        for ( String fileName : list ) {
            File file = new File(indexDir.getPath() + "/" + fileName);
            if ( file.isDirectory() ) {
                continue;
            } 
            
            file.delete();
        }
        
        // 覆盖更新后的索引文件到索引目录。注： 索引文件不从Temp目录下删除，下次更新索引时接着往该索引文件后添加。
        try {
        	FileHelper.copyFolder(tempIndexDir, indexDir);
        } catch(Exception e) {
        	log.error(e.getMessage(), e);
        }
    }
}

