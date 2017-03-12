package com.boubei.tss.framework.persistence.pagequery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 支持宏定义的查询条件类基类 
 */
public abstract class MacrocodeQueryCondition  {
    
    /**
     * 分页信息对象
     */
    PageInfo page;  
    
    /**
     * 排序字段，格式如 ["o.decode asc", "u.createdTime desc"]
     */
    List<String> orderByFields;  
    
    /**
     * 条件对象中不理睬的属性名称集合
     */
    Set<String> ignores; 
 
    public PageInfo getPage() {
        if(page == null) {
            page = new PageInfo();
        }
        return page;
    }
 
    public Set<String> getIgnoreProperties() {
        if(ignores == null) {
            ignores = new HashSet<String>(); 
            ignores.add("orderByFields");
            ignores.add("ignores");
            ignores.add("page");
        }
        return ignores;
    }
    
	public List<String> getOrderByFields() {
	    if(orderByFields == null) {
	        orderByFields = new ArrayList<String>();
	    }
		return orderByFields;
	}

	public String toConditionString() {
		StringBuffer buffer = new StringBuffer();
		Map<String, Object> conditionsMap = getConditionMacrocodes();
		for(String macro : conditionsMap.keySet()) {
			buffer.append(macro).append(" ");
		}
		return buffer.toString();
	}
	
	public String toString() {
		return toConditionString();
	}
	
    /**
     * 获取条件查询HQL/SQL条件语句宏代码字典
     * @return Map 
     * 			条件宏代码字典对象
     */
	public abstract Map<String, Object> getConditionMacrocodes();

}

	