/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.display.grid.GridAttributesMap;
import com.boubei.tss.framework.web.display.grid.IGridNode;
 
/**
 * 站内消息对象
 */
@Entity
@Table(name = "um_message")
@SequenceGenerator(name = "message_sequence", sequenceName = "message_sequence", initialValue = 1000, allocationSize = 10)
public class Message implements IEntity, IGridNode {
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "message_sequence")
	private Long   id;
	
	private String title;		// 标题
	
	@Column(length = 2000)
	private String content;		// 正文
	
	private Long   senderId;	// 发送者ID
	private String sender;		// 发送者
	private Long   receiverId;	// 接收者ID
	private String receiver;	// 接收者
	
	private Date   sendTime;    // 发送时间
	private Date   readTime;    // 读取时间
 
	@Transient
	private String receiverIds; // 接收者ID列表
	
	public String getContent() {
		return content;
	}
 
	public Long getId() {
		return id;
	}
 
	public String getReceiver() {
		return receiver;
	}
	
	public String getSender() {
		return sender;
	}
 
	public Date getSendTime() {
		return sendTime;
	}
 
	public String getTitle() {
		return title;
	}
 
	public void setContent(String content) {
		this.content = content;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setSender(String sender) {
		this.sender = sender;
	}
 
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
 
	public void setTitle(String title) {
		this.title = title;
	}
 
    public String getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(String receiverIds) {
        this.receiverIds = receiverIds;
    }

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Serializable getPK() {
		return this.getId();
	}

	public GridAttributesMap getAttributes(GridAttributesMap map) {
		String _class = readed() ? "" : "unread";
		map.put("id", id);
		map.put("_title", title);
		map.put("title", "<a href='javascript:void(0)' onclick='showMsgInfo("+this.id+")' class='" + _class + "'>" + title + "</a>");
		map.put("content", content);
		map.put("status", readed() ? 1 : 0);
		map.put("sender", sender);
		map.put("senderId", senderId);
		map.put("sendTime", this.sendTime);
		map.put("opts", "<a href='javascript:void(0)' onclick='showMsgInfo("+this.id+")'>查看</a>&nbsp;/&nbsp;" +
				"<a href='javascript:void(0)' onclick='replyMsg("+this.id+")'>回复</a>&nbsp;/&nbsp;" +
				"<a href='javascript:void(0)' onclick='deleteMsg("+this.id+")'>删除</a>");
		
		return map;
	}
	
	public boolean readed() {
		return this.readTime != null;
	}
}
