package com.boubei.tss;

/**
 *  系统可注册的参数配置（application.properties文件和component_param表）
 */
public interface PX {
	
	// Config: 只能在application.properties里配置
	
	/** 配置文件中应用编号属性名：系统获取Code值，如TSS、CMS */
	static String APPLICATION_CODE = "application.code";

	/** Spring配置文件， 默认为spring.xml */
	static String SPRING_CONTEXT_PATH = "aplication.context";

	/** 是否启用定时Job */
	static String ENABLE_JOB = "job.enable";
	
	// ParamConfig：component_param 或 application.properties
    /** 
     * 资源地址白名单，白名单内的资源允许【匿名访问】
     * url.white.list = /version,.in,.do,.portal,login.html,_forget.html,_register.html.....
     */
	static String URL_WHITE_LIST = "url.white.list";
	
    /** ip白名单，名单内的ip允许跨域访问系统的服务和资源 */
    static String IP_WHITE_LIST = "ip.white.list";
    
    /** session生命周期，单位（秒）*/
	static String SESSION_CYCLELIFE_CONFIG = "session.cyclelife";
	
	/** 日志缓冲池最多可存日志条数的参数  */
    static String LOG_FLUSH_MAX_SIZE = "log_flush_max_size";
    
    /** 文件上传目录，默认为tomcat7/webapps/tss */
    static String UPLOAD_PATH = "upload_path";
    
    /** 报表自定义展示页上传目录 */
    static String REPORT_TL_DIR = "report.template.dir";
    
    /**  用户自定义的参与Freemarker解析参数  */
    static String USER_DEFINED_PARAMS = "userdefinedParams";
    
    /** 邮件服务器配置 email.sys、email.default等 */
    static String MAIL_SERVER_ = "email.";
    
    /** 对含有此处配置的关键字的错误异常进行邮件提醒 */
    static String ERROR_KEYWORD = "error.keyword";
    
    /** LDAP认证地址 */
    static String OA_LDAP_URL = "oa.ldap.url";
    
    /** 如果是从其它系统单点登录到平台（TSS），则自动转到配置的门户首页地址 */
    static String SSO_INDEX_PAGE = "sso.index.page";
    
    // ParamManager: component_param
    /* report_export_url = http://www.boubei.com:8082  导出数据分流机器, 前台页面报表导出时用到 */
    
    /** 读取最新、最热门、最近访问报表时，选取的日志天数，日志量大的，不宜取太多天。默认3天 */
    static String TOP_REPORT_LOG_DAYS = "TOP_REPORT_LOG_DAYS";
    
    /** 可以指定一个系统管理员之外的角色为超级管理员 */
    static String ADMIN_ROLE = "ADMIN_ROLE";
    
    /** QueryCache支持的最大等待线程数量，没有配置默认100 */
    static String MAX_QUERY_REQUEST = "MAX_QUERY_REQUEST";
    
    /** comboParams 定时器配置列表 */
    static String TIMER_PARAM_CODE = "TIMER_PARAM_CODE";
    
	/** 后台Action单独发布的数据服务，用于report/record为下拉框选择数据服务 */
	static String DATA_SERVICE_CONFIG = "DATA_SERVICE_CONFIG";
	
	/** /home/tssbi/temp 用于报表导出及数据表的附件上传 */
	static String ATTACH_PATH  = "TEMP_EXPORT_PATH"; 
	
	/** connectionpool: 默认数据源 */
	static String DEFAULT_CONN_POOL = "default_conn_pool";
	
	/** comboParams  数据源下拉列表 */
	static String DATASOURCE_LIST   = "datasource_list";
	
	/** param Group  缓存池 */
	static String CACHE_PARAM = "CACHE_PARAM";
	
	/** 维护用于邮件推送的配置（收件人列表）*/
	static String EMAIL_MACRO  = "EmailMacros";
	
}
