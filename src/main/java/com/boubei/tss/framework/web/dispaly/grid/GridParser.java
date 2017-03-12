package com.boubei.tss.framework.web.dispaly.grid;

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
