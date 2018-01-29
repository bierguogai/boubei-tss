package com.boubei.tss.portal.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;

/** 
 * 访问量统计实体
 */
@Entity
@Table(name = "portal_flowrate")
@SequenceGenerator(name = "flowRate_sequence", sequenceName = "flowRate_sequence", initialValue = 1, allocationSize = 50)
public class FlowRate implements IEntity {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "flowRate_sequence")
    private Long   id;
    private Long   pageId;
    private String ip;
    private Date   visitTime;
    
    public FlowRate(){
    }
    
    public FlowRate(Long pageId, String ip){
        this.setPageId(pageId);
        this.setIp(ip);
        this.setVisitTime(new Date());
    }
 
    public Long getId() {
        return id;
    }
 
    public String getIp() {
        return ip;
    }
 
    public Long getPageId() {
        return pageId;
    }
 
    public Date getVisitTime() {
        return visitTime;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setIp(String ip) {
        this.ip = ip;
    }
 
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
 
    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
    
	public Serializable getPK() {
		return this.id;
	}
}

