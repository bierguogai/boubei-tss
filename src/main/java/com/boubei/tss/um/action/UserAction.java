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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.cache.extension.CacheLife;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.web.display.grid.DefaultGridNode;
import com.boubei.tss.framework.web.display.grid.GridDataEncoder;
import com.boubei.tss.framework.web.display.grid.IGridNode;
import com.boubei.tss.framework.web.display.tree.ITreeTranslator;
import com.boubei.tss.framework.web.display.tree.LevelTreeParser;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.display.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IUserService;
import com.boubei.tss.um.sso.FetchPermissionAfterLogin;
import com.boubei.tss.um.sso.online.DBOnlineUser;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.XMLDocUtil;

@Controller
@RequestMapping("/auth/user") 
public class UserAction extends BaseActionSupport {

	@Autowired private IUserService userService;
	@Autowired private ILoginService loginService;
	@Autowired private ICommonService commonService;

    /**
     * 获取一个User（用户）对象的明细信息、用户对用户组信息、用户对角色的信息
     */
	@RequestMapping("/detail/{groupId}/{userId}")
    public void getUserInfoAndRelation(HttpServletResponse response, 
    		@PathVariable("userId")  Long userId, 
    		@PathVariable("groupId") Long groupId) {
		
        TreeEncoder existRoleTree = new TreeEncoder(null);
        Map<String, Object> data;
        Map<String, Object> map = new HashMap<String, Object>(); 
        if ( !UMConstants.DEFAULT_NEW_ID.equals(userId) ) { // 编辑用户
            data =  userService.getInfo4UpdateExsitUser(userId);
            
            User user = (User) data.get("UserInfo");
            map.putAll(user.getAttributes4XForm()); 
           
            // 用户已经对应的角色
            Object userRoles = data.get("User2RoleExistTree");
			existRoleTree = new TreeEncoder(userRoles);
        }
        else {
            data =  userService.getInfo4CreateNewUser(groupId);
        }
        
        // 所有该用户自己建立的角色
        TreeEncoder roleTree = new TreeEncoder(data.get("User2RoleTree"), new LevelTreeParser());
        roleTree.setNeedRootNode(false);
        
        // 用户对组
        Object userGroups = data.get("User2GroupExistTree");
		TreeEncoder groupTree = new TreeEncoder(userGroups);
		groupTree.setTranslator( new ITreeTranslator() {
			public Map<String, Object> translate(Map<String, Object> attributes) {
				if( Group.ASSISTANT_GROUP_TYPE.equals( attributes.get("groupType") ) ) {
					attributes.put("name", attributes.get("name") + "（辅助用户组）");
				}
				return attributes;
			}
		} );
 
        XFormEncoder baseinfoXFormEncoder = new XFormEncoder(UMConstants.USER_BASEINFO_XFORM, map);
        
        print(new String[]{"UserInfo", "User2GroupExistTree", "User2RoleTree", "User2RoleExistTree"}, 
                new Object[]{baseinfoXFormEncoder, groupTree, roleTree, existRoleTree});
    }

	/**
	 * 认证方式
	 */
	@RequestMapping("/auth/{groupId}")
	public void initAuthenticateMethod(HttpServletResponse response, @PathVariable("groupId") Long groupId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("groupId", groupId);
		XFormEncoder authXFormEncoder = new XFormEncoder(UMConstants.AUTH_METHOD_XFORM, map);
		print("AuthenticateInfo", authXFormEncoder);
	}
	
	@RequestMapping("/uniteAuth/{groupId}/{authMethod}")
	public void uniteAuthenticateMethod(HttpServletResponse response, 
			@PathVariable("groupId") Long groupId, @PathVariable("authMethod") String authMethod) {
		
		userService.uniteAuthenticateMethod(groupId, authMethod);
        printSuccessMessage();
	}
	
	/**
	 * 新增或修改一个User对象的明细信息、用户对用户组信息、用户对角色的信息
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void saveUser(HttpServletResponse response, HttpServletRequest request, User user) {
		String user2GroupExistTree = request.getParameter("User2GroupExistTree");
    	String user2RoleExistTree  = request.getParameter("User2RoleExistTree");
		userService.createOrUpdateUser(user, user2GroupExistTree, user2RoleExistTree);
        printSuccessMessage();
	}
	
	/**
	 * 启用或者停用用户
	 */
	@RequestMapping(value = "/disable/{groupId}/{id}/{state}")
	public void startOrStopUser(HttpServletResponse response, 
			@PathVariable("groupId") Long groupId, 
			@PathVariable("id") Long id, 
			@PathVariable("state")  int state) {
		
		userService.startOrStopUser(id, state, groupId);
        printSuccessMessage();
	}
 
