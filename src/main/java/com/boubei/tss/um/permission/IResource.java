package com.boubei.tss.um.permission;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.display.tree.ILevelTreeNode;

/**
 * <p>
 * 所有"资源视图实体"需要实现的接口。
 * （PermissionInterceptor 将根据本接口判断是否对资源进行自动补齐）
 * </p>
 */
public interface IResource extends ILevelTreeNode, IEntity {
	
	/** 资源名称 */
	String getName();
	
	/** 资源排序号 */
	Integer getSeqNo();
	
    /** 资源decode */
    String getDecode();
	
	/** 资源类型 */
	String getResourceType();
}

	