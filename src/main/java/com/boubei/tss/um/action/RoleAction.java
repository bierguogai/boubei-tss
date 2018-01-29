package com.boubei.tss.um.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.display.tree.LevelTreeParser;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.tree.TreeNodeOptionsEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.permission.RolePermission;
import com.boubei.tss.um.entity.permission.RoleResource;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.PermissionService;
import com.boubei.tss.um.permission.dispaly.IPermissionOption;
import com.boubei.tss.um.permission.dispaly.ResourceTreeParser;
import com.boubei.tss.um.permission.dispaly.TreeNodeOption4Permission;
import com.boubei.tss.um.service.IRoleService;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
 
@Controller
@RequestMapping("/auth/role")
public class RoleAction extends BaseActionSupport {

	@Autowired private IRoleService roleService;
	@Autowired private PermissionService permissionService;
	
    /**
     * 获取所有的角色（不包系统级的角色）
     */
	@RequestMapping("/list")
    public void getAllRole2Tree(HttpServletResponse response) {
        List<?> roles = roleService.getAllVisiableRole();
        TreeEncoder treeEncoder = new TreeEncoder(roles, new LevelTreeParser());
        treeEncoder.setNeedRootNode(false);
        print("RoleGroupTree", treeEncoder);
    }

	/**
	 * 获取用户可见的角色组
	 */
	@RequestMapping("/groups")
	public void getAllRoleGroup2Tree(HttpServletResponse response) {
	    List<?> canAddGroups = roleService.getAddableRoleGroups();
		TreeEncoder treeEncoder = new TreeEncoder(canAddGroups, new LevelTreeParser());
		treeEncoder.setNeedRootNode(false);
		print("GroupTree", treeEncoder);
	}
	
   /**
     * 保存一个Role对象的明细信息、角色对用户信息、角色对用户组的信息
     */
	@RequestMapping(method = RequestMethod.POST)
    public void saveRole(HttpServletResponse response, HttpServletRequest request, Role role) {
        boolean isNew = (role.getId() == null);
        
        if(ParamConstants.TRUE.equals(role.getIsGroup())) {
        	roleService.saveRoleGroup(role);
        }
        else {
        	String role2UserIds  = request.getParameter("Role2UserIds");
        	String role2GroupIds = request.getParameter("Role2GroupIds");
        	roleService.saveRole2UserAndRole2Group(role, role2UserIds, role2GroupIds);
        }
        
        doAfterSave(isNew, role, "RoleGroupTree");
    }
    
    /**
     * 获得角色组信息
     */
	@RequestMapping("/group/{id}/{parentId}")
    public void getRoleGroupInfo(HttpServletResponse response, 
    		@PathVariable("id") Long id, 
    		@PathVariable("parentId") Long parentId) {
		
        XFormEncoder xFormEncoder;
        if (UMConstants.DEFAULT_NEW_ID.equals(id)) { // 如果是新增，则返回一个空的无数据的模板
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("parentId", parentId);
            map.put("isGroup", ParamConstants.TRUE);
            xFormEncoder = new XFormEncoder(UMConstants.ROLEGROUP_XFORM, map);
        }
        else {
            Role role = roleService.getRoleById(id);
            xFormEncoder = new XFormEncoder(UMConstants.ROLEGROUP_XFORM, role);
        }
        print("RoleGroupInfo", xFormEncoder);     
    }
    
    /**
     * 获取一个Role（角色）对象的明细信息、角色对用户组信息、角色对用户信息
     */
	@RequestMapping("/detail/{id}/{parentId}")
    public void getRoleInfo(HttpServletResponse response, 
    		@PathVariable("id") Long id, 
    		@PathVariable("parentId") Long parentId) {        
		
        if ( UMConstants.DEFAULT_NEW_ID.equals(id) ) { // 新建角色
            getNewRoleInfo(parentId);
        } 
        else { // 编辑角色
            getEditRoleInfo(id);
        }
    }

