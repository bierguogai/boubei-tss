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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 匿名操作用户
 * </p>
 */
public class Anonymous implements IOperator {

	private static final long serialVersionUID = 5437339121904051176L;

	public static final String _NAME = "匿名用户"; 
    public static final String _CODE = "ANONYMOUS"; 
    public static final Long   _ID   = -10000L;// 匿名用户ID
    
    public static Anonymous one = new Anonymous();
    
    private Anonymous() {
    }

    public Long getId() {
        return _ID;
    }

    public String getLoginName() {
        return _CODE;
    }

    public String getUserName() {
        return _NAME;
    }

    public boolean isAnonymous() {
        return true;
    }
 
    public Map<String, Object> getAttributesMap() {
        return new HashMap<String, Object>();
    }
}