	/**
	 * 删除用户
	 */
	@RequestMapping(value = "/{groupId}/{userId}", method = RequestMethod.DELETE)
	public void deleteUser(HttpServletResponse response, 
			@PathVariable("groupId") Long groupId, 
			@PathVariable("userId")  Long userId) {
		
		userService.deleteUser(groupId, userId);
        printSuccessMessage();
	}
	
	/**
	 * 搜索用户
	 */
	@RequestMapping("/search/{page}")
	public void searchUser(HttpServletResponse response, 
			@PathVariable("page") int page, Long groupId, String searchStr) {
		
        PageInfo users = userService.searchUser(groupId, searchStr, page);
        GridDataEncoder gridEncoder = new GridDataEncoder(users.getItems(), UMConstants.MAIN_USER_GRID);
        print(new String[]{"SourceList", "PageInfo"}, new Object[]{gridEncoder, users});
	}

	/**
     * 根据用户组的id获取所在用户组的所有用户
     */
	@RequestMapping("/list/{groupId}/{page}")
    public void getUsersByGroupId(HttpServletResponse response, 
    		@PathVariable("groupId") Long groupId, @PathVariable("page") int page) {
    	
        PageInfo users = userService.getUsersByGroupId(groupId, page, " u.id asc ");
        GridDataEncoder gridEncoder = new GridDataEncoder(users.getItems(), UMConstants.MAIN_USER_GRID);
        print(new String[]{"SourceList", "PageInfo"}, new Object[]{gridEncoder, users});
    }
 
	/**
	 * 初始化密码
	 */
	@RequestMapping(value = "/initpwd/{groupId}/{userId}/{password}", method = RequestMethod.POST)
	public void initPassword(HttpServletResponse response, 
			@PathVariable("groupId") Long groupId, 
			@PathVariable("userId") Long userId, 
			@PathVariable("password") String password) {	
		
		userService.initPasswordByGroupId(groupId, userId, password);
        printSuccessMessage("初始化密码成功！");
	}

    /**
     * 用户自己修改个人信息
     */
	@RequestMapping(value = "/self", method = RequestMethod.POST)
    public void modifyUserSelf(HttpServletResponse response, User user) {
        userService.updateUser(user);
        printSuccessMessage();
    }

	/**
	 * 获得用户个人信息(注册信息)。
     * 用于用户修改自己的注册信息和密码时用。
	 */
    @RequestMapping("/self/detail")
	public void getUserInfo(HttpServletResponse response) {
    	User user = userService.getUserById(Environment.getUserId());
    	XFormEncoder userEncoder = new XFormEncoder(UMConstants.USER_BASEINFO_XFORM, user);
    	userEncoder.setColumnAttribute("loginName", "editable", "false");
        userEncoder.setColumnAttribute("password",  "editable", "false");
        
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("userId", Environment.getUserId());
        tempMap.put("userName", Environment.getUserName());
        tempMap.put("loginName", Environment.getUserCode());
        XFormEncoder pwdEncoder = new XFormEncoder(UMConstants.PASSWORD_CHANGE_XFORM, tempMap);
        pwdEncoder.setColumnAttribute("userName", "editable", "false");
        
        print(new String[]{"UserInfo", "PasswordInfo"}, new Object[]{userEncoder, pwdEncoder});
	}
    
    // 用户注册是匿名访问，需加到白名单里
    @RequestMapping("/register/form")
	public void getRegisterForm(HttpServletResponse response) {
    	print("UserInfo", new XFormEncoder(UMConstants.USER_REGISTER_XFORM, new User()));
	}

    /**
     * 获取当前在线用户信息
     */
    @RequestMapping("/operatorInfo")
    public void getOperatorInfo(HttpServletResponse response) {
        XmlHttpEncoder encoder = new XmlHttpEncoder();
        encoder.put("id", Environment.getUserId());
        encoder.put("loginName", Environment.getUserCode());
        encoder.put("name", Environment.getUserName());
        
        encoder.print(getWriter());
    }
    
