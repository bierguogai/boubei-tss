package com.boubei.tss.cms.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.um.permission.AbstractPermission;

/** 
 * 站点栏目资源权限表
 */
@Entity
@Table(name = "cms_permission_channel")
public class ChannelPermission extends AbstractPermission {

}

