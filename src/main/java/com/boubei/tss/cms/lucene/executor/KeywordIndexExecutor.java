/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.lucene.executor;

/** 
 * 按文章关键字创建索引。
 */
public class KeywordIndexExecutor extends FieldIndexExecutor {
    
    protected String getFiledName() {
        return "keyword";
    }
}

