/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 可重写HttpServletRequest对象接口
 * </p>
 */
public interface XHttpServletRequest extends HttpServletRequest {
	
	/**
	 * <p> 添加参数值 </p>
	 * @param name
	 * @param value
	 */
	void addParameter(String name, String value);
	
	/**
	 * <p> 设置单值请求头属性 </p>
	 * @param name
	 * @param value
	 */
	void setHeader(String name, String value);
	
	/**
	 * <p> 设置Sevlet路径 </p>
	 * @param servletPath
	 */
	void setServletPath(String servletPath);
	
	/**
	 * <p> 添加Cookie  </p>
	 * @param cookie
	 */
	void addCookie(Cookie cookie);
}

