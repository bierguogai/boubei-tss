package com.boubei.tss.cms;

import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.ParamConstants;

/** 
 * CMS常用常量定义
 */
public interface CMSConstants {
	
    /** 发布类型 */
    static final String PUBLISH_ADD = "1"; // 增量发布
    static final String PUBLISH_ALL = "2"; // 完全发布
    
    /** 布尔型常量 */
    static final Integer STATUS_STOP  = ParamConstants.TRUE;  // 站点停止状态
    static final Integer STATUS_START = ParamConstants.FALSE; // 站点启动状态
   
    /** 下载地址的路径 */
    static final String DOWNLOAD_SERVLET_URL = "/download?id="; 
    
    /** 根结点ID */
    static final Long HEAD_NODE_ID = -1L; 
    
    static final Long DEFAULT_NEW_ID = BaseActionSupport.DEFAULT_NEW_ID;
    
    static final String DEFAULT_DOC_PATH = "doc";
    static final String DEFAULT_IMG_PATH = "img";
    
    /* 文章状态 */
    static final Integer START_STATUS     = 1; // 编辑中
    static final Integer TOPUBLISH_STATUS = 2; // 待发布
    static final Integer XML_STATUS       = 3; // 已发布生成xml文件
    static final Integer OVER_STATUS      = 4; // 过期状态
   
    /* 策略类型常量 */
    static final Integer STRATEGY_TYPE_INDEX   = 1; // 索引策略
    static final Integer STRATEGY_TYPE_PUBLISH = 2; // 发布策略
    static final Integer STRATEGY_TYPE_EXPIRE  = 3; // 文章过期策略

    // XForm 模板
    static final String XFORM_SITE     = "template/cms/xform_site.xml";
    static final String XFORM_CHANNEL  = "template/cms/xform_channel.xml";
    static final String XFORM_ARTICLE  = "template/cms/xform_article.xml";
    
    // Grid 模板
    static final String GRID_ARTICLELIST = "template/cms/grid_article.xml";
    static final String GRID_ATTACHSLIST = "template/cms/grid_attach.xml";
    
    // 资源授权相关
    static final String RESOURCE_TYPE_CHANNEL  = "3";   // 资源类型  站点栏目
    
    // 栏目资源操作ID
    static final String OPERATION_VIEW        = "1"; // 查看浏览
    static final String OPERATION_ADD_CHANNEL = "2"; // 新建栏目
    static final String OPERATION_ADD_ARTICLE = "3"; // 新建文章
    static final String OPERATION_PUBLISH     = "4"; // 发布权限
    static final String OPERATION_EDIT        = "5"; // 编辑权限
    static final String OPERATION_DELETE      = "6"; // 删除权限
    static final String OPERATION_STOP_START  = "7"; // 停用启用
    static final String OPERATION_ORDER       = "8"; // 排序权限
    static final String OPERATION_MOVE        = "9"; // 移动权限
}
