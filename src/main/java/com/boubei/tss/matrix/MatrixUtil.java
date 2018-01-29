package com.boubei.tss.matrix;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.util.EasyUtils;

public class MatrixUtil {
	
	static Logger log = Logger.getLogger(MatrixUtil.class);
	
	static String MATRIX_CALL = "matrix_call";
	
	private static PostMethod genPostMethod(String url) {
		String[] matrixInfos = ParamConfig.getAttribute(MATRIX_CALL, "http://www.boubei.com,ANONYMOUS,0211bdae3d86730fe302940832025419").split(",");
		PostMethod postMethod = new PostMethod(matrixInfos[0] + url);
    	postMethod.addParameter("uName", matrixInfos[1]);
		postMethod.addParameter("uToken", matrixInfos[2]);
		
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		return postMethod;
	}
	
	public static void remoteRecord(Object recordID, Map<String, String> params) {
    	String url = "/tss/xdata/api/rid/" + recordID;
    	PostMethod postMethod = genPostMethod(url);
		
		for(String key : params.keySet()) {
			String value = params.get(key);
			postMethod.addParameter(key, EasyUtils.obj2String(value));
		}
		
		exePostMethod(postMethod);
    }
	
	public static void remoteRecordBatch(Object recordID, String data) {
    	String url = "/tss/xdata/api/cud/" + recordID;
    	PostMethod postMethod = genPostMethod(url);
		postMethod.addParameter("csv", data);
		
		exePostMethod(postMethod); 
    }

	public static void exePostMethod(PostMethod postMethod) {
		HttpClient httpClient = new HttpClient();
		
		int statusCode = 0;
		String rtMsg = null;
		try {
	        statusCode = httpClient.executeMethod(postMethod);
	        rtMsg = postMethod.getResponseBodyAsString();
		} 
		catch(Exception e) {
			rtMsg = e.getMessage();
		} 
		log.info("remote record result: " + statusCode + ", " + rtMsg);
	}

	public static String getIpAddress() {
		String rtip = "unknown.host";
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				} else {
					Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ip = addresses.nextElement();
						if (ip != null && ip instanceof Inet4Address) {
							rtip = ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) { }

		return rtip;
	}
	  
}
