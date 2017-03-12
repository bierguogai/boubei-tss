package com.boubei.tss.framework.web.dispaly.grid;

import java.util.List;

/** 
 * 简易Grid解析器
 */
public class SimpleGridParser extends GridParser {
    
	/**
	 * 解析Grid数据
	 */
	public GridNode parse(Object data) {
		if (data == null) {
			return null;
		}
		
		GridNode root = new GridNode();
		
		List<?> list = (List<?>) data;
        for (Object temp : list) {
			root.addChild(new GridNode(temp, super.columns));
		}
		return root;
	}

}
