package com.boubei.tss.um.permission.dispaly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.framework.web.display.tree.ITreeParser;
import com.boubei.tss.framework.web.display.tree.TreeNode;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.EasyUtils;

public class ResourceTreeParser implements ITreeParser {

	public final static String RESOUCE_NODE = "node";
	public final static String OPTIONS = "options";

	
    public TreeNode parse(Object data) {
		TreeNode root = new TreeNode();

		@SuppressWarnings("unchecked")
		List<ResourceTreeNode> leftTree = (List<ResourceTreeNode>) data; // 资源树 or 角色树
		
		if( EasyUtils.isNullOrEmpty(leftTree) ) return root;
		
	    Map<Long, TreeNode> treeNodeMap = new HashMap<Long, TreeNode>();
        for ( ResourceTreeNode resource : leftTree ) {
            if(checkIsAdmin(resource)) continue;  // 过滤掉系统管理员用户或者系统管理员角色
            
            TreeNode item = new TreeNode(resource);
            Map<String, Object> optionsMap = resource.getOptionAttributes();
			for(String key : optionsMap.keySet()){
                item.setAttribute(key, optionsMap.get(key).toString());
			}
			treeNodeMap.put(resource.getId(), item);	
		}
        
        for ( ResourceTreeNode resource : leftTree ) {
            if(checkIsAdmin(resource)) continue;  // 过滤掉系统管理员用户或者系统管理员角色
            
			TreeNode parent   = treeNodeMap.get(resource.getParentId());
			TreeNode treeNode = treeNodeMap.get(resource.getId());
			if (parent == null) {
				root.addChild(treeNode);
			} 
			else {
				parent.addChild(treeNode);
			}
		}
		return root;
	}

    // 检查是否为 系统管理员用户资源 或者 系统管理员角色。
    private boolean checkIsAdmin(ResourceTreeNode resource) {
        return resource.getId().equals( UMConstants.ADMIN_ROLE_ID ) || resource.getResourceName().equals(UMConstants.ADMIN_ROLE_NAME);
    }

}
