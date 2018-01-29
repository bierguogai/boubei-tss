package com.boubei.tss.portal.engine.model;

/** 
 * <p>
 * 门户结构子节点(包括PageNode,SectionNode,PortletInstanceNode)的接口
 * </p>
 */
public interface SubNode extends Node {
    
    /**
     * 获取子节点所处的页面节点
     */
    PageNode getPage();

    /**
     * 设置父亲节点
     */
    void setParent(Node parentNode);

    /**
     * 设置门户节点
     */
    void setPortal(PortalNode node);
    
    /**
     * 设置页面节点
     */
    void setPage(PageNode node);
}

