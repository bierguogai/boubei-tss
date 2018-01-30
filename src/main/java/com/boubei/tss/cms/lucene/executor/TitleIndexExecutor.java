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
 * 按文章标题创建索引。
 */
public class TitleIndexExecutor extends FieldIndexExecutor {
    
    protected String getFiledName() {
        return "title";
    }
}

