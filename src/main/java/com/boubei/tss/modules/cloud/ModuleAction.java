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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.sso.Environment;

@Controller
@RequestMapping("/auth/module")
public class ModuleAction {
	
	@Autowired private ModuleService service;
	
	@RequestMapping(value = "/{module}", method = RequestMethod.POST)
	@ResponseBody
	public Object selectModule(@PathVariable Long module) {
		service.selectModule(Environment.getUserId(), module);
		return "Success";
	}
	
	@RequestMapping(value = "/{module}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object unSelectModule(@PathVariable Long module) {
		service.unSelectModule(Environment.getUserId(), module);
		return "Success";
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<?> listAvaliableModules() {
		return service.listAvaliableModules();
	}
	
	@RequestMapping(value = "/{user}", method = RequestMethod.GET)
	@ResponseBody
	public List<?> listSelectedModules(@PathVariable Long user) {
		user = Environment.getUserId();
		return service.listSelectedModules(user);
	}
}
