package com.boubei.tss.portal.engine.model;


/** 
 * 可配置布局器参数的节点接口。<br>
 * 页面、版面节点（PageNode, SectionNode) 包含了布局器。
 */
public interface LayoutConfigable {
    
    /**
     * 获取门户结构里（版面或页面）的布局器node。
     * @return
     */
    LayoutNode getLayoutNode();
    
    void setLayoutNode(LayoutNode layoutNode);
}

