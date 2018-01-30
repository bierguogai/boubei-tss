/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.param;

import java.util.List;

import com.boubei.tss.cache.aop.Cached;
import com.boubei.tss.cache.extension.CacheLife;
import com.boubei.tss.modules.log.Logable;
 
public interface ParamService {

	void fireListener(Param param);
	
	/** 保存参数 */
	@Logable(operateObject="系统参数", operateInfo="新增/修改了：${returnVal?default(\"\")}")
	Param saveParam(Param param);
	
	/** 停用、启用参数 */
	@Logable(operateObject="系统参数", operateInfo="<#if args[1]=1>停用<#else>启用</#if>了节点(ID = ${args[0]?default(\"\")})")
	void startOrStop(Long paramId, Integer disabled);
	
	/** 删除参数 */
	@Logable(operateObject="系统参数", operateInfo="删除了（ID=${args[0]?default(\"\")})")
	void delete(Long paramId);

	/** 取所有参数 */
	List<?> getAllParams(boolean includeHidden);
	
	/** 根据ID取参数 */
	Param getParam(Long id);
	
	/** 根据code取参数。供ParamManager使用 */
	@Cached(cyclelife = CacheLife.LONG)
	Param getParam(String code);
	
	/** 取下拉型参数的值 */
	@Cached(cyclelife = CacheLife.LONG)
	List<Param> getComboParam(String code);
	
	/** 取树型参数的值 */
	@Cached(cyclelife = CacheLife.LONG)
	List<Param> getTreeParam(String code);
	
	/**
	 * 参数排序
	 * @param paramId
	 * @param toParamId
	 * @param direction
	 */
	@Logable(operateObject="系统参数", operateInfo="(ID: ${args[0]})节点移动到了(ID: ${args[1]})节点<#if args[2]=1>之下<#else>之上</#if>")
	void sortParam(Long paramId, Long toParamId, int direction);
	
	/**
	 * 复制参数
	 * @param paramId
	 * @param toParamId
	 */
	@Logable(operateObject="系统参数", operateInfo="(ID: ${args[0]})节点复制到了(ID: ${args[1]})节点 下")
	List<?> copyParam(Long paramId, Long toParamId);
	
	/**
	 * 移动参数
	 * @param paramId
	 * @param toParamId
	 */
	@Logable(operateObject="系统参数", operateInfo="(ID: ${args[0]})节点移动到了(ID: ${args[1]})节点 下")
	void move(Long paramId, Long toParamId);
	
	/** 取可以添加参数或者参数组的参数组 */
	List<?> getCanAddGroups();
	
	List<Param> getParamsByParentCode(String code);
}
