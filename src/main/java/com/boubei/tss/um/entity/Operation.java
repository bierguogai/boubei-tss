package com.boubei.tss.um.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.permission.dispaly.IPermissionOption;

/**
 * 权限操作选项。如新增、删除等操作
 */
@Entity
@Table(name = "um_operation", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "applicationId", "resourceTypeId", "operationId" })
})
@SequenceGenerator(name = "Operation_sequence", sequenceName = "Operation_sequence", initialValue = 1000, allocationSize = 10)
public class Operation implements IEntity, ITreeNode, IXForm, IPermissionOption, Serializable {
 
	private static final long serialVersionUID = 5488138848801729520L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "Operation_sequence")
	private Long    id;             // 权限选项ID:权限选项主键
	
	@Column(length = 50, nullable = false)  
	private String  name;           // 名称：权限选项名称
	
	@Column(length = 50, nullable = false)  
	private String  operationId;    // 操作Id
	
	@Column(length = 50, nullable = false)  
	private String  resourceTypeId; // 资源类型ID 
	
	@Column(length = 50, nullable = false)  
	private String  applicationId;  // 应用系统ID:主键
	
	private Integer seqNo;          // 权限选项序号 
  	private String  description;    // 描述:权限选项描述

	/**  （以opt+id,opt+id...方式保存依赖的权限id） */
	private String dependId;        // 权限项横向依赖的id

	/**
	 * 类型分为一下几种
	 * y1.一个节点打钩,所有子节点打钩
	 * y2.一个节点打钩,所有父节点打钩
	 * y3.一个节点打钩,所有子节点、父节点打钩
	 * n1.一个节点去钩,所有子节点去钩
	 * n2.一个节点去钩,所有父节点去钩
	 * n3.一个节点去钩,所有子节点、父节点去钩
	 */
	private String dependParent;    // 权限项纵向依赖的类型（1:向上兼向下，2:向上，3:向下）

	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public String getDependParent() {
		return dependParent;
	}
 
	public void setDependParent(String trigger) {
		this.dependParent = trigger;
	}
 
	public String getApplicationId() {
		return applicationId;
	}
 
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
 
	public String getDescription() {
		return description;
	}
 
	public void setDescription(String description) {
		this.description = description;
	}
 
	public String getDependId() {
		return dependId;
	}
 
	public void setDependId(String dependId) {
		this.dependId = dependId;
	}
 
	public String getOperationId() {
		return operationId;
	}
 
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String operationName) {
		this.name = operationName;
	}
 
	public Integer getSeqNo() {
		return seqNo;
	}
 
	public void setSeqNo(Integer operationOrder) {
		this.seqNo = operationOrder;
	}
 
	public String getResourceTypeId() {
		return resourceTypeId;
	}
 
	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public TreeAttributesMap getAttributes() {
		TreeAttributesMap map = new TreeAttributesMap("option-" + id, name);
		map.put("icon", UMConstants.OPERATION_TREENODE_ICON);
		map.put("nodeType", UMConstants.OPERATION_TREE_NODE);
		map.put("applicationId", applicationId);
		return map;
	}

	public Map<String, Object> getAttributes4XForm() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		map.put("applicationId", applicationId);
		map.put("resourceTypeId", resourceTypeId);
		map.put("operationId", operationId);
		map.put("description", description);
		return map;
	}

	public Map<String, Object> getOptionAttributes() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("operationId", "opt" + operationId);
		map.put("operationName", name);
		map.put("dependId", dependId);
		map.put("dependParent", dependParent);
		return map;
	}

	public void putOptionAttribute(String key, Object value) {	
	}
	
	public Serializable getPK() {
		return this.id;
	}
}
