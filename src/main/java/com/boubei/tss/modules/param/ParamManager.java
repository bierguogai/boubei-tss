package com.boubei.tss.modules.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.boubei.tss.EX;
import com.boubei.tss.cache.extension.PCache;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.timer.SchedulerBean;

/**
 *  调用参数管理功能入口
 */
public class ParamManager {
	
	/** 监听器列表 */
    public static List<ParamListener> listeners = new ArrayList<ParamListener>();
    
    static {
    	ParamManager.listeners.add(new PCache());
    	ParamManager.listeners.add(new SchedulerBean());
    }
    
    public static ParamService getService() {
        return Global.getParamService();
    }
    
    /**
     * 获取简单类型参数
     * @param code
     * @return
     */
    public static Param getSimpleParam(String code){
    	return getService().getParam(code);
    }
    
    /**
     * 获取下拉类型参数列表
     * @param code
     * @return
     */
	public static List<Param> getComboParam(String code){
    	try{
            return getService().getComboParam(code);
    	} catch (Exception e) {
    		throw new BusinessException(EX.parse(EX.F_11, code), e);
		}
    }
    
	public static Param getComboParamItem(String code, String x) {
		List<Param> list = getComboParam(code);
		for(Param p : list) {
			if( x != null && (x.equals(p.getText()) || x.equals(p.getValue())) ) {
				return p;
			}
		}
		return null;
    }
	
    /**
     * 获取树型类型参数列表
     * @param code
     * @return
     */
	public static List<Param> getTreeParam(String code){
    	try{
            return getService().getTreeParam(code);
    	} catch (Exception e) {
    		throw new BusinessException( EX.parse(EX.F_11, code) , e);
		}
    }
    
    /**
     * 根据参数Code读取参数值
     * @param code
     * @return
     */
    public static String getValue(String code){
		Param param = (Param)getService().getParam(code);
        if(param == null) { 
            throw new BusinessException( EX.parse(EX.F_10, code) );
        }
        
    	return param.getValue().replace("\n", ""); // 去掉回车
    }
    
    // 没有配置不抛出异常，返回指定的默认值
    public static String getValue(String code, String defaultVal){
		Param param = (Param)getService().getParam(code);
        if(param == null) { 
            return defaultVal;
        }
    	return param.getValue().replace("\n", ""); // 去掉回车
    }
    
    public static String getValueNoSpring(String code){
        String sql = "select p.value from component_param p " +
        		" where p.type = 1 and p.code=? and p.disabled <> 1";
 
        List<Map<String, Object>> result = SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, code);
        if( result.isEmpty() ){
            throw new BusinessException( EX.parse(EX.F_10, code) );
        }
        
        return (String) result.get(0).get("value");
    }
    
    /** 建参数组 */
    public static Param addParamGroup(Long parentId, String name) {
        Param param = new Param();
        param.setName(name);
        param.setCode(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.GROUP_PARAM_TYPE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 简单参数 */
    public static Param addSimpleParam(Long parentId, String code, String name, String value) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setValue(value);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.SIMPLE_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 下拉型参数 */
    public static Param addComboParam(Long parentId, String code, String name) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.COMBO_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 树型参数 */
    public static Param addTreeParam(Long parentId, String code, String name) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.TREE_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }

    /** 新建设参数项 */
    public static Param addParamItem(Long parentId, String value, String text, Integer mode) {
        Param param = new Param();
        param.setValue(value);
        param.setText(text);
        param.setParentId(parentId);
        param.setType(ParamConstants.ITEM_PARAM_TYPE);
        param.setModality(mode);
        
        getService().saveParam(param);
        return param;
    }
}

	