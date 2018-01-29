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

	