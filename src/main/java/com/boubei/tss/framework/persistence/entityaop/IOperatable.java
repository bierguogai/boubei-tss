/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.persistence.entityaop;

import java.util.Date;

/**
 * 用户操作信息记录接口
 * 
 */
public interface IOperatable {
	
	Date getCreateTime();

	void setCreateTime(Date createTime);

	void setCreatorId(Long creatorId);

	void setCreatorName(String creatorName);

	void setUpdateTime(Date updateTime);

	void setUpdatorId(Long updatorId);

	void setUpdatorName(String updatorName);
	
}

	