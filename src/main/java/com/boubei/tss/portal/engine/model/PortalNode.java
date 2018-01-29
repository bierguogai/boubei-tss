package com.boubei.tss.portal.engine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.boubei.tss.portal.entity.Structure;

/**
 * 门户节点对象：用于解析门户
 */
public class PortalNode extends AbstractNode implements Supplementable{
    
    private Long portalId;
    
    private Map<Long, Node> nodesMap = new HashMap<Long, Node>();
    public  Map<Long, Node> getNodesMap() { return nodesMap; }
    
	/** 门户全局脚本文件列表：每元素为文件相对路径字符串 */
	private List<String> scriptFiles = new ArrayList<String>();

	/** 门户全局样式表文件列表：每元素为文件相对路径字符串 */
	private List<String> styleFiles = new ArrayList<String>();
	
    private String  scriptCode; //门户全局脚本代码
    private String  styleCode;  //门户全局样式表代码

    public String getScriptCode() { return scriptCode; }
    public String getStyleCode()  { return styleCode; }
    public List<String> getScriptFiles() { return scriptFiles; }
    public List<String> getStyleFiles()  { return styleFiles; }
    
    public void setScriptCode(String scriptCode) { this.scriptCode = scriptCode; }
    public void setStyleCode (String styleCode)  { this.styleCode = styleCode; }  

    public PortalNode(Structure ps) {
        this.id = ps.getId();
        this.name = ps.getName();
        this.code = ps.getCode();
        this.portalId = ps.getPortalId();
    }

    public Node getParent() { return null; }

    public PortalNode getPortal() { return this; }
    public Long getPortalId() { return this.portalId; }
    
    /**
     * (从根节点往下)拷贝门户节点树，并修复节点(自下往上)的关系
     */
    public Object clone(){
        PortalNode copy = (PortalNode) super.clone();
        
        //递归克隆子节点以及子节点的子节点（深度优先）
        copy.children = new LinkedHashSet<Node>();
        for( Node child : this.children ){
            copy.children.add( (Node) child.clone() );
        }     
       
        copy.nodesMap = new HashMap<Long, Node>();
        copy.getNodesMap().put(copy.getId(), copy);
        
        //修复子节点中关于其上层节点信息的属性
        for( Node child : copy.children ){
            SubNode kitten = (SubNode) child;
            kitten.setParent(copy);
            repairSubNode(kitten);
        }
        return copy;
    }
    
    /**
     * 修复子节点中关于其上层节点信息的属性
     * @param node
     */
    private void repairSubNode(SubNode node) {
        Set<Node> children = node.getChildren();
        
        if( node instanceof DecoratorConfigable ) {
            DecoratorConfigable dcNode = (DecoratorConfigable) node;
            repairLeaf(dcNode.getDecoratorNode(), node);
            repairLeaf(dcNode.getDecoratorContent(), node);     
        }
        node.getPortal().getNodesMap().put(node.getId(), node);
        
        for( Node child : children ) {
            SubNode kitten = (SubNode) child;
            kitten.setPortal(node.getPortal());
            kitten.setParent(node);
            kitten.setPage(node.getPage());
            repairSubNode(kitten);
        }              
    }
    
    /**
     * 修复叶子节点(LayoutNode, DecoratorNode, PortletNode)中关于其上层节点信息的属性
     * @param leaf
     * @param parent
     */
    private void repairLeaf(SubNode leaf, SubNode parent){
        leaf.setPage(parent.getPage());
        leaf.setParent(parent);
        leaf.setPortal(parent.getPortal());
    }
}
