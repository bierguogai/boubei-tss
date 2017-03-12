package com.boubei.tss.framework.exception.convert;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.ExceptionEncoder;

/** 
 * <p>
 * 默认异常转换器：加一些常见的异常转换
 * </p>
 */
public class DefaultExceptionConvertor implements IExceptionConvertor {

	private Logger log = Logger.getLogger(this.getClass());
	
	public final static String ERROR_1 = "该数据关联引用的其它表数据不存在，保存失败";
	public final static String ERROR_2 = "该数据已被其它数据引用，不能删除";
	public final static String ERROR_3 = "您正在保存的信息可能已经被其它人修改过或删除了，请重新操作一遍试试。";
	
    public Exception convert(Exception e) {
    	if( e != null && e.getMessage() != null) {
    		Throwable firstCause = ExceptionEncoder.getFirstCause(e);
			String msg = e.getMessage() + firstCause.getClass() + firstCause.getMessage();
			log.debug(msg);
			
    		if(msg.indexOf("ConstraintViolationException") >= 0) {
    			if(msg.indexOf("insert") >= 0) {
    				return new BusinessException( ERROR_1 );
    			}
    			else if(msg.indexOf("delete") >= 0) {
    				return new BusinessException( ERROR_2 );
    			}
    			else {
    				return new BusinessException( firstCause.getMessage() );
    			}
    		}
    		
    		if(msg.indexOf("Row was updated or deleted by another transaction") >= 0) {
				return new BusinessException( ERROR_3 );
			}
    	}
        return e;
    }
}
