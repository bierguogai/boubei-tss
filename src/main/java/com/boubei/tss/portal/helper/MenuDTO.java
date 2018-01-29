package com.boubei.tss.portal.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.portal.entity.Navigator;

public class MenuDTO {

	public Long id;
	public String name;   // 菜单（项）名称
	public Long parentId; // 菜单项对应菜单
	public String url;    // url地址
	public String target; // 目标区域，_blank/_self 等

	public String methodName; // 方法名
	public String params;    // 参数

	public List<MenuDTO> children = new ArrayList<MenuDTO>();
	
	public MenuDTO(Navigator menu) {
		this.id = menu.getId();
		this.name = menu.getName();
		this.parentId = menu.getParentId();
		this.url = menu.getUrl();
		this.target = menu.getTarget();
		this.methodName = menu.getMethodName();
		this.params = menu.getParams();
	}
	
	// 要求默认按 decode 排好序
	public static List<MenuDTO> buildTree(Long pId, List<Navigator> list) {
		List<MenuDTO> returnList = new ArrayList<MenuDTO>();
		Map<Long, MenuDTO> map = new HashMap<Long, MenuDTO>();
        
        for(Navigator menu : list) {
        	if(ParamConstants.TRUE.equals(menu.getDisabled())) {
        		continue; // 过滤掉停用的
        	}
        	
        	MenuDTO dto = new MenuDTO(menu);
        	map.put(menu.getId(), dto);
        	
        	if( pId.equals(menu.getParentId()) ) {
        		returnList.add(dto);
        	} 
        	else {
        		MenuDTO parent = map.get(menu.getParentId());
        		if( parent != null ) {
            		parent.children.add( dto );
        		}
        	}
        }
        
        return returnList;
	}
}