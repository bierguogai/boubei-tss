package com.boubei.tss.framework.persistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.sso.Environment;

/**
 * <p> Temp.java </p>
 * 临时表，用以批量查询等操作，可取代in 查询。
 */
@Entity
@Table(name = "TBL_TEMP_")
@SequenceGenerator(name = "temp_sequence", sequenceName = "temp_sequence", initialValue = 1, allocationSize = 10)
public class Temp implements IEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "temp_sequence")
	private Long pk;
    
	private Long id; 
	private Long thread; // 当前线程ID，用以多线程场景
	
	private String udf1;
	private String udf2;
	private String udf3;
	
	public Temp() {
		this.setThread(Environment.threadID());
	}
	
	public Temp(Long id) {
		this();
		this.setId(id);
	}
    
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }
	
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		return "id=" + this.getId() + ", thread=" + this.thread;
	}

	public Long getThread() {
		return thread;
	}

	public void setThread(Long thread) {
		this.thread = thread;
	}
	
	public Serializable getPK() {
		return this.getPk();
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
	}
}

	