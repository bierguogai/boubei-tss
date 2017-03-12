package com.boubei.tss.cache.extension;

import java.util.Set;

import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;

public class CacheHelper {
	
	public static Pool getShorterCache() {
		return JCache.getInstance().getPool(CacheLife.SHORTER.toString());
	}
	
	public static Pool getShortCache() {
		return JCache.getInstance().getPool(CacheLife.SHORT.toString());
	}
	
	public static Pool getLongCache() {
		return JCache.getInstance().getPool(CacheLife.LONG.toString());
	}
	
	public static Pool getLongerCache() {
		return JCache.getInstance().getPool(CacheLife.LONGER.toString());
	}
	
	public static Pool getNoDeadCache() {
		return JCache.getInstance().getPool(CacheLife.NODEAD.toString());
	}
	
	public static void flushCache(String poolName, String likeKey) {
		Pool pool = JCache.getInstance().getPool(poolName);
		Set<Object> keys = pool.listKeys();
		for(Object _key : keys) {
			if(_key.toString().indexOf(likeKey) >= 0) {
				pool.destroyByKey(_key);
			}
		}
	}
	
	
	public final static String CACHE_PARAM = "CACHE_PARAM";
	
	public static Param getCacheParamGroup(ParamService paramService) {
		Param paramGroup = paramService.getParam(CacheHelper.CACHE_PARAM);
		if(paramGroup == null) {
			paramGroup = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "缓存池配置");
			paramGroup.setCode(CacheHelper.CACHE_PARAM);
	        paramService.saveParam(paramGroup);
		}
		return paramGroup;
	}
}
