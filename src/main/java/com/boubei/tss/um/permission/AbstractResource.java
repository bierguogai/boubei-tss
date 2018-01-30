/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.permission;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;

/** 
 * 可授权资源实体的抽象类
 */
@MappedSuperclass
public abstract class AbstractResource implements IEntity, IResource {
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long   id;       // 主键Id
    protected Long   parentId; // 父节点ID 
    
    @Column(nullable = false)  
    protected String name;   // 名称:资源名称
    
    protected String decode; // 层码

    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getParentId() {
        return parentId;
    }
 
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getDecode() {
        return decode;
    }
 
    public void setDecode(String decode) {
        this.decode = decode;
    }
 
	public Serializable getPK() {
		return this.id;
	}

	public Integer getSeqNo() {
		return null;
	}
	
    public TreeAttributesMap getAttributes() {
        return new TreeAttributesMap(id, name);
    }
}

