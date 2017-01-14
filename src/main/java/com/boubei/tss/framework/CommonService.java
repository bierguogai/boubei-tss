package com.boubei.tss.framework;

import java.util.List;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.modules.log.Logable;

public interface CommonService {

	@Logable(operateObject="创建对象", operateInfo="新建了对象 ${args[0]} ")
	void create(IEntity entity);

	@Logable(operateObject="修改对象", operateInfo="修改了对象 ${args[0]} ")
	void update(IEntity entity);

	@Logable(operateObject="删除对象", operateInfo="删除了对象 ${args[0]} : ${args[1]} ")
	void delete(Class<?> entityClass, Long id);

	List<?> getList(String hql, Object...params);
	
	List<?> getList(String hql, String[] args, Object[] params);
	
	IEntity getEntity(Class<?> entityClass, Long id);

}
