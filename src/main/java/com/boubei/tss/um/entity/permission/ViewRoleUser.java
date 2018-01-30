/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.entity.permission;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色视图(包括用户从组继承的角色以及转授而得到的角色)
 */
@Entity
@Table(name = "view_roleuser")
public class ViewRoleUser {
	
	@EmbeddedId 
	private ViewRoleUserId id;// 主键Id
 
	public ViewRoleUserId getId() {
		return id;
	}
 
	public void setId(ViewRoleUserId id) {
		this.id = id;
	}
}

	