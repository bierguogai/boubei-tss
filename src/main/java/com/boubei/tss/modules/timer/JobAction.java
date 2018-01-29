package com.boubei.tss.modules.timer;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.modules.param.ParamListener;
import com.boubei.tss.modules.param.ParamManager;

@Controller
@RequestMapping("/auth/job")
public class JobAction {
	
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	@ResponseBody
	public Object refresh() {
		for( ParamListener bean : ParamManager.listeners) {
			if(bean instanceof SchedulerBean) {
				((SchedulerBean)bean).refresh(false);
			}
		}
		return "Success";
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> listJobs() {
		String sql = "select id, id as value, name from component_job_def " +
				" where disabled=0 and jobClassName like '%etl%' order by name";
		return SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql);
	}
}
