package com.boubei.tss.um.dao;

import com.boubei.tss.cache.aop.Cached;
import com.boubei.tss.cache.extension.CacheLife;
import com.boubei.tss.framework.persistence.IDao;
import com.boubei.tss.um.entity.ResourceType;
import com.boubei.tss.um.entity.ResourceTypeRoot;
import com.boubei.tss.um.permission.RemoteResourceTypeDao;
 
public interface IResourceTypeDao extends IDao<ResourceType>, RemoteResourceTypeDao {

	/**
	 * <p>
	 * 获得一个应用系统的一个资源类型的根节点
	 * </p>
	 * @param applicationId
	 * @param resourceTypeId
	 * @return
	 */
    ResourceTypeRoot getResourceTypeRoot(String applicationId, String resourceTypeId);

	/**
	 * <p>
	 * 获取资源类型
	 * </p>
	 * @param applicationId
	 * 			应用系统id
	 * @param resourceTypeId
	 * 			资源类型id
	 * @return
	 */
    @Cached(cyclelife = CacheLife.NODEAD)
	ResourceType getResourceType(String applicationId, String resourceTypeId);
	
}


	