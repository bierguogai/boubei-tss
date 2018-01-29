package com.boubei.tss.framework;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.util.EasyUtils;

/**
 * 设置一个安全级别的配置参数，依据相应级别来判断是否要进行XSS清理等安全操作
 * 注：当编辑门户组件时, 需要降低安全级别
 */
public class SecurityUtil {
    
	public static String SECURITY_LEVEL = "security.level";
	public static int SECURITY_LEVELS[] = {0, 1, 2, 3, 4, 5, 6};
	public static int LEVEL_1 = SECURITY_LEVELS[1];
	public static int LEVEL_2 = SECURITY_LEVELS[2];
	public static int LEVEL_3 = SECURITY_LEVELS[3];
	public static int LEVEL_4 = SECURITY_LEVELS[4]; // safe
	public static int LEVEL_5 = SECURITY_LEVELS[5]; 
	public static int LEVEL_6 = SECURITY_LEVELS[6]; // hard
	
	public static int getLevel() {
		try {
			return EasyUtils.obj2Int( Config.getAttribute(SECURITY_LEVEL) );
		} catch(Exception e) {
			return SECURITY_LEVELS[0];
		}
	}
	
	public static boolean isSafeMode() {
		return getLevel() >= LEVEL_4;
	}
	
	public static boolean isHardMode() {
		return getLevel() >= LEVEL_6;
	}
	
	public static String fuckXSS(String value, HttpServletRequest request) {
        if( !isSafeMode() || Environment.isAdmin() ) {
        	return value;
        }
        
        return _fuckXSS(value, request);
    }
	
    /**
     * 防止XSS攻击
     */
    public static String _fuckXSS(String value, HttpServletRequest request) {
        if (value == null)  return value;
     
        // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
        // avoid encoded attacks.
        // value = ESAPI.encoder().canonicalize(value);
    	
        // Avoid null characters
        value = value.replaceAll("", "");
        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid anything in a src="http://www.yihaomen.com/article/java/..." type of e­xpression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");
        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid eval(...) e­xpressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid e­xpression(...) e­xpressions
        scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid javascript:... e­xpressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid vbscript:... e­xpressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid onload= e­xpressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        // Avoid onerror= e­xpressions
        scriptPattern = Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        
        return value;
    }
}
