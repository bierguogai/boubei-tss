package com.boubei.tss.dm.data.sqlquery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;


public class SOUtil {
	
	public static Map<String, Object> getProperties(AbstractSO so, String...ignore) {
		Set<String> ignoreNames = new HashSet<String>();
		if(ignore != null && ignore.length > 0) {
			ignoreNames.addAll(Arrays.asList(ignore));
		}
		   
		Map<String, Object> properties = BeanUtil.getProperties(so, ignoreNames);
		Map<String, Object> noNullProperties = new HashMap<String, Object>();
		for(String key : properties.keySet()) {
            Object value = properties.get(key);
            if (value == null) {
                continue;
            }
            if (key.endsWith("Codes") && EasyUtils.isNullOrEmpty(value.toString())) {
                continue;
            }

            if (key.endsWith("Codes")) {
                value = insertSingleQuotes(value.toString());
            }
            noNullProperties.put(key, value);
		}
				
		return noNullProperties;
	}
	
	public static Map<Integer, Object> generateQueryParametersMap(AbstractSO so) {
		Map<String, Object> properties = getProperties(so);
		Map<Integer, Object> parametersMap = new HashMap<Integer, Object>();
		
		String[] parameterNames = so.getParameterNames();
		if(parameterNames != null) {
			for(String parameterName : parameterNames) {
				Object value = properties.get(parameterName);
				if(value != null) {
					parametersMap.put(parametersMap.size() + 1, value);
				}
			}
		}
		
		return parametersMap;
	}
	
    /**
     * 为逗号分隔的每一个值加上单引号
     */
    public static String insertSingleQuotes(String param) {
        if (param == null) {
            return null;
        }
        
        // 支持列表in查询，分隔符支持中英文逗号、中英文分号、空格、顿号
        param = param.replaceAll("，", ",").replaceAll(" ", ",").replaceAll("、", ",");
        if (param.contains(",")) {
            return "\'" + param.replaceAll(",", "\',\'") + "\'";

        } else {
            return "\'" + param + "\'";
        }
    }
	
    public static String freemarkerParse(String script, AbstractSO so) {
    	return DMUtil.freemarkerParse(script, SOUtil.getProperties(so));
    }
}
