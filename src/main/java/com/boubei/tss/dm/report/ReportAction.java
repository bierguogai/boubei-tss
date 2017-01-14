package com.boubei.tss.dm.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.PX;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.report.permission.ReportPermission;
import com.boubei.tss.dm.report.permission.ReportResource;
import com.boubei.tss.dm.report.timer.ReportJob;
import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.web.dispaly.tree.DefaultTreeNode;
import com.boubei.tss.framework.web.dispaly.tree.ITreeNode;
import com.boubei.tss.framework.web.dispaly.tree.LevelTreeParser;
import com.boubei.tss.framework.web.dispaly.tree.StrictLevelTreeParser;
import com.boubei.tss.framework.web.dispaly.tree.TreeEncoder;
import com.boubei.tss.framework.web.dispaly.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.um.helper.PasswordRule;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.StringUtil;
import com.boubei.tss.util.URLUtil;

@Controller
@RequestMapping("/auth/rp")
public class ReportAction extends BaseActionSupport {
    
    @Autowired private ReportService reportService;
    @Autowired private ILoginService loginService;
    
    @RequestMapping("/")
    public void getAllReport(HttpServletResponse response) {
	    checkPwdSecurity();
    	
        List<?> list = reportService.getAllReport();
        TreeEncoder treeEncoder = new TreeEncoder(list, new StrictLevelTreeParser(Report.DEFAULT_PARENT_ID));
        print("SourceTree", treeEncoder);
    }
    
	@RequestMapping("/my/ids")
	@ResponseBody
    public List<Long> getMyReportIds() {
        String pt = ReportPermission.class.getName();
		return PermissionHelper.getInstance().getResourceIdsByOperation(pt, Report.OPERATION_VIEW);
    }
	
    @RequestMapping("/my/{groupId}")
	@ResponseBody
    public List<Object> getReportsByGroup(@PathVariable Long groupId) {
    	List<Report> list = reportService.getReportsByGroup(groupId, Environment.getUserId());
    	List<Report> tempList = new ArrayList<Report>();
    	List<Object> result = new ArrayList<Object>();
	    
    	for(Report report : list) {
			if( report.isActive() ) {
				tempList.add(report);
			}
    	}
    	
    	List<String> topSelf = getTops(true);
	    		
	    if(topSelf.size() > 0) {
	    	Report tg1 = new Report(-1L, "您最近访问", groupId);
	    	Report tg2 = new Report(-2L, "您最近访问", tg1.getId());
			tempList.add(tg1);
			tempList.add(tg2);
	    	tempList.addAll( cloneTops(tg2.getId(), topSelf, list) );
	    }
    	
    	for(Report report : tempList) {
    		Long reportId = report.getId();
    		String name = report.getName();
			Long parentId = report.getParentId();
			result.add(new Object[] { reportId, name, parentId, report.getType() });
    	}
    	
		return result;
    }
    
    /**
     * 如果指定了分组，则只取该分组下的报表
     */
    @RequestMapping("/my")
    public void getMyReports(HttpServletResponse response, Long groupId) {
	    checkPwdSecurity();
	    
	    List<Report> list;
	    if(groupId != null) {
	    	list = reportService.getReportsByGroup(groupId, Environment.getUserId());
	    } else {
	    	list = reportService.getAllReport();
	    }
	    
	    // 查出过去100天个人和整站访问Top10的报表名称
	    List<String> topSelf = getTops(true);
	    List<String> topX = getTops(false);
	    Long selfGroupId = -2L, topGroupId = -3L, newGroupId = -4L;
	    		
	    List<Report> result = new ArrayList<Report>();
	    if(topSelf.size() > 0) {
	    	result.add(new Report(selfGroupId, "您最近访问报表", null));
	    	result.addAll( cloneTops(selfGroupId, topSelf, list) );
	    }
	    if(topX.size() > 0) {
	    	result.add(new Report(topGroupId, "近期热门报表", null));
	    	result.addAll( cloneTops(topGroupId, topX, list) );
	    }
	    
	    result.add(new Report(newGroupId, "近期新出报表", null));
	    List<Report> latest = new ArrayList<Report>();
    	for(Report report : list) {
    		if( !report.isActive()  || report.getId().equals(groupId) )  continue;
 
    		if( !report.isGroup() 
    				&& report.getCreateTime().after(DateUtil.subDays(DateUtil.today(), 10))
    				&& StringUtil.hasCNChar(report.getName())) {
    			
    			latest.add(cloneReport(newGroupId, report));
    		}
    		
    		result.add(report); // 此处将list里的所有report及分组放入到result里
    	}
    	Collections.sort(latest, new Comparator<Report>() {
            public int compare(Report r1, Report r2) {
                return r2.getId().intValue() - r1.getId().intValue();
            }
        });
    	result.addAll(latest.size() > 3 ? latest.subList(0, 3) : latest);
       
        TreeEncoder treeEncoder = new TreeEncoder(result, new LevelTreeParser());
        treeEncoder.setNeedRootNode(false);
        print("SourceTree", treeEncoder);
    }
    
