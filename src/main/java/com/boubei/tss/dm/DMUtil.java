package com.boubei.tss.dm;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.boubei.tss.EX;
import com.boubei.tss.PX;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.report.ScriptParser;
import com.boubei.tss.dm.report.ScriptParserFactory;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.permission.IResource;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

public class DMUtil {
	
	static Logger log = Logger.getLogger(DMUtil.class);
	
	/**
	 * 按角色控制数据源下拉列表，角色信息配置在数据源参数项的备注里，多个角色逗号分隔。
	 */
	public static void setDSList(XFormEncoder xformEncoder) {
		try {
        	List<Param> dsItems = ParamManager.getComboParam(PX.DATASOURCE_LIST);
        	List<Param> _dsItems = new ArrayList<Param>();
        	for(Param ds : dsItems) {
        		boolean flag = false;
        		String permissions = ds.getDescription();
				if( !EasyUtils.isNullOrEmpty(permissions) ) {
					List<Long> ownRoles = Environment.getOwnRoles();
					List<String> permitedRoles = Arrays.asList( permissions.split(",") );
					for(Long ownRole: ownRoles) {
						if( permitedRoles.contains(ownRole.toString()) ) {
							flag = true;
						}
					}
        		} else {
        			flag = true;
        		}
				
				if(flag) {
					_dsItems.add(ds);
				}
        	}
        	
            xformEncoder.fixCombo("datasource", _dsItems);	
        } catch (Exception e) {
        }
	}
	
	/**
	 * 通过uToken令牌，检查指定资源是否被授权给第三方系统访问。
	 */
	public static boolean checkAPIToken(IResource r, String uName, String uToken) {
		ILoginService loginService = (ILoginService) Global.getBean("LoginService");
		
		// 分别按资源的ID及名称 + uName 搜索一遍令牌
		List<String> tokenList = loginService.searchTokes(uName, r.getId().toString(), r.getResourceType()); 
		tokenList.addAll( loginService.searchTokes(uName, r.getName(), r.getResourceType()) );
		
    	if( tokenList.contains(uToken) ) {
    		loginService.mockLogin(uName, uToken);
    		return true;
    	}
    	return false;
	}
	
	// 注：通过tssJS.ajax能自动过滤params里的空值，jQuery发送的ajax请求则不能
    public static Map<String, String> getRequestMap(HttpServletRequest request, boolean isGet) {
    	Map<String, String[]> parameterMap = request.getParameterMap();
    	boolean isJetty = "org.eclipse.jetty.server.Request".equals( request.getClass().getName() );
    	boolean hasUToken = parameterMap.containsKey("uToken");
    	
    	Map<String, String> requestMap = new LinkedHashMap<String, String>();
    	for(String key : parameterMap.keySet()) {
    		String[] values = parameterMap.get(key);
			String value = null;
			
			if( (isGet || hasUToken ) && !isJetty ) { // (tomcat7 or httpClientCall) and (not jetty) 
				try {
					value = new String(values[0].getBytes("ISO-8859-1"), "UTF-8"); 
				} catch (UnsupportedEncodingException e) {
				}
			}
			
			value = (String) EasyUtils.checkNull(value, values[0]);
			requestMap.put( key, value );
    	}
    	
    	requestMap.remove("_time");          // 剔除jsonp为防止url被浏览器缓存而加的时间戳参数
    	requestMap.remove("jsonpCallback"); // jsonp__x,其名也是唯一的
    	requestMap.remove("appCode");      // 其它系统向当前系统转发请求
    	requestMap.remove("ac");
    	
    	return requestMap;
    }
	
	public static String getExportPath() {
		String exportPath = FileHelper.ioTmpDir();
		try {
			exportPath = ParamManager.getValue(PX.ATTACH_PATH);
		} catch(Exception e) {
		}
		return exportPath;
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
	
    /** 为逗号分隔的每一个值加上单引号 */
    public static String insertSingleQuotes(String param) {
        if (param == null) return null;
        
        // 支持列表in查询，分隔符支持中英文逗号、中英文分号、空格、顿号
        param = param.replaceAll("，", ",").replaceAll(" ", ",").replaceAll("、", ",");
        if (param.contains(",")) {
            return "\'" + param.replaceAll(",", "\',\'") + "\'";

        } else {
            return "\'" + param + "\'";
        }
    }
    
    /** 导出数据到CSV文件中时，需要对字段值里包含的特殊符号进行处理，以便可以在Excel中正常打开 */
    public static String preCheatVal(Object value) {
    	if(value == null) {
			value = "";
		}
		String valueS = value.toString().replaceAll(",", "，"); // 导出时字段含英文逗号会错列
		valueS = valueS.replaceAll("\r\n", " ").replaceAll("\n", " "); // 替换掉换行符
		valueS = valueS.replaceAll("\"", ""); // 替换掉英文双引号
		return valueS; 
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
  			if( EasyUtils.isNullOrEmpty(value) ) return null;
  			
  			Date dateObj = DateUtil.parse(value);
  			if(dateObj == null) {
  				throw new BusinessException( EX.parse(EX.DM_01, value) );
  			} 
  			return new Timestamp(dateObj.getTime());
  		}
  		else {
  			return value.trim();
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
  	
	public static Map<String, Object> getFreemarkerDataMap() {
    	Map<String, Object> fmDataMap = new HashMap<String, Object>();
        
      	// 加入登陆用户的信息
      	fmDataMap.put(DMConstants.USER_ID, EasyUtils.obj2String(Environment.getUserId()));
      	fmDataMap.put(DMConstants.USER_CODE, Environment.getUserCode());
		fmDataMap.put(DMConstants.FROM_USER_ID, Environment.getUserInfo(DMConstants.FROM_USER_ID));
		
		// 加入域账号过滤录入表条件的标准片段
		fmDataMap.put(DMConstants.FILTER_BY_DOMAIN, "<#if USERS_OF_DOAMIN??> and creator in (${USERS_OF_DOAMIN}) </#if>");
		
		/* 往dataMap里放入Session里的用户权限、角色、组织等信息，作为宏代码解析。 */
    	try {
    		HttpSession session = Context.getRequestContext().getRequest().getSession();
    		Enumeration<String> keys = session.getAttributeNames();
    		while(keys.hasMoreElements()) {
    			String key = keys.nextElement();
    			fmDataMap.put(key, session.getAttribute(key).toString());
    		}
    	} catch(Exception e) { }
		
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
	
	// 当参数值大于500个字符时，截断参数
	public static String cutParams(String params) {
		if (params != null && params.length() > 500) {
            params = params.substring(0, 500);
        }
		return params;
	}
}
