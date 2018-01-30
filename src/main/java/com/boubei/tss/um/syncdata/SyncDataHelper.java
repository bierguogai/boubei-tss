/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.syncdata;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.dto.UserDTO;
import com.boubei.tss.um.sso.UMPasswordIdentifier;
import com.boubei.tss.um.syncdata.dao.DBDataDao;
import com.boubei.tss.um.syncdata.dao.IOutDataDao;
import com.boubei.tss.um.syncdata.dao.LDAPDataDao;
import com.boubei.tss.util.EasyUtils;

public class SyncDataHelper {
    
	public final static String DRIVER = "driver";
    public final static String URL = "url";
    public final static String USERNAME = "user";
    public final static String PASSWORD = "password";
    
    public final static String QUERY_GROUP_SQL_NAME = "groupSql";
    public final static String QUERY_USER_SQL_NAME  = "userSql";
 
    /**
     * <p>
     * 拷贝用户DTO到实体对象
     * 只拷贝部分基本属性
     * </p>
     * @param user
     * @param userDTO
     */
    public static void setUserByDTO(User user, UserDTO userDTO) {
    	String loginName = userDTO.getLoginName();
		user.setLoginName(loginName);
    	user.setUserName(userDTO.getUserName());
        user.setDisabled(userDTO.getDisabled());
        user.setAccountLife(userDTO.getAccountLife());
        user.setEmail(userDTO.getEmail());
        user.setSex(userDTO.getSex());
        user.setBirthday(userDTO.getBirthday());
        user.setEmployeeNo(userDTO.getEmployeeNo());
        
        if( !EasyUtils.isNullOrEmpty(userDTO.getAuthMethod()) ) {
        	user.setAuthMethod(userDTO.getAuthMethod());
        } else {
        	user.setAuthMethod(UMPasswordIdentifier.class.getName());
        }
        
        String userPassword = userDTO.getPassword();
        if(userPassword == null) {
        	userPassword = loginName; // 默认：密码 = 登陆账号
        }
        if(userPassword.length() < 32) { // 如果是32位，则同步的是已经加密好的密码
        	userPassword = user.encodePassword(userPassword);
        }
        user.setPassword(userPassword);
        user.setPasswordQuestion("?");
        user.setPasswordAnswer( System.currentTimeMillis() + "!" );
        
        user.setFromUserId(userDTO.getId());
    }
    
    public static IOutDataDao getOutDataDao(Integer dataSourceType) {
        if (UMConstants.DATA_SOURCE_TYPE_LDAP.equals(dataSourceType)) {
            return new LDAPDataDao();
        }
        
        if (UMConstants.DATA_SOURCE_TYPE_DB.equals(dataSourceType)) {
            return new DBDataDao();
        }
        
        throw new BusinessException("dataSource type should be one of db,ldap." + dataSourceType);
    }
}

