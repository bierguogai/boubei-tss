package com.boubei.tss.framework.sso.appserver;

/**
 * <p>
 * 应用系统对象，包含应用系统的相关信息
 * 类似：
 * <server code="TSS"  name="它山石" sessionIdName="JSESSIONID" baseURL="http://localhost:8088/tss"/>
 * <server code="DEMO" name="Demo" sessionIdName="JSESSIONID" baseURL="http://localhost:8088/demo"/>
 * </p>
 */
public class AppServer {

    /**
     * 应用Code：与各应用系统配置文件application.properties中的application.code值对应
     */
    private String code;

    /**
     * 应用系统名称
     */
    private String name;

    /**
     * 应用系统的访问路径，包括协议类型、域名、端口(可选)、上下文路径等信息, 如http://www.boubei.com/tss。
     * 如果是同服务器（可简单配置为如： /demo、/al）
     */
    private String baseURL;
 
    /**
     * 应用系统使用对应的Cookie中保存SessionID的参数名称，如Java系统一般为JSESSIONID
     */
    private String sessionIdName;
 
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
 
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionIdName() {
        return sessionIdName;
    }

    public void setSessionIdName(String sessionIdName) {
        this.sessionIdName = sessionIdName;
    }
 
    /**
     * 获取服务器的ContextPath值，baseURL类似："http://127.0.0.1:8088/tss"
     * @return  tss
     */
    public String getPath() {
        if (baseURL == null)
            return null;

        int index = baseURL.lastIndexOf("/");
        if (index > 6) {
            return baseURL.substring(index);
        } else {
            return "/";
        }
    }

    /**
     * 获取服务器的Domain值，baseURL类似："http://127.0.0.1:8088/tss"
     * @return  127.0.0.1
     */
    public String getDomain() {
        if (baseURL == null)
            return null;
        
        int index = baseURL.indexOf("/", 7);
        int colonIndex = baseURL.indexOf(":", 7);
        if (index < 0 && colonIndex < 0) {
            return baseURL.substring(7);
        } 
        else if (colonIndex > -1 && (colonIndex < index || index < 0)) {
            return baseURL.substring(7, colonIndex);
        } 
        else {
            return baseURL.substring(7, index);
        }
    }
}
