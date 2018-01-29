package com.boubei.tss.framework.web.display;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.exception.IBusinessException;
import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * <p>
 * 负责处理错误异常信息的消息编码器。
 * </p>
 *
 */
public class ErrorMessageEncoder implements IDataEncoder {
    
    protected static final Logger log = Logger.getLogger(ErrorMessageEncoder.class);

    protected String message = null;

    private String description = null;

    private boolean relogin = false; // 是否需重新登录系统

    /**
     * 错误信息类型：
     * <li>1－普通业务逻辑错误信息，没有异常发生的
     * <li>2－有异常发生，同时被系统捕获后添加友好错误消息的
     * <li>3－其他系统没有预见的异常信息
     */
    private int type = 1;

    public ErrorMessageEncoder(String errorMessage) {
        this(errorMessage, false);
    }

    public ErrorMessageEncoder(String errorMessage, boolean relogin) {
        this(errorMessage, null, relogin);
    }
 
    public ErrorMessageEncoder(String errorMessage, String description, boolean relogin) {
        this.message = errorMessage;
        this.description = description;
        this.relogin = relogin;
    }

    public ErrorMessageEncoder(Throwable exception) {
        this(getExceptionMessage(exception), exception);
    }

    public ErrorMessageEncoder(String errorMessage, Throwable exception) {
        this.message = errorMessage;
        this.description = getDescription(exception);
        if (BeanUtil.isImplInterface(exception.getClass(), IBusinessException.class)) {
            this.type = 2;
            this.relogin = ((IBusinessException) exception).needRelogin();
        } else {
            this.type = 3;
        }
    }

    private static String getExceptionMessage(Throwable exception) {
        Throwable firstBusinessEx = getFirstBusinessException(exception);
        Throwable firstEx = ExceptionEncoder.getFirstCause(exception);
        firstBusinessEx = (Throwable) EasyUtils.checkNull(firstBusinessEx, firstEx);
        		
        String msg = firstBusinessEx.getMessage();
        return EasyUtils.obj2String(msg);
    }

    /**
     * 获取原始自定义异常：寻找第一个自定义的异常
     *
     * @param exception Throwable
     * @return Throwable
     */
    private static Throwable getFirstBusinessException(Throwable exception) {
        Throwable firstBusinessException = null;
        Throwable cause = exception;
        while (cause != null) {
            if (BeanUtil.isImplInterface(cause.getClass(), IBusinessException.class)
                    || cause instanceof UserIdentificationException) {
                firstBusinessException = cause;
            }
            cause = cause.getCause();
        }
        return firstBusinessException;
    }

    /**
     * 获取异常发生的详细信息
     *
     * @param exception
     * @return
     */
    private static String getDescription(Throwable exception) {
        StringBuffer sb = new StringBuffer();
        while (exception != null) {
            if (exception.getMessage() != null) {
                sb.append(exception.getMessage());
            }
            sb.append("\n\tat " + exception.getClass().getName() + "\n");
            exception = exception.getCause();
        }
        return sb.toString();
    }
 
    public String toXml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"" + DEFAULT_ENCODING + "\"?>");
        sb.append("<Response><Error>");
        sb.append("<msg><![CDATA[").append(this.message).append("]]></msg>");
        sb.append("<description><![CDATA[").append(description).append("]]></description>");
        sb.append("<relogin>").append( EasyUtils.checkTrue(this.relogin, 1, 0) ).append("</relogin>");
        sb.append("<type>").append(this.type).append("</type>");
        sb.append("</Error></Response>");
        
        String returnXml = sb.toString();
        log.debug(returnXml);
        return returnXml;
    }
 
    public void print(XmlPrintWriter writer) {
        writer.append(toXml());
    }

    public String getMessage() {
        return message;
    }
}
