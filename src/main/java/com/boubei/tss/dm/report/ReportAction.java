/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

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

import com.boubei.tss.EX;
import com.boubei.tss.PX;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.permission.ReportPermission;
import com.boubei.tss.dm.report.permission.ReportResource;
import com.boubei.tss.dm.report.timer.ReportJob;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.display.tree.DefaultTreeNode;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.LevelTreeParser;
import com.boubei.tss.framework.web.display.tree.StrictLevelTreeParser;
import com.boubei.tss.framework.web.display.tree.TreeEncoder;
import com.boubei.tss.framework.web.display.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.StringUtil;
import com.boubei.tss.util.URLUtil;

/**
 * [ {'label':'不要缓存','type':'hidden','name':'noCache','defaultValue':'true'} ]
 */
@Controller
@RequestMapping("/auth/rp")
public class ReportAction extends BaseActionSupport {
    
    @Autowired private ReportService reportService;
    @Autowired private RecordService recordService;
    @Autowired private ICommonService commonService;
    
    @RequestMapping("/")
    public void getAllReport(HttpServletResponse response) {
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
    	List<Report> list;
    	if(Report.DEFAULT_PARENT_ID.equals(groupId)) {
    		list = reportService.getAllReport();
    	} else {
    		list = reportService.getReportsByGroup(groupId, Environment.getUserId());
    	}
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
    	result.add(new Report(selfGroupId, "您最近访问报表", null));
    	result.addAll( cloneTops(selfGroupId, topSelf, list) );

    	result.add(new Report(topGroupId, "近期热门报表", null));
    	result.addAll( cloneTops(topGroupId, topX, list) );
	    
	    result.add(new Report(newGroupId, "近期新出报表", null));
	    List<Report> lastest = new ArrayList<Report>();
    	for(Report report : list) {
    		if( !report.isActive()  || report.getId().equals(groupId) )  continue;
 
    		if( !report.isGroup() 
    				&& report.getCreateTime().after(DateUtil.subDays(DateUtil.today(), 10))
    				&& StringUtil.hasCNChar(report.getName())) {
    			
    			lastest.add(cloneReport(newGroupId, report));
    		}
    		
    		result.add(report); // 此处将list里的所有report及分组放入到result里
    	}
    	sortLastest(result, lastest);
       
        TreeEncoder treeEncoder = new TreeEncoder(result, new LevelTreeParser());
        treeEncoder.setNeedRootNode(false);
        print("SourceTree", treeEncoder);
    }

