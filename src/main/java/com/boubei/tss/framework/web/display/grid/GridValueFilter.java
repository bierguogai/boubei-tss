/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.grid;

/** 
 * Grid展示值过滤器.
 * 对即将放入grid map的值先进行预处理
 * 
 */
public interface GridValueFilter {
    
    /**
     * 对即将放入grid map的值先进行预处理
     * @param key
     * @param value
     */
    Object pretreat(Object key, Object value);
    
}

