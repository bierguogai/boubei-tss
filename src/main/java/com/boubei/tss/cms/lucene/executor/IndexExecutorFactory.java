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

import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;
 
public class IndexExecutorFactory {

    public static IndexExecutor create(String className) {
        if ( EasyUtils.isNullOrEmpty( className )) {
            return new DefaultIndexExecutor();
        }
        
        return (IndexExecutor) BeanUtil.newInstanceByName(className);
    }
}