    private void getNewRoleInfo(Long parentId) {
        XFormEncoder roleXFormEncoder;
        TreeEncoder usersTreeEncoder;
        TreeEncoder groupsTreeEncoder;
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parentId", parentId);
        map.put("isGroup", ParamConstants.FALSE);
        
        // 默认的有效时间
        map.put("startDate", DateUtil.format(new Date()));
        Calendar calendar = new GregorianCalendar();
        calendar.add(UMConstants.ROLE_LIFE_TYPE, UMConstants.ROLE_LIFE_TIME);
        map.put("endDate", DateUtil.format(calendar.getTime()));
        
        // 如果是新增,则返回一个空的无数据的模板
        roleXFormEncoder = new XFormEncoder(UMConstants.ROLE_XFORM, map);
       
        Map<String, Object> data = roleService.getInfo4CreateNewRole();

        usersTreeEncoder = new TreeEncoder(data.get("Role2UserTree"));
        usersTreeEncoder.setNeedRootNode(false);
    
        groupsTreeEncoder = new TreeEncoder(data.get("Role2GroupTree"), new LevelTreeParser());
        groupsTreeEncoder.setNeedRootNode(false);
        
        TreeEncoder roleToUserTree = new TreeEncoder(null);
        TreeEncoder roleToGroupTree = new TreeEncoder(null);

        print(new String[]{"RoleInfo", "Role2GroupTree", "Role2UserTree", "Role2GroupExistTree", "Role2UserExistTree"}, 
                new Object[]{roleXFormEncoder, groupsTreeEncoder, usersTreeEncoder, roleToGroupTree, roleToUserTree});
    }

    private void getEditRoleInfo(Long id) {
        Map<String, Object> data = roleService.getInfo4UpdateExistRole(id);
        
        Role role = (Role)data.get("RoleInfo");         
        XFormEncoder roleXFormEncoder = new XFormEncoder(UMConstants.ROLE_XFORM, role);
    
        TreeEncoder usersTreeEncoder = new TreeEncoder(data.get("Role2UserTree"));
        usersTreeEncoder.setNeedRootNode(false);
    
        TreeEncoder groupsTreeEncoder = new TreeEncoder(data.get("Role2GroupTree"), new LevelTreeParser());
        groupsTreeEncoder.setNeedRootNode(false);
        
        TreeEncoder roleToGroupTree = new TreeEncoder(data.get("Role2GroupExistTree"));
        TreeEncoder roleToUserTree = new TreeEncoder(data.get("Role2UserExistTree"));

        print(new String[]{"RoleInfo", "Role2GroupTree", "Role2UserTree", "Role2GroupExistTree", "Role2UserExistTree"}, 
                new Object[]{roleXFormEncoder, groupsTreeEncoder, usersTreeEncoder, roleToGroupTree, roleToUserTree});  
    }
	
