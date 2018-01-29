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
 * 角色与用户关联对象。
 * 策略可以授予用户、也可以授予角色，或者两者兼有。
 */
@Entity
@Table(name = "um_roleuser", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "roleId", "userId", "strategyId" })
})
@SequenceGenerator(name = "roleuser_sequence", sequenceName = "roleuser_sequence", initialValue = 10000, allocationSize = 10)
public class RoleUser implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "roleuser_sequence")
	private Long id;         // 主键Id
    
	private Long roleId;     // 角色Id
	private Long userId;     // 用户Id
	private Long strategyId; // 策略Id
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
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
 
	public Long getUserId() {
		return userId;
	}
 
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Serializable getPK() {
		return this.id;
	}
}
