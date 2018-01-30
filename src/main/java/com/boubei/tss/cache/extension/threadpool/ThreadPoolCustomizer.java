/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
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
    	log.debug(":" + currentThread + ": creating a new work thread.");
    	
        IThreadPool tpool = JCache.getInstance().getThreadPool();
        Thread thread = tpool.createWorkThread();
        thread.start(); // 启动线程
        
        TimeWrapper newThread = new TimeWrapper(thread.getName(), thread, strategy.cyclelife);
        log.debug(":" + currentThread + ": (" + newThread + ") was created.");
        
		return newThread;
    }

    public boolean isValid(Cacheable o) {
    	if(o == null) return false;
    	
        return ((Thread)o.getValue()).isAlive(); 
    }

    public void destroy(Cacheable o) {
        try {
        	Thread poolWorker = (Thread) o.getValue();
            poolWorker.join(50); // 等待线程死亡
        } 
        catch (Exception e) {
            log.error("destroy thread failed" + e.getMessage());
        }
    }

	public Cacheable reloadCacheObject(Cacheable item) {
		return create();
	}
}

