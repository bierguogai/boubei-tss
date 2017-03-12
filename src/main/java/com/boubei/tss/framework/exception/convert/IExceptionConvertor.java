package com.boubei.tss.framework.exception.convert;

/**
 * 异常变换处理器：可以对特殊异常进行自定义信息处理等操作
 * 
 */
public interface IExceptionConvertor {

    Exception convert(Exception be);

}
