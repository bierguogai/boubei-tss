package com.boubei.tss.dm.record.file;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.dispaly.grid.GridAttributesMap;
import com.boubei.tss.framework.web.dispaly.grid.IGridNode;
import com.boubei.tss.util.DateUtil;

/**
 * <p>
 * 数据录入的附件
 * </p>
 * /download/{tableId}/{recordId}/{seqNo}
 */
@Entity
@Table(name = "dm_record_attach")
@SequenceGenerator(name = "record_attach_seq")
public class RecordAttach implements IEntity, IGridNode {
	
	public static final Integer ATTACH_TYPE_PIC = 1;	// 文章附件图片类型
    public static final Integer ATTACH_TYPE_DOC = 2;	// 文章附件OFFICE类型

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "record_attach_seq")
	private Long id;

	private Long itemId; // 所属数据行

	private Long recordId; // 所属数据录入表

	private Integer seqNo = 0; // 附件序号

	@Column(nullable = false)
	private Integer type; // 附件类型 1：图片 2：office文档

	@Column(nullable = false)
	private String name; // 原文件名

	@Column(nullable = false)
	private String fileName; // 附件名称
	private String fileExt;  // 文件后缀

	private Date   uploadDate; // 上传日期
	private String uploadUser; // 上传用户
	
	public String getAttachPath() {
		return getAttachDir(this.recordId, this.itemId) + "/" + this.fileName;
	}
	
	public static String getAttachDir(Long recordId, Long itemId) {
		String attachDir = DMUtil.getExportPath();
        attachDir = attachDir + "/" + recordId + "/" + itemId;
        
        return attachDir;
	}
	
	public String toString() {
		return "record attach: id:" + id + ", name:" + name + ", itemId:" + itemId + ", recordId:" + recordId;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Serializable getPK() {
		return this.id;
	}

	public boolean isImage() {
		return ATTACH_TYPE_PIC.equals(type);
	}

	public boolean isOfficeDoc() {
		return ATTACH_TYPE_DOC.equals(type);
	}

	public GridAttributesMap getAttributes(GridAttributesMap map) {
		map.put("id", this.id);
		map.put("name", this.name);
		map.put("type", this.type);
		map.put("uploadDate", DateUtil.formatCare2Second(uploadDate));
		map.put("uploadUser", this.uploadUser);
		map.put("url", this.getDownloadUrl());
		map.put("_url", "<a href='" + this.getDownloadUrl() + "' target='_blank'>查看</a>");
		map.put("delOpt", "<a href='javascript:void(0)' onclick='delAttach(" + this.getPK() + ")'>删除</a>");
		return map;
	}

	/**
	 * 绝对地址，返回格式类似：/tss/auth/xdata/attach/download/12
	 */
	public String getDownloadUrl() {
		return "/tss/auth/xdata/attach/download/" + getId();
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
}