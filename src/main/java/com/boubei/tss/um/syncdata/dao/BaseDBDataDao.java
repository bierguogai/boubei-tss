package com.boubei.tss.um.syncdata.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.helper.dto.GroupDTO;
import com.boubei.tss.um.helper.dto.UserDTO;
import com.boubei.tss.util.BeanUtil;
 
public abstract class BaseDBDataDao implements IOutDataDao {
    
	// 要求SQL的字段别名 和 DTO里的属性名一致
    protected static String[] groupDtoPropertyNames = new String[]{"id", "parentId", "name", "description"};
    protected static String[] userDtoPropertyNames  = new String[]{"id", "groupId", "loginName", "password", "userName", "sex", "birthday", "email", "employeeNo", "authMethod", "disabled"};
    
    public List<?> getOtherGroups(Map<String, String> paramsMap, String sql, String groupId) {
        sql = sql.replaceAll(":groupId", groupId);
        
        Connection conn = getConnection(paramsMap);
        return getDtosBySQL(conn, sql, groupDtoPropertyNames, GroupDTO.class);
    }

    public List<?> getOtherUsers(Map<String, String> paramsMap, String sql, String groupId, Object...otherParams) {
        sql = sql.replaceAll(":groupId", groupId);
 
        Connection conn = getConnection(paramsMap);
        return getDtosBySQL(conn, sql, userDtoPropertyNames, UserDTO.class);
    }
 
    protected abstract Connection getConnection(Map<String, String> paramsMap);
    
    protected List<?> getDtosBySQL(Connection conn, String sql, String[] dtoPropertyNames, Class<?> clazz) {
        List<Object> items = new ArrayList<Object>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object dto = BeanUtil.newInstance(clazz);
                Map<String, String> attrsMap = new HashMap<String, String>();
                for(int i = 0; i < dtoPropertyNames.length; i++){
                    Object value = rs.getObject(dtoPropertyNames[i]);
                    attrsMap.put(dtoPropertyNames[i], value == null ? null : value.toString());
                }
                BeanUtil.setDataToBean(dto, attrsMap);
                items.add(dto);
            }
            
            rs.close();   
                
        } catch (SQLException e) {
            throw new BusinessException("数据查询错误！", e);
        } finally {
        	try { stmt.close(); } catch (SQLException e) { }
            try { conn.close(); } catch (SQLException e) { }
        }
        return items;
    }
}