/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.web.display.grid.GridDataEncoder;
import com.boubei.tss.framework.web.display.tree.DefaultTreeNode;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IGroupService;
import com.boubei.tss.um.service.IRoleService;
import com.boubei.tss.util.EasyUtils;

@Controller
@RequestMapping( {"/auth/search", "/auth/service"} )
public class GeneralSearchAction extends BaseActionSupport {
	
	@Autowired private GeneralSearchService service;
	@Autowired private IRoleService roleService;
	@Autowired private IGroupService groupService;
	@Autowired private ICommonService commonService;
	
	/**
	 * 检索数据表、报表、菜单、用户组等，无权限过滤。打开资源时需单独过滤权限。
	 */
	@RequestMapping("/resource")
    @ResponseBody
	public List<?> searchResource(String resource, String key) {
		key = "%" +key+ "%";
		String condition = "";
		if("Report,Record".indexOf(resource) >= 0) {
			condition = " and type=1 ";
		}
		
		String hql = "select id, name, decode from " +resource+ " o where o.name like ? and disabled = 0 " + condition;
		String hql2 = "select name from " +resource+ " o where ? like o.decode||'%' and id <> ? order by o.decode";
		List<?> result = commonService.getList(hql, key);
		for(Object obj : result) {
			Object[] objs = (Object[]) obj;
			List<?> parents = commonService.getList(hql2, objs[2], objs[0]);
			objs[2] = EasyUtils.list2Str(parents, " > ");
		}
		
		return result;
	}
 
	/**
	 * 一个组下面所有用户的因转授而获得的角色的情况
	 */
	@RequestMapping("/subauth/{groupId}")
	public void searchUserSubauth(HttpServletResponse response, @PathVariable("groupId") Long groupId) {
		List<?> list = service.searchUserSubauthByGroupId(groupId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_STRATEGY_GRID);
				
        print("SUBAUTH_RESULT", gridEncoder);
	}
	
	/**
	 * 根据用户组查询组下用户（需是登陆用户可见的用户）的角色授予情况
	 */
	@RequestMapping("/roles/{groupId}")
	public void searchRolesByGroup(HttpServletResponse response, @PathVariable("groupId") Long groupId) {
		List<?> list = service.searchUserRolesMapping(groupId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_ROLE_GRID);

		print("ROLE_RESULT", gridEncoder);
	}
	
	/**
	 * 拥有同一个角色的所有用户列表
	 */
	@RequestMapping("/role/users/{roleId}")
	public void searchUsersByRole(HttpServletResponse response, @PathVariable("roleId") Long roleId) {
		List<?> list = service.searchUsersByRole(roleId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_USER_GRID);

        print("ROlE_USERS_RESULT", gridEncoder);
	}
	
	/**
	 * 获取当前用户有查看权限的角色，用于前台生成角色下拉列表
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	@ResponseBody
	public List<Object[]> getVisiableRoles() {
		List<?> list = roleService.getAllVisiableRole();
		List<Object[]> returnList = new ArrayList<Object[]>();
		for(Object temp : list) {
			Role role = (Role) temp;
			boolean isRole = ParamConstants.FALSE.equals(role.getIsGroup());
			boolean enable = ParamConstants.FALSE.equals(role.getDisabled());
			if( isRole && enable && role.getId() > 0 ) {
				returnList.add(new Object[]{ role.getId(), role.getName() });
			}
		}
		return returnList;
	}
	
	@RequestMapping("/roles/tree")
    public void getVisiableRolesTree(HttpServletResponse response) {
        List<Object[]> roles = this.getVisiableRoles();
        List<ITreeNode> nodes = new ArrayList<ITreeNode>();
        for(Object[] role : roles) {
        	nodes.add( new DefaultTreeNode(role[0], (String) role[1]) );
        }
        
        TreeEncoder treeEncoder = new TreeEncoder(nodes);
        treeEncoder.setNeedRootNode(false);
        print("RoleTree", treeEncoder);
    }
	
	@RequestMapping(value = "/rusers/{roleId}", method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getUsersByRoleId(@PathVariable Long roleId) {
		List<User> list = service.searchUsersByRole(roleId);
		List<User> list2 = new ArrayList<User>();
		
		for(User user : list) {
			user = (User) commonService.getEntity(User.class, user.getId());
			list2.add(user);
		}
		return buildUserList(list2);
	}
	
	@RequestMapping(value = "/gusers/{groupId}", method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getUsersByGroupId(@PathVariable Long groupId) {
		List<User> list = groupService.getUsersByGroupId(groupId);
		return buildUserList(list);
	}
	
	@RequestMapping("/rid")
	@ResponseBody
	public Long getRoleId(String role) {
		List<?> roles = commonService.getList(" select id from Role where name=? order by id desc", role);
		return (Long) (roles.isEmpty() ? null : roles.get(0));
	}

	private List<Object> buildUserList(List<User> list) {
		List<Object> returnList = new ArrayList<Object>();
		
		for(User user : list) {
			Map<String, Object> map = user.getAttributes4XForm();
			map.remove("password");
			map.remove("passwordQuestion");
			map.remove("passwordAnswer");
			map.remove("authMethod");
			
			returnList.add(map);
		}
		return returnList;
	}
}