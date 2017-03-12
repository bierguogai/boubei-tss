package com.boubei.tss.framework.web.dispaly.tree;

/** 
 * 默认的多层树节点。
 * 
 */
public class DefaultLevelTreeNode implements ILevelTreeNode {
    private Long id;
    private Long parentId;
    private String name;
    
    public DefaultLevelTreeNode(Long id, String name){
        this(id, new Long(0), name);
    }
    
    public DefaultLevelTreeNode(Long id, Long parentId, String name) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public Long getId() {
        return id;
    }

    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);
        return map;
    }
    
    public boolean equals(Object obj) {
        DefaultLevelTreeNode object = (DefaultLevelTreeNode) obj;
        return this.id.equals(object.getId());
    }
}

