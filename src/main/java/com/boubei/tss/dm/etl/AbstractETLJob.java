package com.boubei.tss.dm.etl;

import java.util.List;

import org.apache.log4j.Logger;

import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.modules.timer.AbstractJob;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MailUtil;

/**
 * ETL Job基类
 */
public abstract class AbstractETLJob extends AbstractJob {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	public static int PAGE_SIZE = 10000;
	
	protected ICommonService commonService = Global.getCommonService();
	protected ReportService reportService = (ReportService) Global.getBean("ReportService");
	protected RecordService recordService = (RecordService) Global.getBean("RecordService");

	protected void excuteJob(String jobConfig, Long jobID) {
		String hql = "from Task where type = ? and status = 'opened' and jobId = ? order by priority desc, id asc ";
		List<?> tasks = commonService.getList(hql, etlType(), jobID);
		
		for(Object task : tasks) {
			excuteTask( (Task) task );
		}
	}
	
	protected abstract String etlType();

	protected abstract void excuteTask(Task task);
	
	protected void setException(TaskLog tLog, Task task, Exception e) {
		tLog.setException("yes");
		tLog.setDetail( ExceptionEncoder.getFirstCause(e).getMessage() );
		
		// 邮件提醒此Task的管理员
		log.error(tLog, e);
		String receiver = (String) EasyUtils.checkNull(task.getManager(), task.getApplier());
		MailUtil.send( task.getName() + "ETL异常", tLog.toString(), receiver.split(","), MailUtil.DEFAULT_MS);
        
	}
}
