package com.boubei.tss.matrix;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.Config;
import com.boubei.tss.modules.license.License;
import com.boubei.tss.modules.license.LicenseAction;
import com.boubei.tss.modules.license.LicenseFactory;
import com.boubei.tss.modules.license.LicenseManager;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/**
 * TSS安装监听器
 * 收集安装端信息：安装应用、安装版本、安装人标识、安装时间、安装ip等
 * 
 * 用户也可以直接打开系统里的注册页面来完成注册。
 * 安装服务器不一定能访问的到boubei.com，在前端Admin登录时完成注册？
 * 
 * 配置参见tss-bi spring.xml
 * <bean id="installListener" class="com.boubei.tss.matrix.InstallListener"/>
 */
public class InstallListener {
	
	Logger log = Logger.getLogger(this.getClass());
	
	static String product = Config.getAttribute("application.code").toLowerCase();
	static String version = Config.getAttribute("application.version");
	static License license;

	public InstallListener() {
		try {
			init();
		} 
		catch(Exception e) {
			// log.error(e.getMessage(), e);
		}
	}
	
	public static String licenseOwner() {
		String owner = "unkown";
		try {
			owner = license.owner;
		} catch(Exception e) { }
		
		return owner;
	}
	
	private void init() throws Exception {
		// 检查license是否存在，不存在先生成一个评测版License
		LicenseManager licenseManager = LicenseManager.getInstance();
		if( !License.validate() ) { 
			initLicense();
			
			// 往boubei.com注册该新建的license
			license = licenseManager.getLicense(product, version);
			registerLicense(license);
		}
	}
	
	private void registerLicense(License license) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("user", license.owner);
		map.put("type", "License");
		map.put("resource", license.product +"|"+ license.version);
		map.put("token", license.toString());
		map.put("expiretime", DateUtil.format(license.expiry));
		
		MatrixUtil.remoteRecord(60, map); // 注册令牌
		
		map = new HashMap<String, String>();
		map.put("product", product + "-" + Config.getAttribute("environment"));
		map.put("appversion", version);
		map.put("packagetime", Config.getAttribute("last.package.time"));
		map.put("owner", license.owner);
		map.put("ip", MatrixUtil.getIpAddress());
		map.put("time", DateUtil.formatCare2Second(new Date()));
		
		Properties props = System.getProperties();   
		map.put("osname", props.getProperty("os.name"));
		map.put("osversion", props.getProperty("os.version"));
		map.put("javaversion", props.getProperty("java.version"));
		map.put("javavendor", props.getProperty("java.vendor"));
		map.put("javahome", props.getProperty("java.home"));
		
		Map<String, String> env = System.getenv();  
		map.put("sysuser", (String) EasyUtils.checkNull(env.get("USERNAME"), env.get("USER")));
		map.put("computer", env.get("COMPUTERNAME"));
		map.put("domain", env.get("USERDOMAIN"));
		
		log.info(map);
		
		MatrixUtil.remoteRecord(65, map); // 注册安装信息
	}

	/**
	 * 第一次启动给设置一个License
	 */
	private void initLicense() throws Exception {
        // 生成公钥、私钥对。
		LicenseFactory.generateKey();
        
		Date now = new Date();
		String expiry = DateUtil.format(DateUtil.addDays(DateUtil.noHMS(now), 365*10+2));
		String owner = String.valueOf( now.getTime() );
		String type = License.LicenseType.Evaluation.toString();
		Map<String, String> map = new LicenseAction().genLicense(owner, product, version, expiry, type);
		String license = map.get("license");
		FileHelper.writeFile(new File(LicenseFactory.LICENSE_DIR +"/"+ product + ".license"), license);
	}
}
