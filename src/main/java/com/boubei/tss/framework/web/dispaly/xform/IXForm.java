package com.boubei.tss.framework.web.dispaly.xform;

import java.util.Map;

/**
 * 定义各个用到XForm的实体需要继承的共同接口
 * 
 * @author Jon.King  2005-9-7
 */
public interface IXForm {
    
	/**
	 * 节点属性集合
     * 
	 * @return Map
	 */
	Map<String, Object> getAttributes4XForm();
}
