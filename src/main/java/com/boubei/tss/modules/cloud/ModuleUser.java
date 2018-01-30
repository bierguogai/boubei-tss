/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.cloud;

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
@Table(name = "cloud_module_user")
@SequenceGenerator(name = "module_user_seq", sequenceName = "module_user_seq", initialValue = 1, allocationSize = 10)
public class ModuleUser implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "module_user_seq")
	private Long id;

	@Column(nullable = false)
	private Long moduleId; // 模块ID

	@Column(nullable = false)
	private Long userId; // 用户ID

	public ModuleUser() {
	}

	public ModuleUser(Long userId, Long moduleId) {
		this();
		this.setUserId(userId);
		this.setModuleId(moduleId);
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

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

}
