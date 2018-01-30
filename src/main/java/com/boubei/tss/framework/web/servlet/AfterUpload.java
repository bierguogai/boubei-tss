/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;

public interface AfterUpload {
	
	String processUploadFile(HttpServletRequest request, 
			String filepath, String orignFileName) throws Exception;

}
