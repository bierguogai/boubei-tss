package com.boubei.tss.framework.web.dispaly.tree;

/**
 * 树节点选项对象(比如权限选项：新增、删除、停用、启用等，每个为一选项)
 */
public class TreeNodeOption {
	
	/** 选项编号 */
	private String id;

	/** 选项文字  */
	private String text;

	/** 依赖的权限选项id字符创：id1,id2  */
	private String dependId;
 
    public void setDependId(String dependId) {
        this.dependId = dependId;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public void setText(String text) {
        this.text = text;
    }

    public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<option>");
		sb.append("<id>").append(id).append("</id>");
		sb.append("<text>").append(text).append("</text>");
		sb.append("<dependId>").append(dependId).append("</dependId>");
		sb.append("</option>");
		return sb.toString();
	}
}
