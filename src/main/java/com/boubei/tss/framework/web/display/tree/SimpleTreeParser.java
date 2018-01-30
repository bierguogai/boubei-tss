/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.tree;

import java.util.Collection;

/**
 * 单层树形结构转换器。
 */
public class SimpleTreeParser implements ITreeParser {

	/**
	 * 解析Tree数据
	 * 
	 * @param data
	 * @return
	 */
	public TreeNode parse(Object data) {
		TreeNode root = new TreeNode();
		if (data == null) {
			return root;
		}

		for (Object nodeInfo : (Collection<?>) data) {
			root.addChild(new TreeNode((ITreeNode) nodeInfo));
		}
		return root;
	}

}
