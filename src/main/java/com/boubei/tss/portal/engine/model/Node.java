package com.boubei.tss.portal.engine.model;

import java.util.Set;

/**
 * 门户结构节点接口
 */
public interface Node extends Cloneable {
    
    /** 节点ID  */
    Long getId();
    
    /** 节点名称 */
    String getName();
    
    /** 节点code值 */
    String getCode();
    
    /** 节点的父亲节点 */
    Node getParent();
    
    /** 节点所处的门户节点 */
    PortalNode getPortal();
    
    /** 节点的下一层儿子节点 */
    Set<Node> getChildren();
    
    /** 克隆本节点  */
    Object clone();
}

	