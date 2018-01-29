package com.boubei.tss.modules.cloud;

import java.util.List;

public interface ModuleService {

	List<?> listAvaliableModules();
	
	List<?> listSelectedModules(Long user);

	void unSelectModule(Long user, Long module);

	void selectModule(Long user, Long module);

}
