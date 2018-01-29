package com.boubei.tss.um.permission.filter;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.BeanUtil;

/**
 * 资源操作时候，对资源进行权限过滤或检查的拦截器。
 */
public class PermissionCheckInterceptor implements MethodInterceptor {
	
	Logger log = Logger.getLogger(getClass());
 
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod(); /* 获取目标方法 */
        Object[] args = invocation.getArguments(); /* 获取目标方法的参数 */
        Object returnVal = invocation.proceed(); /* 调用目标方法的返回值 */

        PermissionTag tag = targetMethod.getAnnotation(PermissionTag.class); // 取得注释对象
        if (tag != null) {
        	IPermissionFilter filter = (IPermissionFilter)BeanUtil.newInstance(tag.filter());
        	
        	log.debug("对方法：" + targetMethod + " 进行权限检查（或过滤）开始。");
        	filter.doFilter(args, returnVal, tag, PermissionHelper.getInstance());
        	log.debug("对方法：" + targetMethod + " 权限检查（或过滤）结束。");
        }

        return returnVal;
    }
}