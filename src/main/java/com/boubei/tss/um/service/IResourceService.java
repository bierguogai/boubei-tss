package com.boubei.tss.um.service;

import java.util.List;

import org.dom4j.Document;

import com.boubei.tss.um.entity.Application;
import com.boubei.tss.um.entity.Operation;
import com.boubei.tss.um.entity.ResourceType;
import com.boubei.tss.um.entity.ResourceTypeRoot;

public interface IResourceService{
	
	/**
	 * <p>
	 * 根据应用系统id获得一个应用系统
	 * </p>
	 * @param applicationId
	 * @return
	 */
	Application getApplication(String applicationId);
	
	/**
	 * 根据id删除应用系统
	 * @param id
	 */
	void removeApplication(Long id);

	/**
	 * <p>
	 * 删除资源类型
	 * </p>
	 * @param id
	 */
	void removeResourceType(Long id);
	
	/**
	 * <p>
	 * 根据id删除操作选项
	 * </p>
	 * 
	 * @param id
	 */
	void removeOperation(Long id);

	/**
	 * <p>
     * 更新资源类型
     * 连同资源类型根节点ID一起更新
     * 还要更新作为根节点的资源
	 * </p>
	 * @param resourceType
	 * @return
	 */
	Object updateResourceType(ResourceType resourceType);

    /**
     * <p>
     * 保存资源类型
     * 同时还要为该类型资源建立一个根节点
     * 以资源类型名字作为根节点名字
     * </p>
     * @param resourceType
     * @return
     */
	Object createResourceType(ResourceType resourceType);

	/**
	 * <p>
	 * 创建或修改Application信息
	 * </p>
	 * 
	 * @param application
	 */
	void saveApplication(Application application);

	/**
	 * <p>
	 * 创建或修改Operation信息
	 * </p>
	 * 
	 * @param operation
	 */
	void updateOperation(Operation operation);
	
	/**
	 * <p>
	 * 新建权限选项
	 * 创建新的权限选项的时候需要让管理员角色拥有此权限.
     * 约束:
     * 1.用户类型
     * 2.主用户组类型
     * 3.辅助用户组类型
     * 4.其他用户组类型
     * 5.应用系统类型
     * 6.功能菜单类型
     * 7.自注册用户组资源类型
	 * </p>
	 * @param operation
	 * @return
	 */
	Operation saveOperation(Operation operation);
	
	/**
	 * 根据ID查询应用系统
	 * @param id
	 * @return
	 */
	Application getApplicationById(Long id);
 
	/**
	 * <p>
	 * 获取应用系统和资源类型
	 * </p>
	 * @return
	 */
	Object[] findApplicationAndResourceType();
 
	/**
	 * <p>
	 * 根据ID查询资源类型
	 * </p>
	 * 
	 * @param id
	 * @return Object
	 */
	ResourceType getResourceTypeById(Long id);
	
	/**
	 * <p>
	 * 根据ID查询操作选项
	 * </p>
	 * 
	 * @param id
	 * @return Object
	 */
	Operation getOperationById(Long id);

	/**
	 * <p>
	 * 获得一个应用系统的一个资源类型的根节点
	 * </p>
	 * @param applicationId
	 * @param resourceTypeId
	 * @return
	 */
    ResourceTypeRoot findResourceTypeRoot(String applicationId,String resourceTypeId);
    
    /**
     * 获得登陆用户可访问的应用系统名称列表
     */
    List<?> getApplications();
    
    
    /**********************************  应用、资源类型、权限选项、资源注册接口  ***********************************************/
    
	/**
     * 设置IResourceService的实现类状态为UM初始化数据状态，以区分UM正式运行时的状态。
	 * @param initial
	 */
	void setInitial(boolean initial);

    /**
     * 导入XML格式的资源配置文件
     * @param doc
     * @param applicationType
     */
    void applicationResourceRegister(Document doc, String applicationType);
}
