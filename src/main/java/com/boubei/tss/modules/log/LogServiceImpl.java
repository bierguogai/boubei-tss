package com.boubei.tss.modules.log;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.persistence.pagequery.PaginationQueryByHQL;
import com.boubei.tss.util.DateUtil;
 
@Service("LogService")
public class LogServiceImpl implements LogService {
    
    @Autowired private ICommonDao dao;
 
    public List<?> getAllOperateObjects() {
    	Date day = DateUtil.subDays(new Date(), 7);
        return dao.getEntities("select distinct o.operateTable from Log o " +
        		" where o.operateTime >= ? order by o.operateTable", day);
    }

    public PageInfo getLogsByCondition(LogQueryCondition condition) {
        String hql = " from Log o " 
        		+ " where 1=1 " + condition.toConditionString() 
        		+ " order by o.operateTime desc ";
 
        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(dao.em(), hql, condition);
        return pageQuery.getResultList();
    }

    public Log getLogById(Long id) {
        return (Log) dao.getEntity(Log.class, id);
    }
}

