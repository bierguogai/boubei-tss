/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>
 * 可用Properties文件配置的Contants基类
 * <p/>
 * 子类可如下编写
 * <pre>
 * public class Constants extends ConfigurableContants {
 *   static {
 *     init("JFramework.properties");
 *   }
 * }
 * <p/>
 * public final static String ERROR_BUNDLE_KEY = getProperty("constant.error_bundle_key", "errors"); }
 * </pre>
 * 
 * <p> ConfigurableContants.java </p> 
 * 
 * @author Jon.King 2006-6-19
 *
 */
public abstract class ConfigurableContants {
	
    protected static Logger log = Logger.getLogger(ConfigurableContants.class);
    
    protected static Properties properties = new Properties();
    
    public static String DEFAULT_PROPERTIES = "application.properties";

    protected static Properties init(String propertiesFileName) {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            URL propertiesFile = URLUtil.getResourceFileUrl(propertiesFileName);
            in = new FileInputStream(propertiesFile.getFile());
            properties.load(in);
        } 
        catch (Exception e) {
            log.error("load " + propertiesFileName + " into Contants error", e);
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return properties;
    }

    protected static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    protected static String getProperty(String key) {
        return properties.getProperty(key);
    }
}


