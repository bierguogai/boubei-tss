package com.boubei.tssx.snapshot;

import java.util.Date;
import java.util.Set;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.CacheLife;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.util.EasyUtils;

/**
 * 快照数据，留作系统演示. com.boubei.tssx.snapshot.SnapshotJob | 0 0/2 * * * ? | X
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
		Pool dataCache = JCache.getInstance().getPool(CacheLife.SHORTER.toString());
		Set<Cacheable> items = dataCache.listItems();
		for(Cacheable item : items) {
			String key = item.getKey().toString();
			snapshot(key, item.getValue());
			
			dataCache.destroyObject(item);
		}
	}
	
	public static void snapshot(String key, Object val) {
		Snapshot o = new Snapshot();
		o.setIkey(key);
		o.setIvalue( val instanceof SQLExcutor ? val.toString() : EasyUtils.obj2Json(val) );
		o.setCreateTime(new Date());
		
		ICommonService commonService = Global.getCommonService();
		commonService.create(o);
	}
}
