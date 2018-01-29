package com.boubei.tss.portal.engine.macrocode;

import com.boubei.tss.portal.engine.model.PortletNode;

/**
 * <p> Portlet宏代码运行容器 </p>
 * <p>
 * 负责解析和运行包含宏代码、变量代码的字符串（HTML、JS、CSS等）
 * </p>
 */
public class PortletMacrocodeContainer extends AbstractMacrocodeContainer{

    /**
     * 在JS中代表当前portletInstance对象，转化为document.getElementById("xxx")（假设此portletInstance的ID="xxx"）
     */
    static final String JS_PORTLET_INSTANCE_MACROCODE = "${js.portletInstance}";   
    
    /**
     * 在JS中代表当前Portlet对象，转化为document.getElementById("Pxxx") （假设此portlet所在门户结构的ID="xxx")
     */
    static final String JS_PORTLET_MACROCODE = "${js.portlet}";
    
    /**
     * js中访问修饰器对象，转化为document.getElementById('Dxxx')（假设此修饰器所在门户结构的ID="xxx"）
     */
    static final String JS_DECORATOR_MACROCODE = "${js.decorator}";
    
    /**
     * 容器初始化函数
     * @param code
     *            String 包含宏代码、变量代码的字符串
     * @param node
     *            PortletNode 代码所在Portlet应用节点对象，包含所有宏代码、变量代码的真实含义等信息
     */
    public PortletMacrocodeContainer(String code, PortletNode node) {
        super(code, node);
        
        // 注：这里是调用父类的方法返回结果： document.getElementById('Pxxx')
        macrocodes.put(JS_PORTLET_MACROCODE, super.getElementInJS()); 
        
        // 获取PortletInstance在页面（网页）上对应的对象： document.getElementById('xxx')
        macrocodes.put(JS_PORTLET_INSTANCE_MACROCODE, getElementInJS());
        
        // 获取Portlet实例所应用的修饰器在页面上对应的对象： document.getElementById('Dxxx')
        macrocodes.put(JS_DECORATOR_MACROCODE, "document.getElementById('D" + node.getParent().getId() + "')");
    }
 
    //特殊，区别修饰器、布局器
    protected String getParentElement() {
        // 获取PortletInstance所在版面【node.getParent().getParent()】在页面（HTML DOCUMENT）上对应的对象 （版面在页面上的对象）
        return "document.getElementById('" + node.getParent().getParent().getId() + "')";
    }
    
    // 特殊，区别修饰器、布局器
    protected Object getElementInJS() {
    	// 获取PortletInstance所在页面（HTML DOCUMENT）上对应的对象： document.getElementById('xxx') （实例自己在页面上的对象）
    	return "document.getElementById('" + node.getParent().getId() + "')";
    }

    protected String getElementId() {
        return "P" + node.getParent().getId();
    }

    protected String getElementPortotypeId() {
        return "PP" + node.getId();
    }
}
