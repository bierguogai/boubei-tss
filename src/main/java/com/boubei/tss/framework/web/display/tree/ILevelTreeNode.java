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

/** 
 * 多层树节点接口。
 */
public interface ILevelTreeNode extends ITreeNode {
	
    /**
     * 获取自身节点编号
     * @return
     */
    Long getId();

    /**
     * 获取父节点编号
     * @return
     */
    Long getParentId();
}
