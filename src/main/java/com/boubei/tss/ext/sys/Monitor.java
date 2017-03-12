package com.boubei.tss.ext.sys;

import org.apache.log4j.Logger;

/**
 * 监控：数据库（MySQL/Oracle等）连接及主从同步、ETL数据抽取、Apache、Tomcat、WebService等
 * 
 * 生成异常信息放到系统异常日志里，再通过定时任务发送出去。
 * 每30分钟，轮询最近30分钟 Monitor-Err 日志， 有的话发邮件出来。
 */
public class Monitor {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	/** 主从同步、是否宕机 */
	void monitoringMySQL() {
//		MonitorUtil.monitoringMySQL("connpool-tssbi-master", "connpool-tssbi-slave");
		MonitorUtil.testDBConn("connpool-tssbi-master");
		
		log.info("monitoring MySQL finished. ");
	}

	/**
	 * 文件夹同步（交由 crontab 来监控）
	 */
	void monitoringFileRsync() {
		
	}

	/** 
	 * Manage页Tomcat状态变成err等各种异常） 
	 */
	void monitoringApache() {
//		MonitorUtil.monitoringApache("www.boubei.com");
		
		log.info("monitoring Apache finished. ");
	}
	
	// 访问 param/json/simple/sysTitle、si/version 服务，返回object数组，以检查 Tomcat是否正常
	void monitoringTomcat() {
		MonitorUtil.monitoringRestfulUrl("http://www.boubei.com/tss/si/version");
		MonitorUtil.monitoringRestfulUrl("http://www.boubei.com/tss/param/json/simple/sysTitle");
		
		String uri = "http://www.boubei.com/tss/param/json/simple/sysTitle";
		String ret = MonitorUtil.visitHttpUrl(uri);
		if(ret == null || ret.indexOf("它山石") < 0 ) {
			MonitorUtil.recordErrLog("读取它山石sysTitle", uri + "访问异常，ret = " + 
					ret + ", 请检查Tomcat及MySQL库是否正常。");
		}
		
		log.info("monitoring Tomcat finished. ");
	}
}
