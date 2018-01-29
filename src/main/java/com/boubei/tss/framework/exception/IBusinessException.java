package com.boubei.tss.framework.exception;

/** 
 * 业务逻辑异常
 */
public interface IBusinessException {

	/**
     * 是否需重新登录系统:
     *  false - 无需登录；
     *  true  - 需要重新登录平台；
     *  
	 * @return 
	 */
	boolean needRelogin();
	
	/**
     * 是否打印异常stack
     */
	boolean needPrint();
}
