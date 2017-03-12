/* ==================================================================   
 * Created [2009-4-27 下午11:32:55] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.framework.web.wrapper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 * 防重复的Http请求响应对象：不能添加同名Cookie信息。
 * </p>
 * <p>
 * 比如：合并http请求的情况，合并后的http请求只需返回一个response响应即可。
 * 
 * 小知识：浏览器客户端是以domain，path，name作为Cookie的唯一标识的，
 * 只要Name、Domain、Path中的任何一项不同，Cookie就是不同的，由此便产生了同名Cookie。
 * 如下面三个名为【token】的cookie是可以共存的    
 *   token=111111; expires=Sat, 23 May 2009 03:48:22 GMT; path=/; domain=.dny.com
     token=222222; expires=Sat, 23 May 2009 03:48:46 GMT; path=/; domain=.test.dny.com
     token=333333; expires=Sat, 23 May 2009 03:48:46 GMT; path=/test/; domain=.dny.com
 * 
 * </p>
 */
public class AvoidRepeatHttpServletResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * 已经添加的Cookie名集合
	 */
	private Set<String> cookies = new HashSet<String>();
 
	public AvoidRepeatHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	/**
	 * <p>
	 * 向Response中添加Cookie信息，同名Cookie不能重复添加
	 * </p>
	 */
	public void addCookie(Cookie cookie) {
		String cookieName = cookie.getName();
        if ( !cookies.contains(cookieName) ) {
			super.addCookie(cookie);
			cookies.add(cookieName);
		}
	}

}
