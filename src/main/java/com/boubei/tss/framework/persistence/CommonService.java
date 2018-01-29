package com.boubei.tss.framework.persistence;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.persistence.IEntity;

@Service("CommonService")
public class CommonService implements ICommonService {
	
	@Autowired private ICommonDao commonDao;

	public void create(IEntity entity) {
		commonDao.createObject(entity);
	}
	
	public void createWithoutLog(IEntity entity) {
		commonDao.createObject(entity);
	}

	public void update(IEntity entity) {
		commonDao.update(entity);
	}

	public void delete(Class<?> entityClass, Long id) {
		commonDao.delete(entityClass, id);
	}

	public List<?> getList(String hql, Object...params) {
		return commonDao.getEntities(hql, params);
	}
	
	public List<?> getList(String hql, String[] args, Object[] params) {
		return commonDao.getEntities(hql, args, params);
	}
	
	public IEntity getEntity(Class<?> entityClass, Long id) {
		return commonDao.getEntity(entityClass, id);
	}
}
