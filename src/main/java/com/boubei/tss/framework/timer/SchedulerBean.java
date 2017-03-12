package com.boubei.tss.framework.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
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
    private Map<String, String> configsMap;
 
    public void afterChange(Param param) {
    	// 为第一次初始化，由ParamServiceImpl初始化完成后触发
    	if(param == null) {
    		refresh();
    		return;
    	}
    	
    	Long parentId = param.getParentId();
    	if(parentId == null) return;
    	
    	Param parent  = ParamManager.getService().getParam(parentId);
		if( parent != null && PX.TIMER_PARAM_CODE.equals(parent.getCode()) ) {
			refresh();
		}
    }
 
    public SchedulerBean() {
    	
    	configsMap = new HashMap<String, String>();
    	
    	// 根据配置决定是否启用定时Job
    	if( !Config.TRUE.equals(Config.getAttribute(PX.ENABLE_JOB)) ) {
    		return;
    	}
    	
    	log.info("SchedulerBean is starting....." );
    	
    	try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
            throw new BusinessException("初始化定时策略出错!", e);
        } 
    }
    
    private void refresh() {
    	if(scheduler == null) return;
    	
        List<Param> list = null;
        try {
        	list = ParamManager.getComboParam(PX.TIMER_PARAM_CODE);
        } catch(Exception e) {
        	log.error("定时任务配置有误", e);
        }
        if( EasyUtils.isNullOrEmpty(list) ) {
        	return;
        }
        
        log.debug("SchedulerBean refresh begin...");
        
        List<String> jobCodes = new ArrayList<String>();
		for(Param param : list) {
			if(ParamConstants.TRUE.equals(param.getDisabled())) {
				continue; // 停用的定时配置不要
			}
			
			String code  = param.getText();
			String value = param.getValue();
			
			String jobName = "Job-" + code;
			jobCodes.add(code);
			
			if( value.equals(configsMap.get(code)) ) {
				continue; // 如果已经生成且没做过修过，则不变
			} else if(configsMap.containsKey(code)) {
				deleteJob(code); // 如果已经存在，且value发生了变化，则先删除旧的Job，重新生成新的Job
			}
			
			// 新增或修过过的定时配置，需要重新生成定时Job
			String configs[] = EasyUtils.split(value, "|"); // jobClassName | timeDescr | customizeConfig
			Class<?> jobClazz = BeanUtil.createClassByName(configs[0].trim());
			JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClazz);
			jobDetail.getJobDataMap().put(jobName, configs[2].trim());
			
			String triggerName = "Trigger-" + code;
			Trigger trigger;
			try {
				String scheduleTime = configs[1].trim();
				trigger = new CronTrigger(triggerName, Scheduler.DEFAULT_GROUP, scheduleTime); // 第三个参数为定时时间
				scheduler.scheduleJob(jobDetail, trigger);
				
				log.info(" scheduler.scheduleJob: " + jobName + " successed. scheduleTime=" + scheduleTime );
				
				configsMap.put(code, value);
			} 
			catch (Exception e) {
				log.error("初始化定时Job【" + jobName + "】失败, config = " + value, e);
			}  
		}
		
		Set<String> deleteJobCodes = new HashSet<String>(configsMap.keySet());
		deleteJobCodes.removeAll(jobCodes);
		for(String code : deleteJobCodes) {
			deleteJob(code); // 停用/删除的定时配置
		}
        
        log.debug("SchedulerBean init end.");
    }
    
    private void deleteJob(String code) {
    	String jobName = "Job-" + code;
    	try {
			scheduler.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
			configsMap.remove(code);
			log.info(" scheduler.deleteJob: " + jobName + " successed." );
		} 
		catch (SchedulerException e) {
			log.error("删除定时Job：" + jobName + "失败", e);
		}
    }
}

