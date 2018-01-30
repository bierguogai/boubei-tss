/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.lucene.executor;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.dom4j.Element;

import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.cms.lucene.AnalyzerFactory;
import com.boubei.tss.cms.lucene.ArticleContent;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

/** 
 * 默认索引执行器：根据文章的所有字段来创建索引。
 * 
 */
public class DefaultIndexExecutor implements IndexExecutor {
    
    protected Logger log = Logger.getLogger(this.getClass());
    
    // 发布日期、创建日期 字段常用于结果排序
    protected final static String FIELD_ISSUEDATE  = "issueDate";
    protected final static String FIELD_CREATETIME = "createTime";
    protected final static String FIELD_CONTENTS   = "contents";
    
    public void createIndex(ArticleContent bean, IndexWriter indexWriter) throws CorruptIndexException, IOException {
        if ( !bean.checkPubUrl() ) return;
        
        // 从发布的xml文件中获取用以创建索引的内容信息
        String publishPath = bean.getPubUrl();
        org.dom4j.Document doc = XMLDocUtil.createDocByAbsolutePath2(publishPath);
        
        String docEncoding = doc.getXMLEncoding();
		String sysEncoding = ArticleHelper.getSystemEncoding();
		String encodeing = (String) EasyUtils.checkTrue(!"UTF-8".equalsIgnoreCase(docEncoding), sysEncoding, docEncoding);
        doc.setXMLEncoding(encodeing);
        
        List<?> childNodes = doc.getRootElement().elements();
        for (int i = 0; i < childNodes.size(); i++) {
            Element element = (Element) childNodes.get(i);
            
            // 除附件外所有字段都作为FIELD
            if ( !"Attachments".equals(element.getName()) ) {
            	bean.getArticleAttributes().put(element.getName(), element.getTextTrim());
            }
        }
        
        Document indexDoc = createIndexDoc( bean );
        indexWriter.addDocument(indexDoc);
    }
 
    /**
     * 创建索引文本
     * @param pubFile 索引文件存放路径
     * @param attachsContent 附件内容
     * @param map 文章属性Map
     * @return
     */
    protected Document createIndexDoc( ArticleContent bean ) throws IOException {
        Document luceneDoc = new Document();
        
        StringBuffer buffer = new StringBuffer();
        for( Entry<String, String> entry : bean.getArticleAttributes().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 发布日期、创建日期 字段特殊处理，用于结果排序
            if(FIELD_ISSUEDATE.equals(key) || FIELD_CREATETIME.equals(key)){
                luceneDoc.add(new Field(key, value, Field.Store.YES, Field.Index.UN_TOKENIZED));
            } 
            else {
                luceneDoc.add(new Field(key, value, Field.Store.YES, Field.Index.NO));
            }
            
            // 将文章标题、关键字、副标题、正文、日期、作者灯信息集合起来加到文章内容里去
            buffer.append(value); 
        }
        buffer.append( EasyUtils.obj2String(bean.getAttachContent()) );
        
        luceneDoc.add(new Field(FIELD_CONTENTS, buffer.toString(), Field.Store.NO, Field.Index.TOKENIZED));
        return luceneDoc;        
    }
 
    public Query createIndexQuery(String searchStr) throws ParseException {
        return createIndexQuery(FIELD_CONTENTS, searchStr);
    }
    
    protected Query createIndexQuery(String filedName, String searchStr) throws ParseException {
    	Analyzer analyzer = AnalyzerFactory.createAnalyzer(searchStr);
    	
        Query query1 =  new QueryParser(filedName, analyzer).parse(searchStr);
        //TermQuery query2 = new TermQuery(new Term(FIELD_ISSUEDATE, searchStr));
        TermQuery query3 = new TermQuery(new Term(FIELD_CREATETIME, searchStr));

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
        //booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query3, BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }
}

