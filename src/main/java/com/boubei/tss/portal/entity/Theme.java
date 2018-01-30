/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.entity;

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
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;

/**
 * 主题实体：包括门户结构以及相对应的布局、修饰器等配置信息
 */
@Entity
@Table(name = "portal_theme", uniqueConstraints = { 
        @UniqueConstraint(name="MULTI_NAME_Theme", columnNames = { "portalId", "name" })
})
@SequenceGenerator(name = "theme_sequence", sequenceName = "theme_sequence", initialValue = 1)
public class Theme extends OperateInfo implements IEntity, ITreeNode {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "theme_sequence")
	private Long    id;
	
	@Column(nullable = false)
	private String  name;   // 主题名称
	
    private Long    portalId;    // 主题所属的门户ID
	
    public Theme() { }
    
    public Theme(Long themeId) {
        this.id = themeId;
    }

    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);
        map.put("icon", "images/theme.gif");
        return map;
    }
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
 
    public Long getPortalId() {
        return portalId;
    }
 
    public void setPortalId(Long portalId) {
        this.portalId = portalId;
    }
 
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Theme other = (Theme) obj;
		return other.getId().equals(this.getId());
    }
	
	public Serializable getPK() {
		return this.id;
	}
}
