package com.boubei.tss.cms.job;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Channel;

/** 
 * 各种定时策略类。
 * 包括有  1 索引策略 2 发布策略 3 过期策略
 */
public class JobStrategy {
	
	public String  name;	    // 策略名称
	public Integer type;        // 策略类型： 1 索引策略 2 发布策略 3 过期策略
    public boolean isIncrement; // 是否增量操作
    public String executorClass; // 发布 /索引实现类类名
    
    public Channel site;         // 对应站点
    
    public String indexPath;            // 发布 /索引文件存放目录
    
    public String getIndexPath(){
        return site.getPath() + "/" + this.indexPath;
    }
	
	static Map<Object, JobStrategy> holder;
 
	static {
		holder = new HashMap<Object, JobStrategy>();
		
        JobStrategy publishStrategy = new JobStrategy();
        publishStrategy.name = "文章发布策略";
        publishStrategy.type = CMSConstants.STRATEGY_TYPE_PUBLISH;
        publishStrategy.indexPath = "publish";
        
        JobStrategy indexStrategy = new JobStrategy();
        indexStrategy.name = "文章索引策略";
        indexStrategy.type = CMSConstants.STRATEGY_TYPE_INDEX;
        indexStrategy.indexPath = "index";

        JobStrategy expireStrategy = new JobStrategy();
        expireStrategy.name = "文章过期策略";
        expireStrategy.type = CMSConstants.STRATEGY_TYPE_EXPIRE;
        
        holder.put(publishStrategy.type, publishStrategy);
        holder.put(indexStrategy.type, indexStrategy);
        holder.put(expireStrategy.type, expireStrategy);
	}
	
	public static JobStrategy getIndexStrategy() {
		return holder.get(CMSConstants.STRATEGY_TYPE_INDEX);
	}
	
	public static JobStrategy getPublishStrategy() {
		return holder.get(CMSConstants.STRATEGY_TYPE_PUBLISH);
	}
	
	public static JobStrategy getExpireStrategy() {
		return holder.get(CMSConstants.STRATEGY_TYPE_EXPIRE);
	}
}

