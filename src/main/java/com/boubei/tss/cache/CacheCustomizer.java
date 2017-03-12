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
 * <pre>
 * 缓存池自定义扩展接口。
 * 本接口提供了扩充缓存池机制的功能。
 * 通过实现本接口，用户可以各种定制各种特殊的缓存要求。
 * 具体操作包括：缓存项创建、销毁以及合法性验证
 * <pre>
 */
public interface CacheCustomizer {

	/**
	 * 创建对象,将对象放入池中。
	 * 本方法在cache check-out方法调用时，池中没有可用对象的时候被调用用来生成新的对象。
	 * 
	 * @return
	 */
	Cacheable create();
	
	/**
	 * 重新载入缓存项
	 * 
	 * @param item
	 * @return
	 */
	Cacheable reloadCacheObject(Cacheable item);

	/**
	 * <pre>
	 * 检测对象是否正常。
	 * 当对象被check-out的时候本方法被调用来判断检出对象是否可以使用。
	 * 实现类实现该方法用以确定check-out的对象不会存在问题而影响使用。
	 * </pre>
	 * @param o
	 * @return
	 */
	boolean isValid(Cacheable o);

	/**
	 * 销毁对象。
	 * 本方法会在池需要整理/清除，或者释放的时候被调用。
	 * 
	 * @param o
	 */
	void destroy(Cacheable o);
	
	void setCacheStrategy (CacheStrategy strategy);
}
