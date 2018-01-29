package com.boubei.tss.modules.progress;

import java.util.Map;
import java.util.Random;

import com.boubei.tss.framework.sso.context.ContextSupportThread;

/**
 * <p> 进度条执行入口  </p>
 * 
 */
public class ProgressManager {
	
	private Progressable progressExcutor;
	private long total;
	private Map<String, Object> params;

	public ProgressManager(Progressable progressExcutor, long total, Map<String, Object> params) {
		this.progressExcutor = progressExcutor;
		this.total = total;
		this.params = params;
	}

	public String execute() {
		final Progress progress = new Progress(total);
		final String code = new Random().nextInt() + "_" + System.currentTimeMillis();
		ProgressPool.putSchedule(code, progress);

		ProgressThreadGroup group = new ProgressThreadGroup("threadGroup_" + code, progress);
		
		new ContextSupportThread(group, "thread_" + code) {
			public void runSupportContext() {
				progressExcutor.execute(params, progress);
			}
		}.start();
		
		return code;
	}
}
