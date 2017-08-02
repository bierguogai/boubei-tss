package com.boubei.tssbi.snapshot;

import com.boubei.tss.modules.timer.AbstractJob;

/**
 * 快照数据，留作系统演示
 * 
 * 本Job开启后，对Shorter Cache和dm_snapshot的快照数据进行双向同步。
 * 制作快照时，对Cache里的缓存项进行持续输出到dm_snapshot；
 * 演示时，将 dm_snapshot 里数据灌回 Cache（Cache生命周期为3分钟，得反复灌）。
 * 
 * Key大致如下：
 * com.boubei.tss.dm.report.ReportService.queryReport(781, {param1=4212990, param2=1}, 1, 100000, -1)
 */
public class SnapshotJob extends AbstractJob {

	protected void excuteJob(String jobConfig, Long jobID) {
		
	}
	
	
	public static void snapshot(String key, Object returnVal) {
		
	}

}
