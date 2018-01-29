package com.boubei.tss.um.permission.filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.boubei.tss.um.UMConstants;

/**
 * 用于标记需要进行权限过滤的方法。
 * 一般对其返回值进行权限过滤，过滤掉没有执行权限操作选项的资源。
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionTag {
    
    public static enum OPERATION_TYPE { VIEW, ADD, EDIT, DELETE, START, STOP, BROWSE };
    
    /**
     * 权限操作选项
     */
    String operation() default "";
    
    /**
     * 资源类型
     */
    String resourceType() default "";
    
    /**
     * 应用
     */
    String application() default UMConstants.TSS_APPLICATION_ID;
    
    /**
     * 可自定义的权限过滤器
     */
    Class<? extends IPermissionFilter> filter() default PermissionFilter.class;
}
