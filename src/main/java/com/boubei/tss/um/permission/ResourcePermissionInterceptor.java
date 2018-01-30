/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.permission;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.persistence.entityaop.MatchByDaoMethodNameInterceptor;

/**
 * 资源变动时候自动维护授权信息的拦截器。 </br>
 * 拦截资源新增、移动、删除等操作，根据权限模型中依赖关系对被操作的资源进行权限修复。 </br>
 */
public class ResourcePermissionInterceptor extends MatchByDaoMethodNameInterceptor {
    
    @Autowired private ResourcePermission resourcePermission;
    
    public static final int MOVE = 5;
    
    protected int judgeManipulateKind(String methodName){
        if(methodName.startsWith("move")) {
            return MOVE;
        } 
        return super.judgeManipulateKind(methodName);
    }
    
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();
        
        IResource resource = null;
        if(args != null) {
        	for (int i = 0; i < args.length; i++) {
    			if (args[i] instanceof IResource) {
    				resource = (IResource) args[i];
    				break;
    			}
            }
        }
		if( resource == null) {
	        return invocation.proceed();			
		}	
		
		String methodName = invocation.getMethod().getName();
		switch (judgeManipulateKind(methodName)) {
			/* 
			 * 新增时注册资源。
	         * 注：修改的时候也会被本拦截器拦住，但IResourcePermission.addResource方法会判断节点的权限是否已经补齐了， 如果已经补齐则不再补齐。 
			 *  TODO 这种做法需要改进，最好能区分“修改”还是“新增”。
			 */
			case SAVE:
				Object returnObj = invocation.proceed();
	            addResource(resource);  //拦截新增资源的权限补齐操作需要在新增保存完成后。
				return returnObj;
			// 移动资源
			case MOVE: 
	            returnObj = invocation.proceed();
	            moveResource(resource); // 拦截移动资源的权限重新补齐操作需要在整个枝移动保存完成后。
	            return returnObj;
	        // 删除资源
			case DELETE:
				deleteResource(resource);
				break;
		}
 
        return invocation.proceed();
	}
	
	/** 新注册一个资源 */
	protected void addResource(IResource resource) {
		Long resourceId = resource.getId();
		String resourceType = resource.getResourceType();
		
		resourcePermission.addResource(resourceId, resourceType);
	}

	/** 资源被移动时修改资源 */
	protected void moveResource(IResource resource){
		Long resourceId = resource.getId();
		String resourceType = resource.getResourceType();
		
		resourcePermission.moveResource(resourceId, resourceType);
	}

	/** 删除一个注册资源 */
	protected void deleteResource(IResource resource) {
		Long resourceId = resource.getId();
		String resourceType = resource.getResourceType();
		
		resourcePermission.delResource(resourceId, resourceType);		
	}
}

	