/* ==================================================================   
 * Created [2007-2-15] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
*/
package com.boubei.tss.cache.extension.workqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.extension.threadpool.IThreadPool;

/** 
 * <pre>
 * 输出记录类操作的管理类超类。 
 * 可输出日志、点击率、访问量等信息。
 * 
 * 本超类中设置了一个存放记录的缓存，当缓存中的记录数到达最大值时，
 * 则会把缓存中的记录交给一个RecordsOutputTask任务，并调用工作线程池来执行该任务。
 * 
 * 本超类中同时还启用了一个守护的记录输出进程，当距离上次输出记录的时间达到最长等待时间时，
 * 则不管缓存是否已满只要有记录就都强制输出。
 * </pre>
 */
public abstract class OutputRecordsManager {
	
    protected Logger log = Logger.getLogger(this.getClass());
    
    private boolean isReady = true; // 池是否已经初始化正确
    
    private final static int maxSize = 12;         //满12条就输出记录
    private final static int maxTime = 3*60*1000;  //定时输出记录的间隔时间：3分钟
    
    private List<Object> bufferedRecords = Collections.synchronizedList(new ArrayList<Object>());
    
    protected IThreadPool tpool;
    
    public OutputRecordsManager() {
        try{
            tpool = JCache.getInstance().getThreadPool();
        } catch(Exception e) {
            isReady = false;
            log.error("初始化【" + this.getClass() + "】时获取线程池时出错。", e);
        }
        
        // 启动定时输出日志的守护线程
        new FlushThread().start(); 
    }

    /**
     * 输出记录信息到缓存中，到临界值时输出缓存中记录到远程服务器端数据库中
     * @param dto
     */
    public synchronized void output(Object record){
        if(isReady) {
            bufferedRecords.add(record);
        }
        
        if(size() >= getMaxSize() ){  
            flush();   
        }    
    }   
    
    private int size() { 
    	return bufferedRecords == null ? 0 : bufferedRecords.size(); 
    }

    /**
     * 执行输出记录（为异步，调用线程池中的一个线程来完成）
     */
    public void flush() {      
        final List<Object> temp = bufferedRecords;
        
        //从缓存队列中清空
        bufferedRecords = Collections.synchronizedList(new ArrayList<Object>());
        
        excuteTask(temp);
    }
    
    /**
     * 执行输出记录（为异步，调用线程池中的一个线程来完成）
     * @param records
     */
    protected abstract void excuteTask(List<Object> records);
    
    
    protected int getMaxSize() { 
    	return maxSize; 
    }
    protected int getMaxTime() { 
    	return maxTime;
    }
    
    /**
     * 记录定时输出，时间一满不管是否满了都输出去
     */
    private class FlushThread extends Thread {
        public void run() {
            while (true) {
                try {
                	/* 定期休眠，醒来后输出记录 */
                    sleep(getMaxTime()); 
                    if(size() > 0 ) {
                        flush();   
                    }
                } catch (InterruptedException e) {
                    log.error("运行OutputRecordsManager.FlushThread线程时出错！", e);
                }
            }
        }
    }
}