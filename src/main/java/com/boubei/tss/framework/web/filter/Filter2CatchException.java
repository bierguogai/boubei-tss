package com.boubei.tss.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.sso.context.Context;

/**
 * <p> 异常过滤器 </p>
 * 
 * 捕获异常并转换成XML输出
 * 
 */
@WebFilter(filterName = "CatchExceptionFilter", urlPatterns = {"/*"})
public class Filter2CatchException implements Filter {
	
    Logger log = Logger.getLogger(Filter2CatchException.class);
 
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
    	
        try {
            chain.doFilter(request, response);
        } 
        catch (Exception e) {
            ExceptionEncoder.encodeException(response, e); // 捕获异常并转换成XML输出
        } 
        finally {
        	// 出现异常后，Filter3Context将没机会销毁Context，需要在此销毁Context
        	if( Context.getToken() != null ) {
        		Context.destroy(); 
    			Context.setToken(null);  /* 在web环境下需要每次调用结束后重置当前线程的Token值，以免串号 */
        	}
        }
    }
 
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CatchExceptionFilter init! appCode=" + Config.getAttribute(PX.APPLICATION_CODE));
    }
    
    public void destroy() {
    }
}
