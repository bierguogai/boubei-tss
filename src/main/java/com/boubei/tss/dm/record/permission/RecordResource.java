package com.boubei.tss.dm.record.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.dm.record.Record;
import com.boubei.tss.um.permission.AbstractResource;

/** 
 * 录入表资源视图 
 */
@Entity
@Table(name = "view_record_resource")
public class RecordResource extends AbstractResource {

	public String getResourceType() {
		return Record.RESOURCE_TYPE;
	}
}