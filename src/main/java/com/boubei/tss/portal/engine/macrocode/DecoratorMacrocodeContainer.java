package com.boubei.tss.portal.engine.macrocode;

import com.boubei.tss.portal.engine.model.DecoratorConfigable;
import com.boubei.tss.portal.engine.model.DecoratorNode;

/**
 * <p> 修饰器宏代码运行容器 </p>
 * <p>
 * 负责解析和运行包含宏代码、变量代码的字符串（HTML、JS、CSS等）
 * </p>
 */
public class DecoratorMacrocodeContainer extends AbstractMacrocodeContainer {
    
    /**
     * js中访问修饰器对象，最终转化为document.getElementById('D×××')（假设此修饰器所在门户结构的ID="xxx"）
     */
    static final String JS_DECORATOR_MACROCODE = "${js.decorator}";
    
    /**
     * js中访问其（本修饰器）所在的版面或页面或portletInstance 包含的Layout或Portlet对象，修饰器用来修饰这些对象的内容
     */
    static final String JS_DECORATOR_CONTENT_MACROCODE = "${js.content}";
    
    /**
     * 版面、页面或Portlet实例的名称，decoratorNode.getParent().getName()。
     */
    static final String DECORATOR_TITLE_MACROCODE = "${title}";
    
    /**
     * Decorator修饰的内容，Object content
     */
    static final String DECORATOR_CONTENT_MACROCODE = "${content}";

    
    /**
     * 容器初始化函数
     * 
     * @param code
     *            String 包含宏代码、变量代码的字符串
     * @param node
     *            DecoratorNode 代码所在修饰器节点对象，包含所有宏代码、变量代码的真实含义等信息
     * @param content
     *            Element 宏代码${content}所对应的Element对象（子节点Element对象Map，用于解析${content}）
     */
    public DecoratorMacrocodeContainer(String code, DecoratorNode node, Object content) {
        this(code, node);
        
        //添加修饰器内容Element对象
        if (content != null) {
            macrocodes.put(DECORATOR_CONTENT_MACROCODE, content);
        }
    }

    public DecoratorMacrocodeContainer(String code, DecoratorNode node) {
        super(code, node);
        macrocodes.put(DECORATOR_TITLE_MACROCODE, node.getParent().getName());
        
        macrocodes.put(JS_DECORATOR_MACROCODE, getElementInJS());
        
        /* js中访问其（本修饰器）所在的版面或页面或portletInstance 包含的Layout或Portlet对象，修饰器用来修饰这些对象的内容 */
        macrocodes.put(JS_DECORATOR_CONTENT_MACROCODE, ((DecoratorConfigable) node.getParent()).getDecoratorContent());
    }
 
    protected String getElementId() {
        return "D" + node.getParent().getId();
    }

    protected String getElementPortotypeId() {
        return "DP" + node.getId();
    }
}