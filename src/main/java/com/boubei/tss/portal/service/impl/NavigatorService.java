package com.boubei.tss.portal.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.dao.INavigatorDao;
import com.boubei.tss.portal.entity.Navigator;
import com.boubei.tss.portal.helper.MenuDTO;
import com.boubei.tss.portal.service.INavigatorService;
import com.boubei.tss.util.MacrocodeCompiler;

@Service("NavigatorService")
public class NavigatorService implements INavigatorService {

	@Autowired private INavigatorDao dao;

    public List<?> getAllNavigator(){
        return dao.getEntities("from Navigator o order by o.decode");
	}
	
	public Navigator saveNavigator(Navigator entity){
		if( entity.getId() == null ) {
		    Long parentId = entity.getParentId();
            Integer nextSeqNo = dao.getNextSeqNo(parentId);
            entity.setSeqNo(nextSeqNo);
            
            Navigator parent = dao.getEntity(parentId);
            if(parent != null) {
            	entity.setPortalId(parent.getPortalId());
            }
		}
		
		return dao.save(entity);
	}
	
	public void deleteNavigator(Long id){
        List<Navigator> children = dao.getChildrenById(id, PortalConstants.NAVIGATOR_EDIT_OPERRATION);
        for( Navigator child : children ){
            dao.deleteNavigator(child);
        }
	}
	
	public Navigator getNavigator(Long id){
        return dao.getEntity(id);
	}
	
	public void disable(Long id, Integer disabled) {
        List<Navigator> list;
        if(ParamConstants.TRUE.equals(disabled)) {
            list = dao.getChildrenById(id, PortalConstants.NAVIGATOR_EDIT_OPERRATION);
        } 
        else {
            list = dao.getParentsById(id);    
        }
        
        for(  Navigator temp : list ){
            temp.setDisabled(disabled);
            dao.updateWithoutFlush(temp);
        }
	}

    public void sort(Long id, Long targetId, int direction) {
        dao.sort(id, targetId, direction);
    }

    public List<?> getNavigatorsByPortal(Long portalId) {
        return dao.getMenusByPortal(portalId);
    }

    public void moveNavigator(Long id, Long targetId) {
        Navigator navigator = dao.getEntity(id);
        
        Navigator target = dao.getEntity(targetId);
		navigator.setPortalId(target.getPortalId());
        navigator.setParentId(targetId);
        navigator.setSeqNo(dao.getNextSeqNo(targetId));
                   
        dao.moveEntity(navigator);
    }
    
    public String getNavigatorXML(Long id) {
		Navigator navigator = getNavigator(id);  
		if(navigator == null) {
			return "<MainMenu/>";
		}
		
		List<Navigator> menuItems = dao.getMenuItems(id, Environment.getUserId());
		
		// 解析地址参数中 ${APP_URL}等信息，取 AppServer的配置信息。
		Collection<AppServer> appservers = Context.getApplicationContext().getAppServers();
		Map<String, Object> macros = new HashMap<String, Object>();
		for(AppServer appserver : appservers) {
			macros.put("${" + appserver.getCode() + "_URL}", appserver.getBaseURL());
		}
	 
		Element node = navigator.compose2Tree(menuItems);
		if(node == null) {
			return "<MainMenu/>";
		}
		return MacrocodeCompiler.run(node.asXML(), macros, true);
	}
	
	public List<MenuDTO> getMenuTree(Long id) {
		List<Navigator> list = dao.getMenuItems(id, Environment.getUserId());
		return MenuDTO.buildTree(id, list);
	}
}

	