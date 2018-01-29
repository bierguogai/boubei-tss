package com.boubei.tss.framework.persistence.entityaop;

import com.boubei.tss.framework.persistence.IEntity;

/**
 * <p> 能够设置decode值的实体接口 </p>
 * <pre>
 * 通常需要满足以下条件：
 * 1、有父子节点关系 
 * 2、同层节点之间需要能进行排序 
 * </pre>
 */
public interface IDecodable extends IEntity {
    
    /**
     * 获取节点的ID
     * @return
     */
    Long getId();

    /**
     * 获取节点父节点的ID
     * @return
     */
    Long getParentId();

    /**
     * <pre>
     * 做为父节点的实体的class。 
     * 有些树结构中的实体由多种类型的对象组成，像菜单（第一层为门户根节点）。
     * 还有当实体做为资源时，一般会取实体的资源视图类为parentClass，因为视图里有一个“全部”节点，而实体表中没有。
     * </pre>
     * @return
     */
    Class<?> getParentClass();

    /**
     * 获取当前实体的排序号
     * @return
     */
    Integer getSeqNo();
    
    /**
     *  设置当前实体的排序号
     * @param seqNo
     */
    void setSeqNo(Integer seqNo);
    
    /**
     * 获取当前实体的decode值
     * @return
     */
    String getDecode();
    
    /**
     * <pre>
     * 获取当前实体的层次值。 留着该字段，在decode出现混乱时， 
     * 可依据此层次值从小到大逐层修复decode值（SQL操作即可）。
     * </pre>
     * @return
     */
    Integer getLevelNo();

    /**
     * 设置当前实体的decode值
     * @param decode
     */
    void setDecode(String decode);

    /**
     * 设置当前实体的层次值
     * @param levelNo
     */
    void setLevelNo(Integer levelNo);
}
