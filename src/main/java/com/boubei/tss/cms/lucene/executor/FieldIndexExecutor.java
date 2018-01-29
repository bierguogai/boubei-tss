package com.boubei.tss.cms.lucene.executor;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

import com.boubei.tss.cms.lucene.ArticleContent;

/** 
 * 根据文章特定字段的索引执行器：根据文章的某一个字段来创建索引。
 * 
 */
public abstract class FieldIndexExecutor  extends DefaultIndexExecutor {
    
    protected abstract String getFiledName();
    
    /**
     * 创建索引文本
     * @param pubFile 索引文件存放路径
     * @param content 附件内容
     * @param map 文章属性Map
     * @return
     */
    protected Document createIndexDoc( ArticleContent bean ) throws IOException {
        Document luceneDoc = new Document();
        
        for( Entry<String, String> entry : bean.getArticleAttributes().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 发布日期、创建日期 字段特殊处理，用于结果排序
            if(FIELD_ISSUEDATE.equals(key) || FIELD_CREATETIME.equals(key)){ 
                luceneDoc.add(new Field(key, value, Field.Store.YES, Field.Index.UN_TOKENIZED));
            } 
            else if( getFiledName().equals(key) ) {
                luceneDoc.add(new Field(getFiledName(), value, Field.Store.YES, Field.Index.TOKENIZED));   
            } 
            else if("content".equals(key)){
                continue; // 文章内容太大，不存入到索引文件
            } 
            else {
                luceneDoc.add(new Field(key, value, Field.Store.YES, Field.Index.NO));
            }
        }
        return luceneDoc;        
    }
 
    public Query createIndexQuery(String searchStr) throws ParseException {
        return super.createIndexQuery(getFiledName(), searchStr);
    }
}

