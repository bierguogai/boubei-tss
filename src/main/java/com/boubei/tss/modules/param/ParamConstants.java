package com.boubei.tss.modules.param;


/**
 * 参数管理常量
 */
public class ParamConstants {

	// =============================== 系统参数常量定义 ===========================//
    public static final String COPY_PREFIX = "copy_"; // 复制前缀(code)
    public static final Long DEFAULT_PARENT_ID = 0L;  // 默认父结点ID
    
	public static final Integer TRUE  = 1; // 是(状态)
	public static final Integer FALSE = 0; // 否(状态)
	
	public static final int SAVE_FLAG = 0;  // 新建操作
	public static final int EDIT_FLAG = 1;  // 编辑操作
	
	//   ========= 组 参数 项 类型 =========
	public static final Integer GROUP_PARAM_TYPE  = 0;	//参数组
	public static final Integer NORMAL_PARAM_TYPE = 1;	//参数
	public static final Integer ITEM_PARAM_TYPE   = 2;	//参数项
	
	//	========= 参数类型 ========
	public static final Integer SIMPLE_PARAM_MODE = 0;	//参数类型  简单参数
	public static final Integer COMBO_PARAM_MODE  = 1;	//参数类型  下拉型参数
	public static final Integer TREE_PARAM_MODE   = 2;	//参数类型  树型参数
    
	//	 =============================== 图标路径定义 ===========================//
	public static final String PARAM_GROUP  = "images/param_group_";		// 参数组图标路径
	public static final String PARAM_ITEM   = "images/param_item_";		// 参数项图标路径
	public static final String PARAM_SIMPLE = "images/param_simple_";	    // 树结点 简单参数图标路径
	public static final String PARAM_COMBO  = "images/param_combo_";	    // 树结点 下拉参数图标路径
	public static final String PARAM_TREE   = "images/param_tree_";	// 树结点 树型参数图标路径
	
	//	 =============================== XFORM模版路径定义 ===========================//	
	public static final String XFORM_PARAM_GROUP   = "template/param/group.xml";	// 参数组模板路径
	public static final String XFORM_PARAM_SIMPLE  = "template/param/simple.xml";	// 简单参数模板路径
	public static final String XFORM_PARAM_COMPLEX = "template/param/complex.xml";	// 复杂（下拉，树型）参数模板路径
	public static final String XFORM_PARAM_ITEM    = "template/param/item.xml";	    // 参数项模板路径
}

	