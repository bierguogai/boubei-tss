/* ==================================================================   
 * Created [2007-2-15] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache.extension.workqueue;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * <pre>
 * 输出记录到数据库的任务抽象超类。
 * 类似日志输出、访问量输出可以通过继承该超类实现。
 * </pre>
 */
public abstract class AbstractTask implements Task {

	protected Logger log = Logger.getLogger(this.getClass());

	private static int count = 0;
	private String name;

	public AbstractTask() {
		count ++;
		String className = this.getClass().getName();
		name = className.substring(className.lastIndexOf(".") + 1) + "-" + count;
	}

	public String toString() {
		return name;
	}

	protected List<Object> records;

	public void fill(List<Object> records) {
		this.records = records;
	}

	public void recycle() throws Exception {
		this.records = null;
	}
}
