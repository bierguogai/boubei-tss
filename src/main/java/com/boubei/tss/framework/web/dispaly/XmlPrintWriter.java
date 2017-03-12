package com.boubei.tss.framework.web.dispaly;

import java.io.PrintWriter;

/** 
 * Xml数据输出流
 */
public class XmlPrintWriter {

	private PrintWriter out;

	public XmlPrintWriter(PrintWriter out) {
		this.out = out;
	}

	/**
	 * 数据流方式输出数据
	 * @param xml
	 * @return
	 */
	public XmlPrintWriter append(Object xml) {
		out.print(xml);
		return this;
	}

	public void println(String xml) {
		out.println(xml);
	}
}