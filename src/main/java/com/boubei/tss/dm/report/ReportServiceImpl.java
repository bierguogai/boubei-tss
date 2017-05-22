package com.boubei.tss.dm.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.data.sqlquery.SOUtil;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.MacrocodeCompiler;

@Service("ReportService")
public class ReportServiceImpl implements ReportService {
	
	Logger log = Logger.getLogger(this.getClass());
    
    @Autowired ReportDao reportDao;
    
    public Report getReport(Long id, boolean auth) {
        Report report;
        if(auth) {
        	report = reportDao.getVisibleReport(id); // 如没有访问权限，将抛出异常
        } else {
        	report = reportDao.getEntity(id);
        }
        
        if(report == null) {
        	String errMsg = "数据服务【" + id + "】无法访问，可能已被删除。";
			throw new BusinessException(errMsg);
        }
        reportDao.evict(report);
        return report;
    }
    
    public Report getReport(Long id) {
        boolean auth;
        if( Environment.isRobot() ) { // 定时JOB
        	auth = false;
        } else {
        	auth = SecurityUtil.getLevel() >= 4;
        }
		return this.getReport(id, auth);
    }
    
	public Long getReportId(String fname, Object idOrName) {
		String hql = "select o.id from Report o where o." +fname+ " = ? order by o.decode";
		List<?> list = reportDao.getEntities(hql, idOrName); 
		if(EasyUtils.isNullOrEmpty(list)) {
			return null;
		}
		return (Long) list.get(0);
	}
	
	// 加userId以便于缓存
    public List<Report> getReportsByGroup(Long groupId, Long userId) {
        return reportDao.getChildrenById(groupId);
    }
    
    @SuppressWarnings("unchecked")
    public List<Report> getAllReport() {
        return (List<Report>) reportDao.getEntities("from Report o order by o.decode");
    }
    
    @SuppressWarnings("unchecked")
    public List<Report> getAllReportGroups() {
        return (List<Report>) reportDao.getEntities("from Report o where o.type = ? order by o.decode", Report.TYPE0);
    }

    public Report saveReport(Report report) {
        if ( report.getId() == null ) {
            Long parentId = report.getParentId();
            Report parent = reportDao.getEntity(parentId);
            if( (parent == null || parent.isActive() ) && report.isGroup() ) {
            	report.setDisabled( ParamConstants.TRUE ); // 报表默认为停用，组看父组的状态
            }
            
			report.setSeqNo(reportDao.getNextSeqNo(parentId));
            reportDao.create(report);
        }
        else {
        	reportDao.refreshEntity(report);
        }
        return report;
    }
    
    public Report delete(Long id) {
    	 Report report = getReport(id);
         List<Report> children = reportDao.getChildrenById(id, Report.OPERATION_DELETE); // 一并删除子节点
         return reportDao.deleteReport(report, children);
    }

    public void startOrStop(Long reportId, Integer disabled) {
        List<Report> list = ParamConstants.TRUE.equals(disabled) ? 
                reportDao.getChildrenById(reportId, Report.OPERATION_DISABLE) : reportDao.getParentsById(reportId);
        
        for (Report report : list) {
            report.setDisabled(disabled);
            reportDao.updateWithoutFlush(report);
        }
        reportDao.flush();
    }

    public void sort(Long startId, Long targetId, int direction) {
        reportDao.sort(startId, targetId, direction);
    }

    public List<Report> copy(Long reportId, Long groupId) {
        Report report = getReport(reportId);
        
        reportDao.evict(report);
        report.setId(null);
        report.setParentId(groupId);
        report.setSeqNo(reportDao.getNextSeqNo(groupId));
        report.setDisabled(ParamConstants.TRUE); // 新复制出来的节点都为停用状态
        
        report = reportDao.create(report);
        List<Report> list = new ArrayList<Report>();
        list.add(report);
        
        return list;
    }

    public void move(Long reportId, Long groupId) {
        List<Report> list  = reportDao.getChildrenById(reportId);
        Report reportGroup = reportDao.getEntity(groupId);
        for (Report temp : list) {
            if (temp.getId().equals(reportId)) { // 判断是否是移动节点（即被移动枝的根节点）
                temp.setSeqNo(reportDao.getNextSeqNo(groupId));
                temp.setParentId(groupId);
            }
            
            // reportGroup有可能是“全部”节点
            if (reportGroup != null && !reportGroup.isActive() ) {
                temp.setDisabled(ParamConstants.TRUE); // 如果目标根节点是停用状态，则所有新复制出来的节点也一律为停用状态
            }
            
            reportDao.moveEntity(temp);
        }
    }
    
