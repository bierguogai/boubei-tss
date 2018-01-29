package com.boubei.tss.modules.license;

import java.io.File;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/** 
 * <pre> 
 * 1、应用程序可以创建以及验证绑定给用户、系统等实体的license。
 * 2、licenses可以是永久性的或者临时性的（在某个特定时期内有效）
 * 3、共享应用程序可以配置试用期licenses
 * 
 * 4、licenses的验证由JAVA Security API提供的数字签名机制来实现。
 *    通过生成公钥/私钥对来分别对licenses进行签名和校验。
 * 
 * 5、license安装模块需要用特殊机制对其进行保护，以防被反编译轻易破解。
 *    可以使用java代码编译混淆器、自定义类装载器来实现。
 * </pre>
 */
public final class LicenseManager {

    private final Log log = LogFactory.getLog(getClass());
    
    private static LicenseManager instance = new LicenseManager();
    
    private LicenseManager(){ }
    
    public static synchronized LicenseManager getInstance() {
        return instance;
    }
    
    private List<License> licenses;

    public void loadLicenses() {
        if( !EasyUtils.isNullOrEmpty(licenses) ) return;
        
        licenses = new ArrayList<License>();
        String files[] = new File(LicenseFactory.LICENSE_DIR).list();
        for(int i = 0; i < files.length; i++){
            String filename = files[i];
            File file = new File(LicenseFactory.LICENSE_DIR, filename);
            if(file.isDirectory() || !filename.endsWith(".license")) {
                continue;
            }
            
            try {
                License license = License.fromConfigFile(filename);
                
                Date expiresDate = license.expiry;
                if(expiresDate != null && expiresDate.before(new Date())) {
                    log.error("license文件 \"" + file.getName() + "\" 已经过期.");
                    continue; 
                }
                if(!validate(license)) {
                    log.error("license文件 \"" + file.getName() + "\" 不合法.");
                    continue; 
                }
                
                licenses.add(license);
            } 
            catch(Exception e) { }
        }
    }
 
    /**
     * 验证产品和版本号是否和license中信息匹配
     * @param product
     * @param version
     * @return
     */
    public boolean validateLicense(String product, String version) {
        License license = getLicense(product, version);
        return license != null;
    }
    
    /**
     * 获取license的类型。
     * @param product
     * @param version
     * @return
     */
    public String getLicenseType(String product, String version) {
        License license =  getLicense(product, version);
        if(license == null) {
        	return null;
        }
        return license.type;
    }
    
    public License getLicense(String product, String version) {
        loadLicenses();
        
        double needsVersion = EasyUtils.obj2Double(version);
        for( License license : licenses ) {
        	double hasVersion = EasyUtils.obj2Double(license.version);
            if(license.product.equals(product) && hasVersion >= needsVersion) {
            	return license;
            }
        }
        
        return null;
    }

    /**
     * <pre>
     * 验证license是否合法：根据公钥验证签名是否合法。
     * </pre>
     * @param license
     * @return
     * @throws Exception
     */
    boolean validate(License license) throws Exception {
        File keyFile = new File(LicenseFactory.PUBLIC_KEY_FILE);
        String publicKey = FileHelper.readFile(keyFile).trim();

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(EasyUtils.decodeHex(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(LicenseFactory.KEY_ALGORITHM);
        java.security.PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
        
        Signature sig = Signature.getInstance(LicenseFactory.KEY_ALGORITHM);
        sig.initVerify(pubKey);
        sig.update(license.getFingerprint());
        return sig.verify(EasyUtils.decodeHex(license.signature));
    }
}