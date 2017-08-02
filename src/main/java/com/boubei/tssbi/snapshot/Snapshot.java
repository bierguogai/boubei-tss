package com.boubei.tssbi.snapshot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.record.AbstractRecordTable;

@Entity
@Table(name = "dm_snapshot")
@SequenceGenerator(name = "snapshot_sequence", sequenceName = "snapshot_sequence", initialValue = 1, allocationSize = 10)
public class Snapshot extends AbstractRecordTable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "snapshot_sequence")
    private Long id; 
	
	@Lob
	@Column(nullable = false)
	private String key;
	
	@Lob
	@Column(nullable = false)
	private String value;
	
	private String cacheLife; // @see CacheLife

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCacheLife() {
		return cacheLife;
	}

	public void setCacheLife(String cacheLife) {
		this.cacheLife = cacheLife;
	}

	public Serializable getPK() {
		return this.getId();
	}
}
