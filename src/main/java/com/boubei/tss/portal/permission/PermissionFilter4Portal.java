/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.permission;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.engine.model.Node;
import com.boubei.tss.portal.engine.model.PortalNode;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.filter.IPermissionFilter;
import com.boubei.tss.um.permission.filter.PermissionTag;

/**
 * 过滤门户节点结构树。
 */
public class PermissionFilter4Portal implements IPermissionFilter {

	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
		Node node = (Node) returnValue;
		
        String operation = PortalConstants.PORTAL_VIEW_OPERRATION;
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(tag.application(), PortalConstants.PORTAL_RESOURCE_TYPE, operation);
        
        doFiltrate(node, permitedResourceIds);
	}
	
	/**
     * 过滤门户节点结构树，递归过程
     * @param node
     * @param permitedResouceIds
     */
    void doFiltrate(Node node, List<Long> permitedResouceIds){
        if( node instanceof PortalNode ) {
        	if( !permitedResouceIds.contains(node.getId()) ) {
        		throw new BusinessException(EX.parse(EX.P_10, node.getName()));
        	}
        	PortalNode portalNode = (PortalNode) node;
        	Map<Long, Node> nodesMap = portalNode.getNodesMap();
        	for(Iterator<Entry<Long, Node>> it = nodesMap.entrySet().iterator(); it.hasNext(); ) {
        		Long nodeId = it.next().getKey();
        		if( !permitedResouceIds.contains(nodeId) ) {
        			it.remove();
        		}
        	}
        }
        
        Set<Node> children = node.getChildren();
        for(Iterator<Node> it = children.iterator(); it.hasNext();){
        	Node child = it.next();
            if( !permitedResouceIds.contains(child.getId()) ){
                it.remove();
                continue;
            }
            doFiltrate(child, permitedResouceIds);
        }
    }
}
