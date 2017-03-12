package com.boubei.tss.cache.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QueryCached {
	
	/**
     * 依据指定参数（比如queryReport(reportId,...)的第一个参数reportId）启用拦截限流，>= 0 则启用。
     * 检查当前查询服务在等待队列中是否超过了阈值（X）25%，超过则不再接受新的查询请求，以防止单个服务耗尽队列
     */
	int limit() default 0;

}
