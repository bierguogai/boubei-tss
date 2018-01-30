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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.framework.web.display.tree.LevelTreeParser;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.SubAuthorize;
import com.boubei.tss.um.service.ISubAuthorizeService;
import com.boubei.tss.util.DateUtil;

/**
 * <p>
 * 权限转授策略相关操作。
 * 策略没有组，没有上下级关系，没有排序id
 * </p>
 */
@Controller
@RequestMapping("/auth/subauthorize")
public class SubAuthorizeAction extends BaseActionSupport {

	@Autowired private ISubAuthorizeService service;
	
	/**
	 * 查找策略列表
	 */
	@RequestMapping("/list")
	public void getSubauth2Tree(HttpServletResponse response) {
		print("RuleTree", new TreeEncoder(service.getStrategyByCreator()));
	}
	
	/**
	 * 获取一个Strategy（策略）对象的明细信息、角色对策略的信息、策略对用户的信息、策略对用户组的信息
	 */
	@RequestMapping("/detail/{id}")
	public void getSubauthInfo(HttpServletResponse response, @PathVariable("id") Long id) {
		XFormEncoder ruleXFormEncoder;
        TreeEncoder ruleToGroupTree;
        TreeEncoder ruleToUserTree;
        TreeEncoder ruleToRoleTree;
		
        Map<String, Object> data;
		if (UMConstants.DEFAULT_NEW_ID.equals(id)) { // 新建策略
            data = service.getSubauthInfo4Create();
            
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("startDate", DateUtil.format(new Date()));
			
			// 默认的有效时间, 结束时间向后推迟7天
			Calendar calendar = new GregorianCalendar();
			calendar.add(UMConstants.STRATEGY_LIFE_TYPE, UMConstants.STRATEGY_LIFE_TIME);
			map.put("endDate", DateUtil.format(calendar.getTime()));
            
			ruleXFormEncoder = new XFormEncoder(UMConstants.STRATEGY_XFORM, map);
		} 
		else { // 编辑策略
			data = service.getSubauthInfo4Update(id);
			ruleXFormEncoder = new XFormEncoder(UMConstants.STRATEGY_XFORM, (SubAuthorize) data.get("RuleInfo"));
		}
        
		TreeEncoder groupsTreeEncoder = new TreeEncoder(data.get("Rule2GroupTree"), new LevelTreeParser());
        groupsTreeEncoder.setNeedRootNode(false);

        TreeEncoder rolesTreeEncoder = new TreeEncoder(data.get("Rule2RoleTree"), new LevelTreeParser());
        rolesTreeEncoder.setNeedRootNode(false);
        
		ruleToGroupTree = new TreeEncoder(data.get("Rule2GroupExistTree"));
		ruleToUserTree  = new TreeEncoder(data.get("Rule2UserExistTree"));
		ruleToRoleTree  = new TreeEncoder(data.get("Rule2RoleExistTree"));
        
		print(new String[]{"RuleInfo", "Rule2GroupTree", "Rule2RoleTree", "Rule2GroupExistTree", "Rule2UserExistTree", "Rule2RoleExistTree"},
                new Object[]{ruleXFormEncoder, groupsTreeEncoder, rolesTreeEncoder, ruleToGroupTree, ruleToUserTree, ruleToRoleTree});
	}

	/**
	 * 修改一个Strategy对象的明细信息、策略对用户信息、策略对用户组、角色对策略的信息
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void saveSubauth(HttpServletResponse response, HttpServletRequest request, SubAuthorize entity) {
		
		String rule2UserIds  = request.getParameter("Rule2UserIds");
    	String rule2GroupIds = request.getParameter("Rule2GroupIds");
    	String rule2RoleIds  = request.getParameter("Rule2RoleIds");
    	
		service.saveSubauth(entity, rule2UserIds, rule2GroupIds, rule2RoleIds);
		printSuccessMessage();
	}

	/**
	 * 删除策略
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response, @PathVariable("id") Long id) {
		service.deleteSubauth(id);
        printSuccessMessage();
	}
	
	/**
	 * 停用/启用策略
	 */
	@RequestMapping(value = "/disable/{id}/{state}")
	public void disable(HttpServletResponse response, 
			@PathVariable("id") Long id, @PathVariable("state") int state) {
		
		service.disable(id, state);
        printSuccessMessage();
	}
}