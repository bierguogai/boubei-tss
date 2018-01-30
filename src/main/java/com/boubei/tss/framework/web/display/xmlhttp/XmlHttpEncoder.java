/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.xmlhttp;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.web.display.IDataEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.util.BeanUtil;

/** 
 * XMLHTTP返回数据格式化对象，把返回的结果数据格式化成特定的XML格式。
 * 在Action即将将数据返回前台时用到，数据将被转换成XML格式，可参考PTActionSupport.java。
 * 
 */
public class XmlHttpEncoder implements IDataEncoder {
    
    protected static final Logger log = Logger.getLogger(XmlHttpEncoder.class);
    
    /** 返回参数列表 */
    private Map<String, Object> returnValues = new HashMap<String, Object>();

    /**
     * <p>
     * 设置返回参数
     * </p>
     * @param name  参数名
     * @param value 参数值
     */
    public void put(String name, Object value) {
        returnValues.put(name, value);
    }

    /**
     * <p>
     * 输出XML信息<br>
     * 如果在调试模式下，输出调试信息，即请求返回数据
     * </p>
     * @return String XML字符串
     */
    public String toXml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"" + DEFAULT_ENCODING + "\"?>");
        sb.append("<Response>");
        for (String key : returnValues.keySet()) {
            Object value = returnValues.get(key);
            if(value == null) continue;

            sb.append("<").append(key).append(">");
            if (BeanUtil.isImplInterface(value.getClass(), IDataEncoder.class)) {
                String xml = ((IDataEncoder) value).toXml();
                if( xml.startsWith("<?xml") ) {
                    xml = xml.substring(xml.indexOf("?>") + 2);
                }
                sb.append(xml);
            } 
            else {
                sb.append(value);
            }
            sb.append("</").append(key).append(">");
        }
        sb.append("</Response>");
        
        String returnXML = sb.toString();
        
        // 输出响应（response）的详细信息：请求数据流返回数据流信息
        log.debug("---------------------------- Response ---------------------------");
        log.debug("AppCode:" + Config.getAttribute(PX.APPLICATION_CODE));
        log.debug("Thread:"  + Thread.currentThread().getName());
        log.debug("Content:");
        log.debug("\n" + returnXML + "\n");
        log.debug("------------------------ End of Response ------------------------");

        return returnXML;
    }

    /**
     * <p>
     * 将数据返回到输出流
     * </p>
     * @param writer    输出流对象
     */
    public void print(XmlPrintWriter writer) {
        writer.append(toXml());
    }
}
