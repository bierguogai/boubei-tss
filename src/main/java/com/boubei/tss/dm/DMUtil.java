package com.boubei.tss.dm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.log.AccessLog;
import com.boubei.tss.dm.log.AccessLogRecorder;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.dm.report.ScriptParser;
import com.boubei.tss.dm.report.ScriptParserFactory;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

public class DMUtil {
	
	static Logger log = Logger.getLogger(DMUtil.class);
	
	public static String getExportPath() {
		try {
			return ParamManager.getValue(PX.TEMP_EXPORT_PATH);
		} catch(Exception e) {
			return FileHelper.ioTmpDir();
		}
	}
	
	// 判断是否为区间查询（从 。。。 到 。。。）
	public static String[] preTreatScopeValue(String value) {
		if(value == null) return new String[] { };
		
  		value = value.trim();
  		if(value.startsWith("[") && value.endsWith("]") && value.indexOf(",") > 0) {
  			String[] vals = value.substring(1, value.length() - 1).split(",");
  			return new String[] { vals[0], vals[1] };

  		}
  		return new String[] { value };
	}
	
  	public static Object preTreatValue(String value, Object type) {
  		if(type == null || value == null) {
  			return value;
  		}
  		
  		type = type.toString().toLowerCase();
  		if("number".equals(type)) {
  			try {
  				if(value.indexOf(".") > 0) {
  	  				return EasyUtils.obj2Double(value);
  	  			}
  	  			return EasyUtils.obj2Long(value);
  			} catch(Exception e) {
				return null; // 如果输入的是空字符串等，会有异常
			}
  		}
  		else if("date".equals(type) || "datetime".equals(type)) {
			try {
				Date dateObj = DateUtil.parse(value);
				return new Timestamp(dateObj.getTime());
			} catch(Exception e) {
				throw new BusinessException("日期【" + value + "】格式有误，请检查。 " + e.getMessage());
			}
  		}
  		else {
  			return value;
  		}
  	} 
  	
  	// oracle的oracle.sql.TIMESTAMP类型的字段，转换为json时会报错，需要先转换为字符串
  	public static Object preTreatValue(Object value) {
  		if(value == null) return null;
  				
  		String valueCN = value.getClass().getName();
		if(valueCN.indexOf("TIMESTAMP") >= 0 ) {
  			return value.toString();
  		}
  		return value;
  	}
  	
  	@SuppressWarnings("unchecked")
	public static Map<String, Object> getFreemarkerDataMap() {
    	Map<String, Object> fmDataMap = new HashMap<String, Object>();
        
      	// 加入登陆用户的信息
      	fmDataMap.put(DMConstants.USER_ID, Environment.getUserId());
      	fmDataMap.put(DMConstants.USER_CODE, Environment.getUserCode());
		fmDataMap.put(DMConstants.FROM_USER_ID, Environment.getUserInfo("fromUserId"));
		
		// 将常用的script片段（权限过滤等）存至param模块，这里取出来加入fmDataMap
		try {
			List<Param> macroParams = ParamManager.getComboParam(PX.SCRIPT_MACRO);
			macroParams = (List<Param>) EasyUtils.checkNull(macroParams, new ArrayList<Param>());
			
			for(Param p : macroParams) {
				String key = p.getText();
				if( !fmDataMap.containsKey(key) ) {
					fmDataMap.put(key, p.getValue());
				}
			}
		} catch(Exception e) { }
		
		/* 往dataMap里放入Session里的用户权限、角色、组织等信息，作为宏代码解析。 */
    	if(Context.getRequestContext() != null) {
    		HttpSession session = Context.getRequestContext().getRequest().getSession();
    		Enumeration<String> keys = session.getAttributeNames();
    		while(keys.hasMoreElements()) {
    			String key = keys.nextElement();
    			fmDataMap.put(key, session.getAttribute(key).toString());
    		}
    	}
		
		return fmDataMap;
    }

