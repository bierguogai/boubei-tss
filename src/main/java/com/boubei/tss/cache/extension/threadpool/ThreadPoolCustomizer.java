/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
*/
package com.boubei.tss.cache.extension.threadpool;

import org.apache.log4j.Logger;

import com.boubei.tss.cache.DefaultCacheCustomizer;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.TimeWrapper;

/** 
 * 线程池自定义类。
 * 创建、验证、销毁工作线程。
 */
public class ThreadPoolCustomizer extends DefaultCacheCustomizer {
    
    protected Logger log = Logger.getLogger(this.getClass());
  
    public Cacheable create() {
    	String currentThread = Thread.currentThread().getName();
    	log.debug(":" + currentThread + ": 开始新建一个工作线程。");
    	
        IThreadPool tpool = JCache.getInstance().getThreadPool();
        Thread thread = tpool.createWorkThread();
        thread.start(); // 启动线程
        
        TimeWrapper newThread = new TimeWrapper(thread.getName(), thread, strategy.cyclelife);
        log.debug(":" + currentThread + ": (" + newThread + ")创建成功！");
        
		return newThread;
    }

    public boolean isValid(Cacheable o) {
    	if(o == null) return false;
    	
        return ((Thread)o.getValue()).isAlive(); 
    }

    public void destroy(Cacheable o) {
    	if(o == null) return;
    	
        Thread poolWorker = (Thread) o.getValue();
        try {
            poolWorker.join(50); //等待线程死亡
        } catch (InterruptedException e) {
            log.error("停止initer线程时被中断", e);
        }
        poolWorker = null;
    }

	public Cacheable reloadCacheObject(Cacheable item) {
		return create();
	}
}

