package com.boubei.tssx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.Global;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.util.EasyUtils;

public class MonitorUtil {
	
	static final String MONITORING_RECEIVERS = "Monitoring-Receivers";

	static String[] getReceiver() {
		try {
			return ParamManager.getValue(MONITORING_RECEIVERS).split(",");
		} catch (Exception e) {
			return "boubei@163.com".split(",");
		}
	}
	
	public static void recordErrLog(String typeCode, String content) {
		Log errLog = new Log( typeCode, content );
		errLog.setOperateTable("Monitor-Err");
		
		try {
			((IBusinessLogger) Global.getBean("BusinessLogger")).output(errLog);
		} 
		catch (Exception e) {
			System.out.println(errLog);
		}
	}
	
	static boolean testDBConn(String ds) {
		try {
			SQLExcutor.query(ds, "select 12 from dual");
		} catch(Exception e) {
			recordErrLog("数据源", "数据源【" +ds+ "】测试连接不成功, " + e.getMessage());
			return false;
		}
		return true;
	}
	
	static Date queryMaxTime4MySQL(String table, String field, String ds) {
		String sql = "select max(" +field+ ") mt from " +table+ " where " +field+ " > DATE_SUB(now(), INTERVAL 1 DAY)";
		List<Map<String, Object>> result = SQLExcutor.query(ds, sql);
		if( !result.isEmpty() ) {
			return (Date) result.get(0).get("mt");
		}
		return null;
	}
	
	static long calculateTimeDelta(Date time1, Date time2) {
		return (time2.getTime() - time1.getTime()) / (1000*60); // 相差分钟数
	}

	static String visitHttpUrl(String url) {
		try {
			GetMethod method = new GetMethod(url);
			HttpClient httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == 200) {
				String responseBody = method.getResponseBodyAsString();
				String ret = new String(responseBody.getBytes("UTF-8"));
				return ret;
			} else {
				System.out.println("调用失败！错误码：" + statusCode + ", " + url);
			}
		} catch(Exception e) {
			recordErrLog("Tomcat & Apache", "URL【" +url+ "】访问异常，" + e.getMessage());
		}
		return null;
	}
	
	static void monitoringRestfulUrl(String url, String expectWord) {
		String ret = MonitorUtil.visitHttpUrl(url);
		if(ret == null 
				|| (!ret.startsWith("[") && !ret.endsWith("]"))
				|| (!EasyUtils.isNullOrEmpty(expectWord) && ret.indexOf(expectWord) < 0) 
			) {
			recordErrLog("Restful Url", "URL【" +url+ "】访问结果异常，ret = " + ret);
		}
	}
	
	static void monitoringApache(String host) {
		String url = "http://" +host+ "/balancer-manager";
		String ret = MonitorUtil.visitHttpUrl(url);
		if(ret != null && ret.indexOf("Init Err") > 0 ) {
			MonitorUtil.recordErrLog("Apache", host + "里的Tomcat服务有异常");
		}
	}
	
	static void monitoringMySQL(String master, String slave) {
		boolean r1 = MonitorUtil.testDBConn(master);
		boolean r2 = MonitorUtil.testDBConn(slave);
		
		if( !r1 || !r2 ) return;
		
		Date mt1 = MonitorUtil.queryMaxTime4MySQL("dm_access_log", "accessTime", master);
		Date mt2 = MonitorUtil.queryMaxTime4MySQL("dm_access_log", "accessTime", slave);
		
		long delta;
		if( mt2 != null && mt1 != null ) {
			delta = MonitorUtil.calculateTimeDelta(mt1, mt2);
		} else {
			delta = 24*60;
		}
			
		if( delta >= 30 ) {
			MonitorUtil.recordErrLog("MySQL-DB", slave + "库中【dm_access_log】表的延时时间达到了【" + delta + "】分钟");
		}
	}
}
