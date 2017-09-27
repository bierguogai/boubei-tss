package com.boubei.tss.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.PX;
import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.dm.report.permission.ReportPermission;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.cloud.ModuleDef;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.service.IRoleService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

@Service("XService")
@SuppressWarnings("unchecked")
public class XServiceImpl implements XService {
	
	@Autowired ICommonDao commonDao;
	@Autowired ParamService paramService;
	@Autowired IRoleService roleService;
	@Autowired ReportService reportService;
	@Autowired RecordService recordService;
	
	public void importModule(String filepath) throws Exception {
		
		PermissionHelper ph = PermissionHelper.getInstance();
		
		String userCode = Environment.getUserCode();
		Param env = ParamManager.getSimpleParam( userCode );
		if(env == null) {
			throw new BusinessException("您不是开发者，不能导入，请先注册为开发者。");
		}
		String[] groups = env.getValue().split(",");
		Long reportGroupId = EasyUtils.obj2Long(groups[0]);
		Long recordGroupId = EasyUtils.obj2Long(groups[1]);
		Long roleGroupId = EasyUtils.obj2Long(groups[3]);
		
		File targetFile = new File(filepath);
		String json = FileHelper.readFile(targetFile);
        
		Map<String, Object> result = new ObjectMapper().readValue(json, Map.class);
		Object obj = result.get("module");
		ModuleDef def = new ObjectMapper().readValue(EasyUtils.obj2Json(obj), ModuleDef.class);
		List<Map<String, Object>> roles = (List<Map<String, Object>>) result.get("roles");
		List<Map<String, Object>> recordPermissions = (List<Map<String, Object>>) result.get("recordPermissions");
		List<Map<String, Object>> reportPermissions = (List<Map<String, Object>>) result.get("reportPermissions");
		List<Map<String, Object>> records = (List<Map<String, Object>>) result.get("records");
		List<Map<String, Object>> reports = (List<Map<String, Object>>) result.get("reports");
		List<Map<String, Object>> dsList = (List<Map<String, Object>>) result.get("datasource");
		
		List<Record> _records = new ArrayList<Record>();
		List<Report> _reports = new ArrayList<Report>();
		
		for(Object m : dsList) {
			Param ds = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), Param.class);
			if(paramService.getParam( ds.getCode() ) != null) continue;
			
