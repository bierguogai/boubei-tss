package com.boubei.tss.framework.web.dispaly.xform;

import org.dom4j.Document;
import org.dom4j.Element;

import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.XMLDocUtil;

/**
 * <p> XForm数据解码 </p>
 * 
 */
public class XFormDecoder {
	
	private static final String XFORM_DATA_ROW_NODE_XPATH = "//data/row";

	/**
	 * 将XML格式的数据设置到新创建的实体中
	 * 
	 * @param dataXml
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	public static Object decode(String dataXml, Class<?> entityClass) {
		Object bean = BeanUtil.newInstance(entityClass);
		if (dataXml != null) {
			Document doc = XMLDocUtil.dataXml2Doc(dataXml);
			Element dataNode = (Element) doc.selectSingleNode(XFORM_DATA_ROW_NODE_XPATH);
			BeanUtil.setDataToBean(bean, XMLDocUtil.dataNodes2Map(dataNode));
		}
		return bean;
	}

}
