package com.boubei.tss.dm.ext;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.Global;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.um.service.IUserService;

/**
 * 清理垃圾权限信息
 * 
 * com.boubei.tss.dm.ext.CleanPermissionJob | 0 0 01 * * ? | X
 * 
 */
public class CleanPermissionJob extends AbstractJob {
	
	IUserService userService = (IUserService) Global.getBean("UserService");
 
	protected void excuteJob(String jobConfig, Long jobID) {
 
		log.info("------------------- 清理权限信息......");
		
		String[] resources = "role,group,report,record,channel,navigator,portal".split(",");
		String[] permissionTables = "um_permission_role,um_permission_group,dm_permission_report,dm_permission_record,cms_permission_channel,portal_permission_navigator,portal_permission_portal".split(",");
		
		int index = 0;
		for(String resource : resources ) {
			String permissionTable = permissionTables[index++];
			
			// 1.清理资源已经不存在的权限信息
			String sql = "delete from " +permissionTable+ " where id > 0 and (roleId not in (select id from um_role) or resourceId not in (select id from view_" +resource+ "_resource) )";
			SQLExcutor.excute(sql, DMConstants.LOCAL_CONN_POOL);
			
			// 2.清理重复生成的权限信息
			sql = "delete from " +permissionTable+ " where id not in (select id from (SELECT min(t.id) id FROM " +permissionTable+ " t group by t.resourceId, t.roleId, t.operationId, t.permissionState, t.isPass, t.isGrant) t)";
			SQLExcutor.excute(sql, DMConstants.LOCAL_CONN_POOL);
		}
		
		log.info("------------------- 清理权限信息 Done");
		
		// 处理过期的用户、角色、转授策略等
		userService.overdue();
	}
	 
}
