/* ==================================================================   
 * Created [2009-09-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.portal.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.service.IComponentService;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.XMLDocUtil;

/** 
 * 组件操作的帮助类。
 * 包括组件导入、导出等操作。
 */
public class ComponentHelper {   
	
	public static String CP_ROOT_NODE_NAME = "ComponentParams";
    
    /**
     * 从参数配置中获取布局器、修饰器、portlet的参数。
     * 参数的存放形式是:
     *  <ComponentParams>
     *     <portlet action="">model/portlet/gg299/paramsXForm.xml</portlet>
     *     <decorator moreUrl="">model/decorator/shyggxshq334/paramsXForm.xml</decorator>
     *  </ComponentParams>
     *  
     * 通过本方法可以获取到portlet实例或者修饰器实例相关的参数。
     * 
     * @param typeName
     * @param params
     * @return
     */
    static String getComponentConfig(String typeName, String params){
        if(params == null) {
            params = "<" + CP_ROOT_NODE_NAME + "><layout/><portlet/><decorator/></" + CP_ROOT_NODE_NAME + ">";
        }
        
        Document paramsDoc = XMLDocUtil.dataXml2Doc(params);
        return paramsDoc.selectSingleNode("//" + typeName).asXML();
    }
    
    public static String getPortletConfig(String params){
        return getComponentConfig(Component.PORTLET, params);
    }
    
    public static String getDecoratorConfig(String params){
        return getComponentConfig(Component.DECORATOR, params);
    }
    
    /**
     * 重新组合参数，上面方法的逆过程。
     * @param layoutConfig
     * @param decoratorConfig
     * @return
     */
    public static String createPortletInstanseConfig(String portletConfig, String decoratorConfig){
        StringBuffer sb  = new StringBuffer("<" + CP_ROOT_NODE_NAME + ">");
        sb.append(portletConfig).append(decoratorConfig);
        return sb.append("</" + CP_ROOT_NODE_NAME + ">").toString();
    }
    
    
    /**
     * *******************************************************************************************************************
     * ****************************************      以下为元素导入、导出    ************************************************
     * *******************************************************************************************************************
     */
    
    /**
     * 导入元素，XML格式或者zip包格式
     */
    public static void importComponent(IComponentService service, File file, Component component, 
    		String desDir, String eXMLFile) {
 
        String fileName = file.getName();
        if (fileName.endsWith(".xml")) {
            importXml(service, component, file);
        } 
        else if (fileName.endsWith(".zip")) {
            importZip(service, component, file, desDir, eXMLFile);
        }
    }
  
    /**
     * 根据元素XML配置文件，将各个属性设置到元素实体中，保存实体。
     * @param service
     * @param component
     * @param file
     * @return
     */
    private static Component importXml(IComponentService service, Component component, File file) {
        Document document = XMLDocUtil.createDocByAbsolutePath(file.getPath());
        
        try {
            org.dom4j.Element rootElement = document.getRootElement();
            if(!rootElement.getName().equals(component.getComponentType())) {
                throw new BusinessException(EX.P_08);
            }
            org.dom4j.Element propertyElement = rootElement.element("property");
            component.setName(propertyElement.elementText("name"));
            component.setDescription(propertyElement.elementText("description"));
            component.setVersion(propertyElement.elementText("version"));
            component.setDefinition(document.asXML());
            if(component.isLayout()) {
                component.setPortNumber(1);
            }
        } catch (Exception e) {
            throw new BusinessException(EX.P_09, e);
        }
        
        return service.saveComponent(component);
    }
    
    /**
     * 如果是导入zip包，则先将包解压到一个临时文件夹，
     * 然后导入其中的元素XML配置文件，成功导入元素后在重新命名临时文件夹为正式名。
     * @param service
     * @param component
     * @param importDir
     * @param desDir
     * @param eXMLFile
     */
    private static void importZip(IComponentService service, Component component, 
    		File importDir, String desDir, String eXMLFile) {
    	
        File tempDir = new File(desDir + "/" + System.currentTimeMillis());
        try {
			FileHelper.upZip(importDir, tempDir);
		} catch (Exception e) { }
        
        if ( !FileHelper.checkFile(tempDir, eXMLFile) ) {
            FileHelper.deleteFile(tempDir);
            throw new BusinessException( EX.parse(EX.P_18, eXMLFile) );
        }
        
        File eXMLFilePath = new File(desDir + "/" + tempDir.getName() + "/" + eXMLFile);
        component = importXml(service, component, eXMLFilePath);

        File newDir = new File(desDir + "/" + component.getCode());
        FileHelper.deleteFile(newDir);
        
        tempDir.renameTo(newDir);
    }
    
    /**
     * 导出一个元素
     * @param modelPath   pms/model/layout(docorator or portlet)的绝对路径
     * @param component        元素实体
     * @param eXMLFile    元素的XML文件名 "/decorator.xml"
     */
    public static void exportComponent(String modelPath, Component component, String eXMLFile) {
        String elementName = EasyUtils.toUtf8String(component.getName());
        String exportFileName = elementName + ".xml";;
        String outPath; // 导出zip或xml文件路径
        
        Document doc = XMLDocUtil.dataXml2Doc(component.getDefinition());
        doc.setXMLEncoding(System.getProperty("file.encoding"));
        
        String subpath = component.getCode();
		File filePath = FileHelper.findPathByName(new File(modelPath), subpath);
        if ( filePath != null ) {
            // 如果在pms/model/layout(docorator or portlet)文件夹下有该导出的xml文件就覆盖该文件,并以zip的形式导出
            exportFileName = elementName + ".zip";
            
            // 写回原来的文件
            FileHelper.writeXMLDoc(doc, filePath + "/" + eXMLFile); 
            
            // 导出成zip文件,并获得zip文件的路径
            outPath = FileHelper.exportZip(modelPath, filePath); 
        }
        else {
            // 如果model/layout(docorator、portlet)文件夹下没有导出的xml文件，则先将文件内容写到一个临时xml文件，再导出该临时文件
            FileHelper.writeXMLDoc(doc, outPath = modelPath + "/" + System.currentTimeMillis() + ".xml");
        }
        
        downloadFileByHttp(outPath, exportFileName);
    }
    
    /**
     * 使用http请求下载附件。
     * @param sourceFilePath 导出文件路径
     * @param exportName  导出名字
     */
    public static void downloadFileByHttp(String sourceFilePath, String exportName) {
    	HttpServletResponse response = Context.getResponse();
        response.reset(); // 设置附件下载页面
        response.setContentType("application/octet-stream"); // 设置附件类型
        response.setContentLength((int) new File(sourceFilePath).length()); // 文件长度
        response.setHeader("Content-Disposition", "attachment; filename=\"" + EasyUtils.toUtf8String(exportName) + "\""); // 设置标头
        
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            outStream = response.getOutputStream();
            inStream = new FileInputStream(sourceFilePath);
            
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inStream.read(b)) != -1) {
                outStream.write(b, 0, len);
                outStream.flush();
            }           
        } catch (IOException e) {
//            throw new BusinessException("download file error:", e);
        } finally {
        	FileHelper.closeSteam(inStream, outStream);        
        }
        new File(sourceFilePath).delete();  // 删除资源文件夹下面的zip文件
    }   
}