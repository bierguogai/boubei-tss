/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.exception.convert;

import java.util.ArrayList;
import java.util.List;

import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p>
 * 列表异常转换器：处理多异常转换器
 * </p>
 */
public class ExceptionConvertorChain implements IExceptionConvertor {

    /** 转换器列表 */
    private List<IExceptionConvertor> convertors;

    /** 转换器类名数组  */
    private String[] classNames;

    /**
     * 构造函数
     * @param classNames
     */
    public ExceptionConvertorChain(String[] classNames) {
        this.classNames = classNames;
    }
 
    public Exception convert(Exception exception) {
        if (convertors == null) {
            init();
        }
        for (IExceptionConvertor convertor : convertors) {
            Exception e = convertor.convert(exception);
            if (e != exception) {
                return e;
            }
        }
        return exception;
    }

    /**
     * 初始化异常转换器列表
     */
    private void init() {
        convertors = new ArrayList<IExceptionConvertor>();
    	for ( String className : classNames ) {
            if ( !EasyUtils.isNullOrEmpty(className) ) {
                Object convertor = BeanUtil.newInstanceByName(className);
                convertors.add( (IExceptionConvertor) convertor );
            }
        }
    }
}
