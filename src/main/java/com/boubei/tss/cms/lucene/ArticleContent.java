package com.boubei.tss.cms.lucene;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储文章内容以及发布路径
 */
public class ArticleContent {
	
	/**
	 * 文章内容，按字段名、字段内容格式存放在map中
	 */
	Map<String, String> articleAttributes = new HashMap<String, String>(); 
    
	String pubUrl;         // 文章发布路径
	String attachContent;  // 附件内容
	
    public ArticleContent(String path, String content){
        this.pubUrl = path;
        this.attachContent = content;
    }

    public String getAttachContent() {
        return attachContent;
    }

    public String getPubUrl() {
        return pubUrl;
    }
    
    /**
     * 检查发布路径是否为合法路径
     */
    public boolean checkPubUrl() {
    	 File pubFile = new File(pubUrl);
    	 if ( !pubFile.exists() || !pubFile.isFile() || !pubFile.getName().endsWith(".xml") ) {
    		 return false;
    	 }
    	 return true;
    }

	public Map<String, String> getArticleAttributes() {
		return articleAttributes;
	}
}

	