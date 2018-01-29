package com.boubei.tss.um.servlet;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.service.IResourceService;

public class ImportAppConfig implements AfterUpload {

	protected Logger log = Logger.getLogger(this.getClass());

	public String processUploadFile(HttpServletRequest request, 
			String filepath, String oldfileName) throws Exception {
		
		 File targetFile = new File(filepath);
         Document doc = new SAXReader().read(targetFile);
         
         IResourceService resourceService = (IResourceService) Global.getBean("ResourceService");
         
         String msg = EX.DEFAULT_SUCCESS_MSG;
         try {
        	 resourceService.applicationResourceRegister(doc, UMConstants.PLATFORM_SYSTEM_APP);
         } catch(Exception e) {
        	 msg = e.getMessage();
         }
         
         return "parent.alert(\"" +msg+ "\");parent.loadInitData();";
	}
}