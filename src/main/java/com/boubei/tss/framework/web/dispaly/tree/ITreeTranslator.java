package com.boubei.tss.framework.web.dispaly.tree;

import java.util.Map;

/** 
 * 树控件节点属性转换器
 * 
 */
public interface ITreeTranslator {
	
    /**
     * 转换节点属性Map
     * @param attributes
     * @return
     */
    Map<String, Object> translate(Map<String, Object> attributes);
}