	/**
	 * 删除角色
	 */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response, @PathVariable("id") Long id) {
		roleService.delete(id);
		printSuccessMessage();
	}
	
	/**
	 * 停用/启用角色
	 */
    @RequestMapping(value = "/disable/{id}/{state}")
	public void disable(HttpServletResponse response, 
			@PathVariable("id") Long id, 
			@PathVariable("state") int state) {
    	
		roleService.disable(id, state);
        printSuccessMessage();
	}
    
    /**
     * 排序
     */
    @RequestMapping(value = "/sort/{id}/{targetId}/{direction}")
    public void sort(HttpServletResponse response,             
    		@PathVariable("id") Long id, 
            @PathVariable("targetId") Long targetId, 
            @PathVariable("direction") int direction) {
    	
    	roleService.sortRole(id, targetId, direction);
        printSuccessMessage();
    } 
 
	/**
	 * 移动
	 */
    @RequestMapping(value = "/move/{id}/{toGroupId}", method = RequestMethod.POST)
	public void move(HttpServletResponse response, 
			@PathVariable("id") Long id, 
			@PathVariable("toGroupId") Long toGroupId) {
    	
		roleService.move(id, toGroupId);        
        printSuccessMessage();
	}
	
	@RequestMapping("/operations/{id}")
	public void getOperation(HttpServletResponse response, @PathVariable("id") Long id) {
        // 角色（组）树上： 匿名角色节点只需一个“角色权限设置”菜单即可
        if(id.equals(UMConstants.ANONYMOUS_ROLE_ID)) {
        	print("Operation", "p1,p2," + UMConstants.ROLE_EDIT_OPERRATION);
        }
        else {
        	List<?> list = PermissionHelper.getInstance().getOperationsByResource(id, 
        			RolePermission.class.getName(), RoleResource.class);
        	print("Operation", "p1,p2," + EasyUtils.list2Str(list));
        }
	}
	
	/**
	 * 查询应用系统列表，以便挑出资源进行授权
	 */
	@RequestMapping("/apps")
	public void getApplications(HttpServletResponse response, 
			@PathVariable("roleId") Long roleId, 
			@PathVariable("isRole2Resource") Integer isRole2Resource) {
		
		List<?> apps = roleService.getPlatformApplication();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleId);
		map.put("isRole2Resource", isRole2Resource);
		map.put("permissionRank", UMConstants.LOWER_PERMISSION);
		
		XFormEncoder xform = new XFormEncoder(UMConstants.SERACH_PERMISSION_XFORM, map);
		xform.fixCombo("applicationId", apps, "applicationId", "name", "|");	

		print("SearchPermissionFrom", xform);
	}
	
	/**
	 * 根据应用获得资源类型。 做 应用系统/资源类型/授权级别 三级下拉框时用
	 */
	@RequestMapping("/resourceTypes/{applicationId}")
	@ResponseBody
	public Object getResourceTypes(HttpServletResponse response, @PathVariable("applicationId") String applicationId) {
		return roleService.getResourceTypeByAppId(applicationId);
	}

	@RequestMapping("/permission/initsearch/{isRole2Resource}/{roleId}")
	public void initSetPermission(HttpServletResponse response, HttpServletRequest request, 
			@PathVariable("isRole2Resource") Integer isRole2Resource, 
			@PathVariable("roleId") Long roleId) {
		
		if( ParamConstants.TRUE.equals(isRole2Resource) ) {
			getApplications(response, roleId, isRole2Resource);
			return;
		}
		
		String applicationId = request.getParameter("applicationId");
    	String resourceType  = request.getParameter("resourceType");
    	applicationId = (String) EasyUtils.checkNull(applicationId, PermissionHelper.getApplicationID());
    	
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleId);
		map.put("isRole2Resource", isRole2Resource);
		map.put("applicationId", applicationId);
		map.put("resourceType", resourceType);
		map.put("permissionRank", UMConstants.LOWER_PERMISSION);

		XFormEncoder xFormEncoder = new XFormEncoder(UMConstants.SERACH_PERMISSION_XFORM, map);
		print("SearchPermissionFrom", xFormEncoder);
	}
	
	// ===========================================================================
	// 授权相关方法
	// ===========================================================================	
	
	/**
	 * 获取授权用的矩阵图
	 */
	@RequestMapping("/permission/matrix/{permissionRank}/{isRole2Resource}/{roleId}")
	public void getPermissionMatrix(HttpServletResponse response, HttpServletRequest request,  
			@PathVariable("permissionRank") String permissionRank, 
			@PathVariable("isRole2Resource") Integer isRole2Resource, 
			@PathVariable("roleId") Long roleId) {  
		
	    if( EasyUtils.isNullOrEmpty(permissionRank) ){
            throw new BusinessException(EX.U_06);
        }
	    
	    List<Long[]> roleUsers = roleService.getRoles4Permission();
	    Object[] matrixInfo;
	    
	    String applicationId = request.getParameter("applicationId");
    	String resourceType  = request.getParameter("resourceType");
    	
    	PermissionService localPermissionService;
	    
	    //  角色对资源授权（“角色维护”菜单，多个资源授权给单个角色）时，生成 资源－操作选项 矩阵
	    if( ParamConstants.TRUE.equals(isRole2Resource) ) {
            if( EasyUtils.isNullOrEmpty(applicationId) ){
                throw new BusinessException(EX.U_07);
            }
            if( EasyUtils.isNullOrEmpty(resourceType) ){
                throw new BusinessException(EX.U_08);
            }
            
            localPermissionService = PermissionHelper.getPermissionService(applicationId, permissionService);
            matrixInfo = localPermissionService.genResource2OperationMatrix(applicationId, resourceType, 
                    roleId, permissionRank, roleUsers);
        } 
        // 资源对角色授权（“资源授予角色”菜单，单个资源授权给多个角色）时，生成 角色－操作选项 矩阵。
        else {
            if( applicationId == null ) {
                applicationId = PermissionHelper.getApplicationID();
            }
            
            localPermissionService = PermissionHelper.getPermissionService(applicationId, permissionService);
            matrixInfo = localPermissionService.genRole2OperationMatrix(applicationId, resourceType, 
                    roleId, permissionRank, roleUsers); // 此时roleId其实是资源ID（resourceId）
        }
        
        TreeNodeOptionsEncoder treeNodeOptionsEncoder = new TreeNodeOptionsEncoder();
        List<?> operations = (List<?>) EasyUtils.checkNull(matrixInfo[1], new ArrayList<Object>());
        for ( Object temp : operations ) {
            treeNodeOptionsEncoder.add(new TreeNodeOption4Permission((IPermissionOption) temp));
        }
        
        TreeEncoder treeEncoder = new TreeEncoder(matrixInfo[0], new ResourceTreeParser());
        treeEncoder.setOptionsEncoder(treeNodeOptionsEncoder);
        treeEncoder.setNeedRootNode(false);
        
        print("PermissionMatrix", treeEncoder);
	}
	
	/**
	 * permissionRank  授权级别(1:普通(10)，2/3:可授权，可授权可传递(11))
	 * permissions   角色资源权限选项的集合, 当资源对角色授权时:  role1|2224,role2|4022
	 */
	@RequestMapping(value = "/permission/{permissionRank}/{isRole2Resource}/{roleId}", method = RequestMethod.POST)
	public void savePermission(HttpServletResponse response, HttpServletRequest request,  
			@PathVariable("permissionRank") String permissionRank, 
			@PathVariable("isRole2Resource") Integer isRole2Resource, 
			@PathVariable("roleId") Long roleId) {  
		
		String applicationId = request.getParameter("applicationId");
    	String resourceType  = request.getParameter("resourceType");
    	String permissions   = request.getParameter("permissions");
    	
	    if( applicationId == null ) {
            applicationId = PermissionHelper.getApplicationID();
        }
	    if(permissions == null) {
    		permissions = "";
    	}
	    
	    PermissionService localPermissionService = PermissionHelper.getPermissionService(applicationId, permissionService);
        
	    // 角色对资源授权（“角色维护”菜单，多个资源授权给单个角色）
        if( ParamConstants.TRUE.equals(isRole2Resource) ) {
        	localPermissionService.saveResources2Role(applicationId, resourceType, roleId, permissionRank, permissions);
        } 
        // 资源对角色授权（“资源授予角色”菜单，单个资源授权给多个角色）
        else {
        	localPermissionService.saveResource2Roles(applicationId, resourceType, roleId, permissionRank, permissions);
        }
        
        printSuccessMessage();
	}
	
	@RequestMapping(value = "/permission/{permissionRank}/{isRole2Resource}/{roleId}", method = RequestMethod.DELETE)
	public void clearPermission(HttpServletResponse response, HttpServletRequest request,  
			@PathVariable("permissionRank") String permissionRank, 
			@PathVariable("isRole2Resource") Integer isRole2Resource, 
			@PathVariable("roleId") Long roleId) {  
		
		String applicationId = request.getParameter("applicationId");
    	String resourceType  = request.getParameter("resourceType");
    	
	    if( applicationId == null ) {
            applicationId = PermissionHelper.getApplicationID();
        }

	    PermissionService localPermissionService = PermissionHelper.getPermissionService(applicationId, permissionService);
	    localPermissionService.clearPermissionByRole(applicationId, resourceType, permissionRank, roleId, isRole2Resource);
 
        printSuccessMessage();
	}
}
