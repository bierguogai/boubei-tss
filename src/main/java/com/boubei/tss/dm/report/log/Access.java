/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.report.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
    
/**
 * 用于分析方法的使用情况
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {

    /** 方法的中文名称 */
    String methodName() default "";
}
