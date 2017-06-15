package com.boubei.tss.dm.record.ddl;

import java.util.Map;

import com.boubei.tss.util.EasyUtils;

public class DDLUtil {

	/**
	 * 字符串的类型字段的长度 = height/18 *255
	 */
	public static int getVarcharLength(Map<Object, Object> fDefs) {
		int length = 255;
		
		String _height = (String) fDefs.get("height");
		if( !EasyUtils.isNullOrEmpty(_height) ) {
			length = Math.max(1, Integer.parseInt(_height.replace("px", ""))/18) * 255;
		}
		
		return length;
	}
	
}
