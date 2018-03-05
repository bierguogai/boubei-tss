/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.BusinessServletException;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.web.display.xmlhttp.XmlHttpDecoder;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.InfoEncoder;
import com.boubei.tss.util.XMLDocUtil;

/**
 * <p> XmlHttp请求数据解码 </p>
 * 
 * 因Ajax发送的post的请求中 name/value 值对无法设置到request.paramters中，
 * 故需要本filter将ajax发送的请求参数拦截放到进一步封装后request对象（request.paramters 是一个不可被手动add值的collection，unmodify）中。
 * 
 * <pre>
 * 将XmlHttp的XML数据解析成Request中的属性。
 * 传入的XMLHttp的XML数据格式，如：
 * <Request><Param><Name><![CDATA[resourceId]]></Name><Value><![CDATA[2]]></Value></Param></Request>，
 * 需要解析成 request.put("resourceId", 2);
 * </pre>
 */
//@WebFilter(filterName = "Filter6XmlHttpDecode", urlPatterns = {"/*"} )
public class Filter6XmlHttpDecode implements Filter {
    
    Logger log = Logger.getLogger(Filter6XmlHttpDecode.class);
 
    public void init(FilterConfig arg0) throws ServletException {
        log.info("Filter6XmlHttpDecode init in " + Config.getAttribute(PX.APPLICATION_CODE));
    }

    /**
     * 通过可重写的Request对象，将xml数据流解析成名值对，重写入Request对象中。
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request; 
            RequestContext requestContext = Context.getRequestContext();
            if (requestContext != null && requestContext.isXmlhttpRequest()) {
                Document doc = null;
                ServletInputStream is = null;
                String requestBody = null;
                try {
                    requestBody = getRequestBody(is = request.getInputStream());
                    
                    // 如果请求的参数数据做了加密，则先解开
                    String encodeKey = requestContext.getValue("encodeKey");
                    if( !EasyUtils.isNullOrEmpty(encodeKey) ) {
                    	int key = EasyUtils.obj2Int(encodeKey);
                    	requestBody = InfoEncoder.simpleDecode(requestBody, key);
                    }
                    
                    doc = XMLDocUtil.dataXml2Doc(requestBody);
                    
                } catch (Exception e) {
                    throw new BusinessException("Get request body error. requestBody = " + requestBody + ", " + e.getMessage());
                } finally {
                    try { is.close(); } catch (Exception e) { } 
                }
                
                if(doc != null) {
                	httpRequest = XmlHttpDecoder.decode(doc.getRootElement(), httpRequest); 
                }
            }
            
            chain.doFilter(httpRequest, response);
        } 
        catch (Exception e) {
            throw new BusinessServletException(e);
        }
    }
    
    private static String getRequestBody(ServletInputStream sis) throws IOException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bLen = 0;
        byte[] buffer = new byte[8 * 1024];
        while ((bLen = sis.read(buffer)) > 0) {
            baos.write(buffer, 0, bLen);
        }
        return new String(baos.toByteArray(), "UTF-8");
    } 
 
    public void destroy() {
        
    }
}
