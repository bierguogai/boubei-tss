package com.boubei.tss.framework.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.BusinessServletException;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;

/**
 * <p> 请求上下文对象（RequestContext）相关过滤器  </p>
 *
 * <pre>
 * 初始化及销毁上下文对象（UserContext、RequestContext等）。 
 * 过滤器顺序：本filter应该配置AutoLoginFilter之前，
 *           后者需要的类似用户名、密码、验证方式、token等信息需要从Context中获取。
 * 
 * 类似在线用户库远程调用可以跳过本过滤器，以防TSS以外平台系统session注销时
 * 调用TSS配置的/remote/OnlineUserService，又会在TSS的UM里产生一个新的session。
 * </pre>
 */
@WebFilter(filterName = "ContextFilter", 
		urlPatterns = {"/*"}, 
		initParams  = {@WebInitParam(name="ignorePaths", value="/remote/OnlineUserService,js,htm,html,jpg,png,gif,ico,css,xml,swf")}
)
public class Filter3Context implements Filter {
	
	private static Logger log = Logger.getLogger(Filter3Context.class);

    private Set<String> ignorePaths = new HashSet<String>();
     
    public void init(FilterConfig filterConfig) throws ServletException {
        String paths = filterConfig.getInitParameter("ignorePaths");
        if (paths != null) {
        	ignorePaths.addAll(Arrays.asList(paths.split(",")));
        }
        log.info("ContextFilter init! appCode=" + Context.getApplicationContext().getCurrentAppCode() + ", ignorePaths=" + paths);
    }
 
    public void destroy() {
    	ignorePaths = null;
    }
 
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        
        String servletPath = RequestContext.getServletPath((HttpServletRequest)request);
        for(String ignore : ignorePaths) {
            if(servletPath.toLowerCase().endsWith(ignore.toLowerCase())) {
                chain.doFilter(request, response);
                return;
            }
        }
        
        log.debug("current request path: " + servletPath);
        
		try {
			Context.initRequestContext((HttpServletRequest) request);
			Context.setResponse((HttpServletResponse) response); 
            
			chain.doFilter(request, Context.getResponse()); // 使用转换后的response
			
			/* 请求结束后销毁Context；如果出现了异常，Filter2CatchException里会进行销毁操作 */
			Context.destroy(); 
		} 
		catch (BusinessServletException e) {
			throw e;
		}
		catch (Exception e) {
			throw new BusinessServletException(e);
		} 
	}
}
