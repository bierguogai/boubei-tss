package com.boubei.tss.matrix;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;

/**
 * 定时收集使用情况：创建多少报表、录入表资源、登陆次数、登录用户数、分时段访问统计、异常信息等，
 * 
 * 集中发往boubei.com，方式：
 * 1、通过前端 JS动态挂载 发送
 * 2、通过httpproxy代理转发，内置一个 BBI 的Appserver，指向www.boubei.com/tss
 * 3、后台JOB定时转发、通过Recorder的API、不要用远程接口
 * 
 * TODO 通过ETL Task写到 boubei.com ??? byDay & byID
 */
public class Collector {
	

	void collectResource() { // 收集每天新增的资源数
		String sql = "select count(*) rnum, '报表' rname from dm_report where type = 1";
		SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql);
	}
	
	void collectOpLog() {
		String sql = "SELECT id, content, operateTable, operateTime, operationCode, operatorIP, operatorName" +
		  " FROM tssbi.component_log t " +
		  " where t.operateTable in ('录入表','报表','站点栏目','门户结构','门户组件','站点栏目','文章','角色','用户','用户登录','系统参数','系统异常') " +
		  "	  and operateTime > '2017-07-01'";
	}
	
	/**
	 * 按天分时段统计访问次数和访问时间
	 */
	void collectReportLog() {
		
	}
}
