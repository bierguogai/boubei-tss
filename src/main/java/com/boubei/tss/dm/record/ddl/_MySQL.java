package com.boubei.tss.dm.record.ddl;

import java.util.Map;

import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.Record;

public class _MySQL extends _Database {
	
	public _MySQL(Record record) {
		super(record);
	}
	
	protected String getFieldType(Map<Object, Object> fDefs) {
		String type = (String) fDefs.get("type"); // string number date datetime hidden
		
		if("number".equals(type)) {
			return " float "; 
		} else if("datetime".equals(type)) {
			return " datetime "; 
		} else if("date".equals(type)) {
			return " date "; 
		}
		else {
			return " varchar(" + DDLUtil.getVarcharLength(fDefs) + ") "; 
		}
	}
	
	public void createTable() {
		if(this.fields.isEmpty()) return;
		
		StringBuffer createDDL = new StringBuffer("create table if not exists " + table + " ( ");
   		for(Map<Object, Object> fDefs : fields) {
			createDDL.append( "`" + fDefs.get("code") + "` " ); 
			createDDL.append(getFieldType(fDefs));
			
			if("false".equals(fDefs.get("nullable"))) {
				createDDL.append( " NOT NULL " ); 
			}
			createDDL.append( ", " );
   		}
   		
   		createDDL.append("`createtime` TIMESTAMP NULL, ");
		createDDL.append("`creator` varchar(50) NOT NULL, ");
		createDDL.append("`updatetime` TIMESTAMP null, ");
		createDDL.append("`updator` varchar(50), ");
		createDDL.append("`version` int(5), ");
   		createDDL.append("`id` bigint(20) not null AUTO_INCREMENT, ");
   		createDDL.append( "primary key (id))" );
   		
   		SQLExcutor.excute(createDDL.toString(), datasource);		
	}

	public void dropTable(String table, String datasource) {
		SQLExcutor.excute("drop table if exists " + this.table, datasource);
	}
}
