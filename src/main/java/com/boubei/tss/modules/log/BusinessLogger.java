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

import java.util.List;

import org.springframework.stereotype.Component;

import com.boubei.tss.PX;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.workqueue.OutputRecordsManager;
import com.boubei.tss.modules.param.ParamConfig;

/**
 * 跟业务操作相关的日志记录器
 * 
 */
@Component("BusinessLogger")
public class BusinessLogger extends OutputRecordsManager implements IBusinessLogger{
   
    private Pool apool;
 
    public BusinessLogger(){
        apool = JCache.getInstance().getTaskPool();
    }

    protected void excuteTask(List<Object> temp) {
    	Cacheable item = apool.checkOut(0);
    	
        LogOutputTask task;
        try {
        	task = (LogOutputTask) item.getValue();
        } catch(Exception e) {
        	task = new LogOutputTask();
        }
        task.fill(temp);
        item.update(task);
        
        log.debug("正在执行业务日志输出，本次共记录【" + temp.size() +  "】条日志。");
        tpool.excute(apool, item);
    }

    public void output(Log dto) {
        super.output(dto);
    }
    
    protected int getMaxSize() {
        try {
            String configValue = ParamConfig.getAttribute(PX.LOG_FLUSH_MAX_SIZE);
            return Integer.parseInt(configValue);
        } 
        catch(Exception e) {
            return super.getMaxSize(); 
        }
    }
}
