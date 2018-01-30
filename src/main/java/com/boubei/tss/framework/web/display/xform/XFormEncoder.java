/* ==================================================================   
 * Created [2009-7-7] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.framework.web.display.xform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.display.IDataEncoder;
import com.boubei.tss.framework.web.display.XmlPrintWriter;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

/**
 * 将数据添加到XForm模板。
 */
public class XFormEncoder implements IDataEncoder {

    static final String XFORM_NODE_NAME = "xform";
    static final String XFORM_DATA_NODE_NAME = "data";
    static final String XFORM_DATA_NODE_XPATH = "/xform/data";
    static final String XFROM_DECLARE_COLUMN_XPATH = "/xform/declare//column";
    static final String XFORM_DATA_ROW_NODENAME = "row";

    Document document;

    /**
     * 将数据添加到XForm模板
     * @param templetURL 
     *          模板路径
     */
    public XFormEncoder(String templetURL) {
        this(templetURL, new HashMap<String, Object>());
    }
    
    /**
     * 将实体中的数据设置到对应模板中
     * @param templetURL 
     *          模板路径
     * @param entity
     *          XForm数据实体
     */
    public XFormEncoder(String templetURL, IXForm entity) {
        this(templetURL, entity == null ? null : entity.getAttributes4XForm());
    }
 
    /**
     * 将数据添加到XForm模板
     * @param templetURL 
     *          模板路径
     * @param attributesMap
     *          数据
     */
    public XFormEncoder(String templetURL, Map<String, Object> attributesMap) {
        XFormTemplet templet = new XFormTemplet(templetURL);
        document = templet.getTemplet();
        
        // 给data节点添加数据
        if(attributesMap != null) {
            Element dataNode = (Element) document.selectSingleNode(XFORM_DATA_NODE_XPATH);
            if( dataNode == null ) {
                dataNode = new DefaultElement(XFORM_DATA_NODE_NAME);
                document.getRootElement().add(dataNode);
            }
            dataNode.clearContent(); // 增加row节点时先删除原先的row节点
            dataNode.add(XMLDocUtil.map2DataNode(attributesMap, XFORM_DATA_ROW_NODENAME));
        }
    }
 
    /**
     * 设置Column上的属性值
     * 
     * @param columnName 节点名称
     * @param name       属性名称
     * @param value      属性值
     */
    public void setColumnAttribute(String columnName, String name, String value) {
        Element column = (Element) document.selectSingleNode(XFROM_DECLARE_COLUMN_XPATH + "[@name='" + columnName + "']");
        if(column == null){
            throw new BusinessException(EX.parse(EX.F_07, columnName));
        }
        column.addAttribute(name, value);
    }
    
    public void fixCombo(String field, Collection<?> list, String v, String n, String seperator){
    	if(list == null) return;
    	
    	String[] objs = EasyUtils.list2Combo(list, v, n, seperator);
        this.setColumnAttribute(field, "values", objs[0]);
        this.setColumnAttribute(field, "texts", objs[1]);
    }
    
    public void fixCombo(String field, Collection<?> list){
    	fixCombo(field, list, "value", "text", "|");
    }

    public String toXml() {
        return document.asXML();
    }

    public void print(XmlPrintWriter out) {
        out.append(document.asXML());
    }
}
