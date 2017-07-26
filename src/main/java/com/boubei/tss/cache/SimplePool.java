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

		ContainerFactory factory = ContainerFactory.getInstance();
		String containerName = strategy.code;
		poolContainer = factory.create(poolCollectionClass, containerName);

		log.info("pool[" + strategy.name + "] init succeed.");
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
