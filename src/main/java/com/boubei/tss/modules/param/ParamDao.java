/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.param;

import java.util.List;

import com.boubei.tss.framework.persistence.ITreeSupportDao;
 
public interface ParamDao extends ITreeSupportDao<Param>{

	List<?> getAllParam(boolean includeHidden);
	
	/**
     * 根据code值获取参数。（区分参数组、参数、参数项的概念）
	 * @param code
	 * @return
	 */
	Param getParamByCode(String code);
	
	List<?> getCanAddGroups();
	
	List<Param> getChildrenByDecode(String decode);
}
