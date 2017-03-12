/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */
package com.boubei.tss.cache;

import java.util.EventObject;

/**
 * 对象池事件
 */
public class PoolEvent extends EventObject {

	private static final long serialVersionUID = -1574889170922509995L;

	public static final int CHECKOUT = 1;
	public static final int CHECKIN = 2;
	public static final int MAX_POOL_LIMIT_REACHED = 3;
	public static final int MAX_POOL_LIMIT_EXCEEDED = 4;
	public static final int POOL_RELEASED = 5;
	public static final int PUT_IN = 6;
	public static final int REMOVE = 7;
	public static final int POOL_DISABLED = 11;
	public static final int POOL_ENABLED = 12;

	public static final int STRATEGY_CHANGED_CYCLELIFE = 8;
	public static final int STRATEGY_CHANGED_SIZE_REDUCE = 9;
	public static final int STRATEGY_CHANGED_RESET = 10;

	private int type;

	public PoolEvent(Pool pool, int type) {
		super(pool);
		this.type = type;
	}

	/**
	 * 返回创建该事件的池
	 */
	public Pool getPool() {
		return (Pool) getSource();
	}

	public int getType() {
		return type;
	}
}
