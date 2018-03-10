/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.exception.convert;

import org.apache.log4j.Logger;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.exception.IBusinessException;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p>
 * 默认异常转换器：加一些常见的异常转换
 * </p>
 */
public class DefaultExceptionConvertor implements IExceptionConvertor {

	private Logger log = Logger.getLogger(this.getClass());
	
    public Exception convert(Exception e) {
    	if( e != null && e.getMessage() != null) {
    		Throwable firstCause = ExceptionEncoder.getFirstCause(e);
			String msg = e.getMessage() + firstCause.getClass() + firstCause.getMessage();
			log.debug(msg);
			
    		if(msg.indexOf("ConstraintViolationException") >= 0) {
    			if(msg.indexOf("insert") >= 0) {
    				return new BusinessException( EX.ERR_UNIQUE );
    			}
    			else if(msg.indexOf("delete") >= 0) {
    				return new BusinessException( EX.ERR_HAS_FKEY );
    			}
    			else {
    				return new BusinessException( firstCause.getMessage() );
    			}
    		}
    		
    		if(msg.indexOf("Row was updated or deleted by another transaction") >= 0) {
				return new BusinessException( EX.ERR_LOCK_VERSION );
			}
    		
    		boolean needPrint = false, needRelogin = false;;
    		if( e instanceof IBusinessException ) {
    			needPrint = ((IBusinessException) e).needPrint();
    			needRelogin = ((IBusinessException) e).needRelogin();
    		}
    		msg = firstCause.getMessage();
    		if( !needRelogin && !EasyUtils.isNullOrEmpty(msg) ) {
    			
    			// MySQL/Oracle字段不能为空
    			if(msg.indexOf("cannot be null") >= 0) {
    				msg = msg.replaceAll("Column", EX.COLUMN).replaceAll("cannot be null", EX.ERR_NOT_NULL);
    			}
        		msg = msg.replaceAll("ORA-01407:", EX.ERR_NOT_NULL);
        		
        		// 违反唯一性约束
        		msg = msg.replaceAll("Duplicate entry", EX.ERR_UNIQUE);
        		msg = msg.replaceAll("ORA-00001:", EX.ERR_UNIQUE);
        		
        		if( !msg.equals(firstCause.getMessage()) ) { // 如果异常Msg内容发生了改变，重新抛出异常
        			msg = msg.replaceAll("com.boubei.tss.framework.exception.BusinessException: " + EX.EXCEPTION, "");
            		msg = msg.replaceAll("com.boubei.tss.framework.exception.BusinessException:", "");
        			return new BusinessException( msg, needPrint );
        		}
    		}
    	}
        return e;
    }
}
