package com.boubei.tss.framework;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.boubei.tss.framework.persistence.IEntity;

@MappedSuperclass
public abstract class AbstractEntity implements IEntity {

	@Version
	private int lockVersion = 0;

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public abstract Long getId();

	public Serializable getPK() {
		return this.getId();
	}

	public int getLockVersion() {
		return lockVersion;
	}

	public void setLockVersion(int lockVersion) {
		this.lockVersion = lockVersion;
	}
	
	public boolean equals(Object obj) {
		return this.getPK().equals( ((IEntity)obj).getPK() );
	}
}
