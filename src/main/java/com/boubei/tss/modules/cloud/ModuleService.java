/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.cloud;

import java.util.List;

public interface ModuleService {

	List<?> listAvaliableModules();
	
	List<?> listSelectedModules(Long user);

	void unSelectModule(Long user, Long module);

	void selectModule(Long user, Long module);

}
