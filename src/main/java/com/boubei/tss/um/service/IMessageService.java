package com.boubei.tss.um.service;

import java.util.List;

import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.um.entity.Message;
import com.boubei.tss.um.helper.MessageQueryCondition;
 
public interface IMessageService {
 
	/**
	 * 发送短消息
	 * @param message
	 */
	void sendMessage(Message message);
	
	/**
	 * 查看短消息 并将标志位改成已读
	 * @param id
	 * @return
	 */
	Message viewMessage(Long id);
	
	/**
	 * 批量设置消息为已阅
	 * @param id
	 */
	void batchRead(String ids);
	
	/**
	 * 删除短消息
	 * @param id
	 */
	void deleteMessage(String id);
	
	/**
	 * <p>
	 * 获取收件箱列表
	 * </p>
	 * @return
	 */
	List<Message> getInboxList();
	
	int getNewMessageNum();
	
	PageInfo getInboxList(MessageQueryCondition condition);
}