package com.boubei.tss.portal.engine.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.boubei.tss.portal.entity.Structure;

/**
 * 页面节点对象：用于解析门户中使用到的页面
 */
public class PageNode extends AbstractSubNode implements DecoratorConfigable, LayoutConfigable, Supplementable {

	/** 页面全局脚本文件列表：每元素为文件相对路径字符串 */
	private List<String> scriptFiles = new ArrayList<String>();

	/** 页面全局样式表文件列表：每元素为文件相对路径字符串 */
	private List<String> styleFiles = new ArrayList<String>();

	private String scriptCode; //页面全局脚本代码
	private String styleCode; //页面全局样式表代码
	private LayoutNode layoutNode; //页面布局器
	private DecoratorNode decoratorNode; //页面修饰器
	
	public PageNode(Structure ps, Node parentNode) {
		this.id = ps.getId();
		this.name = ps.getName();
		this.code = ps.getCode();
		this.setParent(parentNode);
	}

	public String getScriptCode() { return scriptCode; }
	public String getStyleCode () { return styleCode; }

	public List<String> getScriptFiles() { return scriptFiles; }
	public List<String> getStyleFiles () { return styleFiles; }

	public void setScriptCode(String scriptCode) { this.scriptCode = scriptCode; }
	public void setStyleCode (String styleCode)  { this.styleCode  = styleCode; }
	
	public DecoratorNode getDecoratorNode() { return decoratorNode; }
	public LayoutNode getLayoutNode() { return layoutNode; }

	public void setDecoratorNode(DecoratorNode decorator) { this.decoratorNode = decorator; }
	public void setLayoutNode(LayoutNode layout) { this.layoutNode = layout; }

	public AbstractElementNode getDecoratorContent() {
		return layoutNode;
	}

	public PortalNode getPortal() {
		return (PortalNode) this.parent;
	}

	public PageNode getPage() {
		return this;
	}

	public void setPortal(PortalNode node) {
		this.setParent(node);
	}

	public void setPage(PageNode node) { }

	public Object clone() {
		PageNode copy = (PageNode) super.clone();
		copy.children = new LinkedHashSet<Node>();
		for ( Node kitten : this.children ) {
			copy.children.add( (Node) kitten.clone() );
		}
		copy.decoratorNode = (DecoratorNode) this.decoratorNode.clone();
		copy.layoutNode = (LayoutNode) this.layoutNode.clone();
		return copy;
	}
}