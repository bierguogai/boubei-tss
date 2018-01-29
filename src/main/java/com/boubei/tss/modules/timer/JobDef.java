package com.boubei.tss.modules.timer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.dm.record.ARecordTable;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.EasyUtils;

/**
 * 保存后再afterListener 里刷新当前的Job池
 */
@Entity
@Table(name = "component_job_def", uniqueConstraints = { 
        @UniqueConstraint(name = "code_unique", columnNames = { "code" })
})
@SequenceGenerator(name = "job_def_seq", sequenceName = "job_def_seq", initialValue = 1, allocationSize = 10)
public class JobDef extends ARecordTable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "job_def_seq")
    private Long id; 
    
	/** 定时Job名称 */
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String code;
	
	/** 定时Job执行类 */
	@Column(nullable = false)
	private String jobClassName;
	
	/** 定时策略 */
	@Column(nullable = false)
	private String timeStrategy;
	
	/** 定时Job自定义配置 */
	@Column(length = 2000)
	private String customizeInfo;
	
	/** 状态：停用、启用 */
	@Column(nullable = false)
	private Integer disabled = ParamConstants.FALSE;
	
	@Column(length = 2000)
	private String remark;
	
	private String description;
	
	public JobDef() { 
	}
	
	public JobDef(Param p) {
		String text  = p.getText();
		String value = p.getValue();
		
		this.setName(text);
		this.setCode("Job-p-" + p.getId());
		
		String configs[] = EasyUtils.split(value, "|");
		this.setJobClassName (configs[0].trim());
		this.setTimeStrategy (configs[1].trim());
		this.setCustomizeInfo(configs[2].trim());
		this.setDisabled(p.getDisabled());
	}
	
	public boolean equals(Object obj) {
        if(obj instanceof JobDef){
        	JobDef def = (JobDef) obj;
            return EasyUtils.obj2String(this.jobClassName).equals(def.getJobClassName())
                    && EasyUtils.obj2String(this.timeStrategy).equals(def.getTimeStrategy())
                    && EasyUtils.obj2String(this.customizeInfo).equals(def.getCustomizeInfo())
                    && this.disabled.equals(def.getDisabled());
        }
        return false;
    }
	
	public boolean isDisabled() {
		return ParamConstants.TRUE.equals( this.getDisabled() );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	public String getTimeStrategy() {
		return timeStrategy;
	}

	public void setTimeStrategy(String timeStrategy) {
		this.timeStrategy = timeStrategy;
	}

	public String getCustomizeInfo() {
		return customizeInfo;
	}

	public void setCustomizeInfo(String customizeInfo) {
		this.customizeInfo = customizeInfo;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Serializable getPK() {
		return this.getId();
	}
}
