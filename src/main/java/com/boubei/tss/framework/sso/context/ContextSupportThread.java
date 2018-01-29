package com.boubei.tss.framework.sso.context;

/**
 * <p>
 * 支持上下文管理的线程类
 * </p>
 */
public abstract class ContextSupportThread extends Thread {
	
	/** 用户令牌 */
	private String token;

	/** 构造函数  */
	public ContextSupportThread() {
		super();
		token = Context.getToken();
	}

	/**
	 * 构造函数
	 * @param group
	 * @param name
	 */
	public ContextSupportThread(ThreadGroup group, String name) {
		super(group, name);
		token = Context.getToken();
	}

	/**
	 * <p>
	 * 线程默认运行入口（已初始化上下文）
	 * </p>
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Context.setToken(token);
		runSupportContext();
	}

	/**
	 * <p>
	 * 线程自定义运行入口
	 * </p>
	 */
	public abstract void runSupportContext();
}

