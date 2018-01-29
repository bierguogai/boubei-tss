package com.boubei.tss.modules.search;

import com.boubei.tss.framework.web.display.grid.GridAttributesMap;
import com.boubei.tss.framework.web.display.grid.IGridNode;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.User;

public class UserRoleDTO implements IGridNode {
    
	public Long userId;
	public Long roleId;
	public String userName;
	public String roleName;
	public String description;
 
	public UserRoleDTO(User user, Role role) {
		this.userId = user.getId();
		this.userName = user.getUserName();
		this.roleId = role.getId();
		this.roleName = role.getName();
		this.description = role.getDescription();
	}

	public GridAttributesMap getAttributes(GridAttributesMap map) {
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("roleId", roleId);
        map.put("roleName", roleName);
        map.put("description", description);
        
		return map;
	}
}
