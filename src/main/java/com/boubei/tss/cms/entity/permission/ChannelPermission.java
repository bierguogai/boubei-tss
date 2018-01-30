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

import com.boubei.tss.um.permission.AbstractPermission;

/** 
 * 站点栏目资源权限表
 */
@Entity
@Table(name = "cms_permission_channel")
public class ChannelPermission extends AbstractPermission {

}

