package com.boubei.tssx.snapshot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.record.ARecordTable;

@Entity
@Table(name = "dm_snapshot")
@SequenceGenerator(name = "snapshot_sequence", sequenceName = "snapshot_sequence", initialValue = 1, allocationSize = 10)
public class Snapshot extends ARecordTable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "snapshot_sequence")
    private Long id; 
	
	@Column(nullable = false, length = 1000)
	private String ikey;
	
	@Lob
	@Column(nullable = false)
	private String ivalue;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Serializable getPK() {
		return this.getId();
	}

	public String getIkey() {
		return ikey;
	}

	public void setIkey(String ikey) {
		this.ikey = ikey;
	}

	public String getIvalue() {
		return ivalue;
	}

	public void setIvalue(String ivalue) {
		this.ivalue = ivalue;
	}
}
