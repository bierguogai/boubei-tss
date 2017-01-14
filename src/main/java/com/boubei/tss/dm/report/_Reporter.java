package com.boubei.tss.dm.report;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.PX;
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.log.AccessLog;
import com.boubei.tss.dm.log.AccessLogRecorder;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.web.dispaly.grid.DefaultGridNode;
import com.boubei.tss.framework.web.dispaly.grid.GridDataEncoder;
import com.boubei.tss.framework.web.dispaly.grid.IGridNode;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.InfoEncoder;

/**
 * http://localhost:9000/dm/data/123/1/100
 * 
 * 默认情况下，相同登陆用户，执行相同查询条件的查询结果会被缓存3分钟。
 * 如要避开缓存，方式有两种: /tss/data/json/123?noCache=true 或者
 * {'label':'是否缓存','type':'hidden','name':'noCache','defaultValue':'true'}
 * 
 * 按用户缓存: /tss/data/json/123?uCache=true 或者 
 * {'label':'按用户缓存','type':'hidden','name':'uCache','defaultValue':'true'}
 * 按用户缓存使用于查询脚本里加了用户信息（session里的账号、组织、角色等信息，比如${loginName}），这类情形必须使用uCache或noCache
 */
@Controller
@RequestMapping( {"/display", "/data", "/api"} )
public class _Reporter extends BaseActionSupport {
    
    @Autowired private ReportService reportService;
    @Autowired private ILoginService loginService;
    
	@RequestMapping("/{reportId}/define")
    @ResponseBody
    public Object getReportParamDefine(@PathVariable("reportId") Long reportId) {
		Report report = reportService.getReport(reportId);
		
		boolean hasScript = !EasyUtils.isNullOrEmpty(report.getScript());
		String displayUri = report.getDisplayUri();
		return new Object[] {report.getName(), report.getParam(), displayUri, hasScript};
    }
	
    /**
     * 1、完成接口调用时令牌校验 & 自动登录
     * 2、根据每个报表的具体配置来确定使用具体的缓存策略。可分为：不缓存、按用户缓存、按参数缓存。
     */
    private Object getLoginUserId(Map<String, String> requestMap, Long reportId) {
    	/* 其它系统调用接口时，传入其在TSS注册的用户ID; 检查令牌，令牌有效则自动完成登陆 */
    	String uName  = requestMap.get("uName"), 
    		   uToken = requestMap.get("uToken");
    	if(uToken != null) {
    		String tokenList = ParamManager.getValue(PX.API_TOKEN_LIST, uToken); // 如果未启用API令牌发放
        	String cToken = InfoEncoder.string2MD5(reportId + ":" + uName);
    		if( cToken.equalsIgnoreCase(uToken) && tokenList.indexOf(uToken) >= 0 ) {
        		IOperator user = loginService.getOperatorDTOByLoginName(uName);
        		String token = TokenUtil.createToken(uToken, user.getId());
        		IdentityCard card = new IdentityCard(token, user);
        		Context.initIdentityInfo(card); 
        	}
    		else {
    			throw new BusinessException("令牌验证未获通过，调用接口失败。");
    		}
    	} 
    	
    	/* 如果传入的参数要求不取缓存的数据，则返回当前时间戳作为userID，以触发缓存更新。*/
    	Long userId;
    	if( "true".equals(requestMap.get("noCache")) ) {
    		userId = System.currentTimeMillis(); // 按时间戳缓存，白存了，永远无法再次命中
    	}
    	else if( "true".equals(requestMap.get("uCache")) ) {
    		userId = Environment.getUserId();  // 按【用户 + 参数】缓存
    	}
    	else {
    		userId = -1L; // 只按查询【参数】缓存
    	}
    	return userId;
    }
    
	// 注：通过tssJS.ajax能自动过滤params里的空值，jQuery发送的ajax请求则不能
    private Map<String, String> getRequestMap(HttpServletRequest request, boolean isGet) {
    	Map<String, String[]> parameterMap = request.getParameterMap();
    	Map<String, String> requestMap = new HashMap<String, String>();
    	boolean isJetty = "org.eclipse.jetty.server.Request".equals( request.getClass().getName() );
    	for(String key : parameterMap.keySet()) {
    		String[] values = parameterMap.get(key);
    		if(values != null && values.length > 0) {
    			String value;
				if(isGet && !isJetty ) { // tomcat7, (not jetty)
    				try {
    					value = new String(values[0].getBytes("ISO-8859-1"), "UTF-8"); 
    				} catch (UnsupportedEncodingException e) {
    					value = values[0];
    				}
    			}
    			else {
    				value = values[0];
    			}
				
    			requestMap.put( key, value );
    		}
    	}
    	
    	requestMap.remove("_time"); // 剔除jsonp为防止url被浏览器缓存而加的时间戳参数
    	requestMap.remove("jsonpCallback"); // jsonp__x,其名也是唯一的
    	
    	return requestMap;
    }
 
