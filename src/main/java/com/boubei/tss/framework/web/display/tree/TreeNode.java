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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.boubei.tss.util.XmlUtil;

/**
 * 树节点对象
 * 
 * _open = "true" 默认打开
 */
public class TreeNode {
	
    public static final String DISABLED = "0";  // disabled
    public static final String ENABLED  = "1";  // enabled

    public static final String TREENODE_ATTR_CANSELECTED = "canselected"; // 可选
    public static final String TREENODE_ATTR_DISPLAY = "display"; // 是否显示

    public static final String TREENODE_ATTR_ID   = "id";  
    public static final String TREENODE_ATTR_NAME = "name";

    Map<String, Object> attributes; // 节点属性

    List<TreeNode> children = new ArrayList<TreeNode>(); // 子节点：TreeNode对象

    public TreeNode() { 
        attributes = new HashMap<String, Object>();
    }

    public TreeNode(ITreeNode item) {
        attributes = item.getAttributes();
    }
    
    public Object getId() {
        return attributes.get(TREENODE_ATTR_ID);
    }
    
    /**
     * 初始化树节点。
     * @param id
     * @param name
     * @param display
     */
    void initTreeNode(String id, String name, boolean display) {
        attributes.put(TREENODE_ATTR_ID, id);
        attributes.put(TREENODE_ATTR_NAME, name);
        attributes.put(TREENODE_ATTR_DISPLAY, display ? ENABLED : DISABLED);
    }

    /**
     * 生成节点的xml数据。
     * 递归调用，将整棵树输出为XML格式数据
     *
     * @param translator
     * @param tree_node_name
     * @return
     */
    public String toXml(String nodeName, ITreeTranslator translator) {
        Map<String, Object> newAttributes = isRootNode() ? 
                attributes : translator.translate(attributes); // 判断是否是Root节点，root节点无需转换
        
        StringBuffer sb = new StringBuffer();
        if(display()) {
            sb.append("<").append(nodeName);
            for (Entry<String, Object> entry : newAttributes.entrySet()) {
                sb.append(" ").append(entry.getKey()).append("=\"").append(XmlUtil.toFormXml(entry.getValue())).append("\"");
            }
            sb.append(">");
        }

        // 递归调用，将整棵树输出为XML格式数据
        for (TreeNode childNode : children) {
            sb.append(childNode.toXml(nodeName, translator));
        }
        
        if(display()) {
            sb.append("</").append(nodeName).append(">");
        }
        
        return sb.toString();
    }
    
    boolean isRootNode() {
        Object treeNodeId = this.getId();
		return TreeEncoder.TREE_ROOT_NODE_ID.equals(treeNodeId);
    }
    
    boolean display() {
        Object display = attributes.get(TreeNode.TREENODE_ATTR_DISPLAY);
		return display == null || ENABLED.equals(display);
    }

    /**
     * 新增一个儿子节点。
     * @param treeNode
     */
    public void addChild(TreeNode treeNode) {
        children.add(treeNode);
    }

    /**
     * 禁止选择
     */
    public void disabled() {
        attributes.put(TREENODE_ATTR_CANSELECTED, DISABLED);
    }

    /**
     * 允许选择
     */
    public void enabled() {
        attributes.put(TREENODE_ATTR_CANSELECTED, ENABLED);
    }
    
    /**
     * 设置节点属性值
     * 
     * @param name
     * @param value
     */
    public void setAttribute(String name, String value){
        attributes.put(name, value);
    }
}