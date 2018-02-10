/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.engine.macrocode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.portal.engine.model.AbstractElementNode;
import com.boubei.tss.util.MacrocodeCompiler;

/** 
 * <p>
 * 元素宏代码容器。元素包括 布局器、修饰器、portlet
 * </p> 
 */
public abstract class AbstractMacrocodeContainer {
    
    protected Logger log = Logger.getLogger(this.getClass());
    
    /**
     * <p>
     * 转成执行结果： 宏、变量解析后的执行结果代码
     * </p>
     * @return String 
     */
    public String compile() {
        return MacrocodeCompiler.run(code, macrocodes, true);
    }
 
    /**
     * 布局器、修饰器、Portlet资源文件所在的相对路径
     */
    static final String ELEMENT_BASEPATH_MACROCODE = "${basepath}";
    
    /**
     * Decorator的根节点元素的编号，即"Dxxx"（假设布局器所在的门户结构：Portlet实例或版面或页面的ID="xxx") 或<br>
     * Layout   的根节点元素的编号，即"Lxxx"（假设布局器所在门户结构：页面或版面的ID="xxx"）              或<br>
     * Portlet  的根节点元素的编号，即"Pxxx"（假设Portlet所在的门户结构：portletInstance的ID="xxx"）
     */
    static final String ELEMENT_ID_MACROCODE = "${id}";
    
    /**
     * Decorator的原型ID，即"DPyyy"（假设此Decorator的ID="yyy"） 或<br>
     * Layout   的原型ID，即"LPyyy"（假设此Layout   的ID="yyy"） 或<br>
     * Portlet  的原型ID，即"PPyyy"（假设此Portlet  的ID="yyy"）。
     * 
     * 用于同类型布局器、修饰器、portlet共享相同CSS时。
     */
    static final String ELEMENT_PROTOTYPE_ID_MACROCODE = "${prototype.id}";
    
    /**
     * js中访问此修饰器对象，同${js.decorator}，document.getElementById('D×××') 或<br>
     * js中访问此布局器对象，同${js.layout}，   document.getElementById("Pxxx") 或<br>
     * js中访问此Portlet实例对象，同${js.portletInstance}， document.getElementById("xxx")  ---- 特殊，区别${js.portlet}以及上两种
     */
    static final String JS_THIS_MACROCODE = "${js.this}";
    
    /**
     * 在JS中访问当前修饰器所在的门户结构：版面或页面或portletInstance对象   或<br>
     * 在JS中访问当前布局器所在的门户结构：版面或页面对象                    或<br>
     * 在JS中访问当前Portlet所在的门户结构：portletInstance对象所在的门户结构：版面或页面对象 ---- 特殊,区别上两种<br>
     * <br>
     * 最终转化为document.getElementById('id')， id为门户结构的ID
     */
    static final String JS_ELEMENT_PARENT_MACROCODE = "${js.parent}";

    /**
     * 原执行代码：包含宏代码、变量等
     */
    protected String code;
    
    /**
     * 元素节点
     */
    protected AbstractElementNode node;
    
    /**
     * 宏代码池
     */
    protected Map<String, Object> macrocodes;
    
    /**
     * 容器初始化函数
     * 
     * @param code
     *            String 包含宏代码、变量代码的字符串
     * @param node
     *            元素 代码所在元素节点对象，包含所有宏代码、变量代码的真实含义等信息
     */
    public AbstractMacrocodeContainer(String code, AbstractElementNode node){
        this.code = code;
        this.node = node;
        macrocodes = new HashMap<String, Object>();
        
        // ${basepath}: 布局器、修饰器、Portlet资源文件所在的相对路径
        macrocodes.put(ELEMENT_BASEPATH_MACROCODE, Environment.getContextPath() + "/" + node.getResourcePath() + "/");
        
        macrocodes.put(ELEMENT_ID_MACROCODE, getElementId());
        macrocodes.put(ELEMENT_PROTOTYPE_ID_MACROCODE, getElementPortotypeId());
        
        macrocodes.put(JS_THIS_MACROCODE, getElementInJS());
        macrocodes.put(JS_ELEMENT_PARENT_MACROCODE, getParentElement());
        
        // 添加修饰器对应自定义参数集合
        macrocodes.putAll(getParameters());
    }
    
    /**
     * <p>
     * 获取元素所在门户结构对应的对象： document.getElementById('parentId')
     * </p>
     * @return JS脚本
     */
    protected String getParentElement() {
        return "document.getElementById('" + node.getParent().getId() + "')";
    }
    
    /**
     * <p>
     * 获取元素在页面上对应的自己的对象：document.getElementById('D/L/P×××')
     * </p>
     * @return String JS脚本
     */
    protected Object getElementInJS () {
        return "document.getElementById('" + getElementId() + "')";
    }
    
    /**
     * <p>
     * 获取网页（页面）上对应本元素的标识值："D/L/P" + 所在页面、版面或Portlet实例的ID
     * </p>
     * @return 
     *         String "D/L/P" + ID
     */
    protected abstract String getElementId();
    
    /**
     * <p>
     * 获取网页（页面）上对应本元素原型的标识值："D/L/P" + "P" + 元素的ID
     * </p>
     * @return 
     *         String "D/L/P" + "P" + 元素的ID
     */
    protected abstract String getElementPortotypeId();
    
    /**
     * 获取元素的自定义参数集合：参数名/参数值 Map
     * @return
     */
    protected Map<String, Object> getParameters() {
        Map<String, String> paramsMap = node.getParameters();
        Map<String, Object> params = new HashMap<String, Object>();
    	for( Entry<String, String> entry : paramsMap.entrySet() ) {
            String key = MacrocodeCompiler.createVariable(entry.getKey());
			params.put(key, entry.getValue());
        }
        
        return params;
    }
}
