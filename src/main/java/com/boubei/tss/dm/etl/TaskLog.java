package com.boubei.tss.dm.etl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.util.EasyUtils;

@Entity
@Table(name = "dm_etl_task_log")
@SequenceGenerator(name = "task_log_sequence", sequenceName = "task_log_sequence", initialValue = 1, allocationSize = 10)
public class TaskLog implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "task_log_sequence")
	private Long id;

	@Column(nullable = false)
	private Long taskId;

	private String taskName;
	private Date excuteTime;
	private long runningMS;

	@Column(nullable = false, length = 2000)
	private String detail;

	private String exception; // yes|no

	private String dataDay; // 数据日期
	private Long maxID; // 数据最大ID
	
	public TaskLog() { 
	}

	public TaskLog(Task task) {
        this.setExcuteTime(new Date());
        this.setTaskId(task.getId());
        this.setTaskName(task.getName());
	}
	
	public String toString() {
		return taskName + ", " + EasyUtils.checkNull(dataDay, maxID) + ", " + detail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getExcuteTime() {
		return excuteTime;
	}

	public void setExcuteTime(Date excuteTime) {
		this.excuteTime = excuteTime;
	}

	public long getRunningMS() {
		return runningMS;
	}

	public void setRunningMS(long runningMS) {
		this.runningMS = runningMS;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDataDay() {
		return dataDay;
	}

	public void setDataDay(String dataDay) {
		this.dataDay = dataDay;
	}

	public Long getMaxID() {
		return maxID;
	}

	public void setMaxID(Long maxID) {
		this.maxID = maxID;
	}

	public Serializable getPK() {
		return this.getId();
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
}
