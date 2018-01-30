/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.log;

import com.boubei.tss.cache.extension.workqueue.TaskPoolCustomizer;

/**
 * 日志输出任务池的自定义类。
 *
 */
public class LogOutputTaskPoolCustomizer extends TaskPoolCustomizer {
 
    protected String getTaskClass() {
        return LogOutputTask.class.getName();
    }
    
}

