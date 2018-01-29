package com.boubei.tss.portal.engine.model;

import com.boubei.tss.portal.entity.Component;

/**
 * 修饰器节点对象：用于解析门户中使用到的修饰器
 */
public class DecoratorNode extends AbstractElementNode {
    
    /**
     * 将修饰器的实体转换为 DecoratorNode 对象，
     * @param obj
     * 			修饰器Entity
     * @param parent
     * 			修饰器所在的门户结构Node
     * @param parameterOnPs
     * 			门户结构上定义的参数内容（修饰器实例化时自定义参数值）
     */
    public DecoratorNode(Component obj, SubNode parent, String parametersOnPs){
        super(obj, parent, parametersOnPs);
    }
    
    public Object clone(){
        DecoratorNode copy = (DecoratorNode) super.clone();
        return copy;
    }
}