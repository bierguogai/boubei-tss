/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.grid;

/** 
 * Grid数据解析器
 */
public abstract class GridParser {
    
	protected GridColumn[] columns;

	/**
	 * 解析Grid数据
	 */
	public abstract GridNode parse(Object data);

	/**
	 * 字段名数组
	 * 
	 * @param columns
	 */
	public void setColumns(GridColumn[] columns) {
		this.columns = columns;
	}
}
