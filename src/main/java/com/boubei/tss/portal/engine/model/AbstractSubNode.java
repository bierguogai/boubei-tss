package com.boubei.tss.portal.engine.model;

public abstract class AbstractSubNode extends AbstractNode implements SubNode {

    protected Node parent; // 父节点对象

    public Node getParent() {
        return this.parent;
    }
    
    public void setParent(Node parent) { 
        this.parent = parent; 
    }

    public abstract PageNode getPage();

    public abstract void setPage(PageNode node);
    
    public abstract void setPortal(PortalNode node);

    public Object clone() {
        return super.clone();
    }
}
