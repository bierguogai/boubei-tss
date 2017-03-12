package com.boubei.tss.framework.web.dispaly.tree;

/** 
 * 树型结构解析器
 * 
 */
public interface ITreeParser {

    /**
     * 解析数据，返回包含整个数据树型结构的根节点。
     * @param data
     * @return
     */
    TreeNode parse(Object data);
    
}
