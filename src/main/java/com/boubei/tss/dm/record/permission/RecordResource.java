/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.record.permission;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.boubei.tss.dm.record.Record;
import com.boubei.tss.um.permission.AbstractResource;

/** 
 * 数据录入表资源视图 
 */
@Entity
@Table(name = "view_record_resource")
public class RecordResource extends AbstractResource {

	public String getResourceType() {
		return Record.RESOURCE_TYPE;
	}
}