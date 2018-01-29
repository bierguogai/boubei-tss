package com.boubei.tss.portal.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.um.permission.AbstractPermission;

/** 
 * 门户结构授权表
 */
@Entity
@Table(name = "portal_permission_portal")
public class PortalPermission extends AbstractPermission {
    
}

