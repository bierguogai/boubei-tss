package com.boubei.tss.um.helper;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.MacrocodeQueryCondition;

/**
 * 用户查询条件对象
 */
public class UMQueryCondition extends MacrocodeQueryCondition {
	
	private Long groupId; // 用户组Id
	private Collection<Long> groupIds; // 用户组Ids
	
	private String loginName;  // 用户名
	private String userName;   // 姓名
	private String employeeNo; // 员工编号
	private Date   birthday;   // 出生年月
	private String certificateNo; // 证件号
	
	protected String getCreatorIdField() {
		return "u.creatorId";
	}
	
    public Map<String, Object> getConditionMacrocodes() {
        Map<String, Object> map = super.getConditionMacrocodes();
        
        map.put("${loginName}",  " and u.loginName  like :loginName");
        map.put("${userName}",   " and u.userName   like :userName");
        map.put("${employeeNo}", " and u.employeeNo like :employeeNo");
        map.put("${birthday}",   " and u.birthday >= :birthday");
        map.put("${certificateNo}", " and u.certificateNo like :certificateNo");
        
        return map;
    }

	public Date getBirthday() {
		return birthday;
	}
 
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
 
	public String getCertificateNo() {
        if(certificateNo != null){
        	certificateNo = "%" + certificateNo.trim() + "%";           
        }
		return certificateNo;
	}
 
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
 
	public String getEmployeeNo() {
        if(employeeNo != null){
        	employeeNo = "%" + employeeNo.trim() + "%";           
        }
		return employeeNo;
	}
 
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
 
	public Long getGroupId() {
		return groupId;
	}
 
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
 
	public String getLoginName() {
		if(loginName != null){
			loginName = "%" + loginName.trim() + "%";           
        }
		return loginName;
	}
 
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
 
	public String getUserName() {
        if(userName != null){
        	userName = "%" + userName.trim() + "%";           
        }
		return userName;
	}
 
	public void setUserName(String userName) {
		this.userName = userName;
	}
 
	public Collection<Long> getGroupIds() {
		return groupIds;
	}
 
	public void setGroupIds(Collection<Long> groupIds) {
		this.groupIds = groupIds;
	}
}
