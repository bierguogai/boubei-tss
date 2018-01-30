/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.ddl;

import com.boubei.tss.dm.record.Record;

public class _H2 extends _MySQL {
	
	public _H2(Record record) {
		super(record);
	}
	
	public String toPageQuery(String sql, int page, int pagesize) {
		return super.toPageQuery(sql, page, pagesize);
	}
}
