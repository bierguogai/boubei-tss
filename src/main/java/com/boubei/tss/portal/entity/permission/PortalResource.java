/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.um.permission.AbstractResource;

/** 
 * 门户结构资源视图
 */
@Entity
@Table(name = "view_portal_resource")
public class PortalResource extends AbstractResource {
    
    public String getResourceType() {
        return PortalConstants.PORTAL_RESOURCE_TYPE;
    }

}

