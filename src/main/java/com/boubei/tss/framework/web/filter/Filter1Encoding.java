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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.util.EasyUtils;
 
/**
 * 设置Http request的字符集

@WebFilter(filterName = "Filter1Encoding",
	urlPatterns = {"/*"} , 
	initParams  = {@WebInitParam(name="encoding", value="UTF-8")}
)
 */
public class Filter1Encoding implements Filter {
    
    private static final Log log = LogFactory.getLog(Filter1Encoding.class);

    protected String encoding;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if ( request.getCharacterEncoding() == null ) {
        	request.setCharacterEncoding(this.encoding);
        }
        HttpServletResponse hsr = (HttpServletResponse) response;
        hsr.setHeader("Cache-Control", "No-Cache");

        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        this.encoding = (String) EasyUtils.checkNull(this.encoding, "UTF-8");
        
        log.info("Filter1Encoding init in" + Config.getAttribute(PX.APPLICATION_CODE));
    }
    
    public void destroy() {
        this.encoding = null;
    }
}
