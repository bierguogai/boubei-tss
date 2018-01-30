/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.timer;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.IOperator;
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
	 * 任务执行人
	 */
	protected IOperator jobRobot() {
        return new OperatorDTO(UMConstants.ROBOT_USER_ID, "Job.Robot"); 
	}
	
	protected boolean needSuccessLog() {
		return false;
	}
	
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	try {
    		// 模拟登录，用以初始化Environment
    		IOperator excutor = jobRobot();
			String token = TokenUtil.createToken("1234567890", excutor.getId());
    		IdentityCard card = new IdentityCard(token, excutor);
    		Context.initIdentityInfo(card); 
    			
    		businessLogger = (IBusinessLogger) Global.getBean("BusinessLogger"); // 跑Test时可能没有spring IOC
    	} catch (Exception e) { }
    	
    	JobDetail aJob = context.getJobDetail();
    	String jobName = aJob.getKey().getName();
    	
    	JobDataMap dataMap = aJob.getJobDataMap();
        
        log.info("Job[" + jobName + "] starting...");
        
        String resultMsg;
        Log excuteLog = null;
        
        try {
        	Long preTime = System.currentTimeMillis();
        	
        	String jobConfig = (String) dataMap.get(jobName);
        	Long jobID = (Long) dataMap.get(jobName + "-ID");
			excuteJob(jobConfig, jobID);
        	
        	int methodExcuteTime = (int) (System.currentTimeMillis() - preTime);
        	
        	resultMsg = "Job[" +jobName+ "] end.";
        	log.info(resultMsg);
        	
        	if( needSuccessLog() ) {
        		excuteLog = new Log(jobName + " - success", resultMsg);
            	excuteLog.setMethodExcuteTime(methodExcuteTime);
        	}
        } 
        catch(Exception e) {
        	resultMsg = "Job[" +jobName+ "] error: " + e.getMessage();
        	log.error(resultMsg, e);
        	
        	excuteLog = new Log(jobName + " - " + EX._ERROR_TAG, resultMsg);
        } 
        finally {
        	try {
        		if(excuteLog != null) {
        			excuteLog.setOperateTable("Timer");
        			businessLogger.output(excuteLog);
        		}
        	} 
        	catch(Exception e) { }
        }
    }
    
    protected abstract void excuteJob(String jobConfig, Long jobID);
    
    public void excuteJob(String jobConfig) {
    	excuteJob(jobConfig, null);
    }
}
