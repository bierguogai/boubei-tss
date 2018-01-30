/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.sso;

/** 
 * <p> 单点登录框架相关的一些属性命名 </p> 
 * 
 */
public interface SSOConstants {
    
    /** 配置文件中在线用户管理类属性名 */
    public static final String      ONLINE_MANAGER = "bean.onlineManager";

    /** 配置文件中用户登录自定义器属性名 */
    public static final String    LOGIN_COSTOMIZER = "class.name.LoginCostomizer";

    /**  配置文件中多应用信息存储类属性名  */
    public static final String    APPSERVER_STORER = "class.name.AppServerStorer";

    /** 身份转换对象实现类类名 */
    public static final String IDENTITY_GETTER = "class.name.IdentityGetter";

    /** 存在session里的用户权限（角色）名称  */
    public static final String USER_RIGHTS = "userRights";  // List
    public static final String USER_RIGHTS_S = "_userRights_";  // 逗号分隔字符串
    public static final String USER_ROLES_ = "userRoleList";  // List
    
    /** 存在session里的用户账号名称 */
    public static final String LOGINNAME_IN_SESSION = "loginName";
    public static final String USER_ID = "userId";
    public static final String USER_PASSWORD  = "password";
    
    public static final String USER_DOAMIN  = "DOMAIN";
    public static final String USERS_OF_DOAMIN  = "USERS_OF_DOAMIN";
    public static final String USERIDS_OF_DOAMIN  = "USERIDS_OF_DOAMIN";
    
    public static final String RANDOM_KEY  = "randomKey";
    
    public static final String LOGIN_CHECK_KEY = "loginCheckKey";
}
