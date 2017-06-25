package com.boubei.tss;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.Anonymous;
import com.boubei.tss.modules.license.License;
import com.boubei.tss.modules.license.LicenseAction;
import com.boubei.tss.modules.license.LicenseFactory;
import com.boubei.tss.modules.license.LicenseManager;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.FileHelper;

/**
 * TSS安装监听器，定时收集安装端信息：
 * ip、安装版本、使用情况：创建多少报表、录入表资源、登陆次数、登录用户数、分时段访问统计等，
 * 
 * 集中发往boubei.com，方式：
 * 1、通过前端 JS动态挂载 发送
 * 2、通过httpproxy代理转发，内置一个 BBI 的Appserver，指向www.boubei.com/tss
 * 3、后台JOB定时转发、通过Recorder的API、不要用远程接口
 */
public class InstallListener {

	String product = Config.getAttribute("application.code").toLowerCase();
	String version = Config.getAttribute("application.version");
	
	public InstallListener() {
		try {
			init();
		} 
		catch(Exception e) { }
	}
	
	private void init() throws Exception {
		// 检查license是否存在，不存在先生成一个评测版License
		LicenseManager licenseManager = LicenseManager.getInstance();
		License license = licenseManager.getLicense(product, version);
		if( !License.validate() ) { 
			initLicense();
			
			// 往boubei.com注册该新建的license
			license = licenseManager.getLicense(product, version);
			registerLicense(license);
		}
		
		String owner = license.owner; // 往后围绕owner记录统计信息
		
		// TODO 用户在外网登录时，往boubei.com发送部署BI的域名IP等信息
	}

	private void registerLicense(License license) throws Exception {
		PostMethod postMethod = new PostMethod("http://www.boubei.com/tss/auth/xdata/rid/" + 60);
		postMethod.addParameter("uName", Anonymous._CODE);
		postMethod.addParameter("uToken", "");
		
		postMethod.addParameter("user", license.owner);
		postMethod.addParameter("type", "License");
		postMethod.addParameter("resource", license.product +"|"+ license.version);
		postMethod.addParameter("token", license.toString());
		postMethod.addParameter("expiretime", DateUtil.format(license.expiry));

        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod);
        if(statusCode == 200) {
            String soapResponseData = postMethod.getResponseBodyAsString();
            System.out.println("返回结果: \n" + soapResponseData);     
        }
	}

	/**
	 * 第一次启动给设置一个License
	 * @throws Exception 
	 */
	private void initLicense() throws Exception {
		String expiry = "2099-12-31";
		String owner = String.valueOf( System.currentTimeMillis() );
		String type = License.LicenseType.Evaluation.toString();
		Map<String, String> map = new LicenseAction().genLicense(owner, product, version, expiry, type);
		String license = map.get("license");
		FileHelper.writeFile(new File(LicenseFactory.LICENSE_DIR +"/"+ product + ".license"), license);
	}
}
