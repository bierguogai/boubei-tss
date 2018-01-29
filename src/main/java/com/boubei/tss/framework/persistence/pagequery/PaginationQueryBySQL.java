package com.boubei.tss.framework.persistence.pagequery;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * SQL分页条件查询工具
 */
public class PaginationQueryBySQL extends PaginationQuery {
	
	/** 原生SQL查询时候的返回结果类 */
	private Class<?> resultClass; 
	
    public PaginationQueryBySQL(EntityManager em, String sql, 
            MacrocodeQueryCondition condition) {
        
        super(em, sql, condition);
    }
 
    protected Query createQuery(String ql) {
    	if(resultClass != null) {
    		return em.createNativeQuery(ql, resultClass);
    	}
    	return em.createNativeQuery(ql);
    }

	public void setResultClass(Class<?> resultClass) {
		this.resultClass = resultClass;
	}
}
