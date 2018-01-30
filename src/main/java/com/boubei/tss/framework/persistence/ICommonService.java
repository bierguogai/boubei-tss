/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.persistence;

import java.util.List;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.modules.log.Logable;

public interface ICommonService {

	void create(IEntity entity);
	
	void createWithoutLog(IEntity entity);

	@Logable(operateObject="修改记录", operateInfo=" ${args[0]} ")
	void update(IEntity entity);

	@Logable(operateObject="删除记录", operateInfo="删除了 ${args[0]} : ${args[1]} ")
	void delete(Class<?> entityClass, Long id);

	List<?> getList(String hql, Object...params);
	
	List<?> getList(String hql, String[] args, Object[] params);
	
	IEntity getEntity(Class<?> entityClass, Long id);

}
