package com.boubei.tss.modules.param;

/**
 * 系统参数监听器接口。
 * 系统参数的变动将会促发监听器动作。
 */
public interface ParamListener {
	
	void afterChange(Param param);
	
}
