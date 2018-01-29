package com.boubei.tss.framework.exception;

import javax.servlet.ServletException;

/**
 * 业务逻辑异常（用于web层抛出异常）。
 * 
 */
public class BusinessServletException extends ServletException implements IBusinessException {

    private static final long serialVersionUID = -2924685196019749948L;

    /**
     * 是否需重新登录系统:
     *  false-无需登录；
     *  true-需要重新登录平台；
	 */
    private boolean relogin = false;
    
    /**
     * 是否打印异常stack
     */
    private boolean neddPrint = true;
 
    public BusinessServletException(Exception e) {
    	super(e.getMessage());
    	
    	if( !(e instanceof IBusinessException) 
    			&& (e.getCause() instanceof IBusinessException)) {
    		e = (Exception) e.getCause();
    	}
    	
        initCause(e);
        if(e instanceof IBusinessException) {
        	IBusinessException be = (IBusinessException)e;
			this.relogin = be.needRelogin();
        	this.neddPrint = be.needPrint();
    	} 
    }
 
    public BusinessServletException(Exception e, boolean relogin) {
        super(e.getMessage());
        initCause(e);
        this.relogin = relogin;
    }
 
    public BusinessServletException(String msg) {
        super(msg);
    }
 
    public BusinessServletException(String msg, Throwable t) {
        super(msg);
        initCause(t);
    }

    public boolean needRelogin() {
        return this.relogin;
    }

	public boolean needPrint() {
		return !this.relogin && this.neddPrint;
	}
}
