package com.boubei.tss.framework.web.dispaly.tree;

import java.util.Map;

import com.boubei.tss.framework.web.dispaly.IDataEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;

/**
 * Tree对象：生成Tree控件所需要的xml数据
 * 
 */
public class TreeEncoder implements IDataEncoder {
    
	static final String TREE_NODE_NAME = "treeNode";
	static final String TREE_ROOT_NODE_ID = "_root";
	static final String TREE_ROOT_NODE_NAME = "全部";

    /** 默认节点属性转换器：没有任何转换 */
    final static ITreeTranslator DEFAULT_TRANSLATOR = new ITreeTranslator() {
		    public Map<String, Object> translate(Map<String, Object> attributes) {
		        return attributes;
		    }
		};
	
	/** 默认树解析器：单层树解析器 */
	final static ITreeParser DEFAULT_TREE_PARSER = new SimpleTreeParser();

    private Object data ;       // 源数据
    private ITreeParser parser; // 解析器
    private ITreeTranslator translator = DEFAULT_TRANSLATOR;
    private TreeNodeOptionsEncoder optionsEncoder; // 树节点选项编码器
    
    private boolean needRootNode  = true;  // 是否需要根节点
    private boolean rootCanSelect = true;  // 跟节点是否可以选择
    private String  rootNodeName  = TREE_ROOT_NODE_NAME; // 根节点名称，允许更改
    
	//单层树构造器
	public TreeEncoder(Object data) {
		this(data, DEFAULT_TREE_PARSER);
	}

	//自带解析器的构造器
	public TreeEncoder(Object data, ITreeParser parser) {
		this.data = data;
		this.parser = parser;
	}

	/**
	 * 生成xml数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public String toXml() {
		StringBuffer sb = new StringBuffer("<actionSet>");
		
		if (optionsEncoder != null) {
			sb.append(optionsEncoder.toXml());
		}
		
		TreeNode root = parser.parse(data); // 解析完成后，node为根节点
		if (root != null) {
		    root.initTreeNode(TREE_ROOT_NODE_ID, rootNodeName, needRootNode);
            if ( rootCanSelect ) {
            	root.enabled();
            } else {
                root.disabled();
            }
			sb.append(root.toXml(TREE_NODE_NAME, translator));
		}
		return sb.append("</actionSet>").toString();
	}

	public void print(XmlPrintWriter out) {
		out.append(toXml());
	}

	/**
	 * 设置是否需要根节点，默认为 true
	 * @param needRootNode
	 */
	public void setNeedRootNode(boolean needRootNode) {
		this.needRootNode = needRootNode;
	}

	/**
	 * 设置根节点的名称，默认为“全部”
	 * @param rootNodeName
	 */
	public void setRootNodeName(String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}

	/**
	 * 设置根节点是否可选。
	 * @param rootCanSelect
	 */
	public void setRootCanSelect(boolean rootCanSelect) {
		this.rootCanSelect = rootCanSelect;
	}

	/**
	 * 设置树节点的操作权限编码。
	 * @param optionsEncoder
	 */
	public void setOptionsEncoder(TreeNodeOptionsEncoder optionsEncoder) {
		this.optionsEncoder = optionsEncoder;
	}
    
    /**
     * <p> 设置树节点属性转换工具 </p>
     * @param translator
     */
    public void setTranslator(ITreeTranslator translator){
        this.translator = translator;
    }
}
