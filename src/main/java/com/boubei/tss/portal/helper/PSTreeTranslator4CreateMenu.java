package com.boubei.tss.portal.helper;

import java.util.Map;

import com.boubei.tss.framework.web.display.tree.ITreeTranslator;
import com.boubei.tss.framework.web.display.tree.TreeNode;
import com.boubei.tss.portal.entity.Structure;

/**
 * 创建门户结构导航菜单时，用以弹出选择。
 */
public class PSTreeTranslator4CreateMenu implements ITreeTranslator {
	
	private final int type;

	public PSTreeTranslator4CreateMenu(int type) {
		this.type = type;
	}

	public Map<String, Object> translate(Map<String, Object> attributes) {
	    Object psType = attributes.get("type");
	    switch(type) {
	    case 3: //可以选择portlet instance/版面/页面
	        if(psType.equals(Structure.TYPE_PORTAL)) {
	            attributes.put(TreeNode.TREENODE_ATTR_CANSELECTED, "0"); 
	        }
	        break;
	    case 2: // 可以选择门户/页面
	        if(psType.equals(Structure.TYPE_PORTAL) || psType.equals(Structure.TYPE_PAGE)) {
	            attributes.put(TreeNode.TREENODE_ATTR_CANSELECTED, "0"); 
	        }
	        break;
	    case 1:  // 可以选择版面/页面
	        if(psType.equals(Structure.TYPE_PORTAL) || psType.equals(Structure.TYPE_PORTLET_INSTANCE)) {
	            attributes.put(TreeNode.TREENODE_ATTR_CANSELECTED, "0"); 
	        }
	        break;
	    case 0: // 只有portlet instance可选
	        if( !psType.equals(Structure.TYPE_PORTLET_INSTANCE) ) {
	            attributes.put(TreeNode.TREENODE_ATTR_CANSELECTED, "0"); 
	        }
	        break;
	    }
	    return attributes;
	}
}
