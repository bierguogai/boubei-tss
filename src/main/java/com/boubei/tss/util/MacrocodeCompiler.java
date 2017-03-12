/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 宏代码编译器
 * </p>
 */
public class MacrocodeCompiler {
    /**
     * 宏代码标志
     */
    public static char MACROCODE_SYMBOL = '$';

    /**
     * 变量标志
     */
    public static char VARIABLE_SYMBOL = '#';

    /**
     * 宏代码名称起始标记：必须跟在宏代码、变量标志后有效
     */
    public static char NAME_START_SYMBOL = '{';

    /**
     * 宏代码名称结束标记：起始标记后第一个此符号为配对的结束标记
     */
    public static char NAME_END_SYMBOL = '}';

    /**
     * 空格字符串，用于变量初始化
     */
    protected static char NULL = ' ';

    /**
     * <p>
     * 将包含宏代码、变量等的脚本字符串解析成字符串片段后放到segment列表中<br>
     * 以宏代码：${}、#{}分开，风别存放到List对象中<br>
     * 如下：<br>
     * 普通代码<br>
     * ${XXXX}<br>
     * 普通代码<br>
     * #{YYYY}<br>
     * 普通代码
     * </p>
     * 
     * @param code
     *            String 包含宏代码、变量等的字符串 比如 123${xxx}456
     * @return List 
     *            代码片段列表 List{123, ${xxx}, 456}
     */
    private static List<String> compile(String code) {
        if (code == null) {
            return null;
        }
        
        List<String> segment = new ArrayList<String>();
        char curChar = NULL;
        char preChar = NULL;
        int startIndex = 0;
        int endIndex = 0;
        boolean isMacro = false;
        for (int curIndex = 0;curIndex < code.length(); curIndex++) {
            preChar = curChar;
            curChar = code.charAt(curIndex);
            if (isMacro) {
                // 检测宏代码结束标记 curChar = '}'
                if ( isEndSymbol(curChar) ) {
                    isMacro = false;
                    endIndex = curIndex + 1;  // 连 “}” 一块截
                    if (endIndex > startIndex) {
                        segment.add(code.substring(startIndex, endIndex)); // 将宏代码、变量截取出来
                        startIndex = endIndex;
                    }
                }
            } else {
                // 检测宏代码起始标记 preChar + curChar = "&{" or "#{"
                if (isStartSymbol(curChar) && isMacroStartTag(preChar) ) {
                    isMacro = true;
                    endIndex = curIndex - 1;  // 截取时回退两位，剔除 “${” 或 “#{”
                    if (endIndex > startIndex) {
                        segment.add(code.substring(startIndex, endIndex)); // 将非宏代码、变量截取出来
                        startIndex = endIndex;
                    }
                }
            }
        }
        if (startIndex < code.length()) {
            segment.add(code.substring(startIndex, code.length()));
        }
        return segment;
    }

    /**
     * <p>
     * 将包含宏代码、变量代码的字符串解释其代码含义后，返回执行结果字符串 如下：<br>
     * ${portlet}:PortletNode对象<br>
     * #{title}:Portlet名称
     * </p>
     * 
     * @param script
     *            String 包含宏代码、变量等的字符串
     * @param macrocodes
     *            Map 宏代码、值对应表
     *  @param ignoreNull
     *  		    是否忽略值为空的宏，是的话不解析该宏
     * @return String 
     * 			   宏代码、变量执行后的字符串
     */
    public static String run(String script, Map<String, ? extends Object> macrocodes, boolean ignoreNull) {
    	if(macrocodes == null) return script;
    	
    	List<String> segment = compile(script);
        if (segment == null) return ""; 
        
        // 对macrocodes进行预处理，没有以$、#打头的Key，改成以${key}
        Map<String, Object> copy = new HashMap<String, Object>();
        for(String key : macrocodes.keySet()) {
        	char firstChar = key.charAt(0);
        	if(isMacroStartTag(firstChar)) {
        		copy.put(key, macrocodes.get(key));
        	}
        	else {
        		copy.put(createMacroCode(key), macrocodes.get(key));
        	}
        }
        
        StringBuffer sb = new StringBuffer();
        for(String item : segment) {
        	item = EasyUtils.obj2String(item);
            
            if (item.length() > 3
            		&& isMacroStartTag(item.charAt(0)) 
            		&& isStartSymbol(item.charAt(1)) 
            		&& isEndSymbol(item.charAt(item.length() - 1)) ) {
                
                Object macro = copy.get(item);
                if (macro != null) {
                    sb.append(macro);
                }  
                else {
                	if( ignoreNull ) {
                		sb.append(item); // 放回去，不解析
                	}
                }
            } 
            else {
                sb.append(item);
            }
        }
        return sb.toString();
    }
    
    public static String run(String script, Map<String, ? extends Object> macrocodes) {
        return run(script, macrocodes, false);
    }
    
    /**
     * 支持无限宏嵌套，直到所有嵌套都被解析完成。
     * TODO 谨防闭环式死循环出现，如果循环100次还没结束，则跳出。
     */
    public static String runLoop(String script, Map<String, ? extends Object> macrocodes, boolean ignoreNull) {
    	String current = script, pre = null;
    	int count = 0;
    	while(current != null && !current.equals(pre) && count++ < 100) {
    		pre = current;
    		current = run(current, macrocodes, ignoreNull);
    	}
    	
    	return current;
    }
    
    public static String runLoop(String script, Map<String, ? extends Object> macrocodes) {
    	return runLoop(script, macrocodes, true);
    }
    
    /**  是否宏代码标记 '$' or '#'  */
    private static boolean isMacroStartTag(char c){
        return c == MACROCODE_SYMBOL || c == VARIABLE_SYMBOL;
    }
    
    /** 是否宏定义开始标记 '{'  */
    private static boolean isStartSymbol(char c){
        return c == NAME_START_SYMBOL ;
    }
    /** 是否宏定义结束标记 '}'  */
    private static boolean isEndSymbol(char c){
        return c == NAME_END_SYMBOL;
    }
    
    /**
     * 生成 ${key}
     * @param key
     * @return ${key}
     */
    public static String createMacroCode(String key){
        return MACROCODE_SYMBOL + (NAME_START_SYMBOL + key) + NAME_END_SYMBOL;
    }
    
    /**
     * 生成 #{key}
     * @param key
     * @return  #{key}
     */
    public static String createVariable(String key){
        return VARIABLE_SYMBOL + (NAME_START_SYMBOL + key) + NAME_END_SYMBOL;
    }
}
