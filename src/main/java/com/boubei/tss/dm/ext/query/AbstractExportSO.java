/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.ext.query;

/**
 * 需要导出的数据的查询条件
 *
 */
public abstract class AbstractExportSO extends AbstractSO {

	private static final long serialVersionUID = -8242722351039220496L;

	public abstract String getExportFileName();
	
}
