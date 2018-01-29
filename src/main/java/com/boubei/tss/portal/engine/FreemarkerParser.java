package com.boubei.tss.portal.engine;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/** 
 * <p> FreemarkerParser.java </p> 
 * freeMarker解析器
 */
public class FreemarkerParser {
    
    protected Logger log = Logger.getLogger(this.getClass());
    
    /** 配置用于加载Freemarker的模板文件。*/
    private Configuration cfg = new Configuration();
    
    /** DataModel，存放参数、静态类（statics）等 */
    private Map<String, Object> root = new HashMap<String, Object>();
    
    public FreemarkerParser(){
        root.put("statics", BeansWrapper.getDefaultInstance().getStaticModels()); 
    }
    
    public Map<String, Object> getDataModel() {
    	return root;
    }
    
    /**
     * 初始化Freemarker的Configuration对象，将指定目录下的模板文件加载进来。
     * 如果指定的目录为空，则默认取classes/freemarker目录的下模板文件。
     * 
     * @param templatePath
     */
    public FreemarkerParser(File templatePath){
    	this();
    	
        //默认freemarker模板文件存放在WEB-INF目录下的freemarker目录里
        String ftlDir = URLUtil.getResourceFileUrl("freemarker").getPath(); 
        File defaultTemplatePath = new File(ftlDir);
        templatePath = (File) EasyUtils.checkNull(templatePath, defaultTemplatePath);

        try {
            cfg.setDefaultEncoding("UTF-8");
            cfg.setDirectoryForTemplateLoading(templatePath);
        } 
        catch (IOException e) {
            log.error(" 读取FM模板文件" +templatePath +"时IO异常，装载不成功(" +templatePath.exists()+ ")", e);
            return;
        }
        
        cfg.clearTemplateCache();
        List<String> files = FileHelper.listFilesByType(".ftl", templatePath);
        for( String fileName : files ){
            cfg.addAutoImport(fileName.substring(0, fileName.length() - 4), fileName); // 截去后缀'.ftl'， 以文件自身名字命名
        }
    }
    
    /**
     * 解析模板，返回解析结果
     * @param template
     * @return
     */
    public String parseTemplate(String template) {
    	Writer out2 = new StringWriter();
    	parseTemplate(template, out2);
    	return out2.toString();
    }
    
    /**
     * 解析模板，将解析结果直接输出到Writer对象（一般为直接像前台打出结果）
     * @param template
     * @param out
     */
    public void parseTemplate(String template, Writer out) {
        // 便于使用单元测试时打印出结果
        if(out.getClass().getName().equals("org.springframework.mock.web.MockHttpServletResponse$ResponsePrintWriter")) {
            out = new OutputStreamWriter(System.out);
        }
        
        template = EasyUtils.fmParse(template, root, out, cfg);
		if(template.startsWith("FM-parse-error")) {
			throw new BusinessException(template);
		}
    }
    
    /**
     * 执行freemarker解析操作。<br/>
     * 注：需要分两次用freemarker引擎执行解析。
     * 
     * @param template
     * @param out
     * @throws IOException
     * @throws TemplateException
     */
    public void parseTemplateTwice(String template, Writer out) {
        // 第一步，对portlet中的freemarker标签进行解析
        template = parseTemplate(template);
        
        //第二步，对portlet取回的内容再进行解析（类似通过portlet取回的菜单内容中包含${PT_ip}自定义参数等）
        parseTemplate(template, out);
    }
    
    /**
     * 将包括request请求中的参数 和 用户自定义的参数 在内的参数加入到数据模型（DataModel）中。
     * 
     * 由于request参数多是数组的形势，所以同时要先将数组解开。
     * 注：request中取出来的参数Map值为数组，一般该数组只有一个项。
     * 
     * @param params
     */
    public void putParameters(Map<String, ? extends Object> params){
        if(params == null) return;
        
        for( Entry<String, ? extends Object> entry : params.entrySet() ){
            String key = entry.getKey();
            Object value = params.get(key);
            
            if(value instanceof Object[]){
                Object[] objs = (Object[]) value;
                if(objs.length > 0) {
                    root.put(key, objs[0]);
                }
            }
            else {
                root.put(key, value);
            }
        }
    }
    
    /**
     * 将XML数据转换为NodeModel
     */
    public static NodeModel translateValue(String xmlValue){
    	NodeModel node = null;
        try {
        	node = freemarker.ext.dom.NodeModel.parse(new InputSource(new StringReader(xmlValue)));
        } catch (Exception e) { }
        
        return node;
    }
}