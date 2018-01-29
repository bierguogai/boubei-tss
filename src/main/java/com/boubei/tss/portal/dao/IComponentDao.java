package com.boubei.tss.portal.dao;

import java.util.List;

import com.boubei.tss.framework.persistence.ITreeSupportDao;
import com.boubei.tss.portal.entity.Component;

/** 
 * 对元素（修饰器/布局器/Portlet）数据存取操作的DAO接口
 */
public interface IComponentDao extends ITreeSupportDao<Component>{
 
    /**
     * 保存一个元素（修饰器/布局器/Portlet）。
     * 注意：本方法将会拦截以实现资源注册，所以普通保存应该直接调用save方法。
     * @param obj
     * @return
     */
    Component save(Component obj);

    /**
     * 删除一个元素（修饰器/布局器/Portlet）
     * 删除注册资源由拦截器ResourcePermissionInterceptor完成。
     * @param obj
     * @return
     */
    Component deleteComponent(Component obj);
    
    /**
     * 获取默认的布局器，如果没有默认的则抛出异常
     * @return
     */
    Component getDefaultLayout();
    
    /**
     * 获取默认的修饰器，如果没有默认的则抛出异常
     * @return
     */
    Component getDefaultDecorator();
    
    /*****************************************************************************************************************
     ************************************ 以下是对元素（修饰器/布局器/Portlet）组的操作 ************************************* 
     *****************************************************************************************************************/
    
    /**
     * 删除一个组
     * @param group
     */
    void deleteGroup(Component group);
 
    /**
     * 获取所有儿子节点
     * @param id
     * @return
     */
    List<Component> getChildrenById(Long id);

    /**
     * 获取组以及子组下所有的元素
     * @param groupId
     * @return
     */
    List<?> getComponentsDeeply(Long groupId);
}

