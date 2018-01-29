package com.boubei.tss.portal.engine.macrocode;

import java.util.Map;

import com.boubei.tss.portal.engine.HTMLGenerator;
import com.boubei.tss.portal.engine.model.LayoutNode;

/**
 * <p> 布局器宏代码运行容器 </p>
 * <p>
 * 负责解析和运行包含宏代码、变量代码的字符串（HTML、JS、CSS等）
 * </p>
 */
public class LayoutMacrocodeContainer extends AbstractMacrocodeContainer{
    
    /**
     * js中访问布局器对象，最终转化为document.getElementById("Lxxx")（假设此Layout所在门户结构的ID="xxx"）
     */
    static final String JS_LAYOUT_MACROCODE = "${js.layout}";
    
    /**
     * 容器初始化函数
     * 
     * @param code
     *            String 包含宏代码、变量代码的字符串
     * @param node
     *            LayoutNode 代码所在布局器节点对象，包含所有宏代码、变量代码的真实含义等信息
     * @param children
     *            Map 宏代码${porti}（i=0,1,2,3...）所对应的HTMLGenerator.Element对象( 用于解析${porti} )
     */
    public LayoutMacrocodeContainer(String code, LayoutNode node, Map<String, HTMLGenerator.Element> children) {
        this(code, node);
        macrocodes.put(JS_LAYOUT_MACROCODE, getElementInJS());
        
        // 所有子节点（版面中的版面或Portlet实例）
        if (children != null) {
            macrocodes.putAll(children);
        }
    }

    public LayoutMacrocodeContainer(String code, LayoutNode node) {
        super(code, node);
    }
 
    protected String getElementId() {
        return "L" + node.getParent().getId();
    }

    protected String getElementPortotypeId() {
        return "LP" + node.getId();
    }
}
