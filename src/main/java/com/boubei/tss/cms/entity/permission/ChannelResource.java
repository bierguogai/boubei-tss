/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.um.permission.AbstractResource;

/** 
 * 站点栏目资源视图 
 */
@Entity
@Table(name = "view_channel_resource")
public class ChannelResource extends AbstractResource {

	public String getResourceType() {
		return CMSConstants.RESOURCE_TYPE_CHANNEL;
	}
}

