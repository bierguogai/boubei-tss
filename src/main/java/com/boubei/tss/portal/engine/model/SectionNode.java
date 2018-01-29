package com.boubei.tss.portal.engine.model;

import java.util.LinkedHashSet;

import com.boubei.tss.portal.entity.Structure;

/**
 * 版面节点对象：用于解析门户中使用到的版面
 */
public class SectionNode extends AbstractSubNode implements DecoratorConfigable, LayoutConfigable, IPageElement {

    private PortalNode    portal;        // 所属Portal节点对象
    private PageNode      page;          // 所属页面节点对象
    private LayoutNode    layoutNode;    // 版面布局器
    private DecoratorNode decoratorNode; // 版面修饰器

    public DecoratorNode getDecoratorNode() { return decoratorNode; }
    public LayoutNode getLayoutNode() { return layoutNode; }
    public void setDecoratorNode(DecoratorNode decorator) { this.decoratorNode = decorator; }
    public void setLayoutNode(LayoutNode layout) { this.layoutNode = layout; }
 
    public AbstractElementNode getDecoratorContent() {
		return layoutNode;
	}

	public PageNode getPage() {
		return page;
	}

	public PortalNode getPortal() {
		return portal;
	}

	public void setPage(PageNode page) {
		this.page = page;
	}

	public void setPortal(PortalNode portal) {
		this.portal = portal;
	}

    public SectionNode(Structure ps) {
        this.id = ps.getId();
        this.name = ps.getName();
        this.code = ps.getCode();
    }

    public Object clone() {
        SectionNode copy = (SectionNode) super.clone();
        copy.children = new LinkedHashSet<Node>();
        for( Node child : this.children ){
            copy.children.add((Node) child.clone());
        }
        
        copy.decoratorNode = (DecoratorNode) this.decoratorNode.clone();
        copy.layoutNode    = (LayoutNode) this.layoutNode.clone();
        
        return copy;
    }
    
    public String getPageElementType() {
        return "Section";
    }
}