			Param cpGroup = CacheHelper.getCacheParamGroup(paramService);
			Param dsGroup = paramService.getParam(PX.DATASOURCE_LIST);
			ParamManager.addSimpleParam(cpGroup.getId(), ds.getCode(), ds.getName(), ds.getValue());
			ParamManager.addParamItem(dsGroup.getId(), ds.getCode(), ds.getName(), ParamConstants.COMBO_PARAM_MODE);
		}
		
		Map<Long, Long> map0 = new HashMap<Long, Long>();
		for(Map<String, Object> m : roles) {
			Role role = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), Role.class);
			
			Long oldId = role.getId();
			role.setId(null);
			role.setParentId( (Long) EasyUtils.checkNull(map0.get(role.getParentId()), roleGroupId) );
			roleService.saveRole2UserAndRole2Group(role, "", "");
			
			Long newId = role.getId();
			map0.put(oldId, newId);
			
			// 把当前开发者的开发目录授权给角色
			Record recordGroup = recordService.getRecord(recordGroupId);
			ph.createPermission(newId, recordGroup, Record.OPERATION_CDATA, 1, 0, 0, RecordPermission.class.getName());
			
			Report reportGroup = reportService.getReport(reportGroupId);
			ph.createPermission(newId, reportGroup, Report.OPERATION_VIEW, 1, 0, 0, ReportPermission.class.getName());
		}
		
		Map<Long, Long> map2 = new HashMap<Long, Long>();
		map2.put(Record.DEFAULT_PARENT_ID, Record.DEFAULT_PARENT_ID);
		for(Map<String, Object> m  : records) {
			Record record = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), Record.class);
			
			Long oldId = record.getId();
			record.setId(null);
			record.setParentId( (Long) EasyUtils.checkNull(map2.get(record.getParentId()), recordGroupId) );
			record.setDatasource( DMConstants.LOCAL_CONN_POOL ); // 强制替换为本地数据源，以防创建表结构失败
			
			// 替换定制的展示页面路径
			String pagePath = record.getCustomizePage();
			if(!EasyUtils.isNullOrEmpty(pagePath)) {
				int index = pagePath.lastIndexOf("/");
				pagePath = "/tss/pages/" + Environment.getUserCode() + pagePath.substring(index);
				record.setCustomizePage(pagePath);
			}
			
			recordService.createRecord(record);
			
			map2.put(oldId, record.getId());
			_records.add(record);
		}
		for(Map<String, Object> m : recordPermissions) {
			RecordPermission t = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), RecordPermission.class);
			
			t.setId(null);
			t.setRoleId( map0.get(t.getRoleId()) );
			t.setResourceId( map2.get(t.getResourceId()) );
			if(t.getResourceId() != null && t.getRoleId() != null) {
				commonDao.create(t);
			}
		}
		
		Map<Long, Long> map1 = new HashMap<Long, Long>();
		map1.put(Report.DEFAULT_PARENT_ID, Report.DEFAULT_PARENT_ID);
		for(Map<String, Object> m : reports) {
			Report report = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), Report.class);
			
			Long oldId = report.getId();
			report.setId(null);
			report.setParentId( (Long) EasyUtils.checkNull(map1.get(report.getParentId()), reportGroupId) );
			report.setDatasource( DMConstants.LOCAL_CONN_POOL );
			
			// 替换定制的展示页面路径
			String pagePath = report.getDisplayUri();
			if(!EasyUtils.isNullOrEmpty(pagePath)) {
				int index = pagePath.lastIndexOf("/");
				pagePath = "/tss/pages/" + Environment.getUserCode() + pagePath.substring(index);
				report.setDisplayUri(pagePath);
			}
			
			Integer status = report.getDisabled();
			reportService.createReport(report);
            
            report.setDisabled(status); // 因默认创建分组都是停用状态，但导入分组不需要，保留原来状态
            reportService.updateReport(report);
			
			map1.put(oldId, report.getId());
			_reports.add(report);
		}
		for(Map<String, Object> m : reportPermissions) {
			ReportPermission t = new ObjectMapper().readValue(EasyUtils.obj2Json( m ), ReportPermission.class);
			
			t.setId(null);
			t.setRoleId( map0.get(t.getRoleId()) );
			t.setResourceId( map1.get(t.getResourceId()) );
			if(t.getResourceId() != null && t.getRoleId() != null) {
				commonDao.create(t);
			}
		}
		
		// 替换录入表、报表里的字段定义、JS脚本里的数据服务涉及的ID
		for(Report report : _reports) {
			String paramDef = report.getParam();
			if(paramDef != null) {
				for(Long oldId : map1.keySet()) {
					paramDef = paramDef
							.replaceAll("/data/json/"+oldId+"'", "/data/json/"+map1.get(oldId)+"'")
							.replaceAll("/data/json/"+oldId+"\\^", "/data/json/"+map1.get(oldId)+"\\^")
							.replaceAll("/data/json/"+oldId+"\\?", "/data/json/"+map1.get(oldId)+"\\?");
				}
				for(Long oldId : map2.keySet()) {
					paramDef = paramDef
							.replaceAll("xdata/json/"+oldId+"'", "xdata/json/"+map2.get(oldId)+"'")
							.replaceAll("xdata/json/"+oldId+"\\^", "xdata/json/"+map2.get(oldId)+"\\^")
							.replaceAll("xdata/json/"+oldId+"\\?", "xdata/json/"+map2.get(oldId)+"\\?");
				}
				report.setParam(paramDef);
			}
			
			// 判断是否为录入表链接
			String displayUri = report.getDisplayUri();
			if( displayUri != null && displayUri.startsWith("/tss/modules/dm/recorder.html?id=")) {
				String[] t = displayUri.split("=");
				Long recordId = EasyUtils.obj2Long( t[1] );
				displayUri = t[0] + "=" + EasyUtils.checkNull( map2.get(recordId), recordId );
				report.setDisplayUri(displayUri);
			}
			
			commonDao.update(report);
		}
		
		for(Record record : _records) {
			String fieldsDef = record.getDefine();
			String globalJS = EasyUtils.obj2String( record.getCustomizeGrid() );
			if(fieldsDef != null) {
				for(Long oldId : map1.keySet()) {
					fieldsDef = fieldsDef
							.replaceAll("/data/json/"+oldId+"\\?", "/data/json/"+map1.get(oldId)+"\\?")
							.replaceAll("/data/json/"+oldId+"'", "/data/json/"+map1.get(oldId)+"'")
							.replaceAll("/data/json/"+oldId+"\\^", "/data/json/"+map1.get(oldId)+"\\^");
					globalJS = globalJS
							.replaceAll("/data/json/"+oldId+"'", "/data/json/"+map1.get(oldId)+"'")
							.replaceAll("/data/json/"+oldId+"\\?", "/data/json/"+map1.get(oldId)+"\\?");
				}
				for(Long oldId : map2.keySet()) {
					fieldsDef = fieldsDef
							.replaceAll("xdata/json/"+oldId+"\\?", "xdata/json/"+map2.get(oldId)+"\\?")
							.replaceAll("xdata/json/"+oldId+"'", "xdata/json/"+map2.get(oldId)+"'")
							.replaceAll("xdata/json/"+oldId+"\\^", "xdata/json/"+map2.get(oldId)+"\\^");
					globalJS = globalJS
							.replaceAll("xdata/json/"+oldId+"'", "xdata/json/"+map2.get(oldId)+"'")
							.replaceAll("xdata/json/"+oldId+"\\?", "xdata/json/"+map2.get(oldId)+"\\?");
				}
				record.setDefine(fieldsDef);
				record.setCustomizeGrid(globalJS);
			}
			
			commonDao.update(record);
		}
		
		def.setId(null);
		String[] _roles = def.getRoles().split(",");
		List<Object> defRoles = new ArrayList<Object>();
		for(String _role : _roles) {
			Long oldId = EasyUtils.obj2Long(_role);
			Long newId = map0.get(oldId);
			defRoles.add(newId);
		}
		def.setRoles( EasyUtils.list2Str(defRoles) );
		def.setCreateTime(new Date());
		def.setCreator(Environment.getUserCode());
		def.setVersion(0);
		commonDao.create(def);
	}

}
