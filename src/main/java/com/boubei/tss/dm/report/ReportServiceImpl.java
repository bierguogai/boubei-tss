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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.EX;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.EasyUtils;

@Service("ReportService")
public class ReportServiceImpl implements ReportService {
	
	Logger log = Logger.getLogger(this.getClass());
    
    @Autowired ReportDao reportDao;
    
    public Report getReport(Long id, boolean auth) {
        Report report;
        if(auth) {
        	report = reportDao.getVisibleReport(id); // 如没有访问权限，将抛出异常
        } else {
        	report = reportDao.getEntity(id);
        }
        
        if(report == null) {
			throw new BusinessException( EX.parse(EX.DM_18, id) );
        }
        reportDao.evict(report);
        return report;
    }
    
    public Report getReport(Long id) {
        boolean auth;
        if( Environment.isRobot() ) { // 定时JOB
        	auth = false;
        } else {
        	auth = SecurityUtil.isSafeMode();
        }
		return this.getReport(id, auth);
    }
    
	public Long getReportId(String fname, Object idOrName, int type) {
		String hql = "select o.id from Report o where o." +fname+ " = ? and type = ? order by o.decode";
		List<?> list = reportDao.getEntities(hql, idOrName, type); 
		if(EasyUtils.isNullOrEmpty(list)) {
			return null;
		}
		return (Long) list.get(0);
	}
	
	// 加userId以便于缓存
    public List<Report> getReportsByGroup(Long groupId, Long userId) {
        return reportDao.getChildrenById(groupId);
    }
    
    @SuppressWarnings("unchecked")
    public List<Report> getAllReport() {
        return (List<Report>) reportDao.getEntities("from Report o order by o.decode");
    }
    
    @SuppressWarnings("unchecked")
    public List<Report> getAllReportGroups() {
        return (List<Report>) reportDao.getEntities("from Report o where o.type = ? order by o.decode", Report.TYPE0);
    }

    public Report createReport(Report report) {
        Long parentId = report.getParentId();
        Report parent = reportDao.getEntity(parentId);
        if( (parent == null || parent.isActive() ) && report.isGroup() ) {
        	report.setDisabled( ParamConstants.TRUE ); // 报表默认为停用，组看父组的状态
        }
        
		report.setSeqNo(reportDao.getNextSeqNo(parentId));
        reportDao.create(report);

        return report;
    }
    
    public void updateReport(Report report) {
    	reportDao.refreshEntity(report);
    }
    
    public Report delete(Long id) {
    	 Report report = getReport(id);
         List<Report> children = reportDao.getChildrenById(id, Report.OPERATION_DELETE); // 一并删除子节点
         return reportDao.deleteReport(report, children);
    }

    public void startOrStop(Long reportId, Integer disabled) {
        List<Report> list = ParamConstants.TRUE.equals(disabled) ? 
                reportDao.getChildrenById(reportId, Report.OPERATION_DISABLE) : reportDao.getParentsById(reportId);
        
        for (Report report : list) {
            report.setDisabled(disabled);
            reportDao.updateWithoutFlush(report);
        }
        reportDao.flush();
    }

    public void sort(Long startId, Long targetId, int direction) {
        reportDao.sort(startId, targetId, direction);
    }

    public List<Report> copy(Long reportId, Long groupId) {
        Report report = getReport(reportId);
        
        reportDao.evict(report);
        report.setId(null);
        report.setParentId(groupId);
        report.setSeqNo(reportDao.getNextSeqNo(groupId));
        report.setDisabled(ParamConstants.TRUE); // 新复制出来的节点都为停用状态
        
        report = reportDao.create(report);
        List<Report> list = new ArrayList<Report>();
        list.add(report);
        
        return list;
    }

    public void move(Long reportId, Long groupId) {
        List<Report> list  = reportDao.getChildrenById(reportId);
        Report reportGroup = reportDao.getEntity(groupId);
        for (Report temp : list) {
            if (temp.getId().equals(reportId)) { // 判断是否是移动节点（即被移动枝的根节点）
                temp.setSeqNo(reportDao.getNextSeqNo(groupId));
                temp.setParentId(groupId);
            }
            
            // reportGroup有可能是“全部”节点
            if (reportGroup != null && !reportGroup.isActive() ) {
                temp.setDisabled(ParamConstants.TRUE); // 如果目标根节点是停用状态，则所有新复制出来的节点也一律为停用状态
            }
            
            reportDao.moveEntity(temp);
        }
    }
    
  	public SQLExcutor queryReport(Long reportId, Map<String, String> requestMap, 
  			int page, int pagesize, Object cacheFlag) {
    	
    	Report report = this.getReport(reportId);
    	return ReportQuery.excute(report, requestMap, page, pagesize);
    }
   
}