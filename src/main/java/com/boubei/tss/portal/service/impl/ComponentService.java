package com.boubei.tss.portal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.portal.dao.IComponentDao;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.service.IComponentService;

@Service("ComponentService")
public class ComponentService implements IComponentService {

    @Autowired private IComponentDao dao;

    public List<?> getAllComponentsAndGroups() {
        return dao.getEntities("from Component o order by o.decode ");
    }

    public List<?> getEnabledComponentsAndGroups(int type) {
        return dao.getEntities("from Component o where o.disabled <> 1 and o.type=? order by o.decode", type);
    }
 
    public Component saveComponent(Component component) {
        if (component.getId() == null) {
            Long parentId = component.getParentId();
            Integer nextSeqNo = dao.getNextSeqNo(parentId);
			component.setSeqNo(nextSeqNo);
            
            Component group = dao.getEntity(parentId);
            if( group != null ) {
				component.setType(group.getType());
            }
        }
        
        return dao.save(component);
    }
    
    public Component deleteComponent(Long id) {
        Component component = dao.getEntity(id);
        if(component.isGroup()) {
        	deleteComponentGroup(id);
        	return component;
        }
        
        checkElementInUse(component);
        return dao.deleteComponent(component);
    }
    
    private void deleteComponentGroup(Long groupId) {
        // 检查组下是否有元素尚在使用中
        List<?> components = dao.getComponentsDeeply(groupId);
        for ( Object component : components ) {
            checkElementInUse( (Component) component );
        }
            
        Component entity = dao.getEntity( groupId );
        dao.deleteGroup(entity);
    }

    private void checkElementInUse(Component component) {
        if(ParamConstants.TRUE.equals(component.getIsDefault())) {
            throw new BusinessException(EX.P_11);
        }
        
        Component group = dao.getEntity(component.getParentId());
        String hql = null;
        switch (group.getType()) {
        case Component.PORTLET_TYPE:  // Portlet组
            hql = "from Structure t where t.definer.id = ? and t.type = 3 ";
            break;
        case Component.LAYOUT_TYPE:   // 布局器组
            hql = "select t from Structure t, ThemeInfo ti where t.id = ti.id.structureId and ti.layout.id = ? ";
            break;
        case Component.DECORATOR_TYPE: // 修饰器组
            hql = "select t from Structure t, ThemeInfo ti where t.id = ti.id.structureId and ti.decorator.id = ? ";
            break;
        }     
 
        Long elementId = component.getId();
        if( dao.getEntities(hql, elementId).size() > 0) {
            throw new BusinessException("[" + elementId + ", " + component.getName() + "] is using，cann't delete");
        }
    }
 
    public void disableComponent(Long id, Integer disabled) {
        Component component = getComponent(id);
        if(ParamConstants.TRUE.equals(component.getIsDefault())) {
            throw new BusinessException(EX.P_12);
        }
        
        if (!component.getDisabled().equals(disabled)) {
            component.setDisabled(disabled);
            dao.save(component);
        }
    }
 
    public Component getComponent(Long id) {
        return dao.getEntity(id);
    }
 
    public void sort(Long id, Long targetId, int direction) {
        dao.sort(id, targetId, direction);
    }

	public void moveComponent(Long id, Long groupId) {
		Component component = dao.getEntity(id);
		Component group = dao.getEntity(groupId);
		if(!group.isGroup() || !component.getType().equals(group.getType())) {
			throw new BusinessException("target node is not a " + component.getComponentType() + " group");
		}
		
		component.setParentId(groupId);
		component.setSeqNo(dao.getNextSeqNo(groupId));
                   
        dao.moveEntity(component);
	}
}
