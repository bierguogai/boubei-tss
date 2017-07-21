package com.boubei.tss.dm.report;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.boubei.tss.EX;
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MacrocodeCompiler;

public class ReportQuery {
	
	static Logger log = Logger.getLogger(ReportQuery.class);
	
  	@SuppressWarnings("unchecked")
    public static SQLExcutor excute(Report report, Map<String, String> requestMap, int page, int pagesize) {
    	
		String paramsConfig = report.getParam();
		String reportScript = report.getScript();
		if( EasyUtils.isNullOrEmpty(reportScript) ) {
			return new SQLExcutor();
		}
          
		// 宏代码池
      	Map<String, Object> fmDataMap = DMUtil.getFreemarkerDataMap();
      	
		/* 先预解析，以判断request参数是否用做了宏代码。后续还将有一次解析，以支持宏嵌套。 
		 * eg： ${GetWHListByLogonUser} --> param里的script宏（... user_id = ${fromUserId}) ... ）
		 * 这里先把  ${GetWHListByLogonUser} 解析出来，下面会接着解析${fromUserId} */
      	reportScript = MacrocodeCompiler.runLoop(reportScript, fmDataMap, true); 
      	
      	// 加入所有request请求带的参数
      	fmDataMap.putAll(requestMap);
      	
      	// 过滤掉用于宏解析（ ${paramX} ）后的request参数
     	Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		
		if( !EasyUtils.isNullOrEmpty(paramsConfig) ) {
      		List<LinkedHashMap<Object, Object>> list;
      		try {  
      			paramsConfig = paramsConfig.replaceAll("'", "\"");
  				list = new ObjectMapper().readValue(paramsConfig, List.class);  
      	        
      	    } catch (Exception e) {  
      	        throw new BusinessException( EX.parse(EX.DM_15, report, e.getMessage()) );
      	    }  
      		
      		for(int i = 0; i < list.size(); i++) {
  	        	LinkedHashMap<Object, Object> map = list.get(i);
  	        	
  	        	int index = i + 1;
  	        	String paramKy = "param" + index;
  	        	String paramValue = requestMap.get(paramKy);
				if ( EasyUtils.isNullOrEmpty(paramValue) ) {
					if( "false".equals(map.get("nullable")) ) {
						throw new BusinessException(EX.parse(EX.DM_20, map.get("label")));
					}
					continue;
				}
				
				// 对paramValue进行检测，防止SQL注入
				paramValue = DMUtil.checkSQLInject( paramValue.trim() );
				
				/* 
				 * 判断是否作为宏定义用于freemarker的模板解析
				 * 如一些只用于多级下拉联动的参数，可能并不用于FM(script+参数）
				 */
				Object ignore = EasyUtils.checkNull(map.get("isMacrocode"), map.get("ignore")); 
				if ( reportScript.indexOf("${" + paramKy + "}") > 0 ) {
					ignore = "true";		
				}
				else if(reportScript.indexOf("if " + paramKy) > 0
						&& reportScript.indexOf("if " + paramKy + "??") < 0) {
					// <#if param1==1> or <#elseif param1==1>
					// eg1: <#if param1==1> group by week </#if>  --> is macrocode: true 
					// eg2: <#if param1??> createTime > ? </#if>  --> is macrocode: false
					ignore = "true";
				}
				
				// 隐藏类型的参数
				Object paramType = map.get("type");
				if("hidden".equals(paramType)) {
					ignore = "true";
		  		}
				
				// 将相对时间解析成绝对时间（today - 2 --> 2014-7-20）
				if (Pattern.compile("^today[\\s]*-[\\s]*\\d{1,4}").matcher(paramValue).matches()) {
					int deltaDays = Integer.parseInt(paramValue.split("-")[1].trim());
					Date today = DateUtil.noHMS(new Date());
					paramValue = DateUtil.format(DateUtil.subDays(today, deltaDays));
				} 
				
				// 将相对时间解析成绝对时间（today + 2 --> 2014-7-24）
				if (Pattern.compile("^today[\\s]*\\+[\\s]*\\d{1,4}").matcher(paramValue).matches()) {
					int deltaDays = Integer.parseInt(paramValue.split("\\+")[1].trim());
					Date today = DateUtil.noHMS(new Date());
					paramValue = DateUtil.format(DateUtil.addDays(today, deltaDays));
				} 

				// 处理in查询的条件值，为每个项加上单引号
				if (reportScript.indexOf("in (${" + paramKy + "})") > 0 ||
						reportScript.indexOf("IN (${" + paramKy + "})") > 0) {
					
					paramValue = DMUtil.insertSingleQuotes(paramValue); 
				}
				// 判断参数是否只用于freemarker解析
				else if ( !"true".equals(ignore) ) {
					Object value = DMUtil.preTreatValue(paramValue, paramType);
					paramsMap.put(paramsMap.size() + 1, value);
				}
				
				fmDataMap.put(paramKy, paramValue);
  	        }
      	}
      	
        // 结合 requestMap 进行 freemarker解析 sql，允许指定sql预处理类。
      	fmDataMap.put("report.info", report.toString()); // 用于解析出错时定位report
      	reportScript = DMUtil.customizeParse(reportScript, fmDataMap);
          
		SQLExcutor excutor = new SQLExcutor();
		String datasource = report.getDatasource();
		try {
			excutor.excuteQuery(reportScript, paramsMap, page, pagesize, datasource);
		} catch (Exception e) {
			String exMsg = e.getMessage();
			log.error( report + exMsg + ", params: " + requestMap + ", visitor: " + Environment.getUserName());
			throw new BusinessException(exMsg);
		}

		return excutor;
  	}

}
