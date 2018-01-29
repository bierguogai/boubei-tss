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

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * 字符串工具类：
 * <li>字符串转码工具：将字符串的编码进行转换（默认为GBK转UTF-8）
 * </p>
 */
public class StringUtil {

    /**
     * <p>
     * 转换字符串编码方式
     * </p>
     * 
     * @param str
     * @param oldCharset
     * @param charset
     * @return
     */
    public static String convertCoding(String str, String oldCharset, String charset) {
        byte[] bytes;
        try {
            bytes = str.getBytes(oldCharset);
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException("字符串编码转换失败，不支持的编码方式：" + oldCharset, e1);
        }
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("字符串编码转换失败，不支持的编码方式：" + charset, e);
        }
    }

    /**
     * GBK格式字符串转UTF-8格式
     */
    public static String GBKToUTF8(String str) {
        return convertCoding(str, "GBK", "UTF-8");
    }

    /**
     * UTF-8格式字符串转GBK格式
     */
    public static String UTF8ToGBK(String str) {
        return convertCoding(str, "UTF-8", "GBK");
    }
    
	static Pattern cnPattern = Pattern.compile("[\u4e00-\u9fa5]");
	public static boolean hasCNChar(String str) {
		return cnPattern.matcher(str).find();
	}

}
