package com.boubei.tss.matrix;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.boubei.tss.framework.sso.Anonymous;
import com.boubei.tss.util.EasyUtils;

public class MatrixUtil {
	
	static Logger log = Logger.getLogger(MatrixUtil.class);
	
	static String matrix_host = "http://www.boubei.com";
	static String matrix_user = Anonymous._CODE;
	static String matrix_token = "0211bdae3d86730fe302940832025419";
	
	public static void remoteRecord(Object recordID, Map<String, String> params) throws HttpException, IOException {
    	String url = matrix_host + "/tss/xdata/api/rid/" + recordID;
    	PostMethod postMethod = new PostMethod(url);
    	postMethod.addParameter("uName", matrix_user);
		postMethod.addParameter("uToken", matrix_token);
		
		for(String key : params.keySet()) {
			String value = params.get(key);
			postMethod.addParameter(key, EasyUtils.obj2String(value));
		}
		
		HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod);
        if(statusCode == 200) {
            String soapResponseData = postMethod.getResponseBodyAsString();
            log.info("remote record result: " + soapResponseData);     
        } else {
        	log.info("remote record error: " + statusCode);
        }
    }

	public static String getIpAddress() {
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
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			log.debug("IP地址获取失败", e);
		}

		return "unknown.host";
	}
 
	  
}
