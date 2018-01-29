package com.boubei.tss.portal.engine.model;

/** 
 * <p>
 * 可配置修饰器参数的节点接口。
 * 包括页面、版面、portlet实例
 * </p> 
 */
public interface DecoratorConfigable {
    
    /**
     * 获取修饰器node。
     * @return
     */
    DecoratorNode getDecoratorNode();
    
    void setDecoratorNode(DecoratorNode decoratorNode);
    
    /**
     * 获取修饰器修饰的内容。
     *      页面： LayoutNode
     *      版面： LayoutNode
     *      portlet实例： PortletNode
     * @return
     */
    AbstractElementNode getDecoratorContent();
}

