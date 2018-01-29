package com.boubei.tss.modules.license;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

public class LicenseFactory {

	private final static Log log = LogFactory.getLog(LicenseFactory.class);
	
	public static String LICENSE_DIR = URLUtil.getClassesPath().getPath();
    static String PRIVATE_KEY_FILE = LICENSE_DIR + "/private.key";
    static String PUBLIC_KEY_FILE  = LICENSE_DIR + "/public.key";
    
    // 密钥算法  
    public static final String KEY_ALGORITHM = "DSA";  
      
    // 验证算法 
    public static final String SIGN_ALGORITHM = "SHA1withDSA";  

    /**
     * 用私钥对license进行数据签名
     * @param license
     * @throws Exception
     */
    public static synchronized void sign(License license) throws Exception {
        String privateKey = FileHelper.readFile(new File(PRIVATE_KEY_FILE));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(EasyUtils.decodeHex(privateKey.trim()));
        PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
        
        Signature sig = Signature.getInstance(SIGN_ALGORITHM);
        sig.initSign(privKey);
        sig.update(license.getFingerprint());
        
        license.signature = EasyUtils.encodeHex(sig.sign());
    }

    /**
     * 生成公钥、私钥对。
     * 公钥公开，注意保管好私钥（如果泄露，则有可能被hacker随意创建license）
     * @throws Exception
     */
    public static void generateKey() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        
        log.info("开始生成秘钥");
        DataOutputStream out = new DataOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
        out.writeBytes(EasyUtils.encodeHex(pub.getEncoded()));
        out.close();
        log.info("成功生成：" + PUBLIC_KEY_FILE);
        
        out = new DataOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
        out.writeBytes(EasyUtils.encodeHex(priv.getEncoded()));
        out.close();
        log.info("成功生成：" + PRIVATE_KEY_FILE);
    }
    
}
