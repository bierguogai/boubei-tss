package com.boubei.tss.modules.search;

import java.util.LinkedHashMap;
import java.util.Map;

import com.boubei.tss.framework.web.display.grid.GridAttributesMap;
import com.boubei.tss.framework.web.display.grid.IGridNode;
import com.boubei.tss.util.BeanUtil;

/**
 * 综合查询：用户的因转授而获得的角色的情况
 */
public class SubAuthorizedUserRoleDTO implements IGridNode {
    
    private Long   roleId;       // 角色的id
    private String roleName;
    private Long   strategyId;   // 转授策略id
    private String strategyName; // 转授策略名字

	private Long   subAuthorized2UserId;    // 被转授的用户id
	private String subAuthorized2UserName;  // 被转授的用户名字
	private Long   subAuthorized2GroupId;
	private String subAuthorized2GroupName;
	    
	private Long   subAuthorizedUserId;     // 转授角色的用户id
	private String subAuthorizedUserName;   // 转授角色的用户名字
	private Long   subAuthorizedGroupId;    // 转授角色的用户所在组的ID
	private String subAuthorizedGroupName;

	public GridAttributesMap getAttributes(GridAttributesMap map) {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, properties);
        map.putAll(properties);
		return map;
	}

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public Long getSubAuthorized2UserId() {
        return subAuthorized2UserId;
    }

    public void setSubAuthorized2UserId(Long subAuthorized2UserId) {
        this.subAuthorized2UserId = subAuthorized2UserId;
    }

    public String getSubAuthorized2UserName() {
        return subAuthorized2UserName;
    }

    public void setSubAuthorized2UserName(String subAuthorized2UserName) {
        this.subAuthorized2UserName = subAuthorized2UserName;
    }

    public Long getSubAuthorized2GroupId() {
        return subAuthorized2GroupId;
    }

    public void setSubAuthorized2GroupId(Long subAuthorized2GroupId) {
        this.subAuthorized2GroupId = subAuthorized2GroupId;
    }

    public String getSubAuthorized2GroupName() {
        return subAuthorized2GroupName;
    }

    public void setSubAuthorized2GroupName(String subAuthorized2GroupName) {
        this.subAuthorized2GroupName = subAuthorized2GroupName;
    }

    public Long getSubAuthorizedUserId() {
        return subAuthorizedUserId;
    }

    public void setSubAuthorizedUserId(Long subAuthorizedUserId) {
        this.subAuthorizedUserId = subAuthorizedUserId;
    }

    public String getSubAuthorizedUserName() {
        return subAuthorizedUserName;
    }

    public void setSubAuthorizedUserName(String subAuthorizedUserName) {
        this.subAuthorizedUserName = subAuthorizedUserName;
    }

    public Long getSubAuthorizedGroupId() {
        return subAuthorizedGroupId;
    }

    public void setSubAuthorizedGroupId(Long subAuthorizedGroupId) {
        this.subAuthorizedGroupId = subAuthorizedGroupId;
    }

    public String getSubAuthorizedGroupName() {
        return subAuthorizedGroupName;
    }

    public void setSubAuthorizedGroupName(String subAuthorizedGroupName) {
        this.subAuthorizedGroupName = subAuthorizedGroupName;
    }
}
