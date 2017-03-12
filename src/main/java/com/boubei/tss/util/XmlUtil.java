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

/**
 * <p> XmlUtil.java </p>
 * <p>
 * XML相关工具类
 * </p>
 */
public class XmlUtil {
    
    /**
     * 剔除XML文件中不合法的字符，像 "" 这种字符存在会导致: <br>
     * org.xml.sax.SAXParseException: An invalid XML character (Unicode: 0xb) was found in the element content of the document. <br>
     * 这样的异常出现，而用IE预览XML时却完全正常。<br>
     * 
     * @param in
     * @return
     */
    public static String stripNonValidXMLCharacters(String in) {
        if ( EasyUtils.isNullOrEmpty(in) ) return ""; 
        
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); 
            if ((current == 0x9) ||(current == 0xA) || (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }
    
    /**
     * 将一个对象转换成xml支持的格式.
     * 
     * @param obj
     * @return
     */
    public static String toFormXml(Object obj) {
        if (obj == null) {
            return "";
        }
        
        String message = obj.toString();
        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        return (result.toString());
    }
    
    /**
     * 替换XML数据属性值中的特殊字符，包括<、>、"、&等
     * 
     * @param string
     * @return
     */
    public static String replaceXMLPropertyValue(String xmlStr) {
        return xmlStr.replaceAll("&", "&amp;")
                     .replaceAll("\"", "&quot;")
                     .replaceAll("<", "&lt;")
                     .replaceAll(">", "&gt;");
    }
}

