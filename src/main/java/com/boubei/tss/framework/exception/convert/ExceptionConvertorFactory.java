package com.boubei.tss.framework.exception.convert;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.EasyUtils;

/**
 * 异常转换器工厂类
 * 
 */
public class ExceptionConvertorFactory {
	
	 /** 异常信息转换器实现类类名属性名 */
    static final String EXCEPTION_CONVERTOR = "class.name.ExceptionConvertor";
    
    static IExceptionConvertor convertor = null;

    public static IExceptionConvertor getConvertor() {
        if (convertor == null) {
            String className = Config.getAttribute(EXCEPTION_CONVERTOR);
            if( EasyUtils.isNullOrEmpty(className) ) {
            	className = DefaultExceptionConvertor.class.getName();
            }
            
            String[] classNames = className.split(",");
            convertor = new ExceptionConvertorChain(classNames);
        }
        return convertor;
    }
}