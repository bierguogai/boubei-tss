/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * 信息加/解密工具类 
 * 
 * @author Jon.King 2008-8-18
 */
public class InfoEncoder {

    private static final String KEY_WORD  = "boubei-tss";
	private static final String ALGORITHM = "Blowfish";

    private Cipher getCipher(int Cipher_MODE) {
        SecretKey deskey = new SecretKeySpec(KEY_WORD.getBytes(), ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher_MODE, deskey);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("加密器初始化失败", e);
        } 
    }

    /**
     * 将字符串加密
     * 
     * @param datasource
     *            要加密的数据
     * @return 返回加密后的 byte 数组
     * @throws Exception
     */
    public String createEncryptor(String datasource) {
        byte[] encryptorData;
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
			encryptorData = cipher.doFinal(datasource.getBytes());
        } catch (Exception ex) {
            throw new RuntimeException("加密失败:" + datasource, ex);
        } 

        return new Base64Encoder().encode(encryptorData).replaceAll("\\s", "");
    }

    /**
     * <p>
     * 将字符串解密
     * </p>
     * 
     * @param datasource
     *            要解密的数据
     * @return 返回加密后的 byte[]
     */
    public String createDecryptor(String datasource) {
        byte[] decryptorData = new Base64Encoder().decode(datasource);

        byte[] createDecryptor;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
			createDecryptor = cipher.doFinal(decryptorData);
        } catch (Exception ex) {
            throw new RuntimeException("解密失败:" + datasource, ex);
        } 
        return new String(createDecryptor);
    }

    
    private static final String MESSAGE_DIGEST_TYPE = "MD5";

    /**
     * 加密：MD5
     * 
     * @param str
     * @return
     */
    public static String string2MD5(String str) {
        try {
            MessageDigest alga = MessageDigest.getInstance(MESSAGE_DIGEST_TYPE);
            alga.update(str.getBytes());
            
            byte[] digesta = alga.digest();
            String hs = "";
            for (int n = 0; n < digesta.length; n++) {
            	String stmp = (Integer.toHexString(digesta[n] & 0XFF));
                if (stmp.length() == 1)
                    hs = hs + "0" + stmp;
                else
                    hs = hs + stmp;
            }
            
            str = hs.toUpperCase();
        } 
        catch (NoSuchAlgorithmException ex) { }
        
        return str;
    }
    
    public static String simpleEncode(String info, int key) {
    	if( EasyUtils.isNullOrEmpty(info) ) return info;
    	
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < info.length(); i++) {
			list.add( (info.charAt(i) ^ key % 127) );
		}

		return EasyUtils.list2Str(list, "X");
    }
    
    public static String simpleDecode(String info, int key) {
    	if( EasyUtils.isNullOrEmpty(info) 
    			|| (info.indexOf("X") < 0 && !info.matches("[0-9]+")) )  // 单个字符加密后的情形
    		return info;
    	
		String[] charCodes = info.split("X");
		char a[] = new char[charCodes.length];
		for (int i = 0; i < charCodes.length; i++) {
			a[i] = (char) ( Integer.parseInt(charCodes[i]) ^ key % 127);
		}

		return new String(a);
    }
    
    /**
     * 对敏感信息进行脱敏处理
     */
    public static String cover(String s, int from, int to) {
        if( EasyUtils.isNullOrEmpty(s) ) return s;

        int l = s.length();
        to = Math.min(l, to);
        from = Math.min(l - 1, from);
        from = Math.min(to, from);

        char[] a = s.toCharArray();
        for(int i = 0; i < l; i++) {
            if(i >= from-1 && i < to) {
                a[i] = '*';
            }
        }
        return new String(a);
    }
}