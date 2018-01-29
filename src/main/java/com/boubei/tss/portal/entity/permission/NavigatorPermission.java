package com.boubei.tss.portal.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.um.permission.AbstractPermission;

/** 
 * 导航栏资源权限表
 */
@Entity
@Table(name = "portal_permission_navigator")
public class NavigatorPermission extends AbstractPermission {
    
}

