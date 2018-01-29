package com.boubei.tss.um.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.framework.web.display.tree.ITreeParser;
import com.boubei.tss.framework.web.display.tree.TreeNode;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.util.EasyUtils;

/**
 * 用户组，应用系统树型结构解析器
 */
@SuppressWarnings("unchecked")
public class GroupTreeParser implements ITreeParser {
 
	public TreeNode parse(Object data) {
		List<Group> mainAndAssistantGroups = (List<Group>) data;
		
		TreeNode root = new TreeNode();
		Map<Long, TreeNode> treeNodeMap = new HashMap<Long, TreeNode>();
		
		// 解析主用户组和辅助用户组
		if ( !EasyUtils.isNullOrEmpty(mainAndAssistantGroups) ) {
	        for (Group group : mainAndAssistantGroups) {
	            TreeNode item = new TreeNode(group);
	            treeNodeMap.put(group.getId(), item);
	        }
	        
	        parserGroup(root, (List<Group>) mainAndAssistantGroups, treeNodeMap);
		}
		
		return root;
	}

	/**
	 * 解析用户组节点， 挂靠到各自的父节点下
	 * @param root 应用的根节点
	 * @param groups 
	 * @param treeNodeMap
	 */
    protected void parserGroup(TreeNode root, List<Group> groups, Map<Long, TreeNode> treeNodeMap) {
		for ( Group group : groups ) {
			TreeNode groupParent = (TreeNode) treeNodeMap.get(group.getParentId());
		    TreeNode treeNode = (TreeNode) treeNodeMap.get(group.getId());
		    if (groupParent == null) {
		    	root.addChild(treeNode);
		    } else {
		    	groupParent.addChild(treeNode);
		    }
		}
	}
}
