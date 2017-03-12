package com.boubei.tss.framework.timer;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;

public abstract class AbstractJob implements Job {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	IBusinessLogger businessLogger;
	
	/**
	 * 模拟登录，用以初始化Environment
	 */
	protected void initEnv() {
        String token = TokenUtil.createToken("1234567890", UMConstants.ROBOT_USER_ID);
		IdentityCard card = new IdentityCard(token, new OperatorDTO(UMConstants.ROBOT_USER_ID, "Job.Robot"));
		Context.initIdentityInfo(card); 
	}
	
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	try {
    		initEnv();
    		businessLogger = (IBusinessLogger) Global.getBean("BusinessLogger"); // 跑Test时可能没有spring IOC
    	} catch (Exception e) { }
    	
    	JobDetail jobDetail = context.getJobDetail();
    	String jobName = jobDetail.getName();
    	
    	JobDataMap dataMap = jobDetail.getJobDataMap();
        
        log.info("定时任务：（" + jobName + "）开始执行。");
        
        String resultMsg;
        Log excuteLog = null;
        
        try {
        	Long preTime = System.currentTimeMillis();
        	
        	excuteJob((String) dataMap.get(jobName));
        	
        	int methodExcuteTime = (int) (System.currentTimeMillis() - preTime);
        	
        	resultMsg = "定时任务：（" + jobName + "）执行完成。";
        	log.info(resultMsg);
        	
        	excuteLog = new Log(jobName + " - 成功", resultMsg);
        	excuteLog.setMethodExcuteTime(methodExcuteTime);
        } 
        catch(Exception e) {
        	resultMsg = "定时任务：（" + jobName + "）执行出错了: " + e.getMessage();
        	log.error(resultMsg, e);
        	
        	excuteLog = new Log(jobName + " -【失败!!!】", resultMsg);
        } 
        finally {
        	if(excuteLog != null && businessLogger != null) {
        		excuteLog.setOperateTable("定时任务");
                businessLogger.output(excuteLog);
        	}
        }
    }
    
    protected abstract void excuteJob(String jobConfig);
}
