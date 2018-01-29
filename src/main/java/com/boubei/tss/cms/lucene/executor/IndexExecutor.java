package com.boubei.tss.cms.lucene.executor;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

import com.boubei.tss.cms.lucene.ArticleContent;

/** 
 * <p> IIndexExecutor.java </p> 
 * 索引执行器接口。
 * 可以通过扩展本接口来创建不同的索引，以及 根据索引查询
 */
public interface IndexExecutor {
    
    /**
     * 创建索引
     * @param bean
     * @param indexWriter
     * @throws CorruptIndexException
     * @throws IOException
     */
    void createIndex(ArticleContent bean, IndexWriter indexWriter) throws CorruptIndexException, IOException;
    
    /**
     * 创建搜索Query
     * @param searchStr
     * @return
     * @throws ParseException
     */
    Query createIndexQuery(String searchStr) throws ParseException;
}