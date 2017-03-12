/* ==================================================================   
 * Created [2006-12-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cache;

/**
 * 缓存池监听器接口。
 * 
 */
public interface Listener {

	/**
	 * 缓存池事件被触发后执行相应的操作
	 * 
	 * @param poolEvent
	 */
	void dealwithPoolEvent(PoolEvent poolEvent);
}
