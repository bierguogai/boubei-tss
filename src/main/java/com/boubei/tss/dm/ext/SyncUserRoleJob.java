package com.boubei.tss.dm.ext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.framework.timer.AbstractJob;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * 自动同步用户和角色对应关系，这关系可能由某第三方模块维护
 * 
 * com.boubei.tss.dm.ext.SyncUserRoleJob | 0 12 * * * ? | 
 * 		select userId user, roleId1 role1, roleId2 as role2 from xx_roleuser where updatetime > ? or createtime > ?@datasource1
 * 
 */
public class SyncUserRoleJob extends AbstractJob {
	
	private String insertSQL = "insert into um_roleuser (userId, roleId) values(?, ?)";
	private String deleteSQL = "delete from um_roleuser where userId=? and roleId=? and strategyId is null";
	
	/* 
	 * jobConfig的格式为 : 
	 * 		sql @ datasource
	 */
	protected void excuteJob(String jobConfig) {
		log.info("开始用户对角色信息同步......");
		
		String info[] = EasyUtils.split(jobConfig, "@");
		if(info.length != 2)  {
			log.info("用户对角色信息同步的配置信息有误。" + jobConfig);
			return;
		}
		 
		String sql = info[0];
		String dataSource = info[1];
		Date fromDay = DateUtil.subDays(DateUtil.today(), 3);
		
		List<Map<String, Object>> list = SQLExcutor.query(dataSource, sql, fromDay, fromDay);
		List<Object[]> addList = new ArrayList<Object[]>();
		List<Object[]> delList = new ArrayList<Object[]>();
		
		for(Map<String, Object> item : list) {
			Long user  = EasyUtils.obj2Long(item.get("user"));
			String[] role1 = EasyUtils.obj2String(item.get("role1")).split(","); // 需要新增的用户角色关系
			String[] role2 = EasyUtils.obj2String(item.get("role2")).split(","); // 需要删除的用户角色关系
			
			for(String role : role1) {
				if( !EasyUtils.isNullOrEmpty(role) ) {
					Long _role = EasyUtils.obj2Long(role);
					if( getCount(user, _role) == 0 ) {
						addList.add(new Object[]{ user, _role });
					}
				}
			}
			
			for(String role : role2) {
				if( !EasyUtils.isNullOrEmpty(role) ) {
					Long _role = EasyUtils.obj2Long(role);
					delList.add(new Object[]{ user, _role });
				}
			}
		}
		
		SQLExcutor.excuteBatchII(insertSQL, addList, DMConstants.LOCAL_CONN_POOL);
		SQLExcutor.excuteBatchII(deleteSQL, delList, DMConstants.LOCAL_CONN_POOL);
		
		log.info("完成用户对角色信息同步。");
	}
	
	int getCount(Long user, Long role) {
		String sql = "select count(*) num from um_roleuser where roleId = ? and userId = ?";
		Object result = SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, role, user).get(0).get("num");
		return EasyUtils.obj2Int(result);
	}
	 
}
