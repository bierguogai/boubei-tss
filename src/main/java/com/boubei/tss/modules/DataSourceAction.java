package com.boubei.tss.modules;

import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.PX;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;

@Controller
@RequestMapping("/auth/ds")
public class DataSourceAction {
	
	@Autowired ParamService paramService;
	
	/** 系统配置参数, 只有创建者本人或Admin可以操作 */
	@RequestMapping(value = "/param/config", method = RequestMethod.GET)
	@ResponseBody
	public Object getCacheConfigs() {	
		String sql = " select id, code, value, creatorId from component_param " +
				"	where parentId=(select id from component_param where code =?) " +
				"	 and ? in (creatorId,-1) ";
		
		return SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, PX.CACHE_PARAM, Environment.getUserId());
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> listDS() {
		Param dsGroup = paramService.getParam(PX.DATASOURCE_LIST);
		String sql = "select * from (" +
				"	select text name, value from component_param where parentId=? and creatorId in (?,-1) order by decode) t" +
				" union all select 'restful-ws' as name, 'restful-WebService' as value from dual ";
		
		return SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, dsGroup.getId(), Environment.getUserId());
	}
	
	@RequestMapping(value = "/connpool", method = RequestMethod.POST)
	@ResponseBody
	public Object configConnpool(String code, String name, String value) {
		Param param = paramService.getParam(code);
		if(param == null) {
			// 新增时还需要在 ComboParam（”数据源列表“） 下增加一个param选项
			Param paramGroup = CacheHelper.getCacheParamGroup(paramService);
			Param dsGroup = paramService.getParam(PX.DATASOURCE_LIST);
			
			ParamManager.addSimpleParam(paramGroup.getId(), code, name, value);
			ParamManager.addParamItem(dsGroup.getId(), code, name, ParamConstants.COMBO_PARAM_MODE);
			
			return "数据源配置成功";
		} 
		else {
			param.setValue(value);
			param.setName(name);
			paramService.saveParam(param);
			
			// 修改“数据源列表”里下拉项的Name值
			List<Param> list = ParamManager.getComboParam(PX.DATASOURCE_LIST);
			for(Param p : list) {
				if( code.equals(p.getValue()) ) {
					p.setText(name);
					paramService.saveParam(p);
					break;
				}
			}
			
			return "数据源配置修改成功";
		}
	}
	
	@RequestMapping(value = "/connpool/{paramId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object delConnpool(HttpServletResponse response, @PathVariable("paramId") Long paramId) {
		Param param = paramService.getParam(paramId);
        paramService.delete(paramId);   
        
        // 删除“数据源列表”的下拉项
        String code = param.getCode();
        List<Param> list = ParamManager.getComboParam(PX.DATASOURCE_LIST);
		for(Param item : list) {
			if( code.equals(item.getValue()) ) {
				paramService.delete(item.getId());  
				break;
			}
		}
		
		JCache.pools.remove(code);
        
        return "成功删除数据源";
    }
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	@ResponseBody
	public Object testConn(String driver, String url, String user, String pwd) {
        try {
            Class.forName(driver);
			DriverManager.getConnection(url, user, pwd);
        } 
        catch (Exception e) {
            return "测试连接失败，原因：" + e.getMessage();
        } 
		return "测试连接成功";
	}

}
