package com.boubei.tss.um.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.IEntity;

/**
 * 用户与用户组关联对象
 */
@Entity
@Table(name = "um_groupuser", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "groupId", "userId" })
})
@SequenceGenerator(name = "groupuser_sequence", sequenceName = "groupuser_sequence", initialValue = 10000, allocationSize = 10)
public class GroupUser implements IEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "groupuser_sequence")
	private Long id;  
	
	@Column(nullable = false)
	private Long groupId; // 用户组ID
	
	@Column(nullable = false)
	private Long userId;  // 用户ID
    
    public GroupUser() { }
    
    public GroupUser(Long userId, Long groupId) {
        this.setUserId(userId);
        this.setGroupId(groupId);
    }
 
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
