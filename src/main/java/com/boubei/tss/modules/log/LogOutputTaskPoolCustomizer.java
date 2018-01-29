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

