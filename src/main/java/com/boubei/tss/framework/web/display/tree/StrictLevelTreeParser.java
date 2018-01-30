/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.tree;

import java.util.Map;

import com.boubei.tss.util.EasyUtils;


/** 
 * 严格的多层树型解析器。
 * 
 * 如果某个节点的父节点丢失，则其子节点全部丢失。
 * 
 * @see com.boubei.tss.framework.web.display.tree.LevelTreeParser
 */
public class StrictLevelTreeParser extends LevelTreeParser {
	
	private Long rootId;
	
	public StrictLevelTreeParser(Long rootId) {
		this.rootId = rootId;
	}
    
    protected void composeTree(TreeNode root, ILevelTreeNode entity, Map<Long, TreeNode> treeNodeMap) {
        Long parentId = entity.getParentId();
       
        TreeNode parent   = treeNodeMap.get(parentId);
        TreeNode treeNode = treeNodeMap.get(entity.getId());
        
        if( rootId.equals( parentId ) ) {
            root.addChild(treeNode);
        }
        else {
        	// if parent == null, current node will append to a empty node
        	parent = (TreeNode) EasyUtils.checkNull(parent, new TreeNode());
            parent.addChild(treeNode);
        }
    }
}
