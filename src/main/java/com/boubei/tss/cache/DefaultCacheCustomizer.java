/* ==================================================================   
 * Created [2009-4-27 下午11:32:55] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cache;

import org.apache.log4j.Logger;


/**
 * 默认的缓存池自定义类。 
 * 
 * 对象中的create()，isValid()方法不做任何操作。
 * 
 */
public class DefaultCacheCustomizer implements CacheCustomizer {

	protected Logger log = Logger.getLogger(this.getClass());
	
	protected CacheStrategy strategy;
	
    public void setCacheStrategy(CacheStrategy strategy) {
        this.strategy = strategy;
    }

	public Cacheable create() {
		return null;
	}
	
	/**
	 * 默认的缓存项加载器什么都不做
	 */
	public Cacheable reloadCacheObject(Cacheable item) {
		return null;
	}

	public boolean isValid(Cacheable o) {
		return true;
	}

	public void destroy(Cacheable o) {
		if (o != null) {
			Object value = o.getValue();
			if (value != null) {
				value = null;
			}
			o = null;
		}
	}
}
