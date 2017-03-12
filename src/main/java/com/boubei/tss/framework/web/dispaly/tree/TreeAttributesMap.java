package com.boubei.tss.framework.web.dispaly.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** 
 * 树节点属性集合：name/value
 * 
 */
public class TreeAttributesMap implements Map<String, Object>{
	
    private Map<String, Object> attributes = new HashMap<String, Object>();
    
    /**
     * 构造器
     * @param id
     * @param name
     */
    public TreeAttributesMap(Object id, Object name){
        attributes.put(TreeNode.TREENODE_ATTR_ID, id);
        attributes.put(TreeNode.TREENODE_ATTR_NAME, name);
    }
    
    /**
     * 获取所需要要的属性
     * @return
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    /**
     * 添加附属属性
     * @param name
     * @param value
     */
	public Object put(String key, Object value) {
		if(value == null) {
			return null;
		}
		if(value instanceof String) {
			value = value.toString().replace('&', '|'); // & 会导致树节点无法显示
		}
		
		return attributes.put(key, value);
	}

	public void clear() {
		attributes.clear();
	}

	public boolean containsKey(Object key) {
		return attributes.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return attributes.containsValue(value);
	}

	public Set<Entry<String, Object>> entrySet() {
		return attributes.entrySet();
	}

	public Object get(Object key) {
		return attributes.get(key);
	}

	public boolean isEmpty() {
		return attributes.isEmpty();
	}

	public Set<String> keySet() {
		return attributes.keySet();
	}

	public Object remove(Object key) {
		return attributes.remove(key);
	}

	public int size() {
		return attributes.size();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		attributes.putAll(m);
	}

	public Collection<Object> values() {
		return attributes.values();
	}
}
