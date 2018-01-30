/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.exception;

/**
 * <p>
 * 用户身份识别类业务逻辑错误
 * </p>
 */
public class UserIdentificationException extends Exception {
 
	private static final long serialVersionUID = -4729575896596213410L;

    public UserIdentificationException() {
        super();
    }
    
	public UserIdentificationException(String message) {
        super(message);
    }
}
