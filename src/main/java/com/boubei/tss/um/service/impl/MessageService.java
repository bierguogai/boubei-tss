/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.persistence.pagequery.PaginationQueryByHQL;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.entity.Message;
import com.boubei.tss.um.helper.MessageQueryCondition;
import com.boubei.tss.um.service.IMessageService;
import com.boubei.tss.util.EasyUtils;
 
@Service
public class MessageService implements IMessageService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired private ICommonDao commonDao;
	@Autowired private IGroupDao groupDao;
 
	public void sendMessage(Message message){
		String[] receiverIds = message.getReceiverIds().split(",");
		for(String receiverId : receiverIds) {
			Long _receiveId;
			try {
				_receiveId = EasyUtils.obj2Long(receiverId);
			} catch(Exception e) {
				continue;
			}
			
			Message temp = new Message();
			temp.setReceiverId(_receiveId);
			temp.setReceiver(receiverId);
			temp.setTitle(message.getTitle());
			temp.setContent(message.getContent());
			
			temp.setSenderId(Environment.getUserId());
			temp.setSender(Environment.getUserName());
			temp.setSendTime(new Date());
			
            commonDao.createWithoutFlush(temp);
		}
		commonDao.flush();
	}
 
	public Message viewMessage(Long id) {
		Message message = (Message) commonDao.getEntity(Message.class, id);
		message.setReadTime(new Date());
		commonDao.update(message);
		
		return message;
	}
	
	public void batchRead(String ids) {
		if("view_all".equals(ids)) { // 设置用户的所有站内消息为已阅
			String hql = "update Message m set m.readTime = ?  where m.receiverId = ? and readTime is null";
			commonDao.executeHQL(hql, new Date(), Environment.getUserId());
			return;
		}
		
		String[] idArray = ids.split(",");
		for(String _id : idArray) {
			Message message = (Message) commonDao.getEntity( Message.class, EasyUtils.obj2Long(_id) );
			message.setReadTime(new Date());
			commonDao.update(message);
		}
	}
	
	public void deleteMessage(String ids){
		if("del_all".equals(ids)) { // 清空用户的站内消息
			List<?> list = commonDao.getEntities("from Message m where m.receiverId = ?", Environment.getUserId());
			commonDao.deleteAll(list);
			return;
		}
		
		String[] idArray = ids.split(",");
		for(String _id : idArray) {
			commonDao.delete( Message.class, EasyUtils.obj2Long(_id) );
		}
	}
 
	@SuppressWarnings("unchecked")
	public List<Message> getInboxList(){
		Long userId = Environment.getUserId();
		String hql = " from Message m where m.receiverId = ? order by m.id desc ";
		return (List<Message>) commonDao.getEntities(hql, userId);
	}
	
	public int getNewMessageNum() {
		Long userId = Environment.getUserId();
		String hql = " select count(m) from Message m where m.receiverId = ? and readTime is null order by m.id desc ";
		List<?> list = commonDao.getEntities(hql, userId);
		return EasyUtils.obj2Int( list.get(0) );
	}
	
	public PageInfo getInboxList(MessageQueryCondition condition) {
		Long userId = Environment.getUserId();
		condition.setReceiverId(userId);
        String hql = " from Message o " 
        		+ " where 1=1 " + condition.toConditionString() 
        		+ " order by o.id desc ";
 
        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(commonDao.em(), hql, condition);
        return pageQuery.getResultList();
    }
 
}
