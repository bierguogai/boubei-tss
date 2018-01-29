package com.boubei.tss.um.entity.permission;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色关系表(包括继承的组的角色)
 */
@Entity
@Table(name = "um_roleusermapping")
public class RoleUserMapping implements Serializable{
	
	private static final long serialVersionUID = -2132184041472900185L;

	@EmbeddedId  
	private RoleUserMappingId id;
 
	public RoleUserMappingId getId() {
		return id;
	}
 
	public void setId(RoleUserMappingId id) {
		this.id = id;
	}
	
	public String toString() {
	    return id.toString();
	}
}

	