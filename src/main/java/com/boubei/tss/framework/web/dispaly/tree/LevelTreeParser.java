package com.boubei.tss.framework.web.dispaly.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 多层树形结构转换器。
 * 
 * 如果由于数据错误，其中一个节点没有父亲节点，则会把该节点挂载到根节点下。
 */
public class LevelTreeParser implements ITreeParser {
   
	public TreeNode parse(Object data) {
		
		// 继承ILevelTreeNode接口的实体列表
		@SuppressWarnings("unchecked")
		List<ILevelTreeNode> list = (List<ILevelTreeNode>)data;  
		
        TreeNode root = new TreeNode();
        if(list == null || list.isEmpty()) {
        	return root;
        }

        Map<Long, TreeNode> treeNodeMap = new HashMap<Long, TreeNode>();
        
        for(ILevelTreeNode entity : list) {
            TreeNode treeNode = new TreeNode(entity); // 将实体转换成真正的TreeNode对象
			treeNodeMap.put(entity.getId(), treeNode);
        }
        
        for(ILevelTreeNode entity : list){
            composeTree(root, entity, treeNodeMap);
        }
        
        return root;
    }

    protected void composeTree(TreeNode root, ILevelTreeNode entity, Map<Long, TreeNode> treeNodeMap) {
        TreeNode parent   = treeNodeMap.get(entity.getParentId());
        TreeNode treeNode = treeNodeMap.get(entity.getId());
        if(parent == null) {
            root.addChild(treeNode);
        } 
        else {
            parent.addChild(treeNode);
        }
    }

}