	void sortLastest(List<Report> result, List<Report> lastest) {
		Collections.sort(lastest, new Comparator<Report>() {
            public int compare(Report r1, Report r2) {
                return r2.getId().intValue() - r1.getId().intValue();
            }
        });
    	result.addAll(lastest.size() > 3 ? lastest.subList(0, 3) : lastest);
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
	    
	    SQLExcutor ex = new SQLExcutor();
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
	
    @RequestMapping("/template")
    public void getReportTLs(HttpServletResponse response) {
    	StringBuffer sb = new StringBuffer("<actionSet>"); 
    	
    	// pages or more/bi4 等目录下
    	String rtd = DMConstants.getReportTLDir();
 		File reportTLDir = new File(URLUtil.getWebFileUrl(rtd).getPath());
		List<File> files = FileHelper.listFilesByTypeDeeply("html", reportTLDir);
		int index = 1;
 		for (File file : files) {
			String filePath = file.getPath();
			String treeName = filePath.substring( Math.max(0, filePath.indexOf("/tss/" + rtd)) );
			sb.append("<treeNode id=\"").append(index++).append("\" name=\"").append(treeName).append("\"/>");
		}
 		
 		// more/bi_template 下
 		File delfaultDir = new File(URLUtil.getWebFileUrl(DMConstants.REPORT_TL_DIR_DEFAULT).getPath());
 		if( !delfaultDir.equals(reportTLDir) ) {
 			files = FileHelper.listFilesByTypeDeeply("html", delfaultDir);
 	 		for (File file : files) {
 				sb.append("<treeNode id=\"").append(index++)
 				  .append("\" name=\"").append("/tss/more/bi_template/" + file.getName()).append("\"/>");
 			}
 		}
 		
 		sb.append("</actionSet>");
        print("SourceTree", sb);
    }
    
    @RequestMapping("/groups")
    public void getAllReportGroups(HttpServletResponse response) {
        List<?> list = reportService.getAllReportGroups();
        TreeEncoder treeEncoder = new TreeEncoder(list, new StrictLevelTreeParser(Report.DEFAULT_PARENT_ID));
        treeEncoder.setNeedRootNode(false);
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
            map.put("needLog", ParamConstants.TRUE);
            xformEncoder = new XFormEncoder(uri, map);
        } 
        else {
            Long reportId = EasyUtils.obj2Long(reportIdValue);
            Report report = reportService.getReport(reportId);
            xformEncoder = new XFormEncoder(uri, report);
        }
        
        if( Report.TYPE1 == type ) {
        	DMUtil.setDSList(xformEncoder);
        }
 
        print("SourceInfo", xformEncoder);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveReport(HttpServletResponse response, Report report) {
        boolean isnew = (null == report.getId());
        if(isnew) {
        	reportService.createReport(report);
        } else {
        	reportService.updateReport(report);
        }
        
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
    
    // 设置报表是否可订阅
    @RequestMapping(value = "/mailable/{id}/{state}", method = RequestMethod.POST)
    public void subscribe(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("state") int state) {
        Report report = reportService.getReport(id);
        if( EasyUtils.isNullOrEmpty(report.getScript()) ) {
        	throw new BusinessException(EX.DM_19);
        }
        report.setMailable(state);
        reportService.updateReport(report);
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
    public void saveJobParam(HttpServletResponse response, Long reportId, boolean self, String configVal) {
		Param jobParam = paramService.getParam(PX.TIMER_PARAM_CODE);
		if(jobParam == null) {
			jobParam = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, PX.TIMER_PARAM_CODE, "定时配置");
    	}
		
		String value = ReportJob.class.getName() + " | " + configVal;
		
		Param jobParamItem = getJobParam(reportId, self);
		if(jobParamItem == null) {
			Report report = reportService.getReport(reportId);
			String jobCode = "ReportJob-" + reportId + (self ? "-" + Environment.getUserId() : "");
			String text = report.getName() + (self ? "-" + Environment.getUserCode() : "");
			jobParamItem = ParamManager.addParamItem(jobParam.getId(), value, text, jobParam.getModality());
			jobParamItem.setUdf1(jobCode);
			
			// 可推送的报表自动设置为允许订阅
			report.setMailable(ParamConstants.TRUE);
	        reportService.updateReport(report);
		}
		else {
			jobParamItem.setValue(value);
		}
		paramService.saveParam(jobParamItem);
        
        printSuccessMessage("订阅成功");
    }

	@RequestMapping(value = "/schedule", method = RequestMethod.GET)
	@ResponseBody
    public Object[] getJobParam(HttpServletResponse response, Long reportId, boolean self) {
		Param jobParam = getJobParam(reportId, self);
		if(jobParam != null) {
			String value = jobParam.getValue();
			return EasyUtils.split(value, "|");
		}
		return null;
    }
	
	private Param getJobParam( Long reportId, boolean self) {
		String jobCode = "ReportJob-" + reportId + (self ? "-" + Environment.getUserId() : "");
		List<Param> jobParamItems = paramService.getParamsByParentCode(PX.TIMER_PARAM_CODE);
		if(jobParamItems != null) {
			for(Param param : jobParamItems) {
				if(jobCode.equals(param.getUdf1())) {
					return param;
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/schedule", method = RequestMethod.DELETE)
	@ResponseBody
    public void delJobParam(HttpServletResponse response, Long reportId, boolean self) {
		Param jobParam = getJobParam(reportId, self);
		if(jobParam != null) {
			paramService.delete(jobParam.getId());
		}
		printSuccessMessage("成功取消邮件推送");
    }
	
	/** 
	 * 前台下拉列表可用的数据服务 
	 * 所有带script且displayUri为空的report, 后台单独发布的服务（配置到param里）
	 * */
	@RequestMapping("/dataservice")
    public void getDataServiceList(HttpServletResponse response) {
		List<ITreeNode> result = new ArrayList<ITreeNode>();
		
		// 数据表服务
		List<Record> records = recordService.getRecordables();
		for(Record dt : records) {
			String define = dt.getDefine() + "";
			Pattern p = Pattern.compile("name'|'value'", Pattern.CASE_INSENSITIVE); // 忽略大小写
    		Matcher m = p.matcher(define);
        	if( m.find() ) {
        		String nodeName = dt.getName();
        		String url = "/tss/auth/xdata/json/" + dt.getTable() + "/1";
        		try {
        			Record group = recordService.getRecord( dt.getParentId() );
        			nodeName += "(" +group.getName()+ ")";
        		} 
        		catch(Exception e) { }
        		
        		result.add( new DefaultTreeNode(url, nodeName) );
        	}
		}
		
		// 数据服务
        List<?> reports = reportService.getAllReport();
        for(Object temp : reports) {
        	Report report = (Report) temp;
        	String script = report.getScript();
        	if( EasyUtils.isNullOrEmpty(script) ) continue;
        	if( !EasyUtils.isNullOrEmpty(report.getDisplayUri()) ) continue;
        		
        	// 检查参数，数据服务的参数不超过一个;
        	String param = EasyUtils.obj2String( report.getParam() );
        	if( param.indexOf("label") != param.lastIndexOf("label") ) continue;
        	
        	// 检查是否包含了必要的关键字
        	Pattern p = Pattern.compile("text|name|pk", Pattern.CASE_INSENSITIVE); // 忽略大小写
    		Matcher m = p.matcher(script);
        	if( m.find() ) {
        		result.add( new DefaultTreeNode(report.getId(), report.getName()) );
        	}
        }
        
        // 后台Action单独发布的数据服务（配置在param里，eg：/tss/service/xx|XX服务,/tss/service/yy|YY服务）
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
	
	// 报表收藏
	@RequestMapping(value = "/collection/{reportId}/{state}", method = RequestMethod.POST)
	public void collectReport(HttpServletResponse response, 
			@PathVariable("reportId") Long reportId, @PathVariable("state") boolean state) {
		
		String hql = "from ReportUser ru where ru.userId=? and ru.reportId=? and ru.type=1";
		Long userId = Environment.getUserId();
		List<?> list = commonService.getList(hql, userId, reportId);
		ReportUser ru;
		if(list.isEmpty() && state) { // 收藏
			ru = new ReportUser(userId, reportId);
			ru.setType(1);
			commonService.create(ru);
		}
		if( !list.isEmpty() && !state) { // 取消收藏
			ru = (ReportUser) list.get(0);
			commonService.delete(ReportUser.class, (Long) ru.getPK());
		}
		
		printSuccessMessage(state ? "已成功收藏" : "已取消收藏");
	}
	
	// 收藏的报表
	@RequestMapping(value = "/collection", method = RequestMethod.GET)
	@ResponseBody
	public List<?> queryCollectReports() {
		String hql = "select r.id, r.name from Report r, ReportUser ru " +
				" where r.id = ru.reportId and ru.userId = ? and ru.type=1 and r.disabled<>1 ";
		return commonService.getList(hql, Environment.getUserId());
	}
	
	// 报表点赞|差评
	@RequestMapping(value = "/zan/{reportId}/{type}", method = RequestMethod.POST)
	public void zanReport(HttpServletResponse response,
			@PathVariable Long reportId, @PathVariable Integer type) {
		
		String hql = "from ReportUser ru where ru.userId=? and ru.reportId=? and ru.type=?";
		Long userId = Environment.getUserId();
		List<?> list = commonService.getList(hql, userId, reportId, type);
		if( list.isEmpty() ) {
			ReportUser ru = new ReportUser(userId, reportId);
			ru.setType(type);
			commonService.create(ru);
			printSuccessMessage();
		} 
	}
	
	@RequestMapping(value = "/zan/{reportId}/{type}", method = RequestMethod.GET)
	@ResponseBody
	public Object countZan(@PathVariable Long reportId, @PathVariable Integer type) {
		String hql = "select count(*) from ReportUser ru where ru.reportId=? and ru.type=?";
		return commonService.getList(hql, reportId, type).get(0);
	}
}
