package com.boubei.tss.matrix;


/**
 * 定时收集使用情况：创建多少报表、录入表资源、登陆次数、登录用户数、分时段访问统计、异常信息等，
 * 
 * 集中发往boubei.com，方式：
 * 1、前端 JSONP
 * 2、通过httpproxy代理转发，内置一个 BBI 的Appserver，指向www.boubei.com/tss
 * 3、后台JOB定时转发、通过Recorder的API、不要用远程接口
 * 4、通过ETL Task写到 boubei.com
 */
public class Collector {
	

	/**
	 * 收集每天新增的资源数
select name, createTime ct, updateTime ut, lockVersion, param, script, datasource, displayuri, paramuri, needlog, mailable
 from dm_report where type = 1 and updateTime > curdate()
 
select name, createTime ct, updateTime ut, lockVersion, rctable, define, datasource, customizejs, customizepage, customizetj, 
   customizeGrid, needlog, needfile, batchimp
 from dm_record where type = 1 and updateTime > curdate()
	 */
	void collectResource() {
		 
	}
	
	/**
	 * 收集操作日志
SELECT id, operatetable, operationcode, operatetime, operatoip, operatorbrowser, operatorname, content
 FROM tssbi.component_log
 WHERE t.operateTable IN ('录入表','报表','站点栏目','门户结构','门户组件','站点栏目','文章','角色','用户','用户登录','系统参数','系统异常')
   AND operateTime > '2017-07-01'
	 */
	void collectOpLog() {
		
	}
	
	/**
	 * 按天分时段统计访问次数和访问时间
select  'report' type, date_format(accessTime, '%Y-%m-%d') day, date_format(accessTime, '%H') hour, 
	ROUND(avg(runningTime)) rt, count(*) visitnum, count(distinct l.userId) visitusernum
 from dm_access_log l, um_user u
 where l.userId = u.id
   -- and l.accessTime >= ? and l.accessTime < ?
 group by  date_format(accessTime,'%H') 
union all
SELECT operateTable type, date_format(operatetime, '%Y-%m-%d') day, date_format(operatetime, '%H') hour, 0 rt,
  count(*) visitnum, count(distinct operatorid) visitusernum
 FROM tssbi.component_log
 WHERE operateTable like 'record-%'
   -- and l.operatetime >= ? and l.operatetime < ?
group by  date_format(operatetime,'%H')
order by hour asc
	 */
	void collectReportLog() {
		
	}
}
