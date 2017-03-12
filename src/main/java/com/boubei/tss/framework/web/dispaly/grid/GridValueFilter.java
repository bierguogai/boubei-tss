package com.boubei.tss.framework.web.dispaly.grid;

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

