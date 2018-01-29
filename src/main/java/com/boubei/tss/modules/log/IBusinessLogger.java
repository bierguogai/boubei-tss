package com.boubei.tss.modules.log;

/**
 * <p>
 * 业务数据日志处理对象接口
 * </p>
 */
public interface IBusinessLogger {
    /**
     * <p>
     * 输出日志
     * </p>
     * @param dto
     */
    void output(Log dto);
    
    /**
     * <p>
     * 强制输出日志信息
     * </p>
     */
    void flush();  
    
}