    private List<Report> cloneTops(Long topGroupId, List<String> topX, List<Report> list) {
    	List<Report> result = new ArrayList<Report>();
    	for(String cn : topX) {
    		for(Report rp : list) {
	    		if(cn.endsWith("-" + rp.getId()) && rp.isActive() && !rp.isGroup()) {
	        		result.add( cloneReport(topGroupId, rp) );
	        		break;
	    		}
	    	}
    	}
    	
    	return result;
    }

	private Report cloneReport(Long topGroupId, Report report) {
		Report clone = new Report();
		BeanUtil.copy(clone, report);
		clone.setParentId(topGroupId);
		return clone;
	}
 
    private List<String> getTops(boolean onlySelf) {
    	String sql = "select className name, count(*) value, max(l.accessTime) lastTime, max(methodCnName) cn " +
	    		" from dm_access_log l " +
	    		" where l.accessTime >= ? " + (onlySelf ? " and l.userId = ?" : "") +
	    		" group by className " +
	    		" order by " + (onlySelf ? "lastTime" : "value")  + " desc";
    	
	    Map<Integer, Object> params = new HashMap<Integer, Object>();
	    
	    // 日志量大的，不宜取太多天; 默认取3天
	    int historyDays = 3;
		try {
			historyDays = EasyUtils.obj2Int( ParamManager.getValue(PX.TOP_REPORT_LOG_DAYS, "3") ); 
		} catch (Exception e) {}
	    params.put(1, DateUtil.subDays(DateUtil.today(), historyDays));
	    
	    if(onlySelf) {
	    	params.put(2, Environment.getUserId());
	    }
	    
	    SQLExcutor ex = new SQLExcutor(false);
		ex.excuteQuery(sql, params , DMConstants.LOCAL_CONN_POOL);
	    
	    List<String> tops = new ArrayList<String>();
	    int max = onlySelf ? 5 : 3;
	    for( Map<String, Object> row : ex.result){
	    	if(tops.size() < max) {
	    		String reportName = (String) row.get("name");
	    		String reportCnName = (String) row.get("cn");
	    	    if(StringUtil.hasCNChar(reportCnName)) {
	    	    	tops.add(reportName);
	    	    }
	    	}
	    }
	    return tops;
    }

    // add 2014.12.17 检查用户的密码强度，太弱的话强制要求修改密码
	private void checkPwdSecurity() {
    	Object strengthLevel = null;
    	try {
    		Long operatorId = Environment.getUserId();
			IOperator operator = loginService.getOperatorDTOByID(operatorId);
			strengthLevel = operator.getAttributesMap().get("passwordStrength");
    	} catch(Exception e) {
    		// do nothing
     	}
    	if(strengthLevel != null && EasyUtils.obj2Int(strengthLevel) <= PasswordRule.LOW_LEVEL
    			&& SecurityUtil.getSecurityLevel() >= 4 ) {
			throw new BusinessException("您的密码过于简单，请点右上角【修改密码】菜单重置密码后，再进行访问！");
		}
	}
	
    @RequestMapping("/template")
    public void getReportTLs(HttpServletResponse response) {
    	StringBuffer sb = new StringBuffer("<actionSet>"); 
    	
    	// modules/btr or modules/wms 等目录下
    	String rtd = DMConstants.getReportTLDir();
 		File reportTLDir = new File(URLUtil.getWebFileUrl(rtd).getPath());
		List<File> files = FileHelper.listFilesByTypeDeeply("html", reportTLDir);
		int index = 1;
 		for (File file : files) {
			String treeName = "../../" + rtd;
			File parentFile = file.getParentFile();
			if( !parentFile.equals(reportTLDir) ) {
				treeName +=  "/" + parentFile.getName();
			}
			treeName +=  "/" + file.getName();
			
			sb.append("<treeNode id=\"").append(index++).append("\" name=\"").append(treeName).append("\"/>");
		}
 		
 		// dm/template 下
 		File delfaultDir = new File(URLUtil.getWebFileUrl(DMConstants.REPORT_TL_DIR_DEFAULT).getPath());
 		if( !delfaultDir.equals(reportTLDir) ) {
 			files = FileHelper.listFilesByTypeDeeply("html", delfaultDir);
 	 		for (File file : files) {
 				String treeName = "../../more/bi_template/" + file.getName();
 				sb.append("<treeNode id=\"").append(index++).append("\" name=\"").append(treeName).append("\"/>");
 			}
 		}
 		sb.append("</actionSet>");
		
        print("SourceTree", sb);
    }
    
