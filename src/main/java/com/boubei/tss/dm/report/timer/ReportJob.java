/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.report.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.boubei.tss.EX;
import com.boubei.tss.dm.DataExport;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.dm.report.log.AccessLogRecorder;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MailUtil;

/**
 * com.boubei.tss.dm.report.timer.ReportJob | 0 36 10 * * ? | 268:各省日货量流向:lovejava@163.com,BL01037:param1=today-1
 * 261:各省生产货量:BL00618,BL01037:param1=today-0
 * 262:报表三:BL00618,BL01037:param1=0,param3=today-1
 * 
 * 收件人支持方式有：email、账号、角色、用户组、参数定义，分别如下
 * lovejava@163.com,BL01037,-1@tssRole,-2@tssGroup,${JK}
 */
public class ReportJob extends AbstractJob {
	
	public static int MAX_ROWS = 200;
	
	ReportService reportService = (ReportService) Global.getBean("ReportService");
	ILoginService loginService  = (ILoginService) Global.getBean("LoginService");
	
	protected boolean needSuccessLog() {
		return true;
	}
	
	/* 
	 * jobConfig的格式为
	 *  
	 *  1:报表一:x1@163.com#sys
     *  2:报表二:x2@x.com
	 *	3:报表三:x3@x.com,x4@x.com:param1=a,param2=b
	 */
	protected void excuteJob(String jobConfig, Long jobID) {
		
		String[] jobConfigs = EasyUtils.split(jobConfig, "\n");
		
		Map<String, ReceiverReports> map = new HashMap<String, ReportJob.ReceiverReports>();
		
		// 收件人一致的定时报表合并起来发送
		for(String jobX : jobConfigs) {
			String reportInfo[] = EasyUtils.split(jobX, ":");
			if(reportInfo.length < 3) continue;
			
			String receiverStr = reportInfo[2].trim();
			ReceiverReports rr = map.get(receiverStr);
			if(rr == null) {
				map.put(receiverStr, rr = new ReceiverReports());
			}
			
			String title = reportInfo[1];
	        rr.reportTitles.add(title);
					
	        Long reportId = EasyUtils.obj2Long(reportInfo[0]);
	        rr.reportIds.add(reportId);
	        
	    	Map<String, String> paramsMap = new HashMap<String, String>();
	    	if(reportInfo.length > 3) { // 参数信息
	    		String[] params = reportInfo[3].split(",");
	    		for(String param : params) {
	    			String[] keyValue = param.split("=");
	    			if(keyValue.length == 2) {
	    				paramsMap.put(keyValue[0].trim(), keyValue[1].trim());
	    			}
	    		}
	    	}
	    	rr.reportParams.add(paramsMap);
		}
		
		for(String receivers : map.keySet()) {
			ReceiverReports rr = map.get(receivers);
			String[] info = MailUtil.parseReceivers(receivers);
			String receiver[] = loginService.getContactInfos( info[1], false );
			
			if(receiver != null && receiver.length > 0) {
				send(info[0], receiver, rr);
			}
		}
	}
	
	private void send(String _ms, String[] receiver, ReceiverReports rr) {
		String title = EasyUtils.list2Str(rr.reportTitles);
		
		JavaMailSenderImpl sender = MailUtil.getMailSender(_ms);
		MimeMessage mailMessage = sender.createMimeMessage();
		
		try {
			// 设置utf-8或GBK编码，否则邮件会有乱码
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
			messageHelper.setFrom( MailUtil.getEmailFrom(_ms) ); // 发送者
			messageHelper.setTo(receiver);                       // 接受者
			messageHelper.setSubject(EX.TIMER_REPORT + ":" + title);        // 主题
			
			// 邮件内容，注意加参数true
			StringBuffer html = new StringBuffer();
			html.append("<html>");
			html.append("<head>");
			html.append("<style type='text/css'> " );
			html.append("	table { border-collapse:collapse; border-spacing:0; }");
			html.append("	td { line-height: 1.42857143; vertical-align: top;  border: 1px solid black; text-align: left;}");
			html.append("	td { margin:0; padding:0; padding: 2px 2px 2px 2px; font-family: 微软雅黑; font-size: 15px;}");
			html.append("	thead td { background-color:#E4E6F5; font-weight: bold; }");
			html.append("</style>");
			html.append("</head>");
			html.append("<body>");
			
			for(int index = 0; index < rr.reportIds.size(); index++) {
				buildEmailContent(rr, index, messageHelper, html);
			}
			
			html.append("</body>");
			html.append("</html>");
			log.debug(html);
			messageHelper.setText(html.toString(), true);
			sender.send(mailMessage);
		} 
		catch (Exception e) {
			log.error(" error when send report email ", e);
		}
	}

	private void buildEmailContent(ReceiverReports rr, int index, 
			MimeMessageHelper messageHelper, StringBuffer html) throws Exception {
		
		Long reportId = rr.reportIds.get(index);
		String title = rr.reportTitles.get(index);
		Map<String, String> paramsMap = rr.reportParams.get(index);
		long start = System.currentTimeMillis();
		SQLExcutor ex = reportService.queryReport(reportId, paramsMap, 0, 10*10000, start);
		AccessLogRecorder.outputAccessLog(reportService, reportId, "showAsMail", paramsMap, start); // 记录日志
		
		String url = "/tss";
		try {
			url = Context.getApplicationContext().getCurrentAppServer().getBaseURL();
		} catch (Exception e) {}

		url += "/modules/dm/report_portlet.html?leftBar=true&id=" + reportId;
		for(String paramKey : paramsMap.keySet()) {
			url += "& " + paramKey + "=" + paramsMap.get(paramKey);
		}
		html.append("<h4>" +EX.DM_21+ "<a href='" + url + "'>" + url + "<a></h4><br>");
		
		if(ex.result.size() > MAX_ROWS) {
			html.append("<h1>" +EX.parse(EX.DM_22, title)+ "</h1>");
		} 
		else {
			html.append("<h1>" + title + "</h1>");
			html.append("<table>");
			// thead
			html.append("<thead><tr>");
	    	for(String field : ex.selectFields) {
	    		html.append("<td>").append("&nbsp;").append(field).append("&nbsp;").append("</td>");
	    	}
	    	html.append("</tr></thead>");
	    	// tbody
	    	html.append("<tbody>");
	    	for( Map<String, Object> row : ex.result) {
				html.append("<tr>");
				for(String field : ex.selectFields) {
		    		Object fieldV = row.get(field);
					html.append("<td>").append(fieldV == null ? "" : fieldV).append("</td>");
		    	}
				html.append("</tr>");
			}
	    	html.append("</tbody>");
			
			html.append("</table><br>");
		}
		
		// 附件内容(注意：此处生成的附件不会被自动删除，将一直存在于 export 目录下，需手动清除)
		String fileName = title + "-" + DateUtil.format(new Date()) + ".csv";
		String exportPath = DataExport.exportCSV(fileName, ex.result, ex.selectFields);
		
		fileName = MimeUtility.encodeWord(fileName); // 使用MimeUtility.encodeWord()来解决附件名称的中文问题
		messageHelper.addAttachment(MimeUtility.encodeWord(fileName), new File(exportPath));
	}
	
	
	/**
	 * 收件人对报表的映射，当一组收件人对应多个报表时，将这些报表合并成一个邮件发送
	 */
	class ReceiverReports {
		List<Long> reportIds = new ArrayList<Long>();
		List<String> reportTitles = new ArrayList<String>();
		List<Map<String, String>> reportParams = new ArrayList<Map<String, String>>();
	}
}
