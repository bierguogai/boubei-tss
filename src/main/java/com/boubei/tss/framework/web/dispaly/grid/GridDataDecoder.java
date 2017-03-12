package com.boubei.tss.framework.web.dispaly.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.XMLDocUtil;


/** 
 * <p> 解析Grid的XML格式数据。</p> 
 * 
 * 一般为格式如：
 *    <data>
 *          <row id = 1 .../>
 *          <row id=2 .../>
 *    </data>
 *    
 * 将XML数据中每个row节点里属性取出，放入一个Map或者一个指定的实体对象中，
 * 然后将这些Map或实体逐个放入List中返回。
 * 
 */
public class GridDataDecoder {
	
    /**
     * 将Grid数据dataXml中各个<row ...>节点的属性值设置到实体中，并将各个实体放入List返回
     * 
	 * @param dataXml  XML数据
	 * @param entityClass 实体的class名称
	 * @return 
	 */
	public static List<Object> decode(String dataXml, Class<?> entityClass){
        Document doc = XMLDocUtil.dataXml2Doc(dataXml);
        
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        @SuppressWarnings("unchecked")
        List<Element> nodeList = doc.selectNodes("//row");
        for(Element node : nodeList){
            mapList.add(XMLDocUtil.dataNode2Map(node));
        }
        
        List<Object> list = new ArrayList<Object>();
        for(Map<String, String> map : mapList){
            Object bean = BeanUtil.newInstance(entityClass);
            BeanUtil.setDataToBean(bean, map);
            list.add(bean);
        }
		return list;
	}
    
}
