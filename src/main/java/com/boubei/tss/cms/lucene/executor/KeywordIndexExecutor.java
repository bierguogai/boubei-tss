package com.boubei.tss.cms.lucene.executor;

/** 
 * 按文章关键字创建索引。
 */
public class KeywordIndexExecutor extends FieldIndexExecutor {
    
    protected String getFiledName() {
        return "keyword";
    }
}

