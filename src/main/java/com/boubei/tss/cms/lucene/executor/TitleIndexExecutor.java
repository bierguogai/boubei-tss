package com.boubei.tss.cms.lucene.executor;

/** 
 * 按文章标题创建索引。
 */
public class TitleIndexExecutor extends FieldIndexExecutor {
    
    protected String getFiledName() {
        return "title";
    }
}

