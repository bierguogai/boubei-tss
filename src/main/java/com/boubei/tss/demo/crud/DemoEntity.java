package com.boubei.tss.demo.crud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.modules.param.Param;

@Entity
@Table(name = "DEMO_ENTITY")
@SequenceGenerator(name = "demo_sequence", sequenceName = "demo_sequence", initialValue = 1000, allocationSize = 10)
public class DemoEntity implements IEntity  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "demo_sequence")
	private Long   	id;
    
    @Column(length = 50)
	private String 	code;
    
    @Column(length = 50)  
	private String 	name;
	
	private String 	udf1;
	private String 	udf2;
	private String 	udf3;
	
	@ManyToOne
	private Param state;
 
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
	public Serializable getPK() {
		return this.id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public Param getState() {
		return state;
	}

	public void setState(Param state) {
		this.state = state;
	}
}

