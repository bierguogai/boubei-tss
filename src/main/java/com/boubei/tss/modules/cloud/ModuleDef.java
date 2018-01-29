package com.boubei.tss.modules.cloud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.boubei.tss.dm.record.ARecordTable;

@Entity
@Table(name = "cloud_module_def")
@SequenceGenerator(name = "module_def_seq", sequenceName = "module_def_seq", initialValue = 1, allocationSize = 10)
@JsonIgnoreProperties(value={"pk"})
public class ModuleDef extends ARecordTable {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "module_def_seq")
    private Long id; 
    
	@Column(nullable = false)
	private String module;
	
	private String kind;
	
	@Column(nullable = false)
	private String roles;
	
	@Column(length = 500)
	private String resource; // 资源目录，多个用逗号分隔
	
	private String status; // creating|opened|closed
	
	@Column(length = 2000)
	private String description;
	
	@Column(length = 2000)
	private String remark;
	
	public Serializable getPK() {
		return this.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
}
