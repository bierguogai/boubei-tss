package com.boubei.tss.portal.engine.model;

/** 
 * 页面组成门户结构元素接口, 包含版面SectionNode和PortletInstanceNode(portlet实例).
 */
public interface IPageElement extends Node {
    
    /**
     * 返回页面元素的名称。
     * 
     * @return  
     * 		版面： Section
     *		portlet实例： PortletInstance
     */
    String getPageElementType();
    
    /**
     * 获取父亲节点
     * @return
     */
    Node getParent();
    
    /**
     * 获取所在页面节点
     * @return
     */
    PageNode getPage();
}

