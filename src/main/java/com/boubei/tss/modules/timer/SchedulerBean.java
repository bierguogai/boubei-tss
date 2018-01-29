package com.boubei.tss.modules.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamListener;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

/** 
 * 定时器调度。
 * 
 * 新增或删除一个job失败,不影响其它job的生成和删除。
 * 
 */
public class SchedulerBean implements ParamListener {
    
	protected Logger log = Logger.getLogger(this.getClass());
	
    private Scheduler scheduler;
    private Map<String, JobDef> defsMap;
 
    @SuppressWarnings("unchecked")
	public void afterChange(Param param) {
    	// 为第一次初始化，由ParamServiceImpl初始化完成后触发
    	if(param == null) {
    		List<Param> params = ParamManager.getComboParam(PX.TIMER_PARAM_CODE);
    		params = (List<Param>) EasyUtils.checkNull(params, new ArrayList<Param>());
    		refresh(true, params.toArray(new Param[]{}) );
    		return;
    	}
    	
    	Long parentId = param.getParentId();
    	if(parentId == null) return;
    	
    	Param parent  = ParamManager.getService().getParam(parentId);
		if( parent != null && PX.TIMER_PARAM_CODE.equals(parent.getCode()) ) {
			refresh(false, param);
		}
    }
 
    public SchedulerBean() {
    	// 根据配置决定是否启用定时Job
    	if( Config.TRUE.equals(Config.getAttribute(PX.ENABLE_JOB)) ) {

    		log.info("SchedulerBean is starting....." );
	    	
	    	defsMap = new HashMap<String, JobDef>();
	    	try {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
				scheduler.start();
			} catch (SchedulerException e) {
	            throw new BusinessException("init SchedulerBean error", e);
	        } 
    	}
    }
    
    // 停用的也要一块，不然在Param被重启用后，jobs找不到相应old，则会重复新建JobDef而失败
    private List<JobDef> getJobDefs(boolean init, Param...params) {
    	List<JobDef> jobDefs = new ArrayList<JobDef>();
    	Map<String, JobDef> jobs = new HashMap<String, JobDef>();
    	
		ICommonService commonService = Global.getCommonService();
		List<?> list = commonService.getList(" from JobDef "); 
		for(Object obj : list) {
			JobDef job = (JobDef) obj;
			jobDefs.add( job );
			jobs.put(job.getCode(), job);
		}
    	
    	// merge params.job into JobDef
    	for(Param param : params) {
    		JobDef job = new JobDef(param);
    		JobDef old = jobs.get(job.getCode());
    		if(old == null) {
    			commonService.create(job);
    			jobDefs.add(job);
    		} 
    		else if(!init) {
    			old.setName(job.getName());
    			old.setJobClassName(job.getJobClassName());
    			old.setTimeStrategy(job.getTimeStrategy());
    			old.setCustomizeInfo(job.getCustomizeInfo());
    			old.setDisabled(job.getDisabled());
    			commonService.update(old);
    		} 
		}
    	
		return jobDefs;
    }
    
    public void refresh(boolean init, Param...params) {
    	if(scheduler == null) return;
    	
    	log.debug("SchedulerBean refresh begin...");
    	
    	List<JobDef> list = getJobDefs(init, params);
    	
        List<String> jobCodes = new ArrayList<String>();
		for(JobDef def : list) {
			
			String code  = def.getCode();
			String jobName = code;
			jobCodes.add( code );
			
			// 如果已经生成且没做过修过（包括没被停用），则不变
			if( def.equals(defsMap.get(code)) ) { 
				continue; 
			} 
			// 如果已经存在，且value发生了变化，则先删除旧的Job，重新生成新的Job
			else if(defsMap.containsKey(code)) {
				deleteJob(code); 
			}
			
			if( def.isDisabled() ) {
				continue; // 停用的JOB剔除后无需再生成
			}
			
			// 新增或修过过的定时配置，需要重新生成定时Job
			@SuppressWarnings("unchecked")
			Class<Job> jobClazz = (Class<Job>) BeanUtil.createClassByName(def.getJobClassName());
			JobDetail aJob = JobBuilder.newJob(jobClazz)
					.withIdentity(jobName)
					.usingJobData(jobName, def.getCustomizeInfo())
					.usingJobData(jobName + "-ID", def.getId())
					.build();
			
			Trigger trigger;
			try {
				String ts = def.getTimeStrategy(); // 定时策略
				trigger = TriggerBuilder.newTrigger().withSchedule( CronScheduleBuilder.cronSchedule(ts) ).build(); 
				scheduler.scheduleJob(aJob, trigger);
				
				log.info(" scheduler.scheduleJob: " + jobName + " successed. timeStrategy=" + ts );
				
				JobDef copy = new JobDef();
				BeanUtil.copy(copy, def);
				defsMap.put(code, copy);
			} 
			catch (Exception e) {
				log.error("init Job[" + jobName + "] failed, config = " + def, e);
			}  
		}
		
		Set<String> deleteJobCodes = new HashSet<String>(defsMap.keySet());
		deleteJobCodes.removeAll(jobCodes);
		for(String code : deleteJobCodes) {
			deleteJob(code); // 停用/删除的定时配置
		}
        
        log.debug("SchedulerBean init end.");
    }
    
    private void deleteJob(String jobName) {
    	try {
			JobKey key = new JobKey(jobName);
			scheduler.deleteJob(key );
			
			defsMap.remove(jobName);
			log.info(" scheduler.deleteJob: " + jobName + " successed." );
		} 
		catch (SchedulerException e) {
			log.error("remove Job[" + jobName + "] failed", e);
		}
    }
}

