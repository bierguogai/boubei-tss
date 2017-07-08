package com.boubei.tss.dm.record.ddl;

import com.boubei.tss.dm.record.Record;

public class _H2 extends _MySQL {
	
	public _H2(Record record) {
		super(record);
	}
	
	public String toPageQuery(String sql, int page, int pagesize) {
		return super.toPageQuery(sql, page, pagesize);
	}
}