    @SuppressWarnings("unchecked")
  	public SQLExcutor queryReport(Long reportId, Map<String, String> requestMap, 
  			int page, int pagesize, Object loginUserId) {
    	
    	Report report = this.getReport(reportId);
    	
		String paramsConfig = report.getParam();
		String reportScript = report.getScript();
		if( EasyUtils.isNullOrEmpty(reportScript) ) {
			return new SQLExcutor();
		}
          
		// 宏代码池
      	Map<String, Object> fmDataMap = DMUtil.getFreemarkerDataMap();
      	
		/* 先预解析，以判断request参数是否用做了宏代码。后续还将有一次解析，以支持宏嵌套。 
		 * eg： ${GetWHListByLogonUser} --> param里的script宏（... user_id = ${fromUserId}) ... ）
		 * 这里先把  ${GetWHListByLogonUser} 解析出来，下面会接着解析${fromUserId} */
      	reportScript = MacrocodeCompiler.runLoop(reportScript, fmDataMap, true); 
      	
      	// 加入所有request请求带的参数（TODO 有注入隐患，如：script = ${xx} 等）
      	fmDataMap.putAll(requestMap);
      	
      	// 过滤掉用于宏解析（ ${paramX} ）后的request参数
     	Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		
		if( !EasyUtils.isNullOrEmpty(paramsConfig) ) {
      		List<LinkedHashMap<Object, Object>> list;
      		try {  
      			paramsConfig = paramsConfig.replaceAll("'", "\"");
  				list = new ObjectMapper().readValue(paramsConfig, List.class);  
      	        
      	    } catch (Exception e) {  
      	        throw new BusinessException( report + "参数配置JSON格式存在错误：" + e.getMessage() );
      	    }  
      		
      		for(int i = 0; i < list.size(); i++) {
  	        	LinkedHashMap<Object, Object> map = list.get(i);
  	        	
  	        	int index = i + 1;
  	        	String paramKy = "param" + index;
  	        	String paramValue = requestMap.get(paramKy);
				if ( EasyUtils.isNullOrEmpty(paramValue) ) {
					if( "false".equals(map.get("nullable")) ) {
						throw new BusinessException("参数【" + map.get("label") + "】不能为空。");
					}
					continue;
				}
				
				paramValue = paramValue.trim();
				Object paramType = map.get("type");
				Object isMacrocode = map.get("isMacrocode"); // 如一些只用于多级下拉联动的参数，可能并不用于script
				
				// 对paramValue进行检测，防止SQL注入
				paramValue = DMUtil.checkSQLInject(paramValue);
				
				// 判断是否作为宏定义用于freemarker的模板解析
				if ( reportScript.indexOf("${" + paramKy + "}") > 0 ) {
					isMacrocode = "true";		
				}
				else if(reportScript.indexOf("if " + paramKy) > 0
						&& reportScript.indexOf("if " + paramKy + "??") < 0) {
					// <#if param1==1> or <#elseif param1==1>
					// eg1: <#if param1==1> group by week </#if>  --> is macrocode: true 
					// eg2: <#if param1??> createTime > ? </#if>  --> is macrocode: false
					isMacrocode = "true";
				}
				
				// 隐藏类型的参数
				if("hidden".equals(paramType)) {
					isMacrocode = "true";
		  		}
				
				// 将相对时间解析成绝对时间（today - 2 --> 2014-7-20）
				if (Pattern.compile("^today[\\s]*-[\\s]*\\d{1,4}").matcher(paramValue).matches()) {
					int deltaDays = Integer.parseInt(paramValue.split("-")[1].trim());
					Date today = DateUtil.noHMS(new Date());
					paramValue = DateUtil.format(DateUtil.subDays(today, deltaDays));
				} 
				
				// 将相对时间解析成绝对时间（today + 2 --> 2014-7-24）
				if (Pattern.compile("^today[\\s]*\\+[\\s]*\\d{1,4}").matcher(paramValue).matches()) {
					int deltaDays = Integer.parseInt(paramValue.split("\\+")[1].trim());
					Date today = DateUtil.noHMS(new Date());
					paramValue = DateUtil.format(DateUtil.addDays(today, deltaDays));
				} 

				// 处理in查询的条件值，为每个项加上单引号
				if (reportScript.indexOf("in (${" + paramKy + "})") > 0 ||
						reportScript.indexOf("IN (${" + paramKy + "})") > 0) {
					
					paramValue = SOUtil.insertSingleQuotes(paramValue); 
				}
				// 判断参数是否只用于freemarker解析
				else if ( !"true".equals(isMacrocode) ) {
					Object value = DMUtil.preTreatValue(paramValue, paramType);
					paramsMap.put(paramsMap.size() + 1, value);
				}
				
				fmDataMap.put(paramKy, paramValue);
  	        }
      	}
      	
        // 结合 requestMap 进行 freemarker解析 sql，允许指定sql预处理类。
      	fmDataMap.put("report.info", report.toString()); // 用于解析出错时定位report
      	reportScript = DMUtil.customizeParse(reportScript, fmDataMap);
          
		SQLExcutor excutor = new SQLExcutor();
		String datasource = report.getDatasource();
		try {
			excutor.excuteQuery(reportScript, paramsMap, page, pagesize, datasource);
		} catch (Exception e) {
			String exMsg = e.getMessage();
			log.error( report + exMsg + ", params: " + requestMap + ", visitor: " + Environment.getUserName());
			throw new BusinessException(exMsg);
		}

		return excutor;
  	}
 
}