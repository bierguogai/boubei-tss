package com.boubei.tss.um.entity.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.um.permission.AbstractPermission;

/**
 * 角色应用资源操作表(补全的表)
 */
@Entity
@Table(name = "um_permission_role")
public class RolePermission extends AbstractPermission {

}
