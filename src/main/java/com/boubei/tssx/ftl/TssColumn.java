/* ==================================================================   
 * Created [2018-02-12] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tssx.ftl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于标记JAVA实体的字段对应数据录入表字段的属性信息
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TssColumn {
    
    /** 中文名 */
    String label();
    boolean isParam()  default false;
    String defaultVal() default "";
    String checkReg()  default "";
    String errorMsg() default "";
    
    String calign()  default "";
    String cwidth()  default "";
    String width()   default "";
    String height()  default "";
    
    String options()  default "";
    String jsonUrl()  default "";
}
