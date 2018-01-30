/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.wrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.web.XHttpServletRequest;

/**
 * <p>
 * XHttpServletRequestWrapper.java
 * 代理请求对象（可自定义或修改请求参数）
 * </p>
 *
 */
public class XHttpServletRequestWrapper extends HttpServletRequestWrapper 
				implements XHttpServletRequest {

	private Map<String, String[]> params = new HashMap<String, String[]>();

	private Map<String, String> headers = new HashMap<String, String>();

	private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

	private String servletPath = null;

	/**
	 * <p>
	 * 获取可复写属性和参数等增强功能的Request对象
	 * </p>
	 * @param request
	 * @return
	 */
	public static XHttpServletRequest wrapRequest(HttpServletRequest request) {
		if (request instanceof XHttpServletRequest) {
			return (XHttpServletRequest) request;
		}
		return new XHttpServletRequestWrapper(request);
	}
 
	private XHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	
	public void addParameter(String name, String value) {
		params.put(name, new String[] { value });
	}

	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.putAll(super.getParameterMap());
		map.putAll(params);
		return map;
	}

	public Enumeration<String> getParameterNames() {
		return new Enumerator(getParameterMap().keySet());
	}
	
	// 获得如checkbox类（名字相同，但值有多个）的数据
	public String[] getParameterValues(String name) {
		String[] values = getParameterMap().get(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = SecurityUtil.fuckXSS( values[i], this );
        }
        return encodedValues;
	}
	
	// 取指定名字参数的值（单值）
    public String getParameter(String name) {
        String[] value = params.get(name);
        if (value != null && value.length > 0) {
            return value[0];
        }
        return SecurityUtil.fuckXSS( super.getParameter(name), this );
    }

	public String getHeader(String name) {
		String value = headers.get(name);
		if ( value != null ) {
			return value;
		}
		return SecurityUtil.fuckXSS( super.getHeader(name), this );
	}
	
	public Enumeration<String> getHeaderNames() {
	    Set<String> set = new HashSet<String>();
	    
		Enumeration<String> e = super.getHeaderNames();
		while (e.hasMoreElements()) {
			set.add(e.nextElement());
		}
		
		set.addAll(headers.keySet());
		
		return new Enumerator(set);
	}

	public Enumeration<String> getHeaders(String name) {
		Enumeration<String> e = super.getHeaders(name);
		Set<String> set = new HashSet<String>();
		String value = headers.get(name);
		if (value != null) {
		    set.add(value);
		} 
		else {
			while (e.hasMoreElements()) {
				set.add(e.nextElement());
			}
		}
		return new Enumerator(set);
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}
 
	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}
 
	public String getServletPath() {
		if (this.servletPath != null) {
			return this.servletPath;
		}
		return super.getServletPath();
	}

	/**
	 * <p>
	 * 获取Cookie。
	 * cookie如果重名，优先取父目录下的cookie，遍历superCookies时候需要从后往前遍历，
     *      因为父目录下cookie相比子目录的同名cookie，在数组的靠前位置。
     * </p>
	 * @return
	 * @see javax.servlet.http.HttpServletRequestWrapper#getCookies()
	 */
	public Cookie[] getCookies() {
		Cookie[] superCookies = super.getCookies();
		Map<String, Cookie> cookiesMap = new HashMap<String, Cookie>();
		if (superCookies != null) {
			for (int i = superCookies.length - 1; i >= 0; i--) {
				Cookie cookie = superCookies[i];
				
				// 过滤一些不正常的值，比如%22打头的token
				String value = cookie.getValue();
				if( value.startsWith("%22") && !value.endsWith("%22") ) continue;
				
				cookiesMap.put(cookie.getName(), cookie);
			}
		}
		
		cookiesMap.putAll(cookies);
		
		if (cookiesMap.isEmpty()) return null;
		
		Cookie[] allCookiesArray = new Cookie[cookiesMap.size()];
		int i = 0;
		for (Entry<String, Cookie> entry : cookiesMap.entrySet()) {
			allCookiesArray[i++] = entry.getValue();
		}
		return allCookiesArray;
	}

	/**
	 * <p>  设置cookie </p>
	 *
	 * @param cookie
	 */
	public void addCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
	}

	private static class Enumerator implements Enumeration<String> {

		private Iterator<String> iter = null;

		public Enumerator(Set<String> set) {
			iter = set.iterator();
		}
 
		public boolean hasMoreElements() {
			return iter.hasNext();
		}
 
		public String nextElement() {
			return iter.next();
		}
	}
}
