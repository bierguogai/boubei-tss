package com.boubei.tss.modules.progress;

/** 
 * <p> 进度条线程组 </p>
 * 用于中止线程。
 * 
 */
public class ProgressThreadGroup extends ThreadGroup {
	private Progress progress;

	public ProgressThreadGroup(String name, Progress progress) {
		super(name);
		this.progress = progress;
	}

	public void uncaughtException(Thread t, Throwable e){
        super.uncaughtException(t, e);
		progress.setException(e);
		progress.setNormal(false); // 设置为异常
	}
}
