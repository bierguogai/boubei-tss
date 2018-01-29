package com.boubei.tss.um.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.IEntity;

/**
 * 角色与用户组关联对象
 */
@Entity
@Table(name = "um_rolegroup", uniqueConstraints = { @UniqueConstraint(columnNames = {"roleId", "groupId", "strategyId" }) })
@SequenceGenerator(name = "rolegroup_sequence", sequenceName = "rolegroup_sequence", initialValue = 10000, allocationSize = 10)
public class RoleGroup implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rolegroup_sequence")
	private Long id; // 主键Id
	
	private Long groupId;    // 用户组Id
	private Long roleId;     // 角色Id
	private Long strategyId; // 策略Id
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public Long getGroupId() {
		return groupId;
	}
 
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
 
	public Long getRoleId() {
		return roleId;
	}
 
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
 
	public Long getStrategyId() {
		return strategyId;
	}
 
	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}
	
	public Serializable getPK() {
		return this.id;
	}
}