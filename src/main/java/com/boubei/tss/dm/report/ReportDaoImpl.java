/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.report;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.persistence.TreeSupportDao;

@Repository("ReportDao")
public class ReportDaoImpl extends TreeSupportDao<Report> implements ReportDao {

	public ReportDaoImpl() {
        super(Report.class);
    }
 
	public Report deleteReport(Report report, List<Report> children) {
        for(Report entity : children) {
        	deleteById(entity.getId());
        }
        return report;
	}

	public List<Report> getChildrenById(Long id, String operationId) {
		return getChildrenById(id);
	}

	public Report getVisibleReport(Long id) {
		return getEntity(id);
	}
}