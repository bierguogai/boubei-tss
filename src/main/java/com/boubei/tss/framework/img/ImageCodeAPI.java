package com.boubei.tss.framework.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 生成图形验证码、条形码、二维码等图形
 */
@Controller
@RequestMapping("/imgcode")
public class ImageCodeAPI {
	
	/**
	 * 生成图形验证码：http://localhost:9000/tss/imgcode/ck/8341
	 */
	@RequestMapping(value = "/ck/{code}", method = RequestMethod.GET)
	public void createCKCodeImg(@PathVariable("code") String code,
			HttpServletRequest request, HttpServletResponse response) {
 
		ServletOutputStream outputStream = null;
		int width = 62, height = 30;
        
		try {   
	        Font font = new Font("Serif", Font.ITALIC, 24);   
	        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
	        Graphics2D g2 = (Graphics2D)bi.getGraphics();   
	        g2.setBackground(Color.WHITE);   
	        g2.clearRect(0, 0, width, height);   
	        g2.setPaint(Color.RED);   
	        g2.setFont(font);
	           
	        FontRenderContext context = g2.getFontRenderContext();   
	        Rectangle2D bounds = font.getStringBounds(code, context);   
	        double x = (width - bounds.getWidth()) / 2.5;   
	        double y = (height - bounds.getHeight()) / 1.2;   
	        double baseY = y - bounds.getY();   
	           
	        g2.drawString(code, (int)x, (int)baseY);   
	        
	        outputStream = response.getOutputStream(); 
	        ImageIO.write(bi, "jpg", outputStream); 
	        
		} catch (IOException e) {
		} finally {
			if(outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) { }
			}
		}	
	}
	
	/**
	 * 生成条形码：http://localhost:9000/tss/imgcode/bar/10249025592?size=1.2
	 */
	@RequestMapping(value = "/bar/{code}", method = RequestMethod.GET)  
    public void createBarCodeImg(@PathVariable("code") String code, 
    		HttpServletRequest request, HttpServletResponse response) throws IOException { 
   
		if ( StringUtils.isEmpty(code) ) return;
		String _size = request.getParameter("size");
		double size = 1.0d;
		if( !EasyUtils.isNullOrEmpty(_size) ) {
			try {
				size = EasyUtils.obj2Double(_size);
			} catch(Exception e) { }
		}
		
        final int dpi = (int) (120*size); // 精细度
        final double moduleWidth = UnitConv.in2mm(1.0f*size / dpi); // module宽度
 
        // 配置对象
        Code128Bean bean = new Code128Bean();
        bean.setModuleWidth(moduleWidth);
        bean.setHeight(8.0D * Math.min(size, 2));
        bean.doQuietZone(false);
 
        ServletOutputStream ous = response.getOutputStream();
        try {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, "image/png", dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
            
            bean.generateBarcode(canvas, code); // 生成条形码
            canvas.finish(); // 结束绘制
        } 
        catch (IOException e) {
            throw new BusinessException("生成条形码时出错了", e);
        } finally {
        	if(ous != null) {
				try {
					ous.flush();
					ous.close();
				} catch (IOException e) { }
			}
        }
    }  
	
	@RequestMapping(value = "/qrbar/{code}", method = RequestMethod.GET)  
	public void createQrBarCodeImg(@PathVariable("code") String code,
			HttpServletRequest request, HttpServletResponse response) {
		
		String msg = request.getParameter("msg");
		if(msg == null) {
			msg = code;
		}
		
		QRCodeWriter writer = new QRCodeWriter();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		BitMatrix bitMatrix = null;
		try {
			bitMatrix = writer.encode(msg, BarcodeFormat.QR_CODE, 300, 300);
			
			ServletOutputStream outputStream = response.getOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", outputStream);
			outputStream.flush();
			outputStream.close();
			
		} catch (Exception e) {
			throw new BusinessException("生成二维码时出错了", e);
		} 
	}
}