	/** 用Freemarker引擎解析脚本 */
	public static String freemarkerParse(String script, Map<String, ?> dataMap) {
		String rtScript = EasyUtils.fmParse(script, dataMap);
		if(rtScript.startsWith("FM-parse-error")) {
			Map<String, Object> paramsMap = new HashMap<String, Object>();
	    	for(String key : dataMap.keySet()) {
	    		if(key.startsWith("param") || key.startsWith("report.")) {
	    			paramsMap.put(key, dataMap.get(key));
	    		}
	    	}
	    	log.info("\n------------ params-----------: " + paramsMap + "\n" );
		}

	    return rtScript;
	}
	
	public static String customizeParse(String script) {
		Map<String, Object> dataMap = getFreemarkerDataMap();
		return customizeParse(script, dataMap);
	}
	
	public static String customizeParse(String script, Map<String, Object> dataMap) {
		ScriptParser scriptParser = ScriptParserFactory.getParser();
      	if(scriptParser == null) {
      		script = DMUtil.freemarkerParse(script, dataMap);
      	} else {
      		script = scriptParser.parse(script, dataMap);
      	}
      	
      	return script;
	}
	
    public static Object spiritx(int x, String ds, String sql) {
    	if(x == 1) {
    		return SQLExcutor.query(ds, sql);
    	} else { 
    		SQLExcutor.excute(sql, ds); 
    		return "success"; 
    	}
    }

	/**
	 * 对paramValue进行检测，防止SQL注入
	 */
	public static String checkSQLInject(String paramValue) {
		String _pVal = (paramValue+"").toLowerCase();
		
		String sqlKeyword = "and|or|exec|alter|drop|create|insert|select|delete|update|from|count|like|chr|mid|master|truncate|char|declare";
		String[] sqlKeywords = sqlKeyword.split("\\|");
		for (String keyword : sqlKeywords) {
			if (_pVal.indexOf(keyword + " ") >=0 || _pVal.indexOf(" " + keyword) >=0 ) {
				return "inject-word:" + keyword; 
			}
		}
		
		// ' * % ; = - + > < ( )
		return paramValue.replaceAll("\'|=|;|>|<", " "); 
		/* - + %号可能做为连接符存在字段值里：2016-10-12, today-1, today+1, %m-%d 
		 * |\\(|\\) : 东风重卡(17.5), 此类查询条件可能含有括号，先放开
		 * */
	}
	
	/**
	 * 记录下报表的访问信息。
	 */
	public static void outputAccessLog(ReportService reportService, Long reportId, 
			String methodName, Map<String, String> requestMap, long start) {
		
		Report report = reportService.getReport(reportId);
		String reportName = report.getName();
		
		// 过滤掉定时刷新类型的报表
		boolean ignoreLog = ParamConstants.FALSE.equals(report.getNeedLog());
		if( ignoreLog ) return;
		
		String params = "";
		for(Entry<String, String> entry : requestMap.entrySet()) {
			params += entry.getKey() + "=" + entry.getValue() + ", ";
		}
        params = cutParams(params);
		
		// 方法的访问日志记录成败不影响方法的正常访问，所以对记录日志过程中各种可能异常进行try catch
        try {
            AccessLog log = new AccessLog();
            log.setClassName("Report-" + reportId);
    		log.setMethodName( methodName );
    		log.setMethodCnName( reportName );
            log.setAccessTime( new Date(start) );
            log.setRunningTime( System.currentTimeMillis() - start );
            log.setParams(params);
            
            // 记录访问人，没有则记为匿名访问
            Long userId = (Long) EasyUtils.checkNull(Environment.getUserId(), UMConstants.ANONYMOUS_USER_ID);
			log.setUserId(userId);
			log.setIp( Environment.getClientIp() );

            AccessLogRecorder.getInstanse().output(log);
        } 
        catch(Exception e) {
        	log.error("记录报表【" + reportName + "." + methodName + "】访问日志时出错：" + e.getMessage());
        }
	}
	
	public static String cutParams(String params) {
		if (params != null && params.length() > 500) {
            params = params.substring(0, 500);
        }
		return params;
	}
}
