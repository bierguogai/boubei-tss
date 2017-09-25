package com.boubei.tss.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boubei.tss.PX;
import com.boubei.tss.dm.DataExport;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.modules.cloud.ModuleService;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/**
 * 开发者专属资源（角色、数据源、录入表、报表、Job、Task）：导入、导出
 */
@Controller
@RequestMapping("/auth/dev/x")
@SuppressWarnings("unchecked")
public class DeveloperX {
	
	@Autowired private ModuleService service;
	@Autowired private ICommonService commonService;
	@Autowired ReportService reportService;
	@Autowired RecordService recordService;
	@Autowired ILoginService loginService;
	
	// 导出模块
	@RequestMapping("/{developer}")
	public void export(HttpServletResponse response, 
            @PathVariable("developer") String developer) {
		
		Long developerId = loginService.getOperatorDTOByLoginName(developer).getId();
		
		// 数据源
		List<Param> dsList = ParamManager.getComboParam(PX.DATASOURCE_LIST);
		for(Iterator<Param> it = dsList.iterator(); it.hasNext(); ) {
			if(!developerId.equals( it.next().getCreatorId() )) {
				it.remove();
			}
		}
		
		// 模块所包含的角色 及 资源权限
		List<Object> recordPermissions = new ArrayList<Object>();
		List<Object> reportPermissions = new ArrayList<Object>();
		
		List<Role> roles = (List<Role>) commonService.getList("from Role where creatorId=?", developerId);
		for(Role role : roles) {
			Long roleId = role.getId();
			recordPermissions.addAll(commonService.getList(" from RecordPermission where roleId = ?", roleId));
			reportPermissions.addAll(commonService.getList(" from ReportPermission where roleId = ?", roleId));
		}
		
		// 资源（录入表、报表）
		List<?> records = commonService.getList("from Record where creatorId=?", developerId);
		List<?> reports = commonService.getList("from Report where creatorId=?", developerId);
		
		// Job、Task
		List<?> jobs = commonService.getList("from JobDef where creator = ?", developer);
		List<?> tasks = commonService.getList("from Task where creator = ?", developer);
 
		// 先输出内容到服务端的导出文件中
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("datasource", dsList);
        result.put("roles", roles);
		result.put("recordPermissions", recordPermissions);
		result.put("reportPermissions", reportPermissions);
		result.put("records", records);
		result.put("reports", reports);
		result.put("jobs", jobs);
		result.put("tasks", tasks);
        
        String exportPath = DataExport.getExportPath() + "/" + developer + ".json";
        String json = EasyUtils.obj2Json(result);
        FileHelper.writeFile(exportPath, json, false);
        DataExport.downloadFileByHttp(response, exportPath);
	}
	
	// 导入开发者资源
	public String processUploadFile(HttpServletRequest request,
			String filepath, String orignFileName) throws Exception {
		
//		File targetFile = new File(filepath);
//		String json = FileHelper.readFile(targetFile);
        
		return "parent.alert('导入成功'); parent.loadInitData();";
	}
	
}
