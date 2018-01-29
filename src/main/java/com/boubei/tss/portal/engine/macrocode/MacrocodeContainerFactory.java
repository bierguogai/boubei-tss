package com.boubei.tss.portal.engine.macrocode;

import java.util.Map;

import com.boubei.tss.portal.engine.HTMLGenerator;
import com.boubei.tss.portal.engine.model.AbstractElementNode;
import com.boubei.tss.portal.engine.model.DecoratorNode;
import com.boubei.tss.portal.engine.model.LayoutNode;
import com.boubei.tss.portal.engine.model.PortletNode;

/**
 * 宏代码执行容器
 */
public class MacrocodeContainerFactory {

    /**
     * <p>
     * 实例化DecoratorNode宏代码容器
     * </p>
     * @param code 
     * 			 包含宏代码的字符串
     * @param node 
     * 			 包含此字符串的Node节点 DecoratorNode 
     * @param content  
     * 			 所修饰的对象(Element)
     * @return
     */
    public static AbstractMacrocodeContainer newInstance(String code, DecoratorNode node, Object content){
        return new DecoratorMacrocodeContainer(code, node, content);
    }

    /**
     * <p>
     * 实例化LayoutNode宏代码容器
     * </p>
     * @param code 
     * 			 包含宏代码的字符串
     * @param node 
     * 			 包含此字符串的Node节点 LayoutNode 
     * @param children 
     * 			 布局器所在的页面或版面包含的子节点HTMLGenerator.Element对象集合（即布局器的多个窗口（port0，port1，port2）包含着的）
     * @return
     */
    public static AbstractMacrocodeContainer newInstance(String code, LayoutNode node, 
    		Map<String, HTMLGenerator.Element> children) {
    	
        return new LayoutMacrocodeContainer(code, node, children);
    }

    /**
     * <p>
     * 实例化AbstractElementNode(DecoratorNode/LayoutNode/PortletNode)宏代码容器
     * </p>
     * @param code  
     * 			 包含宏代码的字符串
     * @param node  
     * 			 包含此code字符串的Node节点
     * @return
     */
    public static AbstractMacrocodeContainer newInstance(String code, AbstractElementNode node){
        if(node instanceof DecoratorNode)
            return new DecoratorMacrocodeContainer(code, (DecoratorNode) node);
        
        if(node instanceof LayoutNode)
            return new LayoutMacrocodeContainer(code, (LayoutNode) node);
        
        if(node instanceof PortletNode)
            return new PortletMacrocodeContainer(code, (PortletNode) node);
        
        return null;
    }
}
