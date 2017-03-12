package com.boubei.tss.framework.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.web.XHttpServletRequest;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.framework.web.dispaly.xmlhttp.XmlHttpDecoder;
import com.boubei.tss.framework.web.wrapper.XHttpServletRequestWrapper;
import com.boubei.tss.util.XMLDocUtil;

/**
 * 解析合并请求的Servlet。
 * 
 *@see com.boubei.tss.core.web.servlet.SSOIntegrateTest
 */
public class Servlet3MultiRequest extends HttpServlet {
	private static final long serialVersionUID = 2840752639965867560L;
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        XmlPrintWriter writer = new XmlPrintWriter(response.getWriter());
        writer.println("<Responses>");
        
		List<Element> requestNodes = XMLDocUtil.selectNodes(parseRequestXML(request), "/Requests/Request");
		if (requestNodes != null) {
			for (Element requestNode : requestNodes) {
                String servletPath = requestNode.attributeValue("url"); //获取子请求的ServletPath
				
                XHttpServletRequest rRequest = XHttpServletRequestWrapper.wrapRequest(request);
                rRequest.setHeader(RequestContext.MULTI_REQUEST, "true");
				rRequest.setServletPath(servletPath);
				XmlHttpDecoder.decode(requestNode, rRequest);
                
                RequestDispatcher rd = request.getRequestDispatcher(servletPath);
				try {
				    // 将多个单功能的文件例如.jsp文件、servlet请求 整合成一个总的Servlet文件，相当于：jsp中 include file="xyz.jsp"
					rd.include(rRequest, response);
				} catch (Exception e) {
					ExceptionEncoder.encodeException(response, e);
				}
			}
		}
		writer.println("</Responses>");
	}

	/**
	 * <p>
	 * 解析合并请求xml数据流
	 * </p>
	 *
	 * @param request
	 */
	private Document parseRequestXML(HttpServletRequest request) {
		try {
			ServletInputStream is = request.getInputStream();

			SAXReader saxReader = new SAXReader();
			try {
				return saxReader.read(is);
			} catch (DocumentException e) {
				throw new BusinessException("解析合并请求的xml数据流失败", e);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new BusinessException("获取合并请求数据流失败", e);
		}
	}
}
