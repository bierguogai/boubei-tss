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

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.record.Record;

public class _SQLServer extends _Database {
	
	public _SQLServer(Record record) {
		super(record);
	}
	
	protected Map<String, String> getDBFiledTypes(int length) {
		Map<String, String> m = new HashMap<String, String>();
		m.put(_Filed.TYPE_NUMBER, "float");
		m.put(_Filed.TYPE_INT, "int");
		m.put(_Filed.TYPE_DATETIME, "datetime");
		m.put(_Filed.TYPE_DATE, "date");
		m.put(_Filed.TYPE_STRING, "varchar(" + length + ")");
		
		return m;
	}

	public void createTable() {		
		StringBuffer createDDL = new StringBuffer("create table " + table + " ( ");
   		for(Map<Object, Object> fDefs : fields) {
			createDDL.append( getFiledDef(fDefs, false) ).append( ", " );
   		}
   		
   		createDDL.append("createtime datetime NULL, ");
		createDDL.append("creator varchar(50) NOT NULL, ");
		createDDL.append("updatetime datetime NULL, ");
		createDDL.append("updator varchar(50), ");
		createDDL.append("version int, ");
   		createDDL.append("id int identity(1,1), constraint " +this.table+ "_id primary key (id) )");
   		
   		try { 
   			SQLExcutor.excute(createDDL.toString(), datasource);
		} 
		catch(Exception e1) { 
			// 检查表是否存在了，没有存在则报错(e1)
			try {
				super.get(-1L);
			} catch(Exception e2) {
				throw e1;
			}
			
			// 没有报错，说明表或sequence可能已经存在，不抛出异常（适用于连接录入到已存在的表）
			log.warn("create oracle table[" + this.recordName + "] error: " + e1.getMessage());
		}
	}
	
	protected String[] getSQLs(String field) {
		String[] names = createNames(field);
		String[] sqls = super.getSQLs(field);
		sqls[3] = "drop index " +this.table+ "." + names[1];
		
		return sqls;
	}
	
	// select top pageSize o.* from (select row_number() over(order by orderColumn) as rn, t.* from(sql) t ) as o where rn > firstIndex;
	public String toPageQuery(String sql, int page, int pagesize) {
		int orderbyIndex = sql.toLowerCase().lastIndexOf("order by");
		String tempSql = sql, orderBy = " order by (select 0) ";
		if( orderbyIndex > 0 ) {
			tempSql = sql.substring(0, orderbyIndex);
			orderBy = sql.substring(orderbyIndex);
		}
		
		int fromRow = pagesize * (page - 1);
		return "select top " +pagesize+ " t.* from ( select ROW_NUMBER() over( " +orderBy+ " ) AS rn, x.* from (" +tempSql+ ") x ) t where t.rn > " + fromRow;
	}
}
