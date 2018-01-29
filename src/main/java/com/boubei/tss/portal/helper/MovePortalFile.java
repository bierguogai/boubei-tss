package com.boubei.tss.portal.helper;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.portal.action.FileAction;
import com.boubei.tss.util.FileHelper;

public class MovePortalFile implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());

	public String processUploadFile(HttpServletRequest request,
			String filepath, String oldfileName) throws Exception {

		String contextPath = request.getParameter("contextPath");
		File baseDir = null;
		if (contextPath != null) {
			contextPath = FileAction.getContextPath(contextPath);
			baseDir = new File(contextPath);
		}
		
		if (baseDir != null && filepath != null) {
			File targetFile = new File(filepath);
			FileHelper.copyFile(baseDir, targetFile);
		}

		return "parent.alert('上传成功!');parent.loadFileTree();";
	}
}
