/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.permission.AbstractResource;

/**
 * 主用户组资源视图
 * </p>
 */
@Entity
@Table(name = "view_group_resource")
public class GroupResource extends AbstractResource {
 
    public String getResourceType() {
		return UMConstants.GROUP_RESOURCE_TYPE_ID;
	}
}
