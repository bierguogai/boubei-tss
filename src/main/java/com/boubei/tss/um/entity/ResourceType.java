/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

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
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.BeanUtil;

/**
 * <pre>
 * 资源类型域对象
 * permissionTable 
 * resourceTable 
 * 
 * 资源分类：私人资源、角色资源、公共资源
 * 资源权限：资源与操作的一套组合，例如“增加用户”是一种权限，“删除用户”是一种权限。
 * 超级管理员：这个角色是神一般的存在，能无视一切阻碍，对所有资源拥有绝对权限，甭管你是私人资源还是角色资源。
 * 
 * 资源权限过滤策略：
 * SessionAuthPolicy： 检测用户是否已经登录，用户登录是进行下面检测的前提。
 * SourcePolicy： 检测访问的资源是否存在，主要检测Source表的记录
 * PermissionPolicy：检测该用户所属的角色，是否有对所访问资源进行对应操作的权限。
 * OwnerPolicy： 如果所访问的资源属于私人资源，则检测当前用户是否该资源的拥有者。
 * 如果通过所有policy的检测，则把请求转发到目标action。
 * </pre>
 */
@Entity
@Table(name = "um_resourcetype", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "applicationId", "resourceTypeId" })
})
@SequenceGenerator(name = "resourcetype_sequence", sequenceName = "resourcetype_sequence", initialValue = 1000, allocationSize = 10)
public class ResourceType extends OperateInfo implements IEntity, ITreeNode, IXForm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "resourcetype_sequence")
	private Long    id; 
	
	@Column(length = 50, nullable = false)  
	private String  applicationId;  // 应用系统Code 
	
	@Column(length = 50, nullable = false)  
	private String  resourceTypeId; // 资源类型Key 
	
	@Column(length = 50, nullable = false)  
	private String  name;           // 资源类型名称  
	private Integer seqNo;          // 资源类型编号 
	private String  description;    // 描述  
    
    private Long   rootId;          // 根节点ID
    
    @Column(nullable = false) 
    private String permissionTable; // 角色资源权限表的类路径
    
    @Column(nullable = false) 
    private String resourceTable;  // 资源表的类路径
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
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
 
	public String getResourceTypeId() {
		return resourceTypeId;
	}
 
	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String resourceTypeName) {
		this.name = resourceTypeName;
	}
 
	public Integer getSeqNo() {
		return seqNo;
	}
 
	public void setSeqNo(Integer resourceTypeOrder) {
		this.seqNo = resourceTypeOrder;
	}
	 
	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}
 
	public String getPermissionTable() {
		return permissionTable;
	}
 
	public String getResourceTable() {
		return resourceTable;
	}
 
    public void setPermissionTable(String permissionTable) {
        this.permissionTable = permissionTable;
    }

    public void setResourceTable(String resourceTable) {
        this.resourceTable = resourceTable;
    }
 
	public TreeAttributesMap getAttributes() {
		TreeAttributesMap map = new TreeAttributesMap("r-" + id, name);
		map.put("_nodeType", UMConstants.RESOURCETYPE_TREE_NODE);
		map.put("resourceTypeId", resourceTypeId);
		map.put("applicationId", applicationId);
		
		map.put("icon", "images/resource_type.gif");
		return map;
	}
 
	public Map<String, Object> getAttributes4XForm() {
		Map<String, Object> map = new HashMap<String, Object>();
		BeanUtil.addBeanProperties2Map(this, map);
		return map;
	}
 
	public int hashCode() {
	    return (this.applicationId + "_" + this.resourceTypeId).hashCode();
	}
	
	public Serializable getPK() {
		return this.id;
	}
}
