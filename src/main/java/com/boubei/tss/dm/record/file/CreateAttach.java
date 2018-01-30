/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.record.file;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.util.FileHelper;

public class CreateAttach implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());
	
	RecordService recordService = (RecordService) Global.getBean("RecordService");
	
	public static int getAttachType(String filepath) {
		int type; // = Integer.parseInt(request.getParameter("type")); 不用前台传入的文件类型
		if( FileHelper.isImage(filepath) ) {
			type = RecordAttach.ATTACH_TYPE_PIC;
		} else {
			type = RecordAttach.ATTACH_TYPE_DOC;
		}
		return type;
	}
	
	public static String getOrignFileName(String orignFileName) {
		int separatorIndex = Math.max(orignFileName.lastIndexOf("\\"), orignFileName.lastIndexOf("/"));
		if( separatorIndex >= 0) {
			orignFileName = orignFileName.substring(separatorIndex + 1);
		}
		return orignFileName;
	}

	public String processUploadFile(HttpServletRequest request,
			String filepath, String orignFileName) throws Exception {

		String record = request.getParameter("recordId");
		Long recordId = null;
    	try { // 先假定是数据表ID（Long型）
    		recordId = Long.valueOf(record.toString());
    	} 
    	catch(Exception e) { // 按名字或表名再查一遍
    		recordId = recordService.getRecordID(record, Report.TYPE1);
    	}
		
		Long itemId = Long.parseLong(request.getParameter("itemId"));
		int type = getAttachType(filepath);
		orignFileName = getOrignFileName(orignFileName);

		// 保存附件信息
		File targetFile = new File(filepath);
		RecordAttach attach = saveAttach(targetFile, recordId, itemId, type, orignFileName);

		/* 
		 * 向前台返回成功信息。
		 * 因为上传附件都是通过一个隐藏的iframe来实现上传的（可防止刷新主页面），所以上传成功回调JS需要加上 parent. 
	    */
		return "parent.addAttach(" + attach.getId() + ", " + attach.getType() + ", '" 
				+ attach.getName() + "', '" + attach.getDownloadUrl() + "', '" + attach.getUploadUser() + "')";
	}
	
	private RecordAttach saveAttach(File file, Long recordId, Long itemId, int type, String oldfileName) {
		
        String attachDir = RecordAttach.getAttachDir(recordId, itemId);
        File rootDir = new File(attachDir);
        
        // 将附件从上传临时目录剪切到站点指定的附件目录里
        String fileName = FileHelper.copyFile(rootDir, file); 
		String fileSuffix = FileHelper.getFileSuffix(fileName);
		
		// 保存附件信息对象
		RecordAttach attach = new RecordAttach();
		attach.setId(null);
		attach.setType(type);
		attach.setName(oldfileName);
		attach.setRecordId(recordId);
		attach.setItemId(itemId);
		attach.setSeqNo(recordService.getAttachSeqNo(recordId, itemId));
		attach.setUploadDate(new Date());
		attach.setUploadUser(Environment.getUserName());
        attach.setFileName(fileName);
        attach.setFileExt(fileSuffix.toLowerCase());
		
        recordService.createAttach(attach);

		return attach;
	}
}