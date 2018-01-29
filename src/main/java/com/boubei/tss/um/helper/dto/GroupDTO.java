package com.boubei.tss.um.helper.dto;

import java.io.Serializable;

import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.EasyUtils;

/**
 * 同步使用的DTO
 */
public class GroupDTO implements Serializable {

    private static final long serialVersionUID = 2427966217868833368L;
    
    private String  id;       // 用户组主键
	private String  parentId; // 父节点ID 
	private String  name;     // 用户组名称
	private String  description;  // 用户组信息描述
	private Integer disabled;     // 用户组状态
	
	public String getId() {
		return id;
	} 
	
	public String getParentId() {
		return parentId;
	}
 
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Integer getDisabled() {
		return (Integer) EasyUtils.checkNull(disabled, ParamConstants.FALSE);
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
 
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
}

	