package com.boubei.tss.um.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;

/**
 * 记录一个应用系统的一类资源的根ID
 */
@Entity
@Table(name = "um_resourcetype_root")
@SequenceGenerator(name = "resourcetypeRoot_sequence", sequenceName = "resourcetypeRoot_sequence", initialValue = 1000, allocationSize = 10)
public class ResourceTypeRoot implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "resourcetypeRoot_sequence")
	private Long   id;             // 主键
	
	private String applicationId;  // 应用系统ID
	private String resourceTypeId; // 资源类型ID
	private Long   rootId;         // 根节点ID
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public String getApplicationId() {
		return applicationId;
	}
 
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
 
	public String getResourceTypeId() {
		return resourceTypeId;
	}
 
	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}
 
	public Long getRootId() {
		return rootId;
	}
 
	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}
	
	public Serializable getPK() {
		return this.id;
	}
}

	