package com.boubei.tss.framework.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.framework.web.dispaly.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.FileHelper;

/**
 * 文件上传的文件载体要么在服务器端具备可执行性，要么具备影响服务器端行为的能力，其发挥作用还需要具备以下几个条件：
 * -1. 上传的文件具备可执行性或能够影响服务器行为，所以文件后所在的目录必须在WEB容器覆盖的路径之内；
 * -2. 用户可以从WEB上访问这个文件，从而使得WEB容器解释执行该文件；
 * -3. 上传后的文件必须经过应用程序的安全检查，以及不会被格式化、压缩等处理改变其内容
 * 
 * 如何安全上传文件:
 * -1. 最有效的，将文件上传目录直接设置为不可执行，对于Linux而言，撤销其目录的'x'权限；
 * -2. 文件类型检查：强烈推荐白名单方式，结合MIME Type、后缀检查等方式；此外对于图片的处理可以使用压缩函数或resize函数，处理图片的同时破坏其包含的HTML代码；
 * -3. 使用随机数改写文件名和文件路径，使得用户不能轻易访问自己上传的文件
 * -4. 单独设置文件服务器的域名
 * 
 * 注：最大可以上传文件大小为20M = 20971520Byte
 */
@WebServlet(urlPatterns="/auth/file/upload")
@MultipartConfig(maxFileSize = 1024 * 1024 * 20)
public class Servlet4Upload extends HttpServlet {

	private static final long serialVersionUID = -6423431960248248353L;

	Logger log = Logger.getLogger(this.getClass());

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		XmlHttpEncoder encoder = new XmlHttpEncoder();
		try {
	        Part part = request.getPart("file");
			String script = doUpload(request, part); // 自定义输出到指定目录
			if(script != null) {
				encoder.put("script", script);
			}
		} catch (Exception e) {
			String errorMsg = "上传失败：" + e.getMessage() + ", " + ExceptionEncoder.getFirstCause(e).getMessage();
			log.error(errorMsg );
			
			String alertErr = errorMsg + "。如果是数据文件，请检查数据内容是否正确。" +
					"同时请检查文件是否过大，单个文件最大不宜超过3M。如果过大，请压缩处理后再上传。！";
            alertErr = Pattern.compile("\t|\r|\n|\'").matcher(alertErr).replaceAll(""); // 剔除换行，以免alert不出来
			encoder.put("script", "alert('" + alertErr + "');");
		}

		response.setContentType("text/html;charset=utf-8");
		encoder.print(new XmlPrintWriter(response.getWriter()));
	}
	
	String doUpload(HttpServletRequest request, Part part) throws Exception {
		// gets absolute path of the web application, tomcat7/webapps/tss
		String defaultUploadPath = request.getServletContext().getRealPath("");
		
		String uploadPath = ParamConfig.getAttribute(PX.UPLOAD_PATH, defaultUploadPath);
        String savePath = uploadPath + File.separator + "uploadFile";
   	 
        File fileSaveDir = new File(savePath);
        if ( !fileSaveDir.exists() ) {
            fileSaveDir.mkdirs();
        }
        
		// 获取上传的文件真实名字(含后缀)
		String contentDisp = part.getHeader("content-disposition");
		String orignFileName = "";
		String[] items = contentDisp.split(";");
		for (String item : items) {
			if (item.trim().startsWith("filename")) {
				orignFileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
				break;
			}
		}
		
		String subfix = FileHelper.getFileSuffix(orignFileName), newFileName;
		
		// 允许使用原文件名
		String useOrignName = request.getParameter("useOrignName");
		if(useOrignName != null) {
			newFileName = orignFileName;
		} else {
			newFileName = System.currentTimeMillis() + "." + subfix; // 重命名
		}
		
        String newFilePath = savePath + File.separator + newFileName;
        
        // 自定义输出到指定目录
		InputStream is = part.getInputStream();
		FileOutputStream fos = new FileOutputStream(newFilePath);
		int data = 0;
		while((data = is.read()) != -1) {
		  fos.write(data);
		}
		fos.close();
		is.close();
		
		String afterUploadClass = request.getParameter("afterUploadClass");
		if(afterUploadClass != null) {
			AfterUpload afterUpload = (AfterUpload) BeanUtil.newInstanceByName(afterUploadClass);
			return afterUpload.processUploadFile(request, newFilePath, orignFileName);
		}
		
		return null;
	}
}
