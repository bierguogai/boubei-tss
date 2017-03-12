package com.boubei.tss.framework.web.dispaly.grid;

import java.util.HashMap;
import java.util.Map;

public class DefaultGridNode implements IGridNode {

	private Map<String, Object> attrs = new HashMap<String, Object>();

	private GridValueFilter filter;

	public DefaultGridNode() {
		filter = new GridValueFilter() {
			public Object pretreat(Object key, Object value) {
				return value;
			}
		};
	}

	public DefaultGridNode(GridValueFilter filter) {
		this.filter = filter;
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public GridAttributesMap getAttributes(GridAttributesMap map) {
		for (Map.Entry<String, Object> entry : attrs.entrySet()) {
			String name = entry.getKey();
			Object value = filter.pretreat(name, entry.getValue());
			if (value != null) {
				map.put(name, value);
			}
		}
		return map;
	}

}
