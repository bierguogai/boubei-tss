package com.boubei.tss.modules.license;

import java.util.Date;
import java.util.Properties;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.ConfigurableContants;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p> 授权许可 </p> 
 */
public final class License extends ConfigurableContants {
	
    /** 授权许可类型 */
    public enum LicenseType {
    	Evaluation,     /** 评测的 */
    	Commercial,     /** 商业的 */
    	Advanced        /** 高级的 */
    }
    
    public String owner; // 令牌授予人
    public String product;     // 产品
    public String version;     // 版本号
    public Date   expiry;      // 过期日期
    public String type; // license类型
    
    String signature;   // license签名
    
    static Properties licenseProps;
    
    /**
     * 把license的属性值拼成一个字符串，然后转身字节数组
     */
    public byte[] getFingerprint() {
        StringBuffer buf = new StringBuffer(100);
        buf.append(product).append(version).append(type);
        buf.append(DateUtil.format(expiry));
        buf.append( EasyUtils.obj2String(owner) );
        
        return buf.toString().getBytes();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("owner=" + owner).append("\n");
        buf.append("product=" + product).append("\n");
        buf.append("version=" + version).append("\n");
        buf.append("type=" + type).append("\n");
        buf.append("expiry=" + DateUtil.format(expiry)).append("\n");
        buf.append("signature=" + signature);
        return buf.toString();
    }

    /**
     * 读取许可文件
     * @param fileName
     */
    public static License fromConfigFile(String fileName) throws Exception {
    	licenseProps = init(fileName);
        
        License license = new License();
        license.owner = licenseProps.getProperty("owner");
        license.product = licenseProps.getProperty("product");
        license.version = licenseProps.getProperty("version");
        license.type = licenseProps.getProperty("type");
        
        String expiry = licenseProps.getProperty("expiry");
        license.expiry = DateUtil.parse(expiry);
        
        license.signature = licenseProps.getProperty("signature");
        
        return license;
    }
    
    // 检查当前系统里的license是否有效
    public static boolean validate() {
    	String appCode = Config.getAttribute("application.code").toLowerCase();
		String version = Config.getAttribute("application.version");
		return LicenseManager.getInstance().validateLicense(appCode, version);
    }
}