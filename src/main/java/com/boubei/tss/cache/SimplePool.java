/* ==================================================================   
 * Created [2007-1-3] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */
package com.boubei.tss.cache;

import com.boubei.tss.util.BeanUtil;

/**
 * <pre>
 * 简单的缓存对象池，适合简单的缓存需求。
 * 
 * 例如：模板、参数值等。
 * </pre>
 */
public class SimplePool extends AbstractPool {

	private Container poolContainer; // 对象池容器

	public SimplePool() {
	}

	public final void init() {
		String poolCollectionClass = strategy.poolContainerClass;
		Class<?> collectionType = BeanUtil.createClassByName(poolCollectionClass);
		if (!Container.class.isAssignableFrom(collectionType)) {
			throw new RuntimeException("指定的池容器类类型非法: "
					+ collectionType.getName() + " (必须实现Container接口)");
		}

		ContainerFactory factory = ContainerFactory.getInstance();
		String containerName = strategy.code;
		poolContainer = factory.create(collectionType.getName(), containerName);

		log.info("缓存池【" + strategy.name + "】初始化成功！");
	}

	public final void release(final boolean forced) {
		poolContainer.clear();
		released = true;
	}

	public final Container getFree() {
		return poolContainer;
	}

	public final Container getUsing() {
		return poolContainer;
	}

	public final int size() {
		return poolContainer.size();
	}
}
