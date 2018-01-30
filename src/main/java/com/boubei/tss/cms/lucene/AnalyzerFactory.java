/* ==================================================================   
 * Created [2006-12-28] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cms.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;

/**
 * 全文检索分词器创建工厂类。
 */
public class AnalyzerFactory {
    
    static final String DICTIONARY_FILE = "dictionary.txt";
    
    static Logger log = Logger.getLogger(AnalyzerFactory.class);
    
    private static Analyzer analyzer;

    /**
     * 创建一个分词解析对象。
     */
    public static Analyzer createAnalyzer() {
        if(analyzer == null){
            
            analyzer = new CJKAnalyzer();
            
         // 此处采用MMAnalyzer，分词粒度为2（分词粒度：当字数等于或超过该参数，且能成词，该词就被切分出来 ）
//            analyzer = new MMAnalyzer();
//            try {
//                URL dictionaryPath = URLUtil.getResourceFileUrl(DICTIONARY_FILE);
//                if(dictionaryPath != null) {
//                    File dictionaryFile = new File(dictionaryPath.getPath());
//                    MMAnalyzer.addDictionary(new FileReader(dictionaryFile));
//                }
//            } catch (Exception e) {
//                log.error("加载字典文件出错", e);
//            }
        }
        return analyzer;
    }
    
    /**
     * 将搜索关键字加入到新字典中
     * @param searchStr
     * @return
     */
    public static Analyzer createAnalyzer(String searchStr){
        analyzer = createAnalyzer();
        
//        if( !MMAnalyzer.contains(searchStr) ){
//            MMAnalyzer.addWord(searchStr);
//            
//            // 写到自己建的词典文件里去
//            try {
//                URL dictionaryPath = URLUtil.getResourceFileUrl(DICTIONARY_FILE);
//                if(dictionaryPath != null){
//                    // FileWriter的第二个参数true表示追加文件到尾部
//                    FileWriter fileWriter = new FileWriter(dictionaryPath.getPath(), true);
//					BufferedWriter bw = new BufferedWriter(fileWriter);   
//                    bw.write(searchStr); // 追加文件内容   
//                    bw.newLine();
//                    bw.close();
//                }
//            } catch (Exception e) {
//                log.error("写入字典文件出错", e);
//            }
//        }
        return analyzer;
    }
}

