package com.boubei.tss.um.entity.permission;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户拥有的能转授的角色视图(包括用户从组继承的角色)
 * 转授（SubAuthorize 子授权）
 */
@Entity
@Table(name = "view_roleuser4subauthorize")
public class ViewRoleUser4SubAuthorize {
	
	@EmbeddedId 
	private ViewRoleUserId id; 
 
	public ViewRoleUserId getId() {
		return id;
	}
 
	public void setId(ViewRoleUserId id) {
		this.id = id;
	}
}

	