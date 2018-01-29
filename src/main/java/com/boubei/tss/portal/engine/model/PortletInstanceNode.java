package com.boubei.tss.portal.engine.model;

import com.boubei.tss.portal.entity.Structure;

/**
 * Portlet实例对象：用于解析门户中使用到的Portlet实例
 */
public class PortletInstanceNode extends AbstractSubNode implements DecoratorConfigable, IPageElement{
 
    private PortalNode    portal;        // 所属Portal节点对象
    private PageNode      page;          // 所属页面节点对象
    private PortletNode   portletNode;   // Portlet对象
    private DecoratorNode decoratorNode; // Portlet修饰器

    public DecoratorNode getDecoratorNode() { return decoratorNode; }
    public PortletNode getPortletNode() { return portletNode; }
    public void setDecoratorNode(DecoratorNode decorator) { this.decoratorNode = decorator; }
    public void setPortletNode(PortletNode portlet) { this.portletNode = portlet; }
 
    public AbstractElementNode getDecoratorContent() {
		return portletNode;
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

	// 无儿子节点，本身为叶子节点 （但PortletInstanceNode可作为其包含的decoratorNode和portletNode的parent）
    public void addChild( Node node ) { 
    	// do nothing
    }

    public PortletInstanceNode(Structure ps) {
        this.id = ps.getId();
        this.name = ps.getName();
        this.code = ps.getCode();
    }
    
    public Object clone(){
        PortletInstanceNode copy = (PortletInstanceNode) super.clone();
        copy.decoratorNode = (DecoratorNode) this.decoratorNode.clone();
        copy.portletNode   = (PortletNode)this.portletNode.clone();
        return copy;
    }
    
    public String getPageElementType() {
        return "PortletInstance";
    }
}