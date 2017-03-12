/* ==================================================================   
 * Created [2006-12-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache;

import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * <pre>
 * 缓存策略: 
 *     不变区
 *       code
 *       name
 *     可变区
 *       普通，不触发池事件
 *         accessMethod
 *         disabled
 *         interruptTime
 *         remark
 *       触发池事件STRATEGY_CHANGED_CYCLELIFE
 *         cyclelife
 *       如果变小了则触发池事件STRATEGY_CHANGED_SIZE_REDUCE，处理同PUT_IN事件；大了不管
 *         poolSize
 *       严重，触发池事件STRATEGY_CHANGED_RESET，重新初始化池
 *         poolClass
 *         poolCollectionClass
 * </pre>
 */
public class CacheStrategy {
    
    public final static String TRUE  = "1";
    public final static String FALSE = "0";

	// 缓存模块提供的对象池类型
	static final String SIMPLE_POOL_CLASS = SimplePool.class.getName();
	static final String BASE_POOL_CLASS = ObjectPool.class.getName();

	final static String DEFAULT_CONTAINER  = MapContainer.class.getName();
	final static String DEFAULT_CUSTOMIZER = DefaultCacheCustomizer.class.getName();

	/** 缓存策略的名称 */
	public String name; 
	
	/** 缓存策略的code */
	public String code; 
	
	/** 缓存策略说明 */
	String remark; 
	
	/** 缓存池的容量（注： size 为零则表示不限定池大小） */
	public Integer poolSize = 0;
	
	/** 初始化缓存池时初始缓存项的个数 */
	Integer initNum = 0;
	
	/** 池中元素的有效期（生命周期） */
	public Long cyclelife = 0L;
	
	/** 中断时间(取不到缓存项时的等待时间) */
	Long interruptTime = 0L;
	
	/** 0：启用（用以检测缓存启用和停用时的执行效率） 1:停用 */
	public String disabled = FALSE;  
													 
	/** 
	 * 是否展示，用以隐藏运行时创建过来的缓存池，像没有名字只有code的那些
	 * 0：隐藏  1:显示 
	 */
	public String visible = TRUE; 
	
	/**
	 * 池的访问方式 
	 * 1: FIFO (first-in, first-out: a queue). 
	 * 2: LIFO (last-in, first-out: a stack). 
	 * 3: RANDOM (a random item is selected for check-out). 
	 * 4：ACCESS_LRU （最近使用） 
	 * 5：ACCESS_LFU （最不常使用）
	 */
	Integer accessMethod = Container.ACCESS_RANDOM;

	/**  缓存池实现类 */
	String poolClass = SIMPLE_POOL_CLASS;
	
	/** 池容器类 */
	String poolContainerClass = DEFAULT_CONTAINER;

	/** 缓冲池自定义类 */
	String customizerClass = DEFAULT_CUSTOMIZER;
	
	/** 连接池等配置内容 或 配置文件 */
	public String paramFile;

	/** 缓存策略的名称 */
	public Pool pool; // 缓存策略里定义的缓存池

	public Pool getPoolInstance() {
		if (pool != null && pool.getCacheStrategy().equals(this)) {
			return pool;
		}

		pool = (AbstractPool) BeanUtil.newInstanceByName(poolClass);
		pool.setCacheStrategy(this);
		
		CacheCustomizer customizer = (CacheCustomizer) BeanUtil.newInstanceByName(customizerClass);
		customizer.setCacheStrategy(this);
		
		pool.setCustomizer(customizer);
 
		// 初始化前需要先设置好customizer，需要用到customizer.create()来新建缓存项。
		pool.init();

		return pool;
	}

	public boolean equals(Object o) {
		if (o instanceof CacheStrategy) {
			CacheStrategy strategy = (CacheStrategy) o;
			return this.code.equals(strategy.code);
		}
		return false;
	}

