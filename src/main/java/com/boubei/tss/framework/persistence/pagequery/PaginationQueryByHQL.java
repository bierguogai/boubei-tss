/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

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
