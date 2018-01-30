/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.grid;

import java.util.List;

/** 
 * 简易Grid解析器
 */
public class SimpleGridParser extends GridParser {
    
	/**
	 * 解析Grid数据
	 */
	public GridNode parse(Object data) {
		GridNode root = new GridNode();
		if (data != null) {
			List<?> list = (List<?>) data;
	        for (Object temp : list) {
				root.addChild(new GridNode(temp, super.columns));
			}
		}
		
		return root;
	}

}
