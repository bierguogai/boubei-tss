package com.boubei.tss.um.permission.dispaly;

import java.util.Map;

/**
 * <p>
 * 树节点操作权限选项。
 * </p>
 */
public interface IPermissionOption {
	
	/**
	 * <p>
	 * 返回权限操作选项的信息，包括选项ID、名称、横向依赖选项ID、纵向依赖选项ID
	 * </p>
	 * @return
	 */
	Map<String, Object> getOptionAttributes();
	
	/**
	 * <p>
	 * 设置option键-值
	 * </p>
	 * @param key
	 * @param value
	 */
	void putOptionAttribute(String key, Object value);	
}

	