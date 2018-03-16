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

import java.util.Map;

import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.util.EasyUtils;

public class _Oracle extends _Database {
	
	public _Oracle(Record record) {
		super(record);
	}
	
	protected Map<String, String> getDBFiledTypes(int length) {
		Map<String, String> m = super.getDBFiledTypes(length);
		m.put(_Field.TYPE_DATETIME, "date");
		
		return m;
	}
	
	public void createTable() {
		if(this.fields.isEmpty()) return;
 
		StringBuffer createDDL = new StringBuffer("create table " + this.table + " ( ");
   		for(Map<Object, Object> fDefs : fields) {
			createDDL.append( getFiledDef(fDefs, false) ).append( ", " );
   		}
   		
   		createDDL.append("createtime date NOT NULL, ");
		createDDL.append("creator varchar(50) NOT NULL, ");
		createDDL.append("updatetime date NULL, ");
		createDDL.append("updator varchar(50), ");
		createDDL.append("version NUMBER(5), ");
   		createDDL.append("id NUMBER(19) NOT NULL");
   		createDDL.append( ")" );
   		
		try { 
			SQLExcutor.excute(createDDL.toString(), datasource);	
	   		SQLExcutor.excute("alter table " + this.table + " add primary key (id)", datasource);
			SQLExcutor.excute("create sequence " + getSeq() + " increment by 1 start with 1", datasource); 
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
	
	private String getSeq() {
		return this.table + "_seq";
	}
	
	protected String createInsertSQL() {
		String valueTags = "", fieldTags = "";
		for(String field : this.fieldCodes) {
			valueTags += "?,";
			fieldTags += field + ",";
		}
		String insertSQL = "insert into " + this.table + "(" + fieldTags + "createtime,creator,version,id) " +
				" values (" + valueTags + " ?, ?, ?, " + getSeq() + ".nextval)";
		return insertSQL;
	}
	
	public Long insertRID(Map<String, String> valuesMap) {
		SQLExcutor ex = new SQLExcutor();
		ex.excuteQuery("select " + getSeq() + ".nextval as id from dual", this.datasource);
		Long newID = EasyUtils.obj2Long( ex.getFirstRow("id") );
		
		String insertSQL = createInsertSQL();
		insertSQL = insertSQL.replace(getSeq() + ".nextval", newID.toString());
		
		Map<Integer, Object> paramsMap = super.buildInsertParams(valuesMap);
		SQLExcutor.excute(insertSQL, paramsMap, this.datasource);
		
		logCUD(newID, "create", " add a new row: " + valuesMap);
		return newID;
	}
	
	public String toPageQuery(String sql, int page, int pagesize) {
		int fromRow = pagesize * (page - 1), 
				toRow = pagesize * page;

		return "SELECT * FROM ( SELECT t.*, ROWNUM RN FROM (\n " + sql + " \n) t WHERE ROWNUM <= " + toRow + ") WHERE RN > " + fromRow;
	}
}