    @RequestMapping("/{reportId}/{page}/{pagesize}")
    public void showAsGrid(HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("reportId") Long reportId, 
            @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {
    	
    	long start = System.currentTimeMillis();
    	Map<String, String> requestMap = getRequestMap(request, false);
		SQLExcutor excutor = reportService.queryReport(reportId, requestMap, page, pagesize, getLoginUserId(requestMap, reportId));
    	
    	outputAccessLog(reportId, "showAsGrid", requestMap, start);
        
        List<IGridNode> temp = new ArrayList<IGridNode>();
        for(Map<String, Object> item : excutor.result) {
            DefaultGridNode gridNode = new DefaultGridNode();
            gridNode.getAttrs().putAll(item);
            temp.add(gridNode);
        }
        GridDataEncoder gEncoder = new GridDataEncoder(temp, excutor.getGridTemplate());
        
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(pagesize);
        pageInfo.setTotalRows(excutor.count);
        pageInfo.setPageNum(page);
        
        print(new String[] {"ReportData", "PageInfo"}, new Object[] {gEncoder, pageInfo});
    }
    
    @RequestMapping("/export/{reportId}/{page}/{pagesize}")
    public void exportAsCSV(HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("reportId") Long reportId, 
            @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {
        
    	long start = System.currentTimeMillis();
    	Map<String, String> requestMap = getRequestMap(request, true);
		SQLExcutor excutor = reportService.queryReport(reportId, requestMap, page, pagesize, getLoginUserId(requestMap, reportId));
		
		String fileName = reportId + "-" + start + ".csv";
        String exportPath;
        
        // 如果导出数据超过了pageSize（前台为导出设置的pageSize为50万），则不予导出并给与提示
		if(pagesize > 0 && excutor.count > pagesize) {
			List<Object[]> result = new ArrayList<Object[]>();
			result.add(new Object[] {"您当前查询导出的数据有" + excutor.count + "行, 超过了单次能导出行数的上限【" + pagesize + "行】，请缩短你的查询范围，分批导出。"});
			
			exportPath = DataExport.getExportPath() + "/" + fileName;
			DataExport.exportCSV(exportPath, result, Arrays.asList("result"));
		}
		else {
			// 先输出查询结果到服务端的导出文件中
			exportPath = DataExport.exportCSV(fileName, excutor.result, excutor.selectFields);
		}
        
        // 下载上一步生成的附件
        DataExport.downloadFileByHttp(response, exportPath);
        
        outputAccessLog(reportId, "exportAsCSV", requestMap, start);
    }
    
    /**
     * 将前台（一般为生成好的table数据）数据导出成CSV格式
     */
    @RequestMapping("/export/data2csv")
    @ResponseBody
    public String[] data2CSV(HttpServletRequest request, HttpServletResponse response) {
    	Map<String, String> requestMap = getRequestMap(request, false);
    	String name = requestMap.get("name");
    	String data = requestMap.get("data");
		
		String fileName = name + "-" + System.currentTimeMillis() + ".csv";
        String exportPath = DataExport.getExportPath() + "/" + fileName;
 
		// 先输出内容到服务端的导出文件中
        DataExport.exportCSV(exportPath, data);
        
        // 记录导出日志
 		Log excuteLog = new Log(name, Environment.getUserCode() + "导出了网页数据：" + fileName );
     	excuteLog.setOperateTable("网页数据导出");
         ((IBusinessLogger) Global.getBean("BusinessLogger")).output(excuteLog);
        
        return new String[] { fileName };
    }
    
    @RequestMapping("/download/{fileName}")
    public void download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        String basePath = DataExport.getExportPath();
        String exportPath = basePath + "/" + fileName + ".csv";
        DataExport.downloadFileByHttp(response, exportPath);
    }
    
    @RequestMapping("/dataexport/{x}")
    @ResponseBody
    public Object dataExport(@PathVariable("x") int x, String ds, String script) {
    	return DMUtil.spiritx(x, ds, script);
    }
    
    /**
     * report可能是report的ID 也 可能是 Name.
     * 注：一次最多能取10万行。
     */
    @RequestMapping("/json/{report}")
    @ResponseBody
    public Object showAsJson(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("report") String report) {
    	
    	/* 允许跨域访问。 经测试JQuery.ajax请求可以跨域调用成功，tssJS.ajax不行 */
    	response.addHeader("Access-Control-Allow-Origin", "*"); 
    	
    	Long reportId;
    	try {
    		reportId = Long.valueOf(report);
    	} catch(Exception e) {
    		reportId = reportService.getReportIdByName(report);
    	}
    	
    	if(reportId == null) {
    		throw new BusinessException("【" + report + "】数据服务不存在。");
    	}
    	
    	String jsonpCallback = request.getParameter("jsonpCallback"); // jsonp是用GET请求
    	Map<String, String> requestMap = getRequestMap(request, jsonpCallback != null);
    	
    	Object page = requestMap.get("page");
    	Object pagesize = requestMap.get("pagesize");
    	if(pagesize == null) {
    		pagesize = requestMap.get("rows");  // easyUI
    	}
    	if(pagesize == null) {
    		pagesize = 10*10000;
    	}
    	int _pagesize = EasyUtils.obj2Int(pagesize);
    	int _page = page != null ? EasyUtils.obj2Int(page) : 1;
    			
    	long start = System.currentTimeMillis();
        SQLExcutor excutor = reportService.queryReport(reportId, requestMap, _page, _pagesize, getLoginUserId(requestMap, reportId));
        
        // 对一些转换为json为报错的类型值进行预处理
        for(Map<String, Object> row : excutor.result ) {
        	for(String key : row.keySet()) {
        		Object value = row.get(key);
        		row.put(key, DMUtil.preTreatValue(value));
        	}
        }
        
        outputAccessLog(reportId, "showAsJson", requestMap, start);
        
        if(page != null) {
        	Map<String, Object> returlVal = new HashMap<String, Object>();
        	returlVal.put("total", excutor.count);
        	returlVal.put("rows", excutor.result);
        	return returlVal;
        }
        
        return excutor.result;
    }
 
    @RequestMapping("/jsonp/{report}")
    public void showAsJsonp(HttpServletRequest request, HttpServletResponse response, @PathVariable("report") String report) {
        // 如果定义了jsonpCallback参数，则为jsonp调用。示例参考：boubei-ui/JSONP.html
        String jsonpCallback = request.getParameter("jsonpCallback");
		if(jsonpCallback != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString;
			try {
				jsonString = objectMapper.writeValueAsString( showAsJson(request, response, report) );
			} catch (Exception e) {  
				jsonString = "";
      	    }  
			
        	print(jsonpCallback + "(" + jsonString + ")");
        }
    }
    
	/**
	 * 记录下报表的访问信息。
	 */
	private void outputAccessLog(Long reportId, String methodName, Map<String, String> requestMap, long start) {
		Report report = reportService.getReport(reportId);
		String reportName = report.getName();
		
		// 过滤掉定时刷新类型的报表
		boolean ignoreLog = ParamConstants.FALSE.equals(report.getNeedLog());
		if( ignoreLog ) return;
		
		String params = "";
		for(Entry<String, String> entry : requestMap.entrySet()) {
			params += entry.getKey() + "=" + entry.getValue() + ", ";
		}
        if (params != null && params.length() > 500) {
            params = params.substring(0, 500);
        }
		
		// 方法的访问日志记录成败不影响方法的正常访问，所以对记录日志过程中各种可能异常进行try catch
        try {
            AccessLog log = new AccessLog();
            log.setClassName("Report-" + reportId);
    		log.setMethodName( methodName );
    		log.setMethodCnName( reportName );
            log.setAccessTime( new Date(start) );
            log.setRunningTime( System.currentTimeMillis() - start );
            log.setParams(params);
            
            // 记录访问人，没有则记为匿名访问
            Long userId = Environment.getUserId();
            if(userId == null) {
            	userId = UMConstants.ANONYMOUS_USER_ID;
            }
			log.setUserId(userId);
			log.setIp( Environment.getClientIp() );

            AccessLogRecorder.getInstanse().output(log);
        } 
        catch(Exception e) {
        	log.error("记录报表【" + reportName + "." + methodName + "】访问日志时出错：" + e.getMessage());
        }
	}

}
