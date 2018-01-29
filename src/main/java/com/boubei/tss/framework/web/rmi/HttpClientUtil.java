package com.boubei.tss.framework.web.rmi;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.BusinessServletException;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.web.XHttpServletRequest;
import com.boubei.tss.util.EasyUtils;

public class HttpClientUtil {
	
	static Logger log = Logger.getLogger(HttpClientUtil.class);
 
    /** 请求超时时间（毫秒数）  */
    public static final int HTTP_REQUEST_TIMEOUT = 30000; // 30秒

    /** Post请求时，Method值  */
    public static final String POST_METHOD = "POST";
    
    /**  HTTP请求类型参数名  */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * 初始化HttpClient对象
     */
    public static HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        
        // 设置Cookie处理策略，RFC_2109是支持较普遍的一个，还有其他cookie协议
        client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        
        // 设置超时时间
        MultiThreadedHttpConnectionManager hcm = new MultiThreadedHttpConnectionManager();
        hcm.getParams().setConnectionTimeout(HTTP_REQUEST_TIMEOUT);
        client.setHttpConnectionManager(hcm);
        return client;
    }
    
    /**
     * <p>
     * 初始化HttpClient对象，同时设置转发应用的Cookie信息。
     * 将当前请求中的cookie信息（除去sessionId cookie 和 token cookie）设置到新的请求中来
     * </p>
     * @param targetAS 转发的目标应用
     * @return
     */
    public static HttpClient getHttpClient(AppServer targetAS) {
    	HttpState initialState = new HttpState();
        HttpServletRequest request = Context.getRequestContext().getRequest();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        cookies = (javax.servlet.http.Cookie[]) EasyUtils.checkNull(cookies, new javax.servlet.http.Cookie[] {});
        
        // 设置转发Cookies信息
        AppServer currentAS = Context.getApplicationContext().getCurrentAppServer();
        for (javax.servlet.http.Cookie cookie : request.getCookies()) {
            String cookieName = cookie.getName();
            if (cookieName.equals(currentAS.getSessionIdName()) 
            		|| cookieName.equals(RequestContext.USER_TOKEN)) {
                continue;
            }
            
            // 保存当前应用以外的sessionId信息的cookie一般是以其应用Code命名的，当前应用的则以JSESSIONID命名
            if (cookieName.equals(targetAS.getCode())) { 
                cookieName = targetAS.getSessionIdName();
            }
            String domain = targetAS.getDomain();
			String path   = targetAS.getPath();
			Cookie apacheCookie = new Cookie(domain, cookieName, cookie.getValue(), path, null, request.isSecure());
            initialState.addCookie(apacheCookie);
        }
        
        HttpClient client = getHttpClient();
        client.setState(initialState);
        
        return client;
    }

    /**
     * <p>
     * 初始化HttpMethod对象。
     * 转发的时候把第一次转发过来header头带的appCode值去掉，理论上不会有二次转发的可能。
     * </p>
	 * @param appServer
	 * @return
	 * @throws IOException
	 * @throws BusinessServletException
	 */
	public static HttpMethod getHttpMethod(AppServer appServer) throws IOException, BusinessServletException {
		RequestContext requestContext = Context.getRequestContext();
        HttpServletRequest request = requestContext.getRequest();
        
        // 并初始化QueryString参数
        String realPath = requestContext.getRealPath();
        String queryString = null;
        if ( EasyUtils.isNullOrEmpty(realPath) ) {
            realPath = request.getServletPath();
            queryString = request.getQueryString();
        } else {
            realPath = URLDecoder.decode(realPath, "UTF-8");
            int index = realPath.indexOf("?");
            if (index > -1) {
                queryString = realPath.substring(index + 1);
                realPath    = realPath.substring(0, index);
            }
        }
        String newurl = appServer.getBaseURL() + realPath;
        log.debug("真实远程地址为：" + newurl);
        
        // 根据请求类型创建请求代理
        HttpMethod httpMethod;
        if (POST_METHOD.equalsIgnoreCase(request.getMethod())) { // POST 
            httpMethod = new PostMethod(newurl);
            
            // 设置请求内容，将原请求中的数据流转给新的请求
            InputStreamRequestEntity requestEntity = new InputStreamRequestEntity(request.getInputStream(), request.getContentType());
			((PostMethod)httpMethod).setRequestEntity(requestEntity);
        } 
        else { // GET
            httpMethod = new GetMethod(newurl);
        }
        
        // 设置QueryString参数
        if ( !EasyUtils.isNullOrEmpty(queryString) ) {
            httpMethod.setQueryString(queryString.replaceAll("appCode=", "ac=")); // 防止url的appCode导致Filter5HttpProxy自我死循环
        }
        
        // 设置请求头参数
        Enumeration<String> headerNames = request.getHeaderNames();
        List<String> igonreHeaders = Arrays.asList("Connection", "Cache-Control", "Accept-Encoding"); // 防止中文乱码
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            
            // 转发的时候去掉header中 cookie（下面单独转发） 和 appCode（转发后不再需要）
            if ("cookie".equalsIgnoreCase(name) || "appCode".equalsIgnoreCase(name))  continue;
            if (igonreHeaders.contains(name)) continue;
            
            httpMethod.setRequestHeader(name, value);
            log.debug(name + "=" + value);
        }
        httpMethod.setRequestHeader("Accept-Encoding", "UTF-8");
        
        // 设置用户令牌
        if (Context.isOnline()){
            httpMethod.setRequestHeader(RequestContext.USER_TOKEN, Context.getToken());
        }
        
        // 设置客户端IP
        httpMethod.setRequestHeader(RequestContext.USER_CLIENT_IP, requestContext.getClientIp());
        return httpMethod;
    }
	
    /**
     * 处理二次转发请求（request2）转发成功后 返回的Cookie信息，将这些cookie设置到初始的请求和响应里
     * @param cookies 
     *            注：是org.apache.commons.httpclient.Cookie
     * @param targetAppServer
     */
    public static void transmitReturnCookies(org.apache.commons.httpclient.Cookie[] cookies, AppServer targetAppServer) {
        RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) return;
        
        XHttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = Context.getResponse();
        if (response == null || request == null) return;
        
        // 转发返回Cookies
        for (int i = 0; i < cookies.length; i++) {
            String cookieName = cookies[i].getName();
            
            //如果当前应用本身的cookie，则无需转发
            if (cookieName.equals(Context.getApplicationContext().getCurrentAppCode())) continue; 
            
            if (cookieName.equals(targetAppServer.getSessionIdName())) {
                cookieName = targetAppServer.getCode();
            }
            
            String cpath = request.getContextPath();
            javax.servlet.http.Cookie cookie = createCookie(cookieName, cookies[i].getValue(), cpath);
            cookie.setMaxAge(-1);
            cookie.setSecure(request.isSecure());
            
            if (response.isCommitted()) {
                response.addCookie(cookie);
            }
            
            // 同时也添加到request中，以用于二次、三次的远程接口调用
            request.addCookie(cookie); 
        }
    }
    
    
    /**
     * TODO tomcat7下如果往cookie写入中文，会出现异常：Control character in cookie value or attribute.
	 * 解决办法，生成Cookie时，对value编码：new Cookie(name, URLEncoder.encode(value, "UTF-8")); 
	 * 			    取出Cookie时再解码：URLDecoder.decode(cookies[i].getName(),"utf-8")
     */
    public static javax.servlet.http.Cookie createCookie(String name, String value, String path) {
    	javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
        cookie.setPath(path);
        return cookie;
    }
}