    @RequestMapping("/groups")
    public void getAllReportGroups(HttpServletResponse response) {
        List<?> list = reportService.getAllReportGroups();
        TreeEncoder treeEncoder = new TreeEncoder(list, new StrictLevelTreeParser(Report.DEFAULT_PARENT_ID));
        treeEncoder.setNeedRootNode(true);
        print("SourceTree", treeEncoder);
    }
    
    @RequestMapping(value = "/detail/{type}")
    public void getReport(HttpServletRequest request, HttpServletResponse response, @PathVariable("type") int type) {
        String uri = null;
        if(Report.TYPE0 == type) {
            uri = DMConstants.XFORM_GROUP;
        } else {
            uri = DMConstants.XFORM_REPORT;
        }
        
        XFormEncoder xformEncoder;
        String reportIdValue = request.getParameter("reportId");
        
        if( reportIdValue == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            
            String parentIdValue = request.getParameter("parentId"); 
            if("_root".equals(parentIdValue)) {
            	parentIdValue = null;
            }
            
            Long parentId = parentIdValue == null ? Report.DEFAULT_PARENT_ID : EasyUtils.obj2Long(parentIdValue);
            map.put("parentId", parentId);
            map.put("type", type);
            map.put("param", "[\n{'label':'不要缓存','type':'hidden','name':'noCache','defaultValue':'true'}\n]");
            xformEncoder = new XFormEncoder(uri, map);
        } 
        else {
            Long reportId = EasyUtils.obj2Long(reportIdValue);
            Report report = reportService.getReport(reportId);
            xformEncoder = new XFormEncoder(uri, report);
        }
        
        if( Report.TYPE1 == type ) {
            try {
            	List<Param> datasources = ParamManager.getComboParam(PX.DATASOURCE_LIST);
                xformEncoder.fixCombo("datasource", datasources);	
            } catch (Exception e) {
            }
        }
 
        print("SourceInfo", xformEncoder);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveReport(HttpServletResponse response, Report report) {
        boolean isnew = (null == report.getId());
        reportService.saveReport(report);
        doAfterSave(isnew, report, "SourceTree");
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, @PathVariable("id") Long id) {
        reportService.delete(id);
        
        // 删除定时JOB，如果有的话
        String jobCode = "ReportJob-" + id;
		List<Param> jobParamItems = paramService.getParamsByParentCode(PX.TIMER_PARAM_CODE);
		if(jobParamItems != null) {
			for(Param temp : jobParamItems) {
				if(jobCode.equals(temp.getUdf1())) {
					paramService.delete(temp.getId());
				}
			}
		}
		
        printSuccessMessage();
    }

    @RequestMapping(value = "/disable/{id}/{disabled}", method = RequestMethod.POST)
    public void startOrStop(HttpServletResponse response, 
            @PathVariable("id") Long id, @PathVariable("disabled") int disabled) {
        
        reportService.startOrStop(id, disabled);
        printSuccessMessage();
    }
 
    @RequestMapping(value = "/sort/{startId}/{targetId}/{direction}", method = RequestMethod.POST)
    public void sort(HttpServletResponse response, 
            @PathVariable("startId") Long startId, 
            @PathVariable("targetId") Long targetId, 
            @PathVariable("direction") int direction) {
        
        reportService.sort(startId, targetId, direction);
        printSuccessMessage();
    }

    @RequestMapping(value = "/copy/{reportId}/{groupId}", method = RequestMethod.POST)
    public void copy(HttpServletResponse response, 
            @PathVariable("reportId") Long reportId, @PathVariable("groupId") Long groupId) {
        
        List<?> result = reportService.copy(reportId, groupId);
        TreeEncoder encoder = new TreeEncoder(result, new LevelTreeParser());
        encoder.setNeedRootNode(false);
        print("SourceTree", encoder);
    }

    @RequestMapping(value = "/move/{reportId}/{groupId}", method = RequestMethod.POST)
    public void move(HttpServletResponse response, 
            @PathVariable("reportId") Long reportId, @PathVariable("groupId") Long groupId) {
        
        reportService.move(reportId, groupId);
        printSuccessMessage();
    }
    
	@RequestMapping("/operations/{resourceId}")
    public void getOperations(HttpServletResponse response, @PathVariable("resourceId") Long resourceId) {
        List<String> list = PermissionHelper.getInstance().getOperationsByResource(resourceId,
                        ReportPermission.class.getName(), ReportResource.class);

        print("Operation", EasyUtils.list2Str(list));
    }
	
	
	@Autowired ParamService paramService;
	
	@RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public void saveJobParam(HttpServletResponse response, Long reportId, String configVal) {
		Param jobParam = paramService.getParam(PX.TIMER_PARAM_CODE);
		if(jobParam == null) {
			jobParam = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, 
	        		PX.TIMER_PARAM_CODE, "定时配置");
    	}
		
		Param jobParamItem = null;
		String jobCode = "ReportJob-" + reportId;
		List<Param> jobParamItems = paramService.getParamsByParentCode(PX.TIMER_PARAM_CODE);
		for(Param temp : jobParamItems) {
			if(jobCode.equals(temp.getUdf1())) {
				jobParamItem = temp;
				break;
			}
		}
		
		String value = ReportJob.class.getName() + " | " + configVal;
		if(jobParamItem == null) {
			String text = reportService.getReport(reportId).getName() + "-" + reportId;
			jobParamItem = ParamManager.addParamItem(jobParam.getId(), value, text, jobParam.getModality());
			jobParamItem.setUdf1(jobCode);
		}
		else {
			jobParamItem.setValue(value);
		}
		paramService.saveParam(jobParamItem);
        
        printSuccessMessage();
    }

