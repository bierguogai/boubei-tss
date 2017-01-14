package com.boubei.tss.framework;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;

@Controller
@RequestMapping("/init")
public class SystemInit {
	
	@Autowired protected ParamService paramService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object init() {
		String[][] items = new String[][]{ 
        		{"1", "停用"}, 
        		{"0", "启用"} 
        	};
        addComboParam("EntityState", "对象状态", items);
		
		items = new String[][]{ 
				{ "1", "超级管理员"},
	        	{ "2", "管理用户"},
	        	{ "3", "实操用户"}
        	};
        addComboParam("UserType", "用户类型", items);
		
		return new Object[] { "Success" };
	}
	
	void addComboParam(String code, String name, String[][] items) {
		Param cp;
		List<Param> list;
		
		if( (cp = paramService.getParam(code)) != null) {
			list = paramService.getComboParam(code);
		}
		else {
			cp = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, code, name);
			list = new ArrayList<Param>();
		}
		
		L:for(String[] item : items) {
			for(Param p : list) {
				if(p.getValue().equals(item[0])) {
					p.setText(item[1]);
					paramService.saveParam(p);
					continue L;
				}
			}
			ParamManager.addParamItem(cp.getId(), item[0], item[1], ParamConstants.COMBO_PARAM_MODE);
		}
	}

}
