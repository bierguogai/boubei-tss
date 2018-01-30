/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.service.IArticleService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.servlet.AfterUpload;

public class CreateAttach implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());

	public String processUploadFile(HttpServletRequest request,
			String filepath, String orignFileName) throws Exception {

		Long articleId = Long.parseLong(request.getParameter("articleId"));
		Long channelId = Long.parseLong(request.getParameter("channelId"));
		int type = com.boubei.tss.dm.record.file.CreateAttach.getAttachType(filepath);
		orignFileName = com.boubei.tss.dm.record.file.CreateAttach.getOrignFileName(orignFileName);

		// 保存附件信息
		File targetFile = new File(filepath);
		IArticleService articleService = (IArticleService) Global.getBean("ArticleService");
		Attachment attachObj = articleService.processFile(targetFile, articleId, channelId, type, orignFileName);

		// 向前台返回成功信息
		String downloadUrl = attachObj.getRelationUrl();
		Integer seqNo = attachObj.getSeqNo();
		String fileName = attachObj.getFileName();
		String fileExt = attachObj.getFileExt();
		
		return "parent.addAttachments(" + seqNo + ", '" + fileName + "', '" 
				+ fileExt + "', '" + orignFileName + "', '" + downloadUrl + "')";
	}
}