    /**
     * 读取所有在线用户列表信息
     */
    @RequestMapping("/online/1")
    public void getOnlineUserInfo(HttpServletResponse response) {
        Collection<?> list = commonService.getList("from DBOnlineUser");
        
        List<IGridNode> dataList = new ArrayList<IGridNode>();
        for(Object item : list) {
        	DBOnlineUser ou = (DBOnlineUser) item;
            DefaultGridNode gridNode = new DefaultGridNode();
            gridNode.getAttrs().put("id", ou.getId());
            gridNode.getAttrs().put("user", ou.getUserName());
            gridNode.getAttrs().put("userIp", ou.getClientIp());
			gridNode.getAttrs().put("loginTime", DateUtil.formatCare2Second(ou.getLoginTime()) );
			gridNode.getAttrs().put("appCode", ou.getAppCode());
			String sessionId = ou.getSessionId();
			gridNode.getAttrs().put("sessionId", sessionId);
            
			if( Context.sessionMap.containsKey(sessionId) ) {
				dataList.add(gridNode);
			}
        }
        
        StringBuffer template = new StringBuffer();
        template.append("<grid><declare sequence=\"true\" header=\"checkbox\">");
        template.append("<column name=\"id\" mode=\"string\" display=\"none\"/>");
        template.append("<column name=\"user\" caption=\"用户名\" width=\"80px\" sortable=\"true\"/>");
        template.append("<column name=\"userIp\" caption=\"用户IP\" width=\"100px\"/>");
        template.append("<column name=\"loginTime\" caption=\"登录时间\" width=\"120px\" sortable=\"true\"/>");
        template.append("<column name=\"appCode\" caption=\"登录应用\" width=\"70px\"/>");
        template.append("<column name=\"sessionId\" caption=\"sessionId\" width=\"120px\"/>");
        template.append("</declare><data></data></grid>");
        
        GridDataEncoder gEncoder = new GridDataEncoder(dataList, XMLDocUtil.dataXml2Doc(template.toString()));
           
        int totalRows = list.size();
        String pageInfo = generatePageInfo(totalRows, 1, totalRows + 1, totalRows); // 加入分页信息，总是只有一页。
        print(new String[]{"ItemList", "PageInfo"}, new Object[]{  gEncoder, pageInfo});
    }
    
	/** 剔除在线用户 */
	@RequestMapping(value = "/online", method = RequestMethod.DELETE)
	public void deleteOnlineUser(HttpServletResponse response, String ids) {		
		String[] _ids = ids.split(",");
		for( String sessionId : _ids ) {
			try {
				Context.sessionMap.get(sessionId).invalidate();
			} catch(Exception e) { }
		}
		
        printSuccessMessage();
	}
    
	/** 用户所属组织、角色等信息的json接口 */
	@RequestMapping(value = "/has", method = RequestMethod.GET)
	@ResponseBody
	public Object[] getUserHas(String refreshFlag) {
		
		Long userId = Environment.getUserId();
		
		if( Config.TRUE.equals(refreshFlag) ) {
			// 刷新用户的缓存信息
	        CacheHelper.flushCache(CacheLife.SHORT.toString(), "ByUserId(" +userId+ ")");
            
            // 刷新session里的缓存
            FetchPermissionAfterLogin obj = new FetchPermissionAfterLogin();
            HttpSession session = obj.loadRights(userId); 
            obj.loadGroups(userId, session);
		}
		
		List<Long> roleIds = loginService.getRoleIdsByUserId(userId);
		
		Object[] userHas = new Object[12];
		userHas[0] = loginService.getGroupsByUserId(userId);  //List<[groupId, groupName]>，截掉"主用户组"
		userHas[1] = roleIds; //List<roleId>
		userHas[2] = userId;
		userHas[3] = Environment.getUserCode();
		userHas[4] = Environment.getUserName();
		userHas[5] = loginService.getAssistGroupIdsByUserId(userId); // List<assistGroupID>
		userHas[6] = Environment.isFirstTimeLogon();
		userHas[7] = Environment.getUserInfo("telephone");
		userHas[8] = Environment.getUserInfo("address");
		userHas[9] = Environment.getUserInfo("email");
		userHas[10] = Environment.getUserInfo("employeeNo");
		userHas[11] = loginService.getRoleNames(roleIds);
		
		return userHas;
	}
}