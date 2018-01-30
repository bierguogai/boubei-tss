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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

/** 
 * 应用地址(html类型)访问权限控制检测过滤器
 * 
 */
@WebFilter(filterName = "AccessingCheckFilter", urlPatterns = {"*.htm", "*.html"})
public class Filter7AccessingCheck implements Filter {
    
    Log log = LogFactory.getLog(Filter7AccessingCheck.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("AccessingCheckFilter init! appCode=" + Context.getApplicationContext().getCurrentAppCode());
    }
 
    public void destroy() { }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String servletPath = req.getServletPath();
        
        AccessingChecker checker = AccessingChecker.getInstance();
        
        HttpSession session = req.getSession(false);
        if(session == null) {
        	/* 如果链接配置了权限检测，则提示先进行登录 */
        	if( checker.isNeedPermission(servletPath) ) {
        		log.warn("试图匿名访问" + servletPath + "失败。"); // "您无权访问本页面，请先进行登录！"
        		((HttpServletResponse)response).sendRedirect(checker.get404URL());
        	}
        	
            chain.doFilter(request, response);
            return;
        }
        
        // 检测权限(无法使用Environment)
        List<Object> userRights = new ArrayList<Object>();
        try {
            List<?> userRightsInSession = (List<?>) session.getAttribute(SSOConstants.USER_RIGHTS);
            if(userRightsInSession != null) {
                userRights.addAll(userRightsInSession);
            }
            
            String operatorName = (String) session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION);
            if(operatorName != null) {
                userRights.add(operatorName);
            }
        } catch(Exception e) { }
        
        log.debug("权限检测开始：" + servletPath);
        if ( !checker.checkPermission(userRights, servletPath) ) {
            ((HttpServletResponse)response).sendRedirect( checker.get404URL() );
        }
        else {
        	log.debug("权限检测通过");
            chain.doFilter(request, response);
        }
    }
}

/** 
 * 权限检测器。
 * 
 * 权限配置文件格式： 
 * <rightConfig>
 *      <servlet name="param.htm" right="权限ID1,权限ID2,Admin"/>
 *      <servlet name="cache.htm" right="权限ID1,权限ID3,Admin,JonKing"/>
 * </rightConfig> 
 * 
 * @author Jon.King 2008/12/19 10:59:06
 */
class AccessingChecker {
	
	Logger log = Logger.getLogger(AccessingChecker.class);

    /** 权限配置文件 */
    private static final String RIGHT_CONFIG_FILE_NAME = "tss/rights.xml";
    
    static Map<String, Set<String>> rightsMap;
    
    static String THE_404_URL; 
    
    private AccessingChecker() {
    	rightsMap = parser();
    }
    
    private static AccessingChecker instance;
    
    public static AccessingChecker getInstance(){
        if(instance == null){
            instance = new AccessingChecker();
        }
        return instance;
    }
    
    boolean isNeedPermission(String servletPath) {
    	return rightsMap.containsKey(servletPath);
    }
        
    /**
     * 检查用户是否拥有相应的权限。
     * 分两步：
     * 1、检查用户拥有的角色是否足够（即是否在应用程序的权限角色之中）
     * 2、检查用户的账号是否在应用程序允许访问的用户名单中
     * 两者有一个通过即可。
     * 注：账号和角色都放userRights里了，
     * 
     * @param userRigths
     *            用户拥有的权限
     * @param servletPath
     *            检测的请求路径
     * @return
     * @throws Exception
     */
    public boolean checkPermission(List<Object> userRights, String servletPath) {
        if (rightsMap == null || rightsMap.isEmpty()) {
            return true;
        }

        Set<String> rights = rightsMap.get(servletPath);
       
        // 如果访问权限控制配置为空，则不需要控制，直接放行。
        if (rights == null || rights.isEmpty()) {
            return true;
        }
        
        // 访问权限控制配置不为空，则用户必须拥有足够的权限才能访问
        if( !EasyUtils.isNullOrEmpty(userRights)) {
            for (Object temp : userRights) {
                // 用户角色ID有可能为Long类型，需要toString一下，因为rights里根据配置文件解析出来的都是String形式。
                if (temp != null && rights.contains(temp.toString()))
                    return true;
            }
        }
        return false;
    }
    
    public String get404URL(){
        return THE_404_URL;
    }

    /**
     * 解析应用程序权限访问控制配置文件。
     * 把应用程序有权限访问的角色ID 以及 账号统一放在Set里。
     * 
     * @param configFile
     * @return
     * @throws Exception
     */
    private Map<String, Set<String>> parser() {
        Map<String, Set<String>> rightsMap = new HashMap<String, Set<String>>();
 
		try {
			Document doc = XMLDocUtil.createDoc(RIGHT_CONFIG_FILE_NAME);
			
			Element root = doc.getRootElement();
	        THE_404_URL = root.attributeValue("url_404");
	        
	        for (Iterator<?> it = root.elementIterator("servlet"); it.hasNext();) {
	            Element servletNode = (Element) it.next();
	            String name = EasyUtils.obj2String( servletNode.attributeValue("name") );
	            String role = EasyUtils.obj2String( servletNode.attributeValue("right") );
               
	            Set<String> rightSet = new HashSet<String>();
                String[] rights = role.split(",");
                for (int i = 0; i < rights.length; i++) {
                    rightSet.add(rights[i]);
                }
                rightsMap.put(name, rightSet);
	        }
		} 
		catch(Exception e) { }
        
        return rightsMap;
    }
}
