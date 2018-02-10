package com.boubei.tssx.serialno;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.record.ARecordTable;

/**
 * 取号器：前缀YYYYMMDD四位递增数字
 */
@Entity
@Table(name = "x_serialno")
@SequenceGenerator(name = "serialno_sequence", sequenceName = "serialno_sequence", initialValue = 1, allocationSize = 10)
public class SerialNO extends ARecordTable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "serialno_sequence")
	private Long id;
	
	@Column(nullable = false, length = 12)
	private String precode;
	
	@Column(nullable = false)
	private Date day;
	
	private String domain;
	
	private int lastNum;

	public Serializable getPK() {
		return this.id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrecode() {
		return precode;
	}

	public void setPrecode(String precode) {
		this.precode = precode;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getLastNum() {
		return lastNum;
	}

	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}
}
