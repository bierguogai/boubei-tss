package com.boubei.tss.dm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;

import com.boubei.tss.EX;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

public abstract class _Database {
	
	static Logger log = Logger.getLogger(_Database.class);
	
	public Long recordId;
	public String recordName;
	public String datasource;
	public String table;
	public String customizeTJ;
	
	private boolean needLog;
	public boolean needFile;
	
	List<Map<Object, Object>> fields;
	public List<String> fieldCodes;
	public List<String> fieldTypes;
	public List<String> fieldNames;
	public List<String> fieldAligns;
	public List<String> fieldWidths;
	public List<String> fieldRole2s;
	
	public String toString() {
		return "【" + this.datasource + "." + this.recordName + "】";
	}
 	
	public _Database(Record record) {
		if(record == null) return;
		
		this.recordId = record.getId();
		this.recordName = record.getName();
		this.datasource = record.getDatasource();
		this.table = record.getTable();
		this.fields = parseJson(record.getDefine());
		this.customizeTJ = record.getCustomizeTJ();
		this.needLog = ParamConstants.TRUE.equals(record.getNeedLog());
		this.needFile = ParamConstants.TRUE.equals(record.getNeedFile());
		
		this.initFieldCodes();
	}
	
	protected void initFieldCodes() {
		this.fieldCodes = new ArrayList<String>();
		this.fieldTypes = new ArrayList<String>();
		this.fieldNames = new ArrayList<String>();
		this.fieldAligns = new ArrayList<String>();
		this.fieldWidths = new ArrayList<String>();
		this.fieldRole2s = new ArrayList<String>();
		for(Map<Object, Object> fDefs : this.fields) {
			this.fieldCodes.add((String) fDefs.get("code"));
			this.fieldTypes.add((String) fDefs.get("type"));
			this.fieldNames.add((String) fDefs.get("label"));
			this.fieldAligns.add( (String)EasyUtils.checkNull(fDefs.get("calign"), "") ); // 列对齐方式
			this.fieldWidths.add( (String)EasyUtils.checkNull(fDefs.get("cwidth"), "") ); // 列宽度
			this.fieldRole2s.add((String) fDefs.get("role2"));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Map<Object, Object>> parseJson(String define) {
		if(EasyUtils.isNullOrEmpty(define)) {
			return new ArrayList<Map<Object,Object>>();
		}
		
		define = define.replaceAll("'", "\"");
		
		try {  
   			List<Map<Object, Object>> list = new ObjectMapper().readValue(define, List.class);  
   			for(int i = 0; i < list.size(); i++) {
   	        	Map<Object, Object> fDefs = list.get(i);
   	        	int index = i + 1;
   	        	
   				String code = (String) fDefs.get("code");
   				code = (EasyUtils.isNullOrEmpty(code) ? "f" + index : code).toLowerCase();
   				fDefs.put("code", code);
   			}
   			return list;
   	    } 
   		catch (Exception e) {  
			throw new BusinessException( EX.parse(EX.DM_15, recordName, e.getMessage()) );
   	    } 
	}
	
	protected abstract String getFieldType(Map<Object, Object> fDefs);
	
	public abstract String toPageQuery(String sql, int page, int pagesize);
	
	/**
	 * 如果相同表名已经存在，直接使用既有表
	 */
	public abstract void createTable();
	
	public void dropTable(String table, String datasource) {
		SQLExcutor.excute("drop table " + table, datasource);
	}
	
	public void updateTable(Record _new) {
		String newDS = _new.getDatasource();
		String table = _new.getTable();
		this.customizeTJ = _new.getCustomizeTJ();
		this.needLog = ParamConstants.TRUE.equals(_new.getNeedLog());
		this.needFile = ParamConstants.TRUE.equals(_new.getNeedFile());
		
		if(!newDS.equals(this.datasource) || !table.equals(this.table)) {
			this.datasource = newDS;
			this.table = table;
			createTable();
			return;
		}
		
		// 比较新旧字段定义的异同（新增的和删除的，暂时只关心新增的）
		List<Map<Object, Object>> newFields = parseJson(_new.getDefine());
				
		// 新增加的字段
		for(Map<Object, Object> fDefs1 : newFields) {
			String code = (String) fDefs1.get("code");
			fDefs1.put("code", code);
			
			boolean exsited = false;
        	for(Map<Object, Object> fDefs2 : this.fields ) {
        		if(code.equals(fDefs2.get("code"))) {
        			exsited = true; 
        			//TODO 进一步判断字段类型及长度，及是否可空等有无发生变化
        		}
        	}
        	
        	if( !exsited ) {
        		String fieldType = getFieldType(fDefs1);
        		try {
    				SQLExcutor.excute("alter table " + this.table + " add " + code + " " + fieldType, newDS);
    			} catch(Exception e) { }
        	}
		}
		
		// 被删除的字段（原来有的，在新的定义里没有了）
		for(Map<Object, Object> fDefs2 : this.fields ) {
			Object oldCode = fDefs2.get("code");
			boolean exsited = false;
    		for(Map<Object, Object> fDefs1 : newFields) {
    			String code = (String) fDefs1.get("code");
 
				if(code.equals(oldCode)) {
        			exsited = true; 
        		}
    		}
    		if( !exsited ) {
    			try {
    				// 先查询该字段是否有值，没有值才删除该列
    				String checkSQL = "select count(*) num from " +this.table+ " where " +oldCode+ " is not null";
    				SQLExcutor ex = new SQLExcutor();
    				ex.excuteQuery(checkSQL, newDS);
    				int count = EasyUtils.obj2Int( ex.getFirstRow("num") );
    				if(count == 0) {
    					SQLExcutor.excute("alter table " +this.table+ " drop column " + oldCode, newDS);
    				} else {
    					SQLExcutor.excute("alter table " +this.table+ " modify " +oldCode+ " null", newDS);
    				}
    			} catch(Exception e) { }
        	}
    	}
		
		this.fields = newFields;
		initFieldCodes();
	}
	
	// 用于单行数据新增
	public void insert(Map<String, String> valuesMap) {
		Map<Integer, Object> paramsMap = buildInsertParams(valuesMap);
		SQLExcutor.excute(createInsertSQL(), paramsMap, this.datasource);
		
		logCUD("", "create", " add a new row: " + valuesMap);
	}
	
	public Long insertRID(Map<String, String> valuesMap) {
		Map<Integer, Object> paramsMap = buildInsertParams(valuesMap);
		Object[] params = new Object[ paramsMap.size() ];
		for(int i = 0; i < params.length; i++) {
			params[i] = paramsMap.get( i+1 );
		}
		
		Long rID = SQLExcutor.excuteInsert(createInsertSQL(), params, this.datasource);
		logCUD(rID, "create", " add a new row: " + valuesMap);
		return rID;
	}

	protected Map<Integer, Object> buildInsertParams(Map<String, String> valuesMap) {
		Map<Integer, Object> paramsMap = new LinkedHashMap<Integer, Object>();
		int index = 0;
		for(String field : this.fieldCodes) {
			Object value = DMUtil.preTreatValue(valuesMap.get(field), fieldTypes.get(index));
			paramsMap.put(++index, value);
		}
		paramsMap.put(++index, new Timestamp(new Date().getTime())); 
		paramsMap.put(++index, Environment.getUserCode());
		paramsMap.put(++index, 0);
		return paramsMap;
	}
	
	// 用于数据清洗或批量导入
	public void insertBatch(Collection<Map<String, String>> valuesMaps) {
		if(valuesMaps == null || valuesMaps.isEmpty()) return;
		
		List<Map<Integer, Object>> paramsList = new ArrayList<Map<Integer,Object>>();
		for(Map<String, String> valuesMap : valuesMaps) {
			Map<Integer, Object> paramsMap = buildInsertParams(valuesMap);
			paramsList.add(paramsMap);
		}
		
		SQLExcutor.excuteBatch(createInsertSQL(), paramsList , this.datasource);
		
		logCUD("", "create", " add some rows: " + valuesMaps);
	}
	
	protected String createInsertSQL() {
		String valueTags = "", fieldTags = "";
		for(String field : this.fieldCodes) {
			valueTags += "?,";
			fieldTags += field + ",";
		}
		String insertSQL = "insert into " + this.table + "(" + fieldTags + "createtime,creator,version) " +
				" values (" + valueTags + " ?, ?, ?)";
		return insertSQL;
	}

	public void update(Long id, Map<String, String> valuesMap) {
		Map<String, Object> old = get(id);
		if( old == null ) {
			throw new BusinessException(EX.DM_16);
		}
		
		// 如果_version值不为空，则用其实现乐观锁控制
		String _version = valuesMap.remove("_version");
		if( !EasyUtils.isNullOrEmpty(_version) ) {
			int version1 = EasyUtils.obj2Int(_version);
			int version2 = EasyUtils.obj2Int(old.get("version"));
			if( version1 < version2 ) {
				throw new BusinessException(EX.DM_17);
			}
		}
		
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		int index = 0;
		String tags = "";
		for(String field : this.fieldCodes) {
			Object value = valuesMap.get(field);
			value = DMUtil.preTreatValue((String)value, fieldTypes.get(index));
			
			paramsMap.put(++index, value);
			tags += field + "=?, ";
		}
		paramsMap.put(++index, new Timestamp(new Date().getTime()));
		paramsMap.put(++index, Environment.getUserCode());
		paramsMap.put(++index, id);
		
		String updateSQL = "update " + this.table + " set " + tags + "updatetime=?, updator=?, version=version+1 where id=?";
		SQLExcutor.excute(updateSQL, paramsMap, this.datasource);
		
		logCUD(id, "update", "\n begin: " + old + " \n after: " + get(id));
	}
	
	public void updateBatch(String ids, String field, String value) {
		String updateSQL = "update " + this.table + " set " + field + "=?, updatetime=?, updator=?, version=version+1 where id in (" + ids + ")";
		
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		int index = 0, fieldIndex = this.fieldCodes.indexOf(field);
		
		paramsMap.put(++index, DMUtil.preTreatValue( value, fieldTypes.get(fieldIndex) ));
		paramsMap.put(++index, new Timestamp(new Date().getTime()));
		paramsMap.put(++index, Environment.getUserCode());
		
		SQLExcutor.excute(updateSQL, paramsMap, this.datasource);
		
		logCUD(ids, "update batch", "\n field: " + field + " \n value: " + value);
	}

	public Map<String, Object> get(Long id) {
		String fieldTags = "";
		for(String field : this.fieldCodes) {
			fieldTags += field + ",";
		}
		String sql = "select " + fieldTags + "creator,version from " + this.table + " where id=?";
		List<Map<String, Object>> list = SQLExcutor.query(this.datasource, sql, id);
		if( EasyUtils.isNullOrEmpty(list) ) {
			return null;
		}
		return list.get(0);
	}

	public void delete(Long id) {
		Map<String, Object> old = get(id);
		
		String updateSQL = "delete from " + this.table + " where id=" + id;
		SQLExcutor.excute(updateSQL, this.datasource);
		
		// 记录删除日志
        logCUD(id, "delete", Environment.getUserCode() + " deleted one row：" + old);
	}
	
	public void logCUD(Object id, String opeartion, String logMsg) {
		if( !this.needLog ) return;
		
		Log excuteLog = new Log(recordName + ", " + id, logMsg);
		excuteLog.setOperateTable("record-" + opeartion);
		((IBusinessLogger) Global.getBean("BusinessLogger")).output(excuteLog);
	}
	
	/**
	 * 查询数据，同时进行权限控制。 <br>
	 * 1、如果用户有浏览、维护数据权限，允许其查询其它人创建的记录；否则只能查询本人创建的记录 <br>
	 * 2、加上 customizeTJ 里强制性的过滤条件 <br>
	 * 
	 * @param page
	 * @param pagesize
	 * @param params
	 * @return
	 */
	public SQLExcutor select(int page, int pagesize, Map<String, String> params) {
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		paramsMap.put(1, Environment.getUserCode());
		
		if(params == null) {
			params = new HashMap<String, String>();
		}
		
		// 增加权限控制，针对有編輯权限的允許查看他人录入数据, '000' <> ? <==> 忽略创建人这个查询条件
		boolean visible = Environment.isAdmin();
		try {
			List<String> permissions = PermissionHelper.getInstance().getOperationsByResource(recordId,
	                RecordPermission.class.getName(), RecordResource.class);
			visible = visible || permissions.contains(Record.OPERATION_VDATA) 
					|| permissions.contains(Record.OPERATION_EDATA);
		} 
		catch(Exception e) { }
		
		// 设置查询条件
		String condition;
		if( visible && !params.containsKey("creator") ) {
			condition = " '000' <> ? ";
		} else {
			condition = " creator = ? ";
		}
		
		for(String key : params.keySet()) {
			String valueStr = params.get(key);
			if( EasyUtils.isNullOrEmpty(valueStr) ) continue;
			
			// 对paramValue进行检测，防止SQL注入
			valueStr = DMUtil.checkSQLInject(valueStr);
			
			if( "creator".equals(key) ) {
				paramsMap.put(1, valueStr);  // 替换登录账号，允许查询其它人创建的数据; 
				continue;
			}
			
			if("updator".equals(key)) {
				condition += " and updator = ? ";
				paramsMap.put(paramsMap.size() + 1, valueStr);
				continue;
			}
			
			if( "id".equals(key) ) {
				condition += " and id = ? ";
				valueStr = valueStr.replace("_copy", "");
				paramsMap.put(paramsMap.size() + 1, EasyUtils.obj2Long(valueStr));
				continue;
			}
			
			int fieldIndex = this.fieldCodes.indexOf(key);
			if(fieldIndex >= 0) {
				String paramType = this.fieldTypes.get(fieldIndex);
				
				String[] vals = DMUtil.preTreatScopeValue(valueStr);				
				if(vals.length == 1) {
					Object val = DMUtil.preTreatValue(vals[0], paramType);
					
					// 字符串支持模糊查询
					if( paramType == null || "string".equals(paramType.toLowerCase()) ) {
						condition += " and " + key + " like ? ";
						val = "%"+val+"%";
					} else {
						condition += " and " + key + " = ? ";
					}
					paramsMap.put(paramsMap.size() + 1, val);
				}
				else {
					condition += " and " + key + " >= ?  and " + key + " <= ? ";
					paramsMap.put(paramsMap.size() + 1, DMUtil.preTreatValue(vals[0], paramType));
					paramsMap.put(paramsMap.size() + 1, DMUtil.preTreatValue(vals[1], paramType));
				}
			}
		}
		
		/* 
		 * 如果用户的域不为空，则只筛选出该域下用户创建的记录
		 * 只有单机部署的BI允许无域（百世快运这类）；SAAS部署必须每个组都要有域，每个人必属于某个域。Admin不属于任何域
		 */
		String _customizeTJ = (String) EasyUtils.checkNull(this.customizeTJ, " 1=1 ");
		_customizeTJ += " <#if USERS_OF_DOAMIN??> and creator in (${USERS_OF_DOAMIN}) </#if> ";
		condition += " and ( " + DMUtil.customizeParse(_customizeTJ + " or -1 = ${userId} ") + " ) ";
		
		// 设置排序方式
		String sortField = params.get("sortField");
		String sortType  = params.get("sortType");
		
		String orderby = "order by ";
		if( !EasyUtils.isNullOrEmpty(sortField) && 
				(this.fieldCodes.contains(sortField) 
						|| "createtime,updatetime".indexOf(sortField) >= 0) ) {
			
			if("onlynull".equals(sortType)) {
				condition += " and " + sortField + " is null ";
			}
			else {
				orderby += sortField;
				if( EasyUtils.isNullOrEmpty(sortType) ) {
					sortType = "asc";
				}
				orderby += " " + sortType + ", ";
			}
		}
		orderby += " id desc "; // 始终加上ID排序，保证查询结果排序方式唯一
		
		String selectSQL = "select " + EasyUtils.list2Str(this.fieldCodes) + 
					",createtime,creator,updatetime,updator,version,id from " + this.table + 
					" where " + condition + orderby;
		
		SQLExcutor ex = new SQLExcutor();
		ex.excuteQuery(selectSQL, paramsMap, page, pagesize, this.datasource);
		
		return ex;
	}

	public Document getGridTemplate() {
		StringBuffer sb = new StringBuffer();
        sb.append("<grid><declare sequence=\"true\" header=\"checkbox\">").append("\n");
        
        int index = 0; 
        for(String filed : fieldNames) {
            String fieldCode = fieldCodes.get(index);
            String fieldAlign = (String) EasyUtils.checkNull(fieldAligns.get(index), "center");
            String fieldWidth = fieldWidths.get(index);
            String fieldRole2 = fieldRole2s.get(index);
            String fieldType  = "string"; // fieldTypes.get(index);==> GridNode里转换异常（date类型要求值也为date）
            
            if(EasyUtils.isNullOrEmpty(fieldWidth)) {
            	fieldWidth = "";
            }
            else if( fieldWidth.startsWith("0")) {
            	fieldWidth = " display=\"none\" ";
            } 
            else {
            	fieldWidth = " width=\"" + fieldWidth + "\" ";
            }
            
            if( PermissionHelper.checkRole(fieldRole2) && !"hidden".equals(fieldTypes.get(index)) ) {
            	sb.append("<column name=\"" + fieldCode + "\" mode=\"" + fieldType + "\" caption=\"" + filed 
					+ "\" align=\"" + fieldAlign + "\" " + fieldWidth + " />").append("\n");
            }
            index++;
        }
        
        if(this.needFile) {
        	sb.append("<column name=\"fileNum\" mode=\"string\" caption=\"附件\" width=\"30px\"/>").append("\n");
        }
        
        // 判断是否显示这5列
        if( (this.customizeTJ+"").indexOf("showCUV") >= 0 ) {
        	sb.append("<column name=\"createtime\"  caption=\"创建时间\" sortable=\"true\" width=\"60px\"/>").append("\n");
            sb.append("<column name=\"creator\"  caption=\"创建人\" sortable=\"true\" width=\"40px\"/>").append("\n");
            sb.append("<column name=\"updatetime\"  caption=\"更新时间\" sortable=\"true\" width=\"60px\"/>").append("\n");
            sb.append("<column name=\"updator\"  caption=\"更新人\" sortable=\"true\" width=\"40px\"/>").append("\n");
            sb.append("<column name=\"version\"  caption=\"更新次数\" width=\"30px\"/>").append("\n");
        } else {
        	sb.append("<column name=\"creator\" display=\"none\"/>").append("\n");
        }
        
        // ID列默认隐藏
        sb.append("<column name=\"id\" display=\"none\"/>").append("\n");
        
        sb.append("</declare>\n<data></data></grid>");
        
    	return XMLDocUtil.dataXml2Doc(sb.toString());
	}
	
	public List<Map<Object, Object>> getFields() {
		return this.fields;
	}
	
	private static Map<String, String> dsMappingType = new HashMap<String, String>();
	
	public static String getDBType(String datasource) {
		String result = dsMappingType.get(datasource);
		if(result != null) return result;
		
		Pool connpool = JCache.getInstance().getPool(datasource);
		if(connpool == null) {
			throw new BusinessException( EX.parse(EX.DM_02, datasource) );
		}
        Cacheable connItem = connpool.checkOut(0);
        Connection conn = (Connection) connItem.getValue();
        
		try {
			String driveName = conn.getMetaData().getDriverName();
			log.debug(" database diverName: 【 " + driveName + "】。");
			
			for(String type : DB_TYPE) {
				if (driveName.startsWith(type)) {
					dsMappingType.put(datasource, result = type);
		        }
			}
		} catch (SQLException e) {
			
		} finally {
            connpool.checkIn(connItem); // 返回连接到连接池
        }
        
		return result;
	}
	
	public static String[] DB_TYPE = new String[] {"MySQL", "Oracle", "H2"};
	
	public static _Database getDB(Record record) {
		String type = getDBType(record.getDatasource());
		return getDB(type, record);
	}
	
	public static _Database getDB(String type, Record record) {
		if( type.startsWith( DB_TYPE[0] ) ) {
			return new _MySQL(record);
		}
		else if( type.startsWith( DB_TYPE[1] ) ) {
			return new _Oracle(record);
		}
		else {
			return new _H2(record);
		}
	}
}
