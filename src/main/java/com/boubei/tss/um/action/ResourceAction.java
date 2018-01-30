/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.action;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Application;
import com.boubei.tss.um.entity.Operation;
import com.boubei.tss.um.entity.ResourceType;
import com.boubei.tss.um.entity.ResourceTypeRoot;
import com.boubei.tss.um.helper.ApplicationTreeParser;
import com.boubei.tss.um.service.IResourceService;

/**
 * 应用资源管理相关Action对象
 */
@Controller
@RequestMapping("/auth/resource")
public class ResourceAction extends BaseActionSupport {

	@Autowired private IResourceService resourceService;
 
	/**
	 * 获取所有的Applicaton对象并转换成Tree相应的xml数据格式
	 */
	@RequestMapping("/apps")
	public void getAllApplication2Tree(HttpServletResponse response) {
		Object applications = resourceService.findApplicationAndResourceType();
		TreeEncoder treeEncoder = new TreeEncoder(applications, new ApplicationTreeParser());
		treeEncoder.setNeedRootNode(false);
 
		print("AppSource", treeEncoder);
	}
	
	/**
	 * 获取一个Application对象的明细信息
	 */
	@RequestMapping(value = "/app/{id}", method = RequestMethod.GET)
	public void getOtherAppInfo(HttpServletResponse response, @PathVariable("id") Long id) {
		Application application;
		if(UMConstants.DEFAULT_NEW_ID.equals(id)) {
			application = new Application();
			application.setApplicationType(UMConstants.OTHER_SYSTEM_APP);
    	}
		else {
			application = resourceService.getApplicationById(id);
		}
        
		XFormEncoder xEncoder = new XFormEncoder(UMConstants.OTHER_APPLICATION_XFORM, application); 
		print("AppDetail", xEncoder);
	}
	
	/**
	 * 获取一个ResourceType对象的明细信息
	 */
	@RequestMapping(value = "/resourceType/{id}", method = RequestMethod.GET)
	public void getResourceTypeInfo(HttpServletResponse response, @PathVariable("id") Long id) {
		ResourceType resourceType = resourceService.getResourceTypeById(id);
        String applicationId  = resourceType.getApplicationId();
		String resourceTypeId = resourceType.getResourceTypeId();
		ResourceTypeRoot rtRoot = resourceService.findResourceTypeRoot(applicationId, resourceTypeId);
		resourceType.setRootId(rtRoot.getRootId());
		
		XFormEncoder xEncoder = new XFormEncoder(UMConstants.RESOURCETYPE_XFORM, resourceType);
		print("ResourceTypeDetail", xEncoder);
	}
	
	/**
	 * 获取一个Operation对象的明细信息
	 */
	@RequestMapping(value = "/operation/{id}", method = RequestMethod.GET)
	public void getOperationInfo(HttpServletResponse response, @PathVariable("id") Long id) {
		// 编辑操作选项
		Operation operation = resourceService.getOperationById(id);
		XFormEncoder xformEncoder = new XFormEncoder(UMConstants.OPERATION_XFORM, operation);
		print("PermissionOption", xformEncoder);
	}
	
	/**
	 * 编辑一个Application对象的明细信息
	 */
	@RequestMapping(value = "/app", method = RequestMethod.POST)
	public void editApplication(HttpServletResponse response, Application application) {
        boolean isNew = application.getId() == null;
        resourceService.saveApplication(application);   
		doAfterSave(isNew, application, "AppSource");
	}
	
	/**
	 * 编辑一个ResourceType对象的明细信息
	 */
	@RequestMapping(value = "/resource", method = RequestMethod.POST)
	public void editResourceType(HttpServletResponse response, ResourceType resourceType) {
        boolean isNew = resourceType.getId() == null;
		if( isNew ) { // 新建
			resourceService.createResourceType(resourceType);			
		}
		else{ // 编辑
			resourceService.updateResourceType(resourceType);	
		}
		doAfterSave(isNew, resourceType, "AppSource");
	}
	
	/**
	 * 编辑一个Operation对象的明细信息
	 */
	@RequestMapping(value = "/operation", method = RequestMethod.POST)
	public void editOperation(HttpServletResponse response, Operation operation) {
        boolean isNew = operation.getId() == null;
		if( isNew ) { // 新建，新建的权限选项要将该权限选项赋予管理员角色(id==-1)
			resourceService.saveOperation(operation);
		}
		else { // 编辑
			resourceService.updateOperation(operation);
		}
		doAfterSave(isNew, operation, "AppSource");
	}
	
	/**
	 * 删除应用系统
	 */
	@RequestMapping(value = "/application/{id}", method = RequestMethod.DELETE)
	public void deleteApplication(HttpServletResponse response, @PathVariable Long id) {
		resourceService.removeApplication(id);
		printSuccessMessage();
	}
	
	/**
	 * 删除资源类型
	 */
	@RequestMapping(value = "/resource/{id}", method = RequestMethod.DELETE)
	public void deleteResourceType(HttpServletResponse response, @PathVariable Long id) {
		resourceService.removeResourceType(id);
        printSuccessMessage();
	}
	
	/**
	 * 删除操作选项
	 */
	@RequestMapping(value = "/operation/{id}", method = RequestMethod.DELETE)
	public void deleteOperation(HttpServletResponse response, @PathVariable Long id) {
		resourceService.removeOperation(id);
        printSuccessMessage();
	}

}
