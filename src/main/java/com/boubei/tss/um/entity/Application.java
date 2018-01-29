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

import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.BeanUtil;

/**
 * 应用系统域对象
 */
@Entity
@Table(name = "um_application", uniqueConstraints = { 
        @UniqueConstraint(name = "MULTI_NAME_APPLICATION ", columnNames = { "name" }),
        @UniqueConstraint(name = "MULTI_ID_APPLICATION", columnNames = { "applicationId" })
})
@SequenceGenerator(name = "application_sequence", sequenceName = "application_sequence", initialValue = 1000, allocationSize = 10)
public class Application extends OperateInfo implements ITreeNode, IXForm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "application_sequence")
    private Long    id; 
	
	@Column(length = 50, nullable = false)  
	private String  applicationId;   // 应用系统Code
	
	@Column(length = 50, nullable = false)  
	private String  name;            // 应用系统名称
	private String  description;     // 应用系统描述
	
	@Column(length = 10)
	private String  applicationType = UMConstants.PLATFORM_SYSTEM_APP; // 应用系统种类，默认为平台系统
	
	private Integer dataSourceType;  // 应用系统用户库接口类型（数据库、LDAP）   1:ldap  2:MySQL/Oracle/H2等
    
	@Column(length = 2000)
	private String  paramDesc;       // 参数描述xml格式(连接到其他应用系统的参数集合)

	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public String getApplicationId() {
		return applicationId;
	}
 
	public void setApplicationId(String applicationCode) {
		this.applicationId = applicationCode;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String applicationName) {
		this.name = applicationName;
	}
 
	public String getApplicationType() {
		return applicationType;
	}
 
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
 
	public Integer getDataSourceType() {
		return dataSourceType;
	}
 
	public void setDataSourceType(Integer dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
 
	public String getDescription() {
		return description;
	}
 
	public void setDescription(String description) {
		this.description = description;
	}
 
	public String getParamDesc() {
		return paramDesc;
	}
 
	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}
 
	public TreeAttributesMap getAttributes() {
		Object id = this.id;
		if(UMConstants.PLATFORM_SYSTEM_APP.equals(applicationType)) {
			id = "tssApp" + this.id;
		}
		TreeAttributesMap map = new TreeAttributesMap(id, name);
		
		map.put("code", applicationId);
		map.put("applicationType", applicationType);
		map.put("nodeType", UMConstants.APPLICATION_TREE_NODE);
		map.put("icon", UMConstants.APPLICATION_TREENODE_ICON);

		return map;
	}
 
	public Map<String, Object> getAttributes4XForm() {
		Map<String, Object> map = new HashMap<String, Object>();
		BeanUtil.addBeanProperties2Map(this, map);
		return map;
	}

	public Serializable getPK() {
		return this.id;
	}
}
