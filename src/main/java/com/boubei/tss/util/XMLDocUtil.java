/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;

/** 
 * <p> 处理XML文档的工具类 </p> 
 */
public class XMLDocUtil {
	
	public static Document createDocByAbsolutePath2(String file) {
		Document rtDoc;
        try{
            rtDoc = createDocByAbsolutePath(file);
        } catch(Exception e){
            String fileContent = FileHelper.readFile(new File(file), "UTF-8");
            rtDoc = XMLDocUtil.dataXml2Doc(fileContent);
        }
        return rtDoc;
	}
    
    /**
     * 根据XML文件的绝对路径来创建文档对象
     * @return
     */
    public static Document createDocByAbsolutePath(String file) {
        SAXReader saxReader = new SAXReader();
        try {
            saxReader.setEncoding("UTF-8");
            return saxReader.read(file);
        } catch (DocumentException e) {
        	throw new RuntimeException("读取XML文件出错：" + file, e);
        }
    }
    
    /**
     * 根据模板的路径：uri 创建文档。
     * 注：如果需要得XML文档位于jar包中，则SAXReader.read()必须使用URL形式得参数，用String等会找不到
     * @return
     */
    public static Document createDoc(String uri) {
        URL fileUrl = URLUtil.getResourceFileUrl(uri);
        if (fileUrl == null) {
            throw new RuntimeException("定义的文件没有找到：" + uri);
        }
        
		SAXReader saxReader = new SAXReader();
        try {
            saxReader.setEncoding("UTF-8");
            return saxReader.read(fileUrl);
        } catch (DocumentException e) {
        	throw new RuntimeException("读取XML文件出错：" + fileUrl, e);
        }
    }

    public static Document dataXml2Doc(String dataXml) {
    	if( EasyUtils.isNullOrEmpty(dataXml) ) return null;
    	
        if (!dataXml.startsWith("<?xml")) {
        	dataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + dataXml;
        }
        
        Document doc;
        try {
            doc = DocumentHelper.parseText(dataXml);
        } 
        catch (DocumentException e) {
        	try {
                doc = DocumentHelper.parseText(XmlUtil.stripNonValidXMLCharacters(dataXml));
            } catch (Exception e1) {
            	throw new RuntimeException("由dataXml生成doc出错：", e);
            }
        }
        return doc;
    }

    /**
     * 将xml节点上各个属性的数据按 name/value 放入到Map中
     * 格式如<row id="1" name="Jon"/>
     * @param dataNode
     * @return
     */
	@SuppressWarnings("unchecked")
	public static Map<String, String> dataNode2Map(Element dataNode) {
        Map<String, String> attrsMap = new HashMap<String, String>();
        if(dataNode != null){
            List<AbstractAttribute> attributes = dataNode.attributes();
        	attributes = (List<AbstractAttribute>) EasyUtils.checkNull(attributes, new ArrayList<AbstractAttribute>());
            for (AbstractAttribute attr : attributes) {
                attrsMap.put(attr.getName(), attr.getValue());
            }
        }
        return attrsMap;
    }

    /**
     * 将row节点下各个属性节点的数据按 node.getName()/node.getText() 放入到Map中
     * 格式如<row><id>1<id/><name>Jon<name/><row/>
     * 注：不支持多值的情况，即同时有两个<name>节点以上的话只会保存下最后name值
     * @param rowNode
     * @return
     */
    public static Map<String, String> dataNodes2Map(Element rowNode) {
        Map<String, String> attrsMap = new HashMap<String, String>();
        if (rowNode != null) {
            for (Iterator<?> it = rowNode.elementIterator(); it.hasNext();) {
                Element node = (Element) it.next();
                attrsMap.put(node.getName(), node.getText());
            }
        }
        return attrsMap;
    }

    /**
     * 将Map中数据按 key/value 设置到xml节点的各个属性上 name/value。
     * 支持多值的情况，即某个属性的值为Object[]数组时。
     * @param attributesMap
     * @param dataNodeName
     * @return 格式如<row><id><![CDATA[1]]><id/><name><![CDATA[Jon]]><name/><row/>
     */
    public static Element map2DataNode(Map<String, ? extends Object> map, String dataNodeName) {
        Element node = new DefaultElement(dataNodeName);
        for ( Entry<String, ? extends Object> entry : map.entrySet() ) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof Object[]) {
                    Object[] objs = (Object[]) value;
                    for (int i = 0; i < objs.length; i++) {
                        addCDATANode(node, name, objs[i]);
                    }
                } else {
                    addCDATANode(node, name, value);
                }
            }
        }
        return node;
    }

    private static void addCDATANode(Element node, String cdataNodeName, Object value) {
        Element valueNode = new DefaultElement(cdataNodeName);
        String valueStr = value.toString();
        valueStr = valueStr.replaceAll("<!\\[CDATA\\[", "&lt;!\\[CDATA\\[");
        valueStr = valueStr.replaceAll("]]>", "]]&gt;");

        valueNode.add(new DefaultCDATA(valueStr));
        node.add(valueNode);
    }
    
    /**
     * 将Map中数据按 key/value 设置到xml节点的各个属性上 name/value。
     * 支持多值的情况，即某个属性的值为Object[]数组时。
     * @param attributesMap
     * @param dataNodeName
     * @return 格式如<row id = "1" name = "Jon"/>
     */
    public static  Element map2AttributeNode(Map<String, Object> map, String dataNodeName){
        Element node = new DefaultElement(dataNodeName);       
        for ( Entry<String, Object> entry : map.entrySet() ) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if(value != null){
                node.addAttribute(name, value.toString());
            }
        }
        return node;
    }
    
    /**
     * 从Document中根据节点路径选择相应节点列表。
     * @param doc
     * @param xPath 如 "/Responses/Response/url"
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Element> selectNodes(Document doc, String xPath) {
        return doc.selectNodes(xPath);
    }
    
    /**
     * 从element中根据节点路径选择相应子节点列表。
     * @param element
     * @param xPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Element> selectNodes(Element element, String xPath) {
        return element.selectNodes(xPath);
    }
    
    /**
     * 获取Dom节点的文本内容。
     * @param node
     * @return
     */
    public static String getNodeText(Node node) {
    	return node == null ? null : node.getText();
    }
}
