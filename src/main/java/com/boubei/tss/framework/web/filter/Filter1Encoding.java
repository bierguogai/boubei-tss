package com.boubei.tss.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
 
/**
 * 设置Http request的字符集
 */
@WebFilter(filterName = "EncodingFilter",
	urlPatterns = {"/*"} , 
	initParams  = {@WebInitParam(name="encoding", value="UTF-8")}
)
public class Filter1Encoding implements Filter {
    
    private static final Log log = LogFactory.getLog(Filter1Encoding.class);

    protected String encoding;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if ( request.getCharacterEncoding() == null ) {
            if (this.encoding != null) {
                request.setCharacterEncoding(this.encoding);
            }
        }
        HttpServletResponse hsr = (HttpServletResponse) response;
        hsr.setHeader("Cache-Control", "No-Cache");

        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        log.info("EncodingFilter init! appCode=" + Config.getAttribute(PX.APPLICATION_CODE));
    }
    
    public void destroy() {
        this.encoding = null;
    }
}
