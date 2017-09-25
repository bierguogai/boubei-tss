package com.boubei.tss.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.permission.ReportPermission;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.cloud.ModuleDef;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

@Service("XService")
@SuppressWarnings("unchecked")
public class XServiceImpl implements XService {
	
	@Autowired ICommonDao commonDao;
	
	public void importModule(String filepath) throws Exception {
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
		ModuleDef def = (ModuleDef) result.get("module");
		List<Role> roles = (List<Role>) result.get("roles");
		List<RecordPermission> recordPermissions = (List<RecordPermission>) result.get("recordPermissions");
		List<ReportPermission> reportPermissions = (List<ReportPermission>) result.get("reportPermissions");
		List<Record> records = (List<Record>) result.get("records");
		List<Report> reports = (List<Report>) result.get("reports");
		List<Param> dsList = (List<Param>) result.get("datasource");
		
		for(Param ds : dsList) {
			ds.setId(null);
			commonDao.create(ds);
		}
		
		Comparator<IDecodable> sorter = new Comparator<IDecodable>() {
            public int compare(IDecodable r1, IDecodable r2) {
                return r2.getLevelNo() - r1.getLevelNo();
            }
        };
		
		Map<Long, Long> map0 = new HashMap<Long, Long>();
		Collections.sort(roles, sorter);
		for(Role role : roles) {
			Long oldId = role.getId();
			role.setId(null);
			role.setParentId( (Long) EasyUtils.checkNull(map0.get(role.getParentId()), roleGroupId) );
			commonDao.create(role);
			map0.put(oldId, role.getId());
		}
		
		Map<Long, Long> map2 = new HashMap<Long, Long>();
		Collections.sort(records, sorter);
		for(Record record : records) {
			Long oldId = record.getId();
			record.setId(null);
			record.setParentId( (Long) EasyUtils.checkNull(map2.get(record.getParentId()), recordGroupId) );
			commonDao.create(record);
			map2.put(oldId, record.getId());
		}
		for(RecordPermission t : recordPermissions) {
			t.setId(null);
			t.setRoleId( map0.get(t.getRoleId()) );
			t.setResourceId( map2.get(t.getResourceId()) );
			commonDao.create(t);
		}
		
		Map<Long, Long> map1 = new HashMap<Long, Long>();
		Collections.sort(reports, sorter);
		for(Report report : reports) {
			Long oldId = report.getId();
			report.setId(null);
			report.setParentId( (Long) EasyUtils.checkNull(map1.get(report.getParentId()), reportGroupId) );
			
			// 判断是否为录入表链接
			String displayUri = report.getDisplayUri();
			if( displayUri != null && displayUri.startsWith("/tss/modules/dm/recorder.html?id=")) {
				String[] t = displayUri.split("=");
				Long recordId = EasyUtils.obj2Long( t[1] );
				displayUri = t[0] + "=" + EasyUtils.checkNull( map2.get(recordId), recordId );
				report.setDisplayUri(displayUri);
			}
			
			commonDao.create(report);
			map1.put(oldId, report.getId());
		}
		for(ReportPermission t : reportPermissions) {
			t.setId(null);
			t.setRoleId( map0.get(t.getRoleId()) );
			t.setResourceId( map1.get(t.getResourceId()) );
			commonDao.create(t);
		}
		
		// 替换录入表、报表里的字段定义、JS脚本里的数据服务涉及的ID
		for(Report report : reports) {
			String paramDef = report.getParam();
			if(paramDef != null) {
				for(Long oldId : map1.keySet()) {
					paramDef = paramDef.replaceAll("json/"+oldId+"'", "json/"+map1.get(oldId)+"'");
				}
				for(Long oldId : map2.keySet()) {
					paramDef = paramDef.replaceAll("json/"+oldId+"'", "json/"+map2.get(oldId)+"'");
				}
				report.setParam(paramDef);
			}
			commonDao.update(report);
		}
		
		for(Record record : records) {
			String fieldsDef = record.getDefine();
			String globalJS = record.getCustomizeGrid();
			if(fieldsDef != null) {
				for(Long oldId : map1.keySet()) {
					fieldsDef = fieldsDef.replaceAll("json/"+oldId+"'", "json/"+map1.get(oldId)+"'");
					globalJS = globalJS.replaceAll("json/"+oldId+"'", "json/"+map1.get(oldId)+"'");
				}
				for(Long oldId : map2.keySet()) {
					fieldsDef = fieldsDef.replaceAll("json/"+oldId+"'", "json/"+map2.get(oldId)+"'");
					globalJS = globalJS.replaceAll("json/"+oldId+"'", "json/"+map2.get(oldId)+"'");
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
		commonDao.create(def);
	}

}
