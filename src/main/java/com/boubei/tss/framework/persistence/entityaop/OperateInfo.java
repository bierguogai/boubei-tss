package com.boubei.tss.framework.persistence.entityaop;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.boubei.tss.framework.persistence.IEntity;

/** 
 * 对数据对象操作的操作者信息记录实体
 */
@MappedSuperclass
public abstract class OperateInfo implements IOperatable, IEntity {
    
    protected Long   creatorId;       // 创建者Id
    protected Date   createTime;     // 创建时间
    protected String creatorName;   // 创建者姓名
    protected Long   updatorId;    // 最后更新者Id
    protected Date   updateTime;  // 最后修改时间
    protected String updatorName;// 最后修改者姓名
    
    @Version
    private int lockVersion = 0;
    
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
 
    public Long getCreatorId() {
        return creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public Long getUpdatorId() {
        return updatorId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
 
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
 
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
 
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
 
    public void setUpdatorId(Long updatorId) {
        this.updatorId = updatorId;
    }
 
    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public int getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(int lockVersion) {
        this.lockVersion = lockVersion;
    }
}

