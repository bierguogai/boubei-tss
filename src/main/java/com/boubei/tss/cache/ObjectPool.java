/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存对象池
 */
public class ObjectPool extends AbstractPool implements Cleaner {

	/**
	 * 空闲的对象池
	 */
	private Container free;

	/**
	 * 使用中的对象池
	 */
	private Container using;

	private static int cleanerCount = 0;
	private Cleaner cleaner; // 清理器
	private InitThread initer;

	public ObjectPool() { }

	/**
	 * <pre>
	 * 初始化指定数量的对象放入池中。
	 * 
	 * 本方法将产生一个新的线程来初始化这些对象。
	 * 
	 * 本方法跟release()方法是相对立的。
	 * </pre>
	 * @param num
	 */
	public final void init() {
		released = false;

		String containerClass = strategy.poolContainerClass;
		
		ContainerFactory factory = ContainerFactory.getInstance();
		String freePoolName = strategy.code + "_free";
		free = free != null ? free : factory.create(containerClass, freePoolName);
		
		String usingPoolName = strategy.code + "_using";
		using = using != null ? using : factory.create(containerClass, usingPoolName);
		
		// 为缓存池添加一个监听器
		listeners.clear();
		addPoolListener( new PoolListener() ); 

		startInitThread( strategy.initNum );
		initCleaner();

		log.info("pool[" + strategy.name + "] init succeed.");
	}

	/**
	 * 启动初始化线程
	 * 
	 * @param num
	 */
	void startInitThread(int num) {
		if (num <= 0) return;
		
		int maxSize = strategy.poolSize;
		num = Math.min(num, maxSize);
		
		shutDownIniter();
		
		initer = new InitThread(num);
		initer.start();
	}

	/**
	 * 创建检查间隔最少为5秒的清除线程。
	 */
	public void initCleaner() {
		shutDownCleaner();
		
		long cyclelife = strategy.cyclelife;
		if (cyclelife > 0) {
			long iVal = Math.max(1000 * 5, cyclelife / 5);
			cleaner = new Cleaner(this, iVal);
			cleaner.start();
		}
	}

	/**
	 * 销毁 cleaner, initer 线程
	 */
	private void shutDownCleaner() {
		if (cleaner != null) {
			cleaner.halt();
			try {
				cleaner.join(); // 等待线程死亡
			} catch (InterruptedException e) {
				log.error("shutdown pool cleaner failed", e);
			}
			cleaner = null;
		}
	}

	private void shutDownIniter() {
		if (initer != null) {
			initer.halt();
			try {
				initer.join(); // 等待线程死亡
			} catch (InterruptedException e) {
				log.error("shutdown pool initer failed", e);
			}
			initer = null;
		}
	}

	public final void release(boolean forced) {
		if (released) {
			return;
		}
		
		released = true;
		shutDownCleaner();
		shutDownIniter();

		synchronized (this) {
			int rel = 0, failed = 0;
			// 销毁所有的缓存项（包括尚在使用中的）
			if (forced) {
				List<Object> usingKeys = new ArrayList<Object>(using.keySet());
				for (Object key : usingKeys) {
					try {
						Cacheable usingItem = using.remove(key);
						destroyObject(usingItem);
						rel ++;
					} catch (Exception e) {
						failed ++;
						log.error("release " +this.getName()+ "'using container failed , when destroy item:" + key, e);
					}
				}
				
			} else {
				if (using.size() > 0) {
					log.info(" waiting using item to checkIn back...");
				}
				
				while (using.size() > 0) {
					try {
						wait(); // 等待，当缓存项被check in时，监听器会调用pool的notifyAll()通知这里
					} catch (InterruptedException e) {
					}
				}
			}
			
			// 销毁当前所有空闲状态的缓存项
			List<Object> freeKeys = new ArrayList<Object>(free.keySet());
			for (Object key : freeKeys) {
				try {
					destroyByKey(key);
					rel ++;
				} catch (Exception e) {
					failed ++;
					log.error("release " +this.getName()+ "'free container failed , when destroy item:" + key, e);
				}
			}

			String s = "release succeed [" + rel + "] items";
			if (failed > 0) {
				s += " ，release failed [" + failed + "] items.";
			}
			log.info(s);

			firePoolEvent(PoolEvent.POOL_RELEASED);
		}
	}

	/**
	 * 线程执行清除池中过期的对象。 <br/>
	 * 当对象尚在使用中，线程将会等待对象返回。 <br/>
	 * 对象返回时将触发线程再次执行清除。 <br/>
	 */
	private final class Cleaner extends Thread {
		
		ObjectPool pool;
		
		/** 定期清除缓存池时间间隔 */
		long interval;  
		
		/** 缓存Cleaner是否已经被挂起 */
		boolean stopped; 

		Cleaner(ObjectPool pool, long interval) {
			this.setName("CleanerThread_" + (cleanerCount ++)); // 设置线程名称
			this.pool = pool;
			this.interval = interval;
		}

		public void start() {
			stopped = false;
			super.start();
		}

		/**
		 * 安全的停止线程的运行，将线程挂起
		 */
		public void halt() {
			if (!isHalted()) {
				stopped = true;
				interrupt(); // 必要的时间再将线程唤醒
			}
		}

		/**
		 * 返回线程的状态，是否被挂起
		 */
		public boolean isHalted() {
			return stopped;
		}

		/**
		 * 处理过期的对象
		 */
		public void run() {
			while ( !stopped ) {
				try {
					sleep(interval);
						
					synchronized (pool) {
						/* 如果池本次没有被清理掉，则说明当前无过期缓存项可清除，再接着轮询也会效果不大，
						 * 不如先使本cleaner线程进入等待休眠状态，当pool池的checkIn或者putIn事件被触发时，再唤醒本cleaner */
						if ( pool.purge() ) {
							pool.wait();  // 等待PoolListener里CHECKIN、PUT_IN时，pool.notifyAll()来唤醒当前线程
						}
					}
				} catch (Exception e) {
					log.error(ObjectPool.this.getName() + " run cleaner error! sleep(" + interval + ")", e);
				}
			}
		}
	}

	/**
	 * 线程执行初始化池中对象。
	 */
	private final class InitThread extends Thread {
		private int num;
		private boolean stopped = false;

		private InitThread(int num) {
			this.num = Math.min(strategy.poolSize, Math.max(num, 0)); // Ensure 0 < num < poolSize.
		}

		public void halt() {
			stopped = true;
		}

		/**
		 * 生成指定数量的缓存对象。如果池中已有对象存在且数量小于指定数目，则线程将补足不足的对象数目。
		 */
		public void run() {
			while (size() < num) {
				if (stopped) {
					log.debug("InitThread " + this.getName() + " has stopped!");
					return;
				}
				try {
					Cacheable item = customizer.create();
					if (item == null) {
						throw new RuntimeException(ObjectPool.this.getName() + " create item failed when initing pool.");
					} else {
						putObject(item.getKey(), item.getValue());
					}
				} catch (Exception e) {
					log.error(" create item failed ", e);
					stopped = true; // 如果本次循环创建对象失败，则将线程标记设置为停用，下次循环判断的时候即可推出循环，以免进入死循环。
				}
			}
			log.debug("[" + ObjectPool.this.getName() + "] inited [" + size() + "] items.");
		}
	}

	public Container getFree() {
		return free;
	}

	public Container getUsing() {
		return using;
	}

	public final int size() {
		return size;
	}
}
