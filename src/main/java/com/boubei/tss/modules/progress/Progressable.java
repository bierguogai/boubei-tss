package com.boubei.tss.modules.progress;

import java.util.Map;

/** 
 * <p> 进度条进口。 </p>
 * 
 * 需要加入进度条机制的执行对象需要继承本接口。
 * 
 */
public interface Progressable {
	/**
     * 执行任务，同时向进度条管理对象返回进度信息
	 * @param params
	 * @param progress
	 */
	void execute(Map<String, Object> params, Progress progress);
}
