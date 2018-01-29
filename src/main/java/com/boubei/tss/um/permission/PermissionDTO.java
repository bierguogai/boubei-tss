package com.boubei.tss.um.permission;

import java.util.ArrayList;
import java.util.List;

import com.boubei.tss.util.EasyUtils;

/**
 * 用户资源权限DTO
 */
public class PermissionDTO {
    
	private Long    resourceId;    // 资源Id
	private Long    roleId;       // 用户Id
	private String  operationId; // 操作选项Id
	private Integer permissionState;  // 选项状态
	private Integer isGrant;         // 是否可授权（0-不可授权,1-可授权）
	private Integer isPass;         // 是否可传递（0-不可传递,1-可传递）
	
	public PermissionDTO(Object[] permissionInfo) {
	    this.resourceId      = EasyUtils.obj2Long( permissionInfo[0] );
	    this.operationId     = (String) permissionInfo[1];
        this.permissionState = EasyUtils.obj2Int( permissionInfo[2] );
        this.isGrant         = EasyUtils.obj2Int( permissionInfo[3] );
        this.isPass          = EasyUtils.obj2Int( permissionInfo[4] );
        this.roleId          = EasyUtils.obj2Long( permissionInfo[5] );
	}
	
	public static List<PermissionDTO> genPermissionDTOList(List<?> permissionList){
        List<PermissionDTO> result = new ArrayList<PermissionDTO>();
        for (Object permission : permissionList) {
            result.add(new PermissionDTO((Object[]) permission));
        }
        return result;
    }

    public Long getResourceId() {
        return resourceId;
    }
    
    public String getOperationId() {
        return operationId;
    }
    
    public Integer getIsGrant() {
        return isGrant;
    }

    public Integer getIsPass() {
        return isPass;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Integer getPermissionState() {
        return permissionState;
    }
}

	