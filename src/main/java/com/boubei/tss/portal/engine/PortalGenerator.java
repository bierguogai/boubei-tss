/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Document;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.portal.engine.model.DecoratorNode;
import com.boubei.tss.portal.engine.model.LayoutNode;
import com.boubei.tss.portal.engine.model.Node;
import com.boubei.tss.portal.engine.model.PageNode;
import com.boubei.tss.portal.engine.model.PortalNode;
import com.boubei.tss.portal.engine.model.PortletInstanceNode;
import com.boubei.tss.portal.engine.model.PortletNode;
import com.boubei.tss.portal.engine.model.SectionNode;
import com.boubei.tss.portal.engine.model.SubNode;
import com.boubei.tss.portal.engine.model.Supplementable;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.entity.Structure;
import com.boubei.tss.util.XMLDocUtil;

/**
 * <p> PortalGenerator.java </p>
 * <p>
 * 门户生成器，负责组装门户结构
 * </p>
 */
public class PortalGenerator {
	
	private Map<Long, Component> decoratorsMap = new HashMap<Long, Component>(); // 修饰器池     
    private Map<Long, Component>    layoutsMap = new HashMap<Long, Component>(); // 布局器池     
    private Map<Long, Component>   portletsMap = new HashMap<Long, Component>(); // portlet池     
    
    private PortalGenerator() { }
    
    /**
     * 组装门户结构
     * @param root 
     * 			门户结构根节点
     * @param list
     * 			门户结构列表
     * @param elements
     * 			 修饰器,布局器,portlet等列表(new Object[]{decorators, layouts, portlets})
     * @return
     * 			 Node树根节点,PortalNode
     */
    public static PortalNode genPortalNode(Structure root, List<Structure> list, Object[] elements){
        if( !root.getType().equals(Structure.TYPE_PORTAL) ) {
            throw new BusinessException(EX.parse(EX.P_07, root.getName()));
        }
        
        PortalGenerator generator = new PortalGenerator();
        generator.compose(root, list);
        
        // 初始化元素池,包括修饰器,布局器,portlet．
        List<?> decoratorsList = (List<?>) elements[0];
        List<?> layoutsList    = (List<?>) elements[1];
        List<?> portletsList   = (List<?>) elements[2];
        
        for( Object temp : decoratorsList ){
            Component element = (Component) temp;
        	generator.decoratorsMap.put( element.getId(), element);
        }
        for( Object temp : layoutsList ){
            Component element = (Component) temp;
        	generator.layoutsMap.put( element.getId(), element);
        }
        for( Object temp : portletsList ){
            Component element = (Component) temp;
        	generator.portletsMap.put( element.getId(), element);
        }
        
        return (PortalNode) generator.ps2Node(root, null);
    }
    
    /**
     * 将一个门户结构节点下所有的子节点递归放入到各自的父节点下
     * <br>
     * |-门户_1 <br>
     * ........|- 页面_1 <br>
     * ................|- portlet应用_1_1 <br>
     * ................|- 版面_1_1 <br>
     * ..........................|- 版面_1_1_2 <br>
     * ......................................|- portlet应用_1_1_2_1 <br>
     * ..........................|- portlet应用_1_1_2 <br>
     * ........|- 页面_2 <br>
     * ........|- 页面_3 <br>
     * 
     * @param list
     */
    private void compose(Structure root, List<Structure> list){
        Map<Long, Structure> map = new HashMap<Long, Structure>();
        map.put(root.getId(), root);

        for ( Structure entity : list ) {
            map.put(entity.getId(), entity);
        }
        
        for ( Structure entity : list ) {
            Structure parent = map.get(entity.getParentId());
            if(parent == null) {
                throw new BusinessException(EX.parse(EX.P_06, entity.getName()));
            }
            
            if (root.getType().equals(parent.getType())) {
                root.addChild(entity);
            } else {
                parent.addChild(entity);
            }
        }
    }
    
    /**
     * 根据门户结构的type值转换为相应的Node节点对象
     * @param ps
     * @param parent
     * @return
     */
    private Node ps2Node(Structure ps, Node parent){
        Node newNode = null;
        switch(ps.getType()) {
	        case Structure.TYPE_PORTAL:
	        	newNode = genPortalNode(ps); 
	        	break;
	        case Structure.TYPE_PAGE:
	        	newNode = genPageNode(ps, parent);   
	        	break;
	        case Structure.TYPE_SECTION:
	        	newNode = genSectionNode(ps, parent);
	        	break;
	        case Structure.TYPE_PORTLET_INSTANCE:
	        default:
	        	newNode = genPortletInstanceNode(ps, parent); 
	        	break;
        }
        
        newNode.getPortal().getNodesMap().put(ps.getId(), newNode);
        return newNode;
    }
    
    /**
     * 生成门户根节点
     * @param ps
     * @return
     */
    private Node genPortalNode(Structure ps) {
        PortalNode portalNode = new PortalNode(ps);        
        
        parseSupplement(portalNode, ps.getSupplement());         
        
        for(Structure child : ps.getChildren() ){
            portalNode.addChild(ps2Node(child, portalNode));
        }
        return portalNode;
    }
    
