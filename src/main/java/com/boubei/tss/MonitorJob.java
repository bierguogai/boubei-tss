package com.boubei.tss;

import java.util.List;
import java.util.Map;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.util.MailUtil;

/**
 * 监控：数据库（MySQL/Oracle等）连接及主从同步、ETL数据抽取、Apache、Tomcat、WebService等
 * 
 * 生成异常信息放到系统异常日志里，再通过定时任务发送出去。
 * 每30分钟，轮询最近30分钟 Monitor-Err 日志， 有的话发邮件出来。
 * 
 * com.boubei.tss.MonitorJob | 0 0/30 * * * ? | X
 * 
 */
public class MonitorJob extends AbstractJob {
	
	int interval = 30; // 间隔时间（分钟）
	String domain = "www.boubei.com";
	String keyword = "卜贝";
	
	protected void excuteJob(String jobConfig, Long jobID) {
		try {
			interval = Integer.parseInt(jobConfig.split(",")[0]);
		} catch(Exception e) { }
		
		try {
			domain = jobConfig.split(",")[1];
			keyword = jobConfig.split(",")[2];
		} catch(Exception e) { }
		
		monitoringMySQL();
		monitoringApache();
		monitoringTomcat();
		
		checking(DMConstants.LOCAL_CONN_POOL, "TSS-BI", "Monitor-Err", "");
		checking(DMConstants.LOCAL_CONN_POOL, "TSS-BI", "ETL-Err", "");
		checking(DMConstants.LOCAL_CONN_POOL, "TSS-BI", "定时任务", "and t.operationCode like '%【失败!!!】%'");
	}
	
	/**
	 * 每30分钟，轮询最近30分钟 Monitor-Err 日志， 有的话发邮件出来
	 */
	public void checking(String ds, String sysName, String errName, String fitler) {
		String sql = "select operationCode 类型, content 内容, operateTime 监测时间 " +
				" from component_log t   " +
				" where t.operateTable = '" +errName+ "' " + fitler +
				"  and t.operateTime > DATE_SUB(NOW(), INTERVAL " +interval+ " MINUTE)";
		List<Map<String, Object>> errList = SQLExcutor.query(ds, sql);
		if(errList.isEmpty()) return;
		
		String content = "", title = "异常提醒：" + sysName + "，" +errName+ ": ";
		for(Map<String, Object> log : errList) {
			content += log + " \n ";
			title += log.get("类型") + "|";
		}
		
		MailUtil.send(title, content,  MonitorUtil.getReceiver() , "sys");
	}
	
	/** 主从同步、是否宕机 */
	void monitoringMySQL() {
//		MonitorUtil.monitoringMySQL("connpool-tssbi-master", "connpool-tssbi-slave");
		MonitorUtil.testDBConn("connpool-tssbi-master");
		
		log.info("monitoring MySQL finished. ");
	}

	/** 文件夹同步（交由 crontab 来监控） */
	void monitoringFileRsync() { }

	/** 
	 * Manage页Tomcat状态变成err等各种异常） 
	 */
	void monitoringApache() {
		// MonitorUtil.monitoringApache(domain); // nginx
		log.info("monitoring Apache finished. ");
	}
	
	// 访问 param/json/simple/sysTitle、si/version 服务，返回object数组，以检查 Tomcat是否正常
	void monitoringTomcat() {
		MonitorUtil.monitoringRestfulUrl("http://" +domain+ "/tss/si/version", null);
		MonitorUtil.monitoringRestfulUrl("http://" +domain+ "/tss/param/json/simple/sysTitle", keyword);
		
		log.info("monitoring Tomcat finished. ");
	}
}
