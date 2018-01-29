package com.boubei.tss.portal.engine.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.util.XMLDocUtil;

/**
 * 元素（修饰器/布局器/portlet）节点超类对象。
 * 
 * 用于解析门户中使用到的修饰器/布局器/portlet
 * 
 */
public abstract class AbstractElementNode extends AbstractSubNode {
    
    protected Component element;  // 元素对象本身

    protected PortalNode portal; // 所属Portal节点对象
    protected PageNode   page;   // 所属页面节点对象
    
    protected String html;       // HTML代码
    protected String script;     // Script代码
    protected String style;      // style代码
    
    protected Map<String, String> events = new HashMap<String, String>();     // 事件定义：onload/onunload
    protected Map<String, String> parameters = new HashMap<String, String>(); // 自定义参数的默认值列表 + 动态配置的参数

    public Map<String, String> getEvents() { return events; }
    public Map<String, String> getParameters() { return parameters; }
 
    public String getHtml()   { return html; }
    public String getScript() { return script; }
    public String getStyle()  { return style; }
    
    public PageNode getPage()     { return page; }
    public PortalNode getPortal() { return portal; }
    
    public void setPage(PageNode page)       { this.page = page; }
    public void setPortal(PortalNode portal) { this.portal = portal; }

	// 无儿子节点，本身为叶子节点
    public void addChild( Node node ) { 
    	// do nothing
    }
 
    /**
     * @param element
     *          元素实体
     * @param parent
     *          元素所在的门户结构Node(PageNode、SectionNode 或 PortletInstanceNode)
     * @param parametersOnPs
     *          元素所在门户结构配置的该元素的参数列表
     */
    public AbstractElementNode(Component element, SubNode parent, String parametersOnPs) {
    	this(element, parent);
        
        //先放入元素自定义参数的默认值(parse()方法里已经放进去了)，然后再放入具体门户结构上定义的参数以覆盖默认的（如下）
    	if(parametersOnPs != null) {
    	    Document paramsDoc = XMLDocUtil.dataXml2Doc(parametersOnPs);
            org.dom4j.Element paramsNode = (org.dom4j.Element) paramsDoc.selectSingleNode("//" + element.getComponentType());
        	Map<String, String> configParamsMap = XMLDocUtil.dataNode2Map(paramsNode);
            if(configParamsMap != null) {
                getParameters().putAll(configParamsMap);
            }
    	}
    }
    
    public AbstractElementNode(Component element, SubNode parent) {
        this.element = element;
        
        this.id   = element.getId();
        this.name = element.getName();
        this.code = element.getCode();
        
        this.parent = parent;
        this.page   = parent.getPage();
        this.portal = parent.getPortal();
        
        parse(element.getDefinition(), element.getComponentType());
    }

    protected Document parse(String definition, String elementName) {
        Document doc = XMLDocUtil.dataXml2Doc(definition);
        
        org.dom4j.Node htmlNode    = doc.selectSingleNode("/" + elementName + "/html");
        org.dom4j.Node scriptNode  = doc.selectSingleNode("/" + elementName + "/script");
        org.dom4j.Node styleNode   = doc.selectSingleNode("/" + elementName + "/style");
        
        this.html   = XMLDocUtil.getNodeText(htmlNode);
        this.script = XMLDocUtil.getNodeText(scriptNode);
        this.style  = XMLDocUtil.getNodeText(styleNode);
        
        List<org.dom4j.Element> eventNodes = XMLDocUtil.selectNodes(doc, "/" + elementName + "/events/attach");
        if(eventNodes != null){
            for( org.dom4j.Element eventNode : eventNodes ){
                this.events.put(eventNode.attributeValue("event"), eventNode.attributeValue("onevent"));
            }   
        }
        
        List<org.dom4j.Element> paramNodes = XMLDocUtil.selectNodes(doc, "/" + elementName + "/parameters/param");
        if(paramNodes != null){
            for( org.dom4j.Element paramNode : paramNodes ){
                getParameters().put(paramNode.attributeValue("name"), paramNode.attributeValue("defaultValue"));
            } 
        } 
        return doc;
    }
 
    public String  getResourcePath(){
        return element.getResourcePath();
    }
    
    public Object clone(){
        AbstractElementNode copy = (AbstractElementNode) super.clone();
        copy.parameters = new HashMap<String, String>(this.parameters);
        copy.events     = new HashMap<String, String>(this.events);
        return copy;
    }
}