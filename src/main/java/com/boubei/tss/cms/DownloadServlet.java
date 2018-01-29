package com.boubei.tss.cms;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.boubei.tss.cms.service.IRemoteArticleService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.util.FileHelper;

/** 
 * <p> DownloadServlet.java </p> 
 * 下载文章附件。传入文章ID以及附件的序号即可下载该附件。
 * 如果是Portal等其它应用配置该servlet，需要这些应用和CMS部署在同一台机器上才行。
 */
@WebServlet(urlPatterns="/download")
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = -6788424017181628016L;
    
    private Logger log = Logger.getLogger(this.getClass());
    
    IRemoteArticleService service;
    
    public void init() {
    	service = (IRemoteArticleService) Global.getBean("RemoteArticleService");
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long articleId;
        Integer seqNo;
        try{
    	    articleId = new Long(request.getParameter("id"));
    	    seqNo = new Integer(request.getParameter("seqNo"));
        } 
        catch(Exception e) {
        	log.debug("下载附件时参数值有误: " + e.getMessage());
        	return;
        }
        
	    AttachmentDTO attach = service.getAttachmentInfo(articleId, seqNo);
        if(attach == null){
        	log.error("附件不存在.");
            return;
        }
        
        String docOrPicPath = attach.basePath[attach.type]; 
        String filePath = attach.basePath[0] + "/" + docOrPicPath + "/" + attach.localPath;
        FileHelper.downloadFile(response, filePath, attach.name);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}
}

