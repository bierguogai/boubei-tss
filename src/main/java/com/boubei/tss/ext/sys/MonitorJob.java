package com.boubei.tss.ext.sys;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.MailUtil;

@Component
public class MonitorJob {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	static int times = 60*1000;
	static boolean inited = false;
	
	public MonitorJob() throws InterruptedException {
		if( !MonitorUtil.isProdEnv() || inited ) {
			return;
		}
		
		inited = true;
		
		new Thread() {
			public void run() {
	            while (true) {
	                try {
	                	log.info("monitoring job starting......");
	                    sleep( 10*times ); // 10分钟
	                    monitoring(); 
	                } catch (InterruptedException e) { }
	            }
	        }
		}.start();
		
		new Thread() {
			public void run() {
	            while (true) {
	                try {
	                	log.info("checking job starting......");
	                    sleep( 10*times );  // 10分钟
	                    checking(DMConstants.LOCAL_CONN_POOL, "TSS-BI", "Monitor-Err", 10);
	                    
	                    Date now = new Date();
						if( DateUtil.getHour(now) == 9 && DateUtil.getMinute(now) <= 11 ) { 
							// 9:00 -- 9:10触发检查tssbi的ETL的err日志，因为是 10min 循环一次
	                    	checking(DMConstants.LOCAL_CONN_POOL, "TSS-BI", "TSS-ETL-Err", 60*12);
	                    	
	                    	// 定时JOB每天检查一次
	                    	checkingJob(DMConstants.LOCAL_CONN_POOL, "TSS-BI", 60*24);
	                    }
	                } 
	                catch (InterruptedException e) { }
	            }
	        }
		}.start();
	}
	
    static Monitor m = new Monitor();
    
	public void monitoring() {
		m.monitoringMySQL();
		m.monitoringApache();
		m.monitoringTomcat();
	}
	
	/**
	 * 每30分钟，轮询最近30分钟 Monitor-Err 日志， 有的话发邮件出来
	 */
	public void checking(String ds, String sysName, String errName, int howLong) {
		String sql = "select operationCode 类型, content 内容, operateTime 监测时间 " +
				" from component_log t   " +
				" where t.operateTable = '" +errName+ "' " +
				"   and t.operateTime > DATE_SUB(NOW(), INTERVAL " +howLong+ " MINUTE)";
		List<Map<String, Object>> errList = SQLExcutor.query(ds, sql);
		if(errList.isEmpty()) return;
		
		String content = "", title = sysName + "-" +errName+ ": ";
		for(Map<String, Object> log : errList) {
			content += log + " \n ";
			title += log.get("类型") + "|";
		}
		
		MailUtil.send(title, content,  MonitorUtil.getReceiver() , "sys");
	}
	
	public void checkingJob(String ds, String sysName, int howLong) {
		String sql = "select operationCode 类型, content 内容, operateTime 执行时间 " +
				" from component_log t   " +
				" where t.operateTable = '定时任务' and t.operationCode like '%【失败!!!】%' " +
				"   and t.operateTime > DATE_SUB(NOW(), INTERVAL " +howLong+ " MINUTE)";
		List<Map<String, Object>> errList = SQLExcutor.query(ds, sql);
		if(errList.isEmpty()) return;
		
		String content = "", title = sysName + "-定时任务失败: ";
		for(Map<String, Object> log : errList) {
			content += log + " \n ";
			title += log.get("类型") + "|";
		}
		
		MailUtil.send(title, content,  MonitorUtil.getReceiver() , "sys");
	}
}
