package com.boubei.tss.cms;

import java.io.Serializable;

/** 
 * <p> 文件附件AttachmentDTO对象, 用于附件下载</p>
 */
public class AttachmentDTO implements Serializable {

    private static final long serialVersionUID = -263389438942564792L;
    
    public Integer type;			// 附件类型	1：图片 2：office文档
    public String  name;			// 原名称
    public String  fileName;		// 文件名
    public String  fileExt;			// 文件后缀
    
    public String  localPath;
    public String[] basePath;      // 站点存放附件的根目录
    
    public AttachmentDTO(Integer type, String name, String fileName, String fileExt, String localPath, String[] basePath){
        this.type = type;
        this.name = name;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.localPath = localPath;
        this.basePath = basePath;
    }
}