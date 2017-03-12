package com.boubei.tss.framework;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.sso.Environment;

@Controller
@RequestMapping("/si")
public class SystemInfo {
	
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	@ResponseBody
	public Object[] getVersion() {
		String packageTime = Config.getAttribute("last.package.time");
		String environment = Config.getAttribute("environment");
		return new Object[] { packageTime, environment };
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody
	public Object[] getLoginUser() {
		return new Object[] { Environment.getUserCode() };
	}
	
	@RequestMapping(value = "/threads", method = RequestMethod.GET)
	@ResponseBody
	public Object[] getThreadInfos() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);  
		return new Object[] { threadMXBean.getThreadCount(), threadInfos };
	}
}
