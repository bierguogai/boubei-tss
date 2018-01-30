/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.Environment;

/** 
 * 负责处理成功信息的消息编码器。
 * 
 */
public class SuccessMessageEncoder implements IDataEncoder {

    private static final Logger log = Logger.getLogger(SuccessMessageEncoder.class);
    
    /** 显示类型：不弹出  */
    public static final int NO_POPUP_TYPE = 0;
    
	/** 类型： 
	 * <li>0:不显示； 
	 * <li>1:普通； */
	private int type = 1;
    
	/** 成功信息  */
	private String message = null;
	
	/** 详细描述  */
	private String description = null;
	
	/**
     * 默认明细信息为NULL，类型为1（普通）
	 * @param message 成功信息
	 */
	public SuccessMessageEncoder(String message){
		this(message, null);
	}
	
    /**
     * 默认明细信息为NULL
     * @param message   成功信息
     * @param type  类型
     */
    public SuccessMessageEncoder(String message, int type){
        this(message, null, type);
    }
	/**
     * 默认类型为1（普通）
	 * @param message  成功信息
	 * @param description  明细信息
	 */
	public SuccessMessageEncoder(String message, String description){
		this(message, description, 1);
	}
	/**
	 * @param message  成功信息
	 * @param description  明细信息
	 * @param type 类型
	 */
	public SuccessMessageEncoder(String message, String description, int type){
		this.message = message;
		this.description = description;
        this.type = type;
	}

    /**
     * <p>
     * 输出XML信息<br>
     * 如果在调试模式下，输出调试信息，即请求返回数据
     * </p>
     * @return String XML字符串
     */
    public String toXml(){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"" + DEFAULT_ENCODING + "\"?>");
        sb.append("<Response><Success>");
        sb.append("<msg><![CDATA[").append(this.message).append("]]></msg>");
        sb.append("<description><![CDATA[").append(description).append("]]></description>");
        sb.append("<type>").append(this.type).append("</type>");
        sb.append("</Success></Response>");
        
        String returnXML = sb.toString();
        
        //输出调试信息：返回数据流信息
        log.debug("-----------------------   Response   ----------------------");
        log.debug("AppCode:" + Config.getAttribute(PX.APPLICATION_CODE));
        log.debug("Thread:" + Environment.threadID());
        log.debug("Content:");
        log.debug(returnXML);
        log.debug("----------------------  End of Response  -------------------");
        return returnXML;
    }

    /**
     * <p>
     * 将成功信息返回到输出流
     * </p>
     * @param writer    输出流对象
     */
    public void print(XmlPrintWriter writer){
        writer.append(toXml());
    }
}
