package com.boubei.tss.framework.web.dispaly.tree;


/** 
 * <p> 单层树节点。 </p> 
 * 
 * TreeAttributesMap包含了树节点的属性信息，如name、path等
 */
public interface ITreeNode {

	/**
	 * 获去节点属性Map
	 * 
	 * @return TreeAttributesMap
	 */
	TreeAttributesMap getAttributes();
}
