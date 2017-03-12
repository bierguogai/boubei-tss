/* ==================================================================   
 * Created [2006-12-31] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache;

import java.util.Collection;
import java.util.Set;

/**
 * <pre>
 * 缓存池容器接口. 
 * 该接口中定义了池的基本操作. 
 * 用户可以通过实现该接口来自定义池容器的实现.
 * </pre>
 */
public interface Container {

	// 对象池容器访问方式

	/** 先进先出 FIFO */
	static final int ACCESS_FIFO = 1;

	/** 后进先出 LIFO */
	static final int ACCESS_LIFO = 2;

	/** 随机 */
	static final int ACCESS_RANDOM = 3;

	/** 最近最少使用（最长时间未被使用） LRU（least frequently used） */
	static final int ACCESS_LRU = 4;

	/** 最近最不常使用（一定时期内被访问次数最少） LFU（Least Recently Used） */
	static final int ACCESS_LFU = 5;

	/**
	 * 从池容器里获取一个对象
	 * 
	 * @param key
	 * @return
	 */
	Cacheable get(Object key);

	/**
	 * 根据缓存池中缓存策略设定的对象访问方式（共有以上所列5种）来获取缓存池中的一个对象。<br/>
	 * 本方法主要在缓存算法类中执行check-out()和remove()方法时候会被调用到。
	 * 
	 * @param accessMethod
	 * @return
	 */
	Cacheable getByAccessMethod(int accessMethod);

	/**
	 * 往池容器中存入一个对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Cacheable put(Object key, Cacheable value);

	/**
	 * 从池容器中清除一个对象
	 * 
	 * @param key
	 * @return
	 */
	Cacheable remove(Object key);

	/**
	 * 获取池容器中的所有key列表
	 * 
	 * @return
	 */
	Set<Object> keySet();

	Collection<Cacheable> valueSet();

	/**
	 * 获取池中元素的个数
	 * 
	 * @return
	 */
	int size();

	/**
	 * 清空池容器
	 */
	void clear();
}
