package com.boubei.tss.dm.etl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.record.ARecordTable;

/**
 * 1、report --> record   WashDataJob
 * 2、restful --> record  G7 ETL、用工考勤 ETL
 * 3、restful --> csv file  WMS ETL
 *    report --> csv file
 */
@Entity
@Table(name = "dm_etl_task")
@SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", initialValue = 1, allocationSize = 10)
public class Task extends ARecordTable {
	
	public static String STATUS_ON  = "opened"; 
	public static String STATUS_OFF = "closed";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "task_sequence")
    private Long id; 
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Long jobId;
	private String jobName;
	
	private Integer priority; // 优先级，按从大到小排序
	
	@Column(nullable = false)
	private String type; // daily/byID
	
	@Column(nullable = false)
	private String sourceDS;
	
	@Lob
	@Column(nullable = false)
	private String sourceScript;
	
	// 输出到DB DS、表空间.表、字段（同dataSQL查询出来的字段名及类型）
	@Column(nullable = false)
	private String targetDS;
	
	// maybe a record or sql(insert/update)
	@Column(nullable = false, length = 2000)
	private String targetScript;
	
	// 任务起始ID
	private Long startID;
	
	// 任务起始日期
	private Date startDay;
	
	// 重复抽取最近X日内的数据，这几日的数据可能上一日的抽取又有了新的变化
	private Integer repeatDays = 0;
	
	// 重抽数据前先执行这个SQL | ByID ETL时用以获取已有数据的最大ID
	private String preRepeatSQL;
	
	// 任务申请人
	@Column(nullable = false)
	private String applier;
	private Date applyDay;
	
	// 任务管理人
	private String manager;
	
	// 任务状态 new、opened、closed
	@Column(nullable = false)
	private String status;
	
	@Column(length = 2000)
	private String remark;
	
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceDS() {
		return sourceDS;
	}

	public void setSourceDS(String sourceDS) {
		this.sourceDS = sourceDS;
	}

	public String getTargetDS() {
		return targetDS;
	}

	public void setTargetDS(String targetDS) {
		this.targetDS = targetDS;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApplier() {
		return applier;
	}

	public void setApplier(String applier) {
		this.applier = applier;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartDay() {
		return startDay;
	}

	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}

	public Serializable getPK() {
		return this.getId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getApplyDay() {
		return applyDay;
	}

	public void setApplyDay(Date applyDay) {
		this.applyDay = applyDay;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Integer getRepeatDays() {
		return repeatDays;
	}

	public void setRepeatDays(Integer repeatDays) {
		this.repeatDays = repeatDays;
	}

	public String getSourceScript() {
		return sourceScript;
	}

	public void setSourceScript(String sourceScript) {
		this.sourceScript = sourceScript;
	}

	public String getTargetScript() {
		return targetScript;
	}

	public void setTargetScript(String targetScript) {
		this.targetScript = targetScript;
	}

	public String getPreRepeatSQL() {
		return preRepeatSQL;
	}

	public void setPreRepeatSQL(String preRepeatSQL) {
		this.preRepeatSQL = preRepeatSQL;
	}

	public Long getStartID() {
		return startID;
	}

	public void setStartID(Long startID) {
		this.startID = startID;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
