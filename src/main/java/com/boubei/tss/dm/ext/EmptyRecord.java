/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.ext;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.record.ARecordTable;

/**
 * 空表，用来对应纯链接录入表目录
 * 
[
  {'label':'x','code':'x'}
]
 */
@Entity
@Table(name = "dm_empty")
@SequenceGenerator(name = "dm_empty_seq", sequenceName = "dm_empty_seq", initialValue = 1, allocationSize = 10)
public class EmptyRecord extends ARecordTable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "dm_empty_seq")
    private Long id; 
    
	private String x;
	
	public Serializable getPK() {
		return this.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}
}
