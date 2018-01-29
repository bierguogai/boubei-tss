package com.boubei.tss.util;

import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.boubei.tss.PX;
import com.boubei.tss.modules.param.ParamConfig;

public class MailUtil {
	
	static Logger log = Logger.getLogger(MailUtil.class);
	
	public static final String DEFAULT_MS = "default"; // 默认邮件服务器
	
	public static JavaMailSenderImpl getMailSender(String _ms) {
		// 读取邮件服务器配置
		String[] configs = getEmailInfos(_ms);
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost( configs[0] );
		mailSender.setPort(25);
		
		if( configs.length == 5 ) {
			mailSender.setUsername( configs[3] );
			mailSender.setPassword( InfoEncoder.simpleDecode(configs[4], 12) ); 
			mailSender.getJavaMailProperties().put("mail.smtp.auth", true);
		}
		
		return mailSender;
	}
	
	// eg: boubei@163.com#default
	public static String[] parseReceivers(String receivers) {
		String _ms = MailUtil.DEFAULT_MS;
		int index = EasyUtils.obj2String(receivers).indexOf("#");
		if(index > 0) {
			_ms = receivers.substring(index + 1);
			receivers = receivers.substring(0, index);
		}
		
		return new String[] {_ms, receivers};
	}
	
	/* 
	 * stmp|from|to|user|pwd, Mail from must equal authorized user 
	 */
	private static String[] getEmailInfos(String _ms) {
		return ParamConfig.getAttribute(PX.MAIL_SERVER_ + _ms).split("\\|");
	}
	
	public static String   getEmailFrom(String _ms) { return getEmailInfos(_ms)[1]; }	
	public static String[] getEmailTo(String _ms) { return getEmailInfos(_ms)[2].split(","); }
	
	public static void send(String subject, String text) {
		send(subject, text, getEmailTo(DEFAULT_MS), DEFAULT_MS);
	}
	
	public static void send(String subject, String text, String receiver[], String _ms) {
		if(receiver == null || receiver.length == 0) {
			log.error("收件人不能为空"); 
			return;
		}
		
		try {
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom( getEmailFrom(_ms) ); // 发送者,这里还可以另起Email别名
			mail.setTo( preCheatEmails(receiver) );
			mail.setSubject(subject);    // 主题
			mail.setText(text);         // 邮件内容
			
			getMailSender(_ms).send(mail);
		} 
		catch (Exception e) {
			log.error("发送文本邮件(" +subject+ ")出错了, " + receiver + "：" + e.getMessage());
		}
	}
	
	public static void sendHTML(String subject, String htmlText, String receiver[], String _ms) {
		// 邮件内容，注意加参数true
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<style type='text/css'> " );
		html.append("	body { margin:0; padding:5px; font-family: 微软雅黑; font-size: 15px;}");
		html.append("</style>");
		html.append("</head>");
		html.append("<body>");
		html.append(htmlText);
		html.append("</body>");
		html.append("</html>");
		
		JavaMailSenderImpl sender = MailUtil.getMailSender(_ms);
		MimeMessage mailMsg = sender.createMimeMessage();
		try {
			// 设置utf-8或GBK编码，否则邮件会有乱码
			MimeMessageHelper mh = new MimeMessageHelper(mailMsg, true, "utf-8");
			mh.setFrom( MailUtil.getEmailFrom(_ms) );  // 发送者
			mh.setTo( preCheatEmails(receiver) );                        // 接受者
			mh.setSubject(subject);                    // 主题
			mh.setText(html.toString(), true);
			
			sender.send(mailMsg);
		} 
		catch (Exception e) {
			log.error("发送邮件【" +subject+ "】出错了, " + Arrays.asList(receiver) + ", " + e.getMessage());
		}
	}
	
	public static String[] preCheatEmails(String[] emails) {
		String[] _emails = new String[emails.length]; 
		for(int i = 0; i < emails.length; i++) {
			_emails[i] = emails[i].replaceAll(" ", "");  // 邮件里不能含有空格
		}
		return _emails;
	}
}