	/**
	 * 缓存策略修改时重新设置各个策略项的值，同时触发相应的事件。
	 * 
	 * @param c
	 */
	public void fireEventIfChanged(CacheStrategy c) {

		if ( !this.cyclelife.equals(c.cyclelife) ) { // 缓存项生命周期发生了改变时：
			this.cyclelife = c.cyclelife;
			pool.firePoolEvent(PoolEvent.STRATEGY_CHANGED_CYCLELIFE);
		}

		if ( this.poolSize < c.poolSize ) { // 池变小
			this.poolSize = c.poolSize;
			pool.firePoolEvent(PoolEvent.STRATEGY_CHANGED_SIZE_REDUCE);
		}

		if ( !this.poolClass.equals(c.poolClass)
				|| !this.poolContainerClass.equals(c.poolContainerClass)) { // 容器变了
			setPoolClass(c.poolClass);
			setPoolContainerClass(c.poolContainerClass);
			pool.firePoolEvent(PoolEvent.STRATEGY_CHANGED_RESET);
		}
		
		if( FALSE.equals( this.disabled ) && TRUE.equals( c.disabled ) ) { // 停用池
			this.setDisabled( c.disabled );
			pool.firePoolEvent(PoolEvent.POOL_DISABLED);
		}
		if( TRUE.equals( this.disabled ) && FALSE.equals( c.disabled ) ) { // 启用池
			this.setDisabled( c.disabled );
			pool.firePoolEvent(PoolEvent.POOL_ENABLED);
		}
		if( c.paramFile != null && !c.paramFile.equals( this.paramFile ) ) { // 连接池的配置参数有变
			this.setParamFile( c.paramFile );
			pool.firePoolEvent(PoolEvent.STRATEGY_CHANGED_RESET);
		}

		setCustomizerClass(c.customizerClass);
		this.initNum = c.initNum;
		this.poolSize = c.poolSize;
		this.accessMethod = c.accessMethod;
		this.interruptTime = c.interruptTime;
		this.remark = c.remark;
	}

	/**
	 * 设置缓存策略的自定义类。同时改变池的customizer对象，如果池已经存在的话。
	 */
	public void setCustomizerClass(String customizerClass) {
		this.customizerClass = EasyUtils.isNullOrEmpty(customizerClass) ? 
				DEFAULT_CUSTOMIZER : customizerClass;
	}

	/**
	 * 设置缓存池类型
	 */
	public void setPoolClass(String poolClass) {
		this.poolClass = EasyUtils.isNullOrEmpty(poolClass) ? 
				SIMPLE_POOL_CLASS : poolClass;
	}

	/**
	 * 设置缓存池的容器类
	 */
	public void setPoolContainerClass(String poolContainerClass) {
		this.poolContainerClass = EasyUtils.isNullOrEmpty(poolContainerClass) ? 
				DEFAULT_CONTAINER : poolContainerClass;
	}

	public void setCyclelife(Long cyclelife) {
		this.cyclelife = cyclelife == null ? 0 : cyclelife;
	}

	public void setInterruptTime(Long interruptTime) {
		this.interruptTime = interruptTime == null ? 0 : interruptTime;
	}

	public void setName(String name) {
		this.name = name == null ? code : name;
	}

	public void setPoolSize(Integer poolSize) {
		if (poolSize != null) {
		    this.poolSize = Math.max(0, poolSize); // 确保 size 不小于 0
		}
	}

	public void setInitNum(Integer initNum) {
		this.initNum = (initNum == null ? 0 : initNum);
	}

    public void setCode(String code) {
        this.code = code;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public void setAccessMethod(Integer accessMethod) {
        this.accessMethod = accessMethod;
    }

    public void setParamFile(String paramFile) {
        this.paramFile = paramFile;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public Integer getInitNum() {
        return initNum;
    }

    public Long getCyclelife() {
        return cyclelife;
    }

    public Long getInterruptTime() {
        return interruptTime;
    }

    public String getDisabled() {
        return disabled;
    }

    public String getVisible() {
        return visible;
    }

    public Integer getAccessMethod() {
        return accessMethod;
    }

    public String getPoolClass() {
        return poolClass;
    }

    public String getPoolContainerClass() {
        return poolContainerClass;
    }

    public String getCustomizerClass() {
        return customizerClass;
    }

    public String getParamFile() {
        return paramFile;
    }
}
