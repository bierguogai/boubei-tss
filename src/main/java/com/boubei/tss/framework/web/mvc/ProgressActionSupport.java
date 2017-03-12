package com.boubei.tss.framework.web.mvc;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
		if (!progress.isNormal()) {
			ProgressPool.removeSchedule(code);
			Throwable t = progress.getException();
			throw new BusinessException("cause:" + t.getCause() + ".Message:"
					+ t.getMessage());
		}
		if (progress.isConceal())
			throw new BusinessException("取消进度成功");

		Object[] info = progress.getProgressInfo();
		StringBuffer progressInfo = new StringBuffer("<actionSet>");
		progressInfo.append("<percent>" + info[0] + "</percent>");
		progressInfo.append("<delay>" + info[1] + "</delay>");
		progressInfo.append("<estimateTime>" + info[2] + "</estimateTime>");
		progressInfo.append("<code>" + code + "</code>");
		progressInfo.append("</actionSet>");

		if (progress.isCompleted()) {
			ProgressPool.removeSchedule(code); // 执行结束则将将进度对象从池中移除
		}
	    print("ProgressInfo", progressInfo.toString());
	}
}
