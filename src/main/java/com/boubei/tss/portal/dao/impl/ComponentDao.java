/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.TreeSupportDao;
import com.boubei.tss.portal.dao.IComponentDao;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

@Repository("ComponentDao")
public class ComponentDao extends TreeSupportDao<Component> implements IComponentDao {
    
    public ComponentDao() {
        super(Component.class);
    }
 
    public Component save(Component obj) {  
        if(obj.getId() == null) {    
            return (Component) createObject(obj);
        }
        
        update(obj);
        return obj;
    }
    
    public Component deleteComponent(Component obj){
        delete(obj);
        
        // 删除资源文件
        URL url = URLUtil.getWebFileUrl(obj.getResourceBaseDir());             
        File path = new File(url.getPath());        
        File fileDir = FileHelper.findPathByName(path, obj.getCode());
        if (fileDir != null) {
            FileHelper.deleteFile(fileDir);
        }
        return obj;
    }
 
    public Component getDefaultLayout(){
        List<?> list = getEntities("from Component o where o.type = ? and o.isDefault = 1", Component.LAYOUT_TYPE);
        if(list.isEmpty()) {
        	throw new BusinessException(EX.P_05);
        }
        return (Component) list.get(0);
    }
    
    public Component getDefaultDecorator(){
        List<?> list = getEntities("from Component o where o.type = ? and o.isDefault = 1", Component.DECORATOR_TYPE);
        if(list.isEmpty()) {
        	throw new BusinessException(EX.P_04);
        }
        return (Component) list.get(0);
    }
    
    /*********************************** 以下是对元素（修饰器/布局器/Portlet）组的操作 *************************************/
    
    public void deleteGroup(Component group){
        List<Component> list = getChildrenById(group.getId());
        for ( Component temp : list ){
            delete(temp); 
        }
    }
    
    //注：子组的decode值可能和父组下的某个组件decode值相同，所以这里条件过滤要加上t.decode <> 子组decode
    public List<?> getComponentsDeeply(Long groupId) {
    	Component group = getEntity(groupId);
        String hql = "from Component t where t.decode like ?  and t.decode <> ? order by t.decode";
        return getEntities(hql, group.getDecode() + "%", group.getDecode());
    }
}