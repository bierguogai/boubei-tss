/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.mvc;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.progress.Progress;
import com.boubei.tss.modules.progress.ProgressPool;

/**
 * 支持进度条显示的MVC Action基类
 */
public class ProgressActionSupport extends BaseActionSupport {

	/**
	 * 获取进度信息
	 */
	@RequestMapping(value = "/progress/{code}", method = RequestMethod.GET)
	public void getProgress(HttpServletResponse response, @PathVariable("code") String code) {
		printScheduleMessage(code);
	}

	/**
	 * 取消，中止进度
	 */
	@RequestMapping(value = "/progress/{code}", method = RequestMethod.DELETE)
	public void doConceal(HttpServletResponse response, @PathVariable("code") String code) {
		Progress progress = (Progress) ProgressPool.getSchedule(code);
		if(progress != null) {
			progress.setIsConceal(true); // 设置中止标志
			printScheduleMessage(code);
		}
	}

	protected void printScheduleMessage(String code) {
		Progress progress = (Progress) ProgressPool.getSchedule(code);
		
		// 检查进度执行过程中是否有异常
		if (!progress.isNormal()) {
			ProgressPool.removeSchedule(code);
			Throwable t = progress.getException();
			throw new BusinessException("cause:" + t.getCause() + ".Message:" + t.getMessage());
		}
		
		// 检查进度条是否已被取消
		if (progress.isConceal()) {
			ProgressPool.removeSchedule(code);
			throw new BusinessException(EX.F_09);
		}
		
		// 检查进度是否已完成
		if (progress.isCompleted()) {
			ProgressPool.removeSchedule(code); // 执行结束则将将进度对象从池中移除
		}

		Object[] info = progress.getProgressInfo();
		StringBuffer progressInfo = new StringBuffer("<actionSet>");
		progressInfo.append("<percent>" + info[0] + "</percent>");
		progressInfo.append("<delay>" + info[1] + "</delay>");
		progressInfo.append("<estimateTime>" + info[2] + "</estimateTime>");
		progressInfo.append("<code>" + code + "</code>");
		progressInfo.append("</actionSet>");
		
	    print("ProgressInfo", progressInfo.toString());
	}
}
