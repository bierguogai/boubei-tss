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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;

@Entity
@Table(name = "dm_report_user")
@SequenceGenerator(name = "report_user_seq", sequenceName = "report_user_seq", initialValue = 1, allocationSize = 10)
public class ReportUser implements IEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "report_user_seq")
    private Long    id; 
    
    @Column(nullable = false)
	private Long reportId; // 报表ID
	
	@Column(nullable = false)
	private Long userId;  // 用户ID
	
	private Integer type;  // 1：收藏; 2：点赞 3：差评
	
    public ReportUser() { }
    
    public ReportUser(Long userId, Long reportId) {
    	this();
        this.setUserId(userId);
        this.setReportId(reportId);
    }
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public Long getUserId() {
		return userId;
	}
 
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Serializable getPK() {
		return this.getId();
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
