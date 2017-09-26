package com.boubei.tss.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.DataExport;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.dm.report.permission.ReportPermission;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.modules.cloud.ModuleDef;
import com.boubei.tss.modules.cloud.ModuleService;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/**
 * 模块功能扩展：导入、导出（角色、录入表、报表、授权信息）
 */
@Controller
@RequestMapping("/auth/modulex")
public class ModuleX implements AfterUpload {
	
	@Autowired ModuleService service;
	@Autowired ICommonService commonService;
	@Autowired ReportService reportService;
	@Autowired RecordService recordService;
	
	// 导出模块
	@RequestMapping("/exp/{moduleId}")
	public void export(HttpServletResponse response, 
            @PathVariable("moduleId") Long moduleId) {
		
		ModuleDef def = (ModuleDef) commonService.getEntity(ModuleDef.class, moduleId);
		
		// 模块所包含的角色 及 资源权限
		List<Role> roleList = new ArrayList<Role>();
		List<Object> recordPermissions = new ArrayList<Object>();
		List<Object> reportPermissions = new ArrayList<Object>();
		
		String[] roles = def.getRoles().split(",");
		for(String role : roles) {
			Long roleId = EasyUtils.obj2Long(role);
			Role r = (Role) commonService.getEntity(Role.class, roleId);
			roleList.add( r );
			recordPermissions.addAll(commonService.getList(" from RecordPermission where roleId = ?", roleId));
			reportPermissions.addAll(commonService.getList(" from ReportPermission where roleId = ?", roleId));
		}
		
		// 资源（录入表、报表）
		Set<String> dataSources = new HashSet<String>();
		Set<Long> ids = new HashSet<Long>();
		List<Record> records = new ArrayList<Record>();
		for(Iterator<Object> it = recordPermissions.iterator(); it.hasNext(); ) {
			RecordPermission p = (RecordPermission) it.next();
			Record record = (Record) commonService.getEntity(Record.class, p.getResourceId());
			if(record != null && record.getLevelNo() > 3) { // 只导出开发目录下的资源
				if( !ids.contains(record.getId()) ) {
					records.add( record ); 
					dataSources.add( record.getDatasource() );
				}
				
				ids.add(record.getId());
			} 
			else {
				it.remove();
			}
		}
		
		ids = new HashSet<Long>();
		List<Report> reports = new ArrayList<Report>();
		for(Iterator<Object> it = reportPermissions.iterator(); it.hasNext(); ) {
			ReportPermission p = (ReportPermission) it.next();
			Report report = (Report) commonService.getEntity(Report.class, p.getResourceId());
			if(report != null && report.getLevelNo() > 3) {
				if( !ids.contains(report.getId()) ) {
					reports.add( report );
					dataSources.add( report.getDatasource() );
				}
				
				ids.add(report.getId());
			} 
			else {
				it.remove();
			}
		}
		
		// 数据源
		dataSources.remove( DMConstants.LOCAL_CONN_POOL );
		List<Param> dsList = new ArrayList<Param>();
		for(String ds : dataSources) {
			dsList.add( ParamManager.getSimpleParam(ds) );
		}
		
		// 对资源进行排序，其中 roleList 只有角色、没有角色组，无需排序
		Comparator<IDecodable> sorter = new Comparator<IDecodable>() {
            public int compare(IDecodable r1, IDecodable r2) {
                int flag = r1.getLevelNo() - r2.getLevelNo();
                if(flag == 0) {
                	flag = r1.getSeqNo() - r2.getSeqNo();
                }
				return flag;
            }
        };
        Collections.sort(records, sorter);
        Collections.sort(reports, sorter);
 
		// 先输出内容到服务端的导出文件中
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("module", def);
        result.put("roles", roleList);
        result.put("recordPermissions", recordPermissions);
		result.put("reportPermissions", reportPermissions);
        result.put("records", records);
        result.put("reports", reports);
        result.put("datasource", dsList);
        
        String exportPath = DataExport.getExportPath() + "/" + def.getModule() + ".json";
        String json = EasyUtils.obj2Json(result);
        FileHelper.writeFile(exportPath, json, false);
        DataExport.downloadFileByHttp(response, exportPath);
	}
	
	// 导入模块
	public String processUploadFile(HttpServletRequest request,
			String filepath, String orignFileName) throws Exception {
		
		XService service = (XService) Global.getBean("XService");
		service.importModule(filepath);
		
		return "parent.alert('导入成功，如果模块包含有自定义的HTML页，其涉及数据服务需另外修改'); parent.loadInitData();";
	}
	
	public static void main(String args[]) {
		String s1 = "'/tss/auth/xdata/json/39?fields=name'";
		String s2 = "data/json/40'";
		System.out.println(s1.replaceAll("xdata/json/2\\?", "xdata/json/69\\?"));
		System.out.println(s2.replaceAll("json/0'", "json/69'"));
	}
}
