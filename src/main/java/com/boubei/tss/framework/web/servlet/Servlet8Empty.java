package com.boubei.tss.framework.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>
 * 空servlet，啥事都不做。
 * </p>
 * 
 * 通过将本servlet配置成 *.in，*.do（配置servlet最后位置），以将处理找不到真实地址的请求。
 * 
 */
@WebServlet(urlPatterns={"*.in", "*.do"} )
public class Servlet8Empty extends HttpServlet {

    private static final long serialVersionUID = 5470879889942418562L;

    Logger log = Logger.getLogger(this.getClass());
 
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		log.debug("请求：" + request.getRequestURI() + " 被 dispatch 到 EmptyServlet。");
	}
    
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
    	doGet(request, response);
    }
}
