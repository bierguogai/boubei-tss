package com.boubei.tss.framework.web.dispaly.tree;

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

