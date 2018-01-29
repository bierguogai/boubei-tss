package com.boubei.tss.portal.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.EX;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.helper.ComponentHelper;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

/**
 * 管理门户、页面、布局器、修饰器、布局器等的附件资源。
 */
@Controller
@RequestMapping("/auth/portal/file")
public class FileAction extends BaseActionSupport {
 
	@RequestMapping("/list")
    public void listAvailableFiles(HttpServletResponse response, HttpServletRequest request) {
        String code = request.getParameter("code");
        String type = request.getParameter("type"); // 判断是何种类型的资源管理
        String filter = request.getParameter("filter");
        String contextPath = request.getParameter("contextPath");
        
        StringBuffer sb = new StringBuffer("<actionSet title=\"\" openednodeid=\"r1.1\">"); 
        
        // 如果访问的是子目录，则提供目录上翻的按钮
        if( contextPath != null) {
        	int indexOfModel = contextPath.indexOf("model"); // portal/model
        	String tlDir = DMConstants.getReportTLDir();
			if( (indexOfModel > 0 && indexOfModel < contextPath.length() - 6) 
					|| ( contextPath.indexOf(tlDir) >= 0 && !contextPath.equals(tlDir)) ) {
        		sb.append("<treeNode id=\"-1\" name=\"..\" />"); 
        	}
        } 
        else {
        	if( Arrays.asList(Component.TYPE_NAMES).contains(type) ) {
        		contextPath = PortalConstants.MODEL_DIR + type;
        	}
        	else if( DMConstants.REPORT_TL_TYPE.equals(type) ) { // 报表模板资源
        		contextPath = DMConstants.getReportTLDir();
        	}
        	else { // 默认取门户资源目录根节点
        		contextPath = PortalConstants.PORTAL_MODEL_DIR;
        	}
        	
            if( !EasyUtils.isNullOrEmpty(code) ) {
            	contextPath = contextPath + "/" + code;
    		}
        }
        
        // 根据type值找根目录
        String absolutePath = URLUtil.getWebFileUrl(contextPath).getPath();
		File baseDir = new File(absolutePath); 
                
        filter = getFilter(filter);
        List<String> files= sortFile(baseDir, FileHelper.listFilesByType(filter, baseDir));
        
        int i = 0;
        for( String fileName : files ){
            sb.append("<treeNode id=\"").append(i++).append("\" name=\"").append(fileName);
            
            boolean isFolder = FileHelper.isFolder(baseDir, fileName);
            if(isFolder) {
                sb.append("\" isFolder=\"").append("1");
            }
            sb.append("\" icon=\"images/" + (isFolder ? "folder.gif" : "file.gif") + "\"/>");
        }
        sb.append("</actionSet>");      
        
        print(new String[] {"ResourceTree", "ContextPath"}, new Object[] {sb, contextPath});
    }
    
    /* 将子文件夹、文件进行归类，文件夹在前 */
    private List<String> sortFile(File baseDir, List<String> files){
        List<String> dirs = new ArrayList<String>();
        for(Iterator<String> it = files.iterator(); it.hasNext();){
            String fileName = it.next();
            if(FileHelper.isFolder(baseDir, fileName)){
                dirs.add(fileName);
                it.remove();
            }
        }
        
        // 对剩下的文件按文件名排序
        Collections.sort(files, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
        dirs.addAll(files);
        return dirs;
    }
    
    /**
     * 下载
     */
    @RequestMapping(method = RequestMethod.GET)
    public void download(HttpServletResponse response, HttpServletRequest request) {
    	String contextPath = request.getParameter("contextPath");
    	String fileNames = request.getParameter("fileNames");
    	String folderNames = request.getParameter("folderNames");
    	
        if( EasyUtils.isNullOrEmpty(fileNames) && EasyUtils.isNullOrEmpty(folderNames)) return;
        
        // 建立临时文件夹存放要下载的所有文件
        contextPath = getContextPath(contextPath);
        File tempDir = new File(contextPath + "temp"); 
        tempDir.mkdir();
        
        List<File> files = new ArrayList<File>();
        if( !EasyUtils.isNullOrEmpty(fileNames) ) {
        	String[] fNames = fileNames.split(",");
            for ( String fileName : fNames ) {
                File file = new File(contextPath + fileName);
                if (file.exists()) {
                	files.add(file);
                }
            }
        }
        
        if( !EasyUtils.isNullOrEmpty(folderNames) ) {
        	String[] fNames = folderNames.split(",");
            for ( String folderName : fNames ) {
                File folder = new File(contextPath + folderName);
                files.addAll(FileHelper.listFilesDeeply(folder));
            }
        }
        
        for(File file : files) {
        	FileHelper.copyFile(tempDir, file, true, false);  // 拷贝文件至临时文件夹
        }
        String zipFilePath = FileHelper.exportZip(contextPath, tempDir); // 将临时文件夹里的文件打包成zip文件
        FileHelper.deleteFile(tempDir); // 删除临时文件夹
        
        ComponentHelper.downloadFileByHttp(zipFilePath, "download.zip"); // 下载zip包
    }
    
    /**
     * 删除文件（文件夹）
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteFile(HttpServletResponse response, HttpServletRequest request) {
    	String contextPath = request.getParameter("contextPath");
    	String fileNames   = request.getParameter("fileNames");
    	String folderNames = request.getParameter("folderNames");
    	
        List<String> pathList = new ArrayList<String>();
        if( !EasyUtils.isNullOrEmpty(fileNames) ) {
            pathList.addAll(Arrays.asList(fileNames.split(",")));
        }
        if( !EasyUtils.isNullOrEmpty(folderNames) ) {
            pathList.addAll(Arrays.asList(folderNames.split(",")));
        }
        
        contextPath = getContextPath(contextPath);
        for(String fileOrDir : pathList){
            try {
                FileHelper.deleteFile(new File(contextPath + fileOrDir));
            } catch (Exception e) { }
        }
 
        print("script", "loadFileTree();");
    }
    
    /**
     * 重命名文件（文件夹）
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void renameFile(HttpServletResponse response, HttpServletRequest request) {
    	String contextPath = request.getParameter("contextPath");
    	String fileName    = request.getParameter("fileName");
    	String newFileName = request.getParameter("newFileName");
    	
    	contextPath = getContextPath(contextPath);
        File newFile = new File(contextPath + newFileName);
        if(newFile.exists()) {
            throw new BusinessException(EX.P_02);
        }
        
        File file = new File(contextPath + fileName);
        file.renameTo(newFile);
        
        print("script", "loadFileTree();");
    }
    
    /**
     * 新建文件夹 
     */
    @RequestMapping(method = RequestMethod.POST)
    public void addDir(HttpServletResponse response, HttpServletRequest request){
    	String contextPath = request.getParameter("contextPath");
    	String newFileName = request.getParameter("newFileName");
    	
    	contextPath = getContextPath(contextPath);
        FileHelper.createDir(contextPath + newFileName);
        print("script", "loadFileTree();");
    }
 
    public static String getContextPath(String contextPath) {
        String rootPath = URLUtil.getWebFileUrl("").getPath();
        return rootPath + "/" + contextPath + "/";
    }
 
    private String getFilter(String filter) {
        if(filter == null || filter.length() == 0 || "*.*".equals(filter)){
            return "";
        }
        if(filter.startsWith("*")){
            return filter.substring(1);
        }
        return filter;
    }
}

