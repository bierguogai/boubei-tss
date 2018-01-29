package com.boubei.tss.dm.report.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.boubei.tss.um.permission.AbstractPermission;

/** 
 *报表资源权限表
 */
@Entity
@Table(name = "dm_permission_report")
@JsonIgnoreProperties(value={"pk"})
public class ReportPermission extends AbstractPermission {

}