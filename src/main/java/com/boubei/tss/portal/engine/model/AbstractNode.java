/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.engine.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.boubei.tss.framework.exception.BusinessException;

public abstract class AbstractNode implements Node {

    protected Long    id;      //节点编号（门户结构节点编号）
    protected String  name;   //节点名称（门户结构节点名称）
    protected String  code;  //门户结构节点的代码,值唯一
    /**
     * 子节点列表
     * --- PortalNode的子节点元素为PageNode对象
     * --- PageNode的子节点元素为SectionNode/PortletInstanceNode对象
     * --- SectionNode的子节点元素为SectionNode/PortletInstanceNode对象
     * --- PortletInstanceNode为叶子节点，无子节点元素
     * --- DecoratorNode为叶子节点，无子节点元素
     * --- LayoutNode为叶子节点，无子节点元素
     * --- PortletNode为叶子节点，无子节点元素
     */
    protected Set<Node> children = new LinkedHashSet<Node>();   
    
    public Set<Node> getChildren() { return children; }
    public void addChild( Node node ) { children.add(node); }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public Long getPortalId() { return getPortal().getPortalId(); }
    
    public abstract Node getParent();

    public abstract PortalNode getPortal();
    
    public int hashCode(){
        return (this.getClass().getName() + "_" + this.getId()).hashCode();
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new BusinessException("clone " + this.getClass().getName() + " object error", e);
        }
    }
}