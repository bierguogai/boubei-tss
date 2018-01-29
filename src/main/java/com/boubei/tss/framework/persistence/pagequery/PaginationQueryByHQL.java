package com.boubei.tss.framework.persistence.pagequery;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * HQL分页条件查询工具
 */
public class PaginationQueryByHQL extends PaginationQuery {
 
    public PaginationQueryByHQL(EntityManager em, String hql, 
            MacrocodeQueryCondition condition) {
        
        super(em, hql, condition);
    }
 
    protected Query createQuery(String hql) {
        return em.createQuery(hql);
    }
    
}