    /**
     * 生成页面节点。
     * 页面节点上还外挂一个修饰器节点和一个布局器节点
     * @param ps
     * @param portalNode
     * @return
     */
    private Node genPageNode(Structure ps, Node portalNode) {       
        PageNode pageNode = new PageNode(ps, portalNode);
            
        parseSupplement(pageNode, ps.getSupplement());         
        
        String parametersOnPs = ps.getParameters(); // 获取门户结构上定义的元素参数列表
        pageNode.setDecoratorNode(new DecoratorNode(decoratorsMap.get(ps.getDecorator().getId()), pageNode, parametersOnPs));
        pageNode.setLayoutNode(new LayoutNode(layoutsMap.get(ps.getDefiner().getId()), pageNode, parametersOnPs));
        
        for(Structure child : ps.getChildren() ){
            pageNode.addChild(ps2Node(child, pageNode));
        }        
        return pageNode;
    }
    
    /**
     * 生成版面节点。
     * 版面节点上还外挂一个修饰器节点和一个布局器节点。
     * @param ps
     * @param parentNode 
     * 				可能是pageNode 也可能是SectionNode 
     * @return
     */
    private Node genSectionNode(Structure ps, Node parentNode) {
        SectionNode sectionNode = new SectionNode(ps);
        repairSupNode(sectionNode, parentNode);

        String parametersOnPs = ps.getParameters(); // 获取门户结构上定义的元素参数列表
        sectionNode.setDecoratorNode(new DecoratorNode(decoratorsMap.get(ps.getDecorator().getId()), sectionNode, parametersOnPs));
        sectionNode.setLayoutNode(new LayoutNode(layoutsMap.get(ps.getDefiner().getId()), sectionNode, parametersOnPs));
        
        for(Structure child : ps.getChildren() ){
            sectionNode.addChild(ps2Node(child, sectionNode));
        }       
        return sectionNode;
    }
    
    /**
     * 生成portlet实例节点。
     * portlet节点上还外挂一个修饰器节点和一个portlet节点
     * @param ps
     * @param parentNode
     * @return
     */
    private Node genPortletInstanceNode(Structure ps, Node parentNode) {
        PortletInstanceNode node = new PortletInstanceNode(ps);
        repairSupNode(node, parentNode);
 
        String parametersOnPs = ps.getParameters(); // 获取门户结构上定义的元素参数列表
        node.setDecoratorNode(new DecoratorNode(decoratorsMap.get(ps.getDecorator().getId()), node, parametersOnPs));
        node.setPortletNode(new PortletNode(portletsMap.get(ps.getDefiner().getId()), node, parametersOnPs));
        
        return node;
    }
    
    /**
     * 设置子节点(SectionNode或PortletInstanceNode)的上层节点,<br>
     * 包括page,portal,parent节点.<br>
     * 如果父节点是PageNode,则当前节点的PageNode就是父节点,<br>
     * PortalNode就是父节点(PageNode)的父节点(PageNode.parent)<br>
     * 如果父节点是SectionNode, 则继续往上找父节点的父节点,直到找到PageNode,再做上一如果的操作.
     * 
     * @param node
     *           被操作节点(SectionNode 或 PortletInstanceNode)
     * @param parentNode
     *           父节点   (PageNode 或 SectionNode)
     */
    private void repairSupNode(SubNode node, Node parentNode){
        node.setParent(parentNode);
        
        PageNode pageNode = null;
        if(parentNode instanceof PageNode){
            pageNode = (PageNode) parentNode;
        }
        if(parentNode instanceof SectionNode){
            Node parent = parentNode.getParent();
            while(pageNode == null){
                if(parent instanceof PageNode){
                    pageNode = (PageNode) parent;
                    break;
                }
                parent = parent.getParent();
            }
        }        
        node.setPage(pageNode);
        node.setPortal((PortalNode) pageNode.getParent());
    }
    
    /**
     * 解析js、css信息。门户或页面上拥有这些信息。
     * @param supplement
     * @return
     */
    private void parseSupplement(Supplementable node, String supplement){
        Document doc = XMLDocUtil.dataXml2Doc(supplement);
        String scriptCode  = doc.selectSingleNode("//script/code").getText();
        String styleCode   = doc.selectSingleNode("//style/code").getText();        
        String scriptFiles = doc.selectSingleNode("//script/file").getText(); //格式:1.js,2js,3js
        String styleFiles  = doc.selectSingleNode("//style/file").getText();  //格式:1.css,2css,3css
        
        List<String> scriptList = new ArrayList<String>();
        List<String> styleList = new ArrayList<String>();
        
        StringTokenizer stk = new StringTokenizer(scriptFiles.replaceAll(",", "\n"));
        while(stk.hasMoreTokens()){
            scriptList.add(stk.nextToken());            
        }
        
        stk = new StringTokenizer(styleFiles.replaceAll(",", "\n"));
        while(stk.hasMoreTokens()){
            styleList.add(stk.nextToken());            
        }
        
        node.setScriptCode(scriptCode);
        node.setStyleCode(styleCode);
        node.getScriptFiles().addAll(scriptList);
        node.getStyleFiles().addAll(styleList);      
    }
}	