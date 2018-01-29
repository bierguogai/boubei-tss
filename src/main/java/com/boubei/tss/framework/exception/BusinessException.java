package com.boubei.tss.framework.exception;

/**
 * 业务逻辑异常。
 *
 */
public class BusinessException extends RuntimeException implements IBusinessException {

    private static final long serialVersionUID = 1759438185530697479L;
 
    public BusinessException(String msg) {
        this(msg, false);
    }
    
    public BusinessException(String msg, boolean neddPrint) {
        super(msg);
        this.neddPrint = neddPrint;
    }
 
    public BusinessException(String msg, Throwable t) {
        super(msg, t);
    }
 
    /**
     * 是否打印异常stack
     */
    private boolean neddPrint = true;

	public boolean needPrint() {
		return this.neddPrint;
	}
	
    public boolean needRelogin() {
        return false;
    }
}
