package com.boubei.tss.modules.log;

import java.util.List;

import com.boubei.tss.framework.persistence.pagequery.PageInfo;
 
public interface LogService {

    /**
     * 获取日志中所有的操作对象
     * @return
     */
    List<?> getAllOperateObjects();

    /**
     * 根据查询条件和分页信息获取日志列表
     * @param condition
     * @return
     */
    PageInfo getLogsByCondition(LogQueryCondition condition);

    /**
     * 根据主键值ID获取对象
     * @param id
     * @return
     */
    Log getLogById(Long id);
}