	@RequestMapping(value = "/schedule", method = RequestMethod.GET)
	@ResponseBody
    public Object[] getJobParam(HttpServletResponse response, Long reportId) {
		String jobCode = "ReportJob-" + reportId;
		List<Param> jobParamItems = paramService.getParamsByParentCode(PX.TIMER_PARAM_CODE);
		if(jobParamItems != null) {
			for(Param temp : jobParamItems) {
				if(jobCode.equals(temp.getUdf1())) {
					String value = temp.getValue();
					return EasyUtils.split(value, "|");
				}
			}
		}
		return null;
    }
	
	/** 
	 * 前台下拉列表可用的数据服务 
	 * 所有带script且displayUri为空的report, 后台单独发布的服务（配置到param里）
	 * */
	@RequestMapping("/dataservice")
    public void getDateServiceList(HttpServletResponse response) {
        List<?> list = reportService.getAllReport();
        List<ITreeNode> result = new ArrayList<ITreeNode>();
        
        for(Object temp : list) {
        	Report report = (Report) temp;
        	String script = report.getScript();
        	if( EasyUtils.isNullOrEmpty(script) ) continue;
        	if( !EasyUtils.isNullOrEmpty(report.getDisplayUri()) ) continue;
        		
        	// 检查参数，数据服务的参数配置需要为空;
        	String param = report.getParam();
        	if( !EasyUtils.isNullOrEmpty(param) ) continue;
        	
        	// 检查是否包含了必要的关键字
        	Pattern p = Pattern.compile("text|name|pk", Pattern.CASE_INSENSITIVE); // 忽略大小写
    		Matcher m = p.matcher(script);
        	if( !m.find() ) continue;
    		
        	result.add( new DefaultTreeNode(report.getId(), report.getName()) );
        }
        
        // 后台Action单独发布的数据服务（配置在param里）
        Param param = paramService.getParam(PX.DATA_SERVICE_CONFIG);
        if(param != null && param.getValue() != null) {
        	String[] array = param.getValue().split(",");
        	for(String _ds : array) {
        		final String[] ds = _ds.split("\\|");
        		if(ds.length == 2) {
        			result.add( new DefaultTreeNode(ds[0], ds[1]) );
        		}
        	}
        }
        
        TreeEncoder treeEncoder = new TreeEncoder(result);
        treeEncoder.setNeedRootNode(false);
        print("DataServiceList", treeEncoder);
    }
	
}
