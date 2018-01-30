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

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 用户信息数据对象接口
 * </p>
 */
public interface IOperator extends Serializable {

    /**
     * <p>
     * 获取用户所有属性集合
     * </p>
     * @return
     */
    Map<String, Object> getAttributesMap();

    /**
     * <p>
     * 获取用户ID
     * </p>
     * @return
     */
    Long getId();

    /**
     * <p>
     * 获取用户登录名
     * </p>
     * @return
     */
    String getLoginName();

    /**
     * <p>
     * 获取用户名
     * </p>
     * @return
     */
    String getUserName();

    /**
     * <p>
     * 是否匿名用户
     * </p>
     * @return
     */
    boolean isAnonymous();
}
