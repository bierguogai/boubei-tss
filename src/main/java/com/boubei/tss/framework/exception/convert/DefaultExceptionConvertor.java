package com.boubei.tss.framework.exception.convert;

import org.apache.log4j.Logger;

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
	
	public final static String ERROR_1 = "字段违反唯一性约束，保存失败";
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
    		
    		boolean needPrint = false, needRelogin = false;;
    		if( e instanceof IBusinessException ) {
    			needPrint = ((IBusinessException) e).needPrint();
    			needRelogin = ((IBusinessException) e).needRelogin();
    		}
    		msg = firstCause.getMessage();
    		if( !needRelogin && !EasyUtils.isNullOrEmpty(msg) ) {
    			
    			// MySQL/Oracle字段不能为空
    			if(msg.indexOf("cannot be null") >= 0) {
    				msg = msg.replaceAll("Column", "字段").replaceAll("cannot be null", "必须填写，不能为空");
    			}
        		msg = msg.replaceAll("ORA-01407:", "字段必须填写，不能为空，");
        		
        		// 违反唯一性约束
        		msg = msg.replaceAll("Duplicate entry", "违反唯一性约束，字段值不能和其它数据重复 ");
        		msg = msg.replaceAll("ORA-00001:", "违反唯一性约束，字段值不能和其它数据重复 ");
        		
        		if( !msg.equals(firstCause.getMessage()) ) { // 如果异常Msg内容发生了改变，重新抛出异常
        			msg = msg.replaceAll("com.boubei.tss.framework.exception.BusinessException: 异常：", "");
            		msg = msg.replaceAll("com.boubei.tss.framework.exception.BusinessException:", "");
        			return new BusinessException( msg, needPrint );
        		}
    		}
    	}
        return e;
    }
}
