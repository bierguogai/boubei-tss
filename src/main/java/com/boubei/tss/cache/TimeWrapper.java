/* ==================================================================   
 * Created [2009-4-27 下午11:32:55] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018  
 * ================================================================== 
 */

package com.boubei.tss.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.util.DateUtil;

/**
 * 对缓冲对象及其Key一起进行包装，使其具有生命周期，同时记录点击量等信息。
 * 
 */
public class TimeWrapper implements Cacheable, Serializable {

	private static final long serialVersionUID = 430479804348050166L;
	
	private Object key, value;
	private long death = 0; // 如果death = 0，则元素永不过期
	private long accessed; // 记录元素最后一次被访问的时间
	private long birthday;
	private int  hit = 0;    // 对象的点击次数
	private long hitLong = 0; // 对象的被使用时间

	/**
	 * 创建一个新的wrapped对象.
	 * 
	 * @param id
	 * @param obj
	 * @param cycleLife 存活时间( > 0)
	 */
	public TimeWrapper(Object key, Object value) {
		this(key, value, 0L);
	}

	public TimeWrapper(Object key, Object value, Long cycleLife) {
		this.key = key;
		this.value = value;
		
		this.birthday = this.accessed = System.currentTimeMillis();
		setCyclelife(cycleLife);
	}

	public Object getKey() {
		return this.key;
	}

	public Object getValue() {
		return this.value;
	}

	/**
	 * 设置元素的有效期。
	 * 
	 * @param cycleLife
	 */
	synchronized void setCyclelife(long cycleLife) {
		if (cycleLife > 0) {
			this.death = this.birthday + cycleLife;
		}
		else {
			this.death = 0;  // cycleLife <= 0  ==> 永不过期
		}
	}

	/**
	 * 判断元素是否已过期。如果death＝0，则元素永不过期
	 */
	public synchronized boolean isExpired() {
		return this.death > 0 && System.currentTimeMillis() > this.death;
	}

	public synchronized void updateAccessed() {
		this.accessed = System.currentTimeMillis();
	}

	public long getAccessed() {
		return this.accessed;
	}
	
	public String getDeath() {
		if(this.death == 0) {
			return " Forever ";
		}
		return DateUtil.formatCare2Second(new Date(death));
	}
	
	public long getBirthday() {
		return this.birthday;
	}

	public int getHit() {
		return this.hit;
	}

	public void addHit() {
		this.hit++;
	}
	
	public long addHitLong() {
		long delta = System.currentTimeMillis() - this.accessed;
		this.hitLong += delta;
		
		return delta;
	}

	public long getHitLong() {
		return this.hitLong;
	}

	public void update(Object value) {
		this.value = value;
	}
	
	/**
	 * 创建一个指定前缀 + 当前序号的 key
	 * 
	 * @param prefix
	 * @return
	 */
	static Map<String, Integer> countsMap = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public static synchronized String createSequenceKey(String prefix) {
	    Integer count = countsMap.get(prefix);
	    if(count == null) {
	        countsMap.put(prefix, count = 1);
	    }
	    else {
	        countsMap.put(prefix, count = count + 1);
	    }
	    
		return prefix + "_" + count;
	}
	
	public boolean equals(Object o) {
		if (o instanceof TimeWrapper) {
			TimeWrapper temp = (TimeWrapper) o;
			return this.key != null && this.key.equals(temp.getKey());
		}
		return false;
	}

	public int hashCode() {
		return this.key.hashCode();
	}
	
	public String toString() {
		return "(" + this.getKey().toString() + " = " + this.value + ")";
	}
}