package com.boubei.tss.cms.helper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 制作图片文件的缩略图
 */
public class ImageProcessor {
    
    private String destFilePath;// 如果需要保存目标文件到其他目录时需要
    
    private String srcFile;
    private String destFile;
    
    private Image img;

    private int width;
    private int height;

    /**
     * 构造函数
     * @param filePath
     *            构造函数参数 源文件（图片）的路径
     * @throws IOException
     */
    public ImageProcessor(String filePath) throws IOException {
        File _file = new File(filePath); // 读入文件
        destFilePath = _file.getParent();
        srcFile = _file.getName();
        
        // 生成文件命名为原文件名 + "_s"
        destFile = srcFile.substring(0, srcFile.lastIndexOf(".")) + "_s.jpg"; 
        
        // 构造Image对象
        img = javax.imageio.ImageIO.read(_file); 
       
        width  = img.getWidth(null);  // 得到源图宽
        height = img.getHeight(null); // 得到源图长
    }

    /**
     * 强制压缩/放大图片到固定的大小
     * 
     * @param w
     *            int 新宽度
     * @param h
     *            int 新高度
     * @throws IOException
     */
    public String resize(int w, int h) throws IOException {
        BufferedImage _image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        _image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图

        String target = destFilePath + "/" + destFile;
        
        FileOutputStream out = new FileOutputStream(target); // 输出到文件流
        ImageIO.write(_image, "JPEG", out);
        out.close();
        
        return target;
    }

    /**
     * 按照固定的比例缩放图片
     * 
     * @param t
     *            double 比例
     * @throws IOException
     */
    public String resize(double t) throws IOException {
        int w = (int) (width * t);
        int h = (int) (height * t);
        return resize(w, h);
    }

    /**
     * 按照最大高度限制，生成最大的等比例缩略图
     * 
     * @param w
     *            int 最大宽度
     * @param h
     *            int 最大高度
     * @throws IOException
     */
    public String resizeFix(int w, int h) throws IOException {
        if (width / height > w / h) {
            h = (int) (height * w / width);
        } else {
            w = (int) (width * h / height);
        }
        return resize(w, h);
    }
}
