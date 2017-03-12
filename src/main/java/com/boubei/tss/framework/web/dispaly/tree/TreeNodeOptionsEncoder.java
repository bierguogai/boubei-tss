package com.boubei.tss.framework.web.dispaly.tree;

import java.util.ArrayList;
import java.util.List;

import com.boubei.tss.framework.web.dispaly.IDataEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;

/**
 * 树节点选项编码器
 */
public class TreeNodeOptionsEncoder implements IDataEncoder {
	
	private List<TreeNodeOption> options = new ArrayList<TreeNodeOption>();
	
	public void add(TreeNodeOption option){
		options.add(option);
	}

	public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<options>");
		for(TreeNodeOption option : options){
			sb.append(option.toXml());
		}
		sb.append("</options>");
		return sb.toString();
	}

	public void print(XmlPrintWriter out) {
		out.append(toXml());
	}

}
