package com.boubei.tss.dm.report.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.dm.report.Report;
import com.boubei.tss.um.permission.AbstractResource;

/** 
 * 数据报表资源视图 
 */
@Entity
@Table(name = "view_report_resource")
public class ReportResource extends AbstractResource {

	public String getResourceType() {
		return Report.RESOURCE_TYPE;
	}
}