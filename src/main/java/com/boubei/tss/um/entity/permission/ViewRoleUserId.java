package com.boubei.tss.um.entity.permission;

import java.io.Serializable;

import javax.persistence.Embeddable;
 
@Embeddable
public class ViewRoleUserId implements Serializable {

	private static final long serialVersionUID = -1721497183867041324L;

	private Long userId; // 用户id
	private Long roleId; // 角色id
 
	public Long getRoleId() {
		return roleId;
	}
 
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return "roleId=" + this.getRoleId() + ",userId=" + this.getUserId();
	}
}

	