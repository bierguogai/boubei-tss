package com.boubei.tss.dm.ddl;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.record.Record;

public class _MySQL extends _Database {
	
	public _MySQL(Record record) {
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
		if(this.fields.isEmpty()) return;
		
		StringBuffer createDDL = new StringBuffer("create table if not exists " + table + " ( ");
   		for(Map<Object, Object> fDefs : fields) {
			createDDL.append( getFiledDef(fDefs, false) ).append( ", " );
   		}
   		
   		createDDL.append("`createtime` TIMESTAMP NULL, ");
		createDDL.append("`creator` varchar(50) NOT NULL, ");
		createDDL.append("`updatetime` TIMESTAMP NULL, ");
		createDDL.append("`updator` varchar(50), ");
		createDDL.append("`version` int(5), ");
   		createDDL.append("`id` bigint(20) NOT NULL AUTO_INCREMENT, ");
   		createDDL.append( "primary key (id))" );
   		
   		SQLExcutor.excute(createDDL.toString(), datasource);		
	}

	public void dropTable(String table, String datasource) {
		SQLExcutor.excute("drop table if exists " + table, datasource);
	}
	
	public String toPageQuery(String sql, int page, int pagesize) {
		int fromRow = pagesize * (page - 1);
		return sql + "\n LIMIT " + (fromRow) + ", " + pagesize;
	}
	
	protected String[] getSQLs(String field) {
		String[] names = createNames(field);
		
		String sql1 = "alter table " +this.table+ " add UNIQUE (" +field+ ")";
		String sql2 = "alter table " +this.table+ " drop index " +field;
		String sql3 = "create index " +names[1]+ " on " +this.table+ " (" +field+ ")";
		String sql4 = "drop index " + names[1] + " on " + this.table;
		return new String[] { sql1, sql2, sql3, sql4 };
	}
}
