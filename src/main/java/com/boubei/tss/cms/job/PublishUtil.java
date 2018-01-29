package com.boubei.tss.cms.job;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.util.FileHelper;

public class PublishUtil {
	
	/**
	 * 生成单个文章发布文件
	 * @param article 
	 * @param publishPath
	 * @return
	 */
	public static String publishOneArticle(Article article, String publishPath) {
        // 删除已发布的文章，如果有的话
        String pubUrl = article.getPubUrl();
        if(pubUrl != null) {
            new File(pubUrl).delete();
        }
        
		// 生成发布路径
		File publishDir = new File(publishPath);
		if (!publishDir.exists()) {
			publishDir.mkdirs();
		}
		
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding(ArticleHelper.getSystemEncoding()); //一般：windows “GBK” linux “UTF－8”
		Element articleNode = doc.addElement("Article");
		
        Map<String, Object> articleAttributes = article.getAttributes4XForm(); // 包含文章的所有属性
        articleAttributes.remove("content");
		addValue2XmlNode(articleNode, articleAttributes);
		
		Element contentNode = articleNode.addElement("content");
		contentNode.addCDATA(article.getContent());
        
		// 发布文章对文章附件的处理
		Element eleAtts = articleNode.addElement("Attachments");
        ArticleHelper.addPicListInfo(eleAtts, article.getAttachments());
        
        // 以 “栏目ID_文章ID.xml” 格式命名文章发布的xml文件
        String fileName = article.getChannel().getId() + "_" + article.getId() + ".xml";
		String filePathAndName = publishPath + "/" + fileName;
        FileHelper.writeXMLDoc(doc, filePathAndName);
		return filePathAndName;
	}
	
	// 将文章的所有属性都做为节点加到发布xml文件中。
	private static void addValue2XmlNode(Element articleElement, Map<String, Object> attributes) {
        Element eleKey = null;
        for( Entry<String, Object> entry : attributes.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value == null) continue;
                       
            eleKey = articleElement.addElement(key);
            eleKey.addCDATA(value.toString()); 
        }
    }
	
}
