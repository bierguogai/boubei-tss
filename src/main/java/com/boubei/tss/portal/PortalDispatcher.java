/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessServletException;
import com.boubei.tss.framework.sso.Anonymous;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.portal.entity.ReleaseConfig;
import com.boubei.tss.portal.entity.Structure;
import com.boubei.tss.portal.entity.Theme;
import com.boubei.tss.portal.service.IPortalService;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p> 门户发布地址转发器 </p> 
 * <pre>
 * 门户发布地址以.portal结尾，本servlet拦截所有此类请求，将其转到真正的门户地址进行访问。
 * </pre>
 */
@WebServlet(urlPatterns="*.portal")
public class PortalDispatcher extends HttpServlet {
 
    private static final long serialVersionUID = -5610690924047339502L;

	/** 发布路径的后缀名 */
	public static final String PORTAL_REDIRECT_URL_SUFFIX = ".portal";
	
	public static final String THE_404_URL = "/tss/404.html";
	public static final String THE_LOGIN_URL = "/tss/login.html";
 
    
    Logger log = Logger.getLogger(this.getClass());
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
			ReleaseConfig issueInfo = getIssueInfo(requestURI);
			if(issueInfo == null) {
				response.sendRedirect(THE_404_URL);
				return;
			}
			
			// 检测相应门户是否可以使用匿名用户访问
			Long portalId = issueInfo.getPortal().getId(), pageId = null;
			if(issueInfo.getPage() != null) {
				pageId = issueInfo.getPage().getId();
			}

			if ( canPortalBrowseByAnonymous(portalId, pageId) ) {
				String redirectPage = getRedirectPath(issueInfo);
	            log.debug("访问门户发布地址被转向至真实地址:" + redirectPage );

	            RequestDispatcher rd = request.getRequestDispatcher(redirectPage);
	            rd.forward(request, response); // 控制权转发
			}
			else {
				response.sendRedirect(THE_LOGIN_URL);
			}
			 
            /* 
             * RequestDispatcher.forward()方法和HttpServletResponse.sendRedirect()方法的区别是：
             * 前者仅是容器中控制权的转向，在客户端浏览器地址栏中不会显示出转向后的地址；
             * 后者则是完全的跳转，浏览器将会得到跳转的地址，并重新发送请求链接，这样，从浏览器的地址栏中可以看到跳转后的链接地址。
             * 所以，前者更加高效，在前者可以满足需要时，尽量使用Request Dispatcher.forward()方法，并且，
             * 这样也有助于隐藏实际的链接。在有些情况下，比如，需要跳转到一个其它服务器上的资源，则必须使用 
             * HttpServletResponse.sendRedirect()方法。
             */
        } catch (Exception e) {
            throw new BusinessServletException(e);
        }
    }
    
    /**
     * <p>
     * 获取发布信息对应的真实地址
     * </p>
     * @param issueInfo
     * @return
     */
    private String getRedirectPath(ReleaseConfig issueInfo) {
        Long portalId = issueInfo.getPortal().getId();
        String redirectPage = "auth/portal/preview/" + portalId + "?x=1";
        
        Structure page = issueInfo.getPage();
		if (page != null) {
            redirectPage += "&pageId=" + page.getId();
        }
		
        Theme theme = issueInfo.getTheme();
		if (theme != null) {
            redirectPage += "&themeId=" + theme.getId();
        }
        return redirectPage;
    }
    
	/**
	 * 获取发布信息
	 */
	private ReleaseConfig getIssueInfo(String uri) {
		String visitUrl = uri.substring(uri.lastIndexOf("/") + 1);
        IPortalService portalService = (IPortalService) Global.getBean("PortalService");
        try {
        	return portalService.getReleaseConfig(visitUrl);
        } 
        catch(Exception e) {
        	log.debug("portal release url is error：" + uri + ", " + e.getMessage());
        	return null;
        }
	}

    /**
     * 检测匿名用户是否对门户有浏览权限。 
     * 比如管理员专用的后台门户就不允许匿名访问。
     * 
     * @param portalId
     * @return
     */
    private boolean canPortalBrowseByAnonymous(Long portalId, Long pageId) {
    	PermissionHelper helper = PermissionHelper.getInstance();

        String application  = UMConstants.TSS_APPLICATION_ID;
        String resourceType = PortalConstants.PORTAL_RESOURCE_TYPE;
        String operration   = PortalConstants.PORTAL_VIEW_OPERRATION;
        Long operatorId = (Long) EasyUtils.checkNull( Environment.getUserId(), Anonymous.one.getId() );
        List<Long> permissons = helper.getResourceIdsByOperation(application, resourceType, operration, operatorId);
        
        return permissons.contains(portalId) && (pageId == null || permissons.contains(pageId) );
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}

