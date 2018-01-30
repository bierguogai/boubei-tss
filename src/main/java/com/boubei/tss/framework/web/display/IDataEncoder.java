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

/** 
 * 各类数据组装接口，组装成符合要求的格式。
 * 
 */
public interface IDataEncoder {
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	 
    /**
     * 将数据信息转换成XML格式
     * @return
     */
    String toXml();
    
    /**
     * 打印输出数据
     * @param out
     */
    void print(XmlPrintWriter out);
}

