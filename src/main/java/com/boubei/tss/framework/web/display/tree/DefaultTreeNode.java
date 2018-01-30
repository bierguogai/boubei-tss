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
 * 默认的单层树节点。
 */
public class DefaultTreeNode implements ITreeNode {
	
    private Object id;
    private String name;
    
    public DefaultTreeNode(Object id, String name){
        this.id = id;
        this.name = name;
    }

    public TreeAttributesMap getAttributes() {
        return new TreeAttributesMap(id, name);
    }
}

