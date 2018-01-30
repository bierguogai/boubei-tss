/* ==================================================================   
 * Created [2007-1-4] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018  
 * ================================================================== 
 */

package com.boubei.tss.cache;

/**
 * 缓存池监听器。
 */
public class PoolListener implements Listener {

	public void dealwithPoolEvent(PoolEvent poolEvent) {
		Pool pool = poolEvent.getPool();
		
		switch (poolEvent.getType()) {
		case PoolEvent.CHECKOUT:
		case PoolEvent.REMOVE:
			/* 移除或者检出缓存项时的事件处理 */
			break;
		case PoolEvent.CHECKIN:
		case PoolEvent.PUT_IN:
			/* 唤醒pool对象休息室中休眠中的线程:
			 * 包括cleaner线程（池为空的时候会休眠），checkout，remove等操作的等待线程。 */
			synchronized (pool) {
				pool.notifyAll();
			}
			checkPoolLimit(pool);
			break;
		case PoolEvent.MAX_POOL_LIMIT_EXCEEDED: 
			/* 如果缓存池容量已 > 极限，则启动【对象移除】 */
			pool.destroyObject( pool.remove() ); 
			break;
		case PoolEvent.MAX_POOL_LIMIT_REACHED: 
			/* 如果缓存池容量已 = 极限，则启动【过期对象清理】 */
			pool.purge(); 
			break;
		case PoolEvent.POOL_RELEASED:
			/* TODO 缓存池被释放后的事件处理 */
			break;
		case PoolEvent.POOL_DISABLED:
			/* 缓存池被停用后的事件处理 */
			pool.release(true);
			break;
		case PoolEvent.POOL_ENABLED:
			/* 缓存池被启用后的事件处理 */
			pool.init();
			break;
		case PoolEvent.STRATEGY_CHANGED_CYCLELIFE:
			cyclelifeChanged(pool);
			break;
		case PoolEvent.STRATEGY_CHANGED_SIZE_REDUCE:
			checkPoolLimit(pool);
			break;
		case PoolEvent.STRATEGY_CHANGED_RESET:
			resetPool(pool, false);
			break;
		}
	}
	
	private void resetPool(Pool pool, boolean force) {
		pool.release(force);
		pool.init();
	}

	private void checkPoolLimit(Pool pool) {
		int maxSize = pool.getCacheStrategy().poolSize;
		
		// 如果maxSize = 0则表示不限制池大小
		if (maxSize > 0) {
			if (pool.size() == maxSize) {
				pool.firePoolEvent(PoolEvent.MAX_POOL_LIMIT_REACHED);
			}
			else if (pool.size() > maxSize) { 
				/* putObject进来时，有可能超过maxSize,因为purge清理不掉【未过期】的对象，此时需要直接按存取规则移除对象 */
				pool.firePoolEvent(PoolEvent.MAX_POOL_LIMIT_EXCEEDED);
			}
		}
	}

	/**
	 * <pre>
	 * 缓存策略的一般内容改变，包括对象生命周期值等。
	 * 
	 * 清空池中已存在缓存项，后续新进来的缓存对象将使用新的生命周期值。
	 * </pre>
	 * @param pool
	 */
	private void cyclelifeChanged(Pool pool) {
		pool.flush();
		
		/* 如果pool扩展实现了Cleaner接口。
		 * 重新初始化清除器，根据缓存项的生命周期值改变清理间隔时间。*/
		if (pool instanceof Cleaner) {
			((Cleaner) pool).initCleaner();
		}
	}
 
	/*
	 * 所有的ObjectPoolListener实例返回相同的hashCode
	 */
	public int hashCode() {
		return 123456789;
	}
}
