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

