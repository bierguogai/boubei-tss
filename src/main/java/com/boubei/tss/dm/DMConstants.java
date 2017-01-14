package com.boubei.tss.dm;

import com.boubei.tss.PX;
import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.modules.param.ParamManager;

public final class DMConstants {
	
	public final static String LOCAL_CONN_POOL = "connectionpool";
    
	public final static String USER_ID      = "userId";
	public final static String USER_CODE    = "userCode";
	public final static String FROM_USER_ID = "fromUserId";
	
	// XForm 模板
	public static final String XFORM_GROUP  = "template/dm/group_xform.xml";
	public static final String XFORM_REPORT = "template/dm/report_xform.xml";
	public static final String XFORM_RECORD = "template/dm/record_xform.xml";
    
    // Grid 模板
	public static final String GRID_RECORD_ATTACH = "template/dm/record_attach_grid.xml";
    
    //报表模板资源文件目录
	public static final String REPORT_TL_DIR_DEFAULT = "more/bi_template";
	public static final String REPORT_TL_TYPE = "reportTL";
	
	public static String getReportTLDir() {
		return ParamConfig.getAttribute(PX.REPORT_TL_DIR, REPORT_TL_DIR_DEFAULT);
	}
	
	public static String getDS(String ds) {
		if( ds == null ) {
            try {
                return ParamManager.getValue(PX.DEFAULT_CONN_POOL).trim(); // 默认数据源
            } catch (Exception e) {
            }
        }
		return ds;
	}
	
	public static String getDefaultDS() {
    	// ParamManager.getValue 有缓存，不宜用。（单元测试环节或自动切换数据源时容易出问题）
        // String datasource = ParamManager.getValue(PX.DEFAULT_CONN_POOL).trim();
        String datasource = ParamManager.getValueNoSpring(PX.DEFAULT_CONN_POOL).trim();
        return datasource;
    }
}
