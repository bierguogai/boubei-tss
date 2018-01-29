/* ==================================================================   
 * Created [2007-1-3] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
*/
package com.boubei.tss.cache.extension.workqueue;

import com.boubei.tss.cache.DefaultCacheCustomizer;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.TimeWrapper;
import com.boubei.tss.util.BeanUtil;

/**
 * 任务池自定义类
 * 
 */
public abstract class TaskPoolCustomizer extends DefaultCacheCustomizer {
    
    public Cacheable create() {
        String taskClassName = getTaskClass();
		Task task = (Task)BeanUtil.newInstanceByName(taskClassName);
		
        String key = TimeWrapper.createSequenceKey("Task");
        return new TimeWrapper(key, task, strategy.cyclelife);
    }
    
    public Cacheable reloadCacheObject(Cacheable item) {
    	return create();
    }

    public boolean isValid(Cacheable o) {
        return o != null && o.getValue() instanceof Task;
    }

    public void destroy(Cacheable o) {
        o = null;
    }
    
    protected abstract String getTaskClass();
}
