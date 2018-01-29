package com.boubei.tss.um.syncdata;

import java.util.Map;

import com.boubei.tss.framework.Global;
import com.boubei.tss.modules.progress.Progress;
import com.boubei.tss.modules.progress.Progressable;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.service.IGroupService;
import com.boubei.tss.util.EasyUtils;

/**
 * 自动同步用户
 * 
 * com.boubei.tss.um.syncdata.SyncUserJob | 0 06 * * * ? | 4,V5
 * 
 */
public class SyncUserJob extends AbstractJob {
	
	ISyncService syncService = (ISyncService) Global.getBean("SyncService");
	IGroupService groupService = (IGroupService) Global.getBean("GroupService");
 
	/* 
	 * jobConfig的格式为 : 
	 * 		mainGroupId1,fromApp1
	 * 		mainGroupId2,fromApp2
	 */
	protected void excuteJob(String jobConfig, Long jobID) {
		log.info("------------------- 用户信息自动同步......");
		
		String[] jobConfigs = EasyUtils.split(jobConfig, "\n");
		
		for(int i = 0; i < jobConfigs.length; i++) {
			String info[] = EasyUtils.split(jobConfigs[i], ",");
			if(info.length < 2) continue;
			 
			Long groupId = EasyUtils.obj2Long(info[0]);
			Group group = groupService.getGroupById(groupId);
			
			String fromApp = (String) EasyUtils.checkNull(group.getFromApp(), info[1]);
	        String fromGroupId = group.getFromGroupId();
	        if ( EasyUtils.isNullOrEmpty(fromGroupId) ) {
	            log.error("自动同步用户时，组【" + group.getName() + "】的对应外部应用组的ID（fromGroupId）为空。");
	            continue;
	        }
	        
	        Map<String, Object> datasMap = syncService.getCompleteSyncGroupData(groupId, fromApp, fromGroupId);
	        ((Progressable) syncService).execute(datasMap, new Progress(10000));
		}
		
		log.info("------------------- 用户信息自动同步 Done");
	}
}
