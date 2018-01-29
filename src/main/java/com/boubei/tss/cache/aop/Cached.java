package com.boubei.tss.cache.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.boubei.tss.cache.extension.CacheLife;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

    /**
     * cache生命周期
     */
	CacheLife cyclelife() default CacheLife.SHORT;
}
