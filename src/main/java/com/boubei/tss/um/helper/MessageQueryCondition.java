package com.boubei.tss.um.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.MacrocodeQueryCondition;

/**
 * 站内信查询条件对象
 */
public class MessageQueryCondition extends MacrocodeQueryCondition {

	private Long   receiverId;
	private String sender;  // 发件人
	private String title;   // 站内信标题
	private String content; // 站内信内容
	private Date searchTime1;
	private Date searchTime2;

	public Map<String, Object> getConditionMacrocodes() {
		Map<String, Object> map = new HashMap<String, Object>(); // 无需关心域，消息都是按接收人过滤
		
		map.put("${receiverId}", " and o.receiverId = :receiverId");
		map.put("${sender}", " and o.sender = :sender");
		map.put("${title}", " and o.title like :title");
		map.put("${content}", " and o.content like :content");
		map.put("${searchTime1}", " and o.sendTime >= :searchTime1");
		map.put("${searchTime2}", " and o.sendTime <= :searchTime2");
		
		return map;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTitle() {
		if (title != null) {
			title = "%" + title.trim() + "%";
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		if (content != null) {
			content = "%" + content.trim() + "%";
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSearchTime1() {
		return searchTime1;
	}

	public void setSearchTime1(Date searchTime1) {
		this.searchTime1 = searchTime1;
	}

	public Date getSearchTime2() {
		return searchTime2;
	}

	public void setSearchTime2(Date searchTime2) {
		this.searchTime2 = searchTime2;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
}