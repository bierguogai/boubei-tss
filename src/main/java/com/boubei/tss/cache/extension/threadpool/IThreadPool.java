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

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.workqueue.Task;

/** 
 * 为线程池独立抽象出一个接口，以方便如使用，如类型转换。 
 */
public interface IThreadPool {
    
    /**
     * 执行一次性的非任务池中check-out的任务。
     * 这类任务一般都是临时创建出来的，执行完后不回收。
     * 
     * @param task
     */
    void excute(Task task);
    
    /**
     * 执行由任务池中check-out的任务缓存项，执行完后由任务池（taskpool）回收任务。
     * 
     * @param pool
     * @param task
     */
    void excute(Pool taskpool, Cacheable o);
    
    /**
     * 创建线程池中的工作线程
     * @return
     */
    Thread createWorkThread();
}

