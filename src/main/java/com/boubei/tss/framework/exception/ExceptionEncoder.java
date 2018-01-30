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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.convert.ExceptionConvertorFactory;
import com.boubei.tss.framework.exception.convert.IExceptionConvertor;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.web.display.ErrorMessageEncoder;
import com.boubei.tss.framework.web.display.IDataEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.util.MailUtil;

/**
 * 异常信息编码器。
 * 
 * 注: 上传导入附件抛出的异常统一在 Servlet4Upload 里处理。不会流到此处。
 */
public class ExceptionEncoder {
    private static Logger log = Logger.getLogger(ExceptionEncoder.class);
 
    static IExceptionConvertor convertor = ExceptionConvertorFactory.getConvertor();

    public static void encodeException(ServletResponse response, Exception be) {
    	RequestContext rc = Context.getRequestContext();
        if( rc == null ) return;
    	
    	try {
            be = convertor.convert(be);

            boolean needRelogin = false, needPrint = true;
            if(be instanceof IBusinessException){
                IBusinessException e = (IBusinessException) be;
                needRelogin = e.needRelogin();
                needPrint = e.needPrint();
                
				if(needPrint) {
                    printErrorMessage(be);
                }
            }
            
            String beMsg = getFirstCause(be).getMessage();
            if( needPrint && !needRelogin ) {
            	String userName = Environment.getUserName();
            	log.warn( userName + ", thread=" +  Environment.threadID()
						+ ", request url: " + rc.getRequest().getServletPath() 
						+ ", request params:" + rc.getRequest().getParameterMap()
						+ ", errMsg = " + beMsg);
            }
            
            // 将异常提示输出到前台
            if (!response.isCommitted() && !rc.isMultiRequest()) {
                response.resetBuffer();
            }
            boolean isXmlhttpRequest = rc.isXmlhttpRequest();
			String contentType = isXmlhttpRequest ? "text/html;charset=UTF-8" : "application/json;charset=UTF-8";
            response.setContentType(contentType);
            
            /* 
             * 在一次请求中(如下载附件出现异常时)，同时调用response.getOutputStream() 和 response.getWriter()将会报错：
             *   getOutputStream() has already been called for this response 
             */
            PrintWriter out;
            try {
            	out = response.getWriter();
            } catch( Exception e ) {
            	out = new PrintWriter( response.getOutputStream() );
            }
            
            if ( isXmlhttpRequest ) {  // tssJS发出的ajax请求, 返回XML格式错误信息
            	IDataEncoder encoder = new ErrorMessageEncoder(be);
                encoder.print( new XmlPrintWriter(out) );
            } 
            else { // HTTP JSON: 默认用json格式往response写入异常信息
            	out.println("{\"errorMsg\": \"" + beMsg + "\"}");
            }
        } 
    	catch (Exception e) {
            log.error("ExceptionEncoder.encodeException时出错：" + e.getMessage());
        }
    }

    /**
     * 打印详细错误信息到日志中
     * @param be
     */
    private static void printErrorMessage(Throwable be) {
        Throwable first = getFirstCause(be);
        
        // 过滤掉不需要输出到控制台（或日志）的异常，比如SocketException等
        if(first != null && first instanceof SocketException) {
            return; 
        }
        
        if (first != null && first != be) {
            printStackTrace(first);
        } else {
        	printStackTrace(be);
        }
    }
    
    /**
     * 读取到最里面一级的异常对象。
     * 异常经过层层重新抛出，但只有最里面一级才是引起本次异常的根本原因。
     * 
     * @param be
     * @return
     */
    public static Throwable getFirstCause(Throwable be) {
        Throwable first = null;
        Throwable cause = be.getCause();
        while (cause != null) {
            first = cause;
            cause = cause.getCause();
        }
        
        return first != null ? first : be;
    }

    /**
     * <p>
     * 打印错误的堆栈信息到错误日志中
     * </p>
     * 
     * @param be
     */
    private static void printStackTrace(Throwable be) {
        StringWriter writer = new StringWriter();
        be.printStackTrace(new PrintWriter(writer)); // 将异常信息输出至 字符串流StringWriter
        String stackTrace = writer.toString();
		log.error(stackTrace);
		
		// 记录异常信息到日志里
		String errorMessage = be.getMessage();
		Log excuteLog = new Log(errorMessage, stackTrace);
    	excuteLog.setOperateTable("系统异常");
        ((IBusinessLogger) Global.getBean("BusinessLogger")).output(excuteLog);
        
        // 对指定了关键字的错误异常进行邮件提醒
        String errorKeyword = ParamConfig.getAttribute(PX.ERROR_KEYWORD, "java.lang.OutOfMemoryError");
        List<String> errorKeywords = Arrays.asList(errorKeyword.split(","));
        
        boolean hitted = false;
        for(String ek : errorKeywords) {
        	if(stackTrace.indexOf(ek) >= 0) {
        		hitted = true;
        		break;
        	}
        }
        
        if(hitted) {
        	MailUtil.send("紧急情况，请速查看详细日志：" + errorMessage, stackTrace);
        }
    }
    
}
