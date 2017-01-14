package com.boubei.tss.demo.crud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.persistence.pagequery.PaginationQueryByHQL;
 
@Service("DemoService")
public class DemoServiceImpl implements DemoService {
    
    @Autowired private DemoDao dao;

	public DemoEntity getEntityById(Long id) {
		return dao.getEntity(id);
	}

	@SuppressWarnings("unchecked")
	public List<DemoEntity> getAllEntities() {
		return (List<DemoEntity>) dao.getEntities("from DemoEntity");
	}

	public DemoEntity create(DemoEntity entity) {
    	// 检查账号是否已经存在
    	List<?> list = dao.getEntities("from DemoEntity o where o.code = ?", entity.getCode());
    	if(list.size() > 0) {
    		throw new BusinessException("相同Code的记录已经存在。");
    	}
    	
		return dao.create(entity);
	}
	
	public DemoEntity update(DemoEntity entity) {
		return (DemoEntity) dao.update(entity);
	}
	
	public DemoEntity delete(Long id) {
		return dao.deleteById(id);
	}
 
    public PageInfo search(DemoSO so) {
        String hql = " from DemoEntity o where 1=1 " + so.toConditionString();
 
        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(dao.em(), hql, so);
        return pageQuery.getResultList();
    }
}

