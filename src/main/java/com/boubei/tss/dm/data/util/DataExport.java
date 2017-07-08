package com.boubei.tss.dm.data.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.data.sqlquery.AbstractExportSO;
import com.boubei.tss.dm.data.sqlquery.AbstractVO;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

public class DataExport {
	
	public static final String CSV_CHAR_SET = "GBK";
	
	static Logger log = Logger.getLogger(DataExport.class);
	
	public static String getExportPath() {
		return DMUtil.getExportPath().replace("\n", "") + "/export";
	}
	
	/**
     * 将已经读取到缓存中的VOList分页展示给前台
     */
    public static Map<String, Object> getDataByPage(List<? extends AbstractVO> voList, int page, int rows ) {
    	Map<String, Object> rlt = new HashMap<String, Object>();
        rlt.put("total", voList.size());

        page = Math.max(1, page);
        rows = Math.max(1, rows);
        int fromLine = (page - 1) * rows;
        int toLine = page * rows;
        if (fromLine <= voList.size()) {
            toLine = Math.min(voList.size(), toLine);
            rlt.put("rows", voList.subList(fromLine, toLine));
        }
        
        if(voList.size() > 0) {
        	rlt.put("headerNames", voList.get(0).displayHeaderNames());
        }

        return rlt;
    }
    
    public static String exportCSV(List<Object[]> data, List<String> cnFields) {
    	String basePath = getExportPath();
        String exportFileName = System.currentTimeMillis() + ".csv";
		String exportPath = basePath + "/" + exportFileName;
		
		DataExport._exportCSV(exportPath, convertList2Array(data), cnFields );
		return exportFileName;
    }
    
    public static String exportCSV(List<? extends AbstractVO> voList, AbstractExportSO so) {
    	String basePath = getExportPath();
        String exportFileName = so.getExportFileName();
		String exportPath = basePath + "/" + exportFileName;

        List<String> cnFields = null;
        if(voList != null && voList.size() > 0) {
        	cnFields = voList.get(0).displayHeaderNames();
        }
        
        Object[][] data = convertList2Array(AbstractVO.voList2Objects(voList));
		_exportCSV(exportPath, data, cnFields );
		
		return exportFileName;
    }
    
    /**  把 List<Object[]> 转换成 Object[][] 的 */
    public static Object[][] convertList2Array(List<Object[]> list) {
        if (list == null || list.isEmpty()) {
            return new Object[0][0];
        }

        int rowSize = list.size();
        int columnSize = list.get(0).length;
        Object[][] rlt = new Object[rowSize][columnSize];

        for (int i = 0; i < rowSize; i++) {
            Object[] tmpArrays = list.get(i);
            for (int j = 0; j < columnSize; j++) {
                rlt[i][j] = tmpArrays[j];
            }
        }
        return rlt;
    }
    
    public static String exportCSVII(String fileName, Object[][] data, List<String> cnFields) {
    	String basePath = getExportPath();
		String exportPath = basePath + "/" + fileName;
		
		_exportCSV(exportPath, data, cnFields );
		return fileName;
    }

    private static void _exportCSV(String path, Object[][] data, List<String> fields) {
    	List<Object[]> list = new ArrayList<Object[]>();
    	for(Object[] temp : data) {
    		list.add(temp);
    	}
    	
    	DataExport.exportCSV(path, list, fields);
    }
 
    public static String exportCSV(String fileName, List<Map<String, Object>> data, List<String> fields) {
    	List<Object[]> list = new ArrayList<Object[]>();
        for (Map<String, Object> row : data) {
        	list.add( row.values().toArray() );
        }
        
        String exportPath = DataExport.getExportPath() + "/" + fileName;
    	exportCSV(exportPath, list, fields);
    	
    	return exportPath;
    }
    
    public static void exportCSV(String path, Collection<Object[]> data, List<String> fields) {
    	exportCSV(path, data, fields, CSV_CHAR_SET);
    }
    
    public static void exportCSV(String path, Collection<Object[]> data, List<String> fields, String charSet) {
        try {
        	File file = FileHelper.createFile(path);
        	boolean append = fields == null;
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file, append), charSet );
            BufferedWriter fw = new BufferedWriter(write);   
            
            if( !append ) {
            	fw.write(EasyUtils.list2Str(fields)); // 表头
            	fw.write("\r\n");
            }

            int index = 0;
            for (Object[] row : data) {
            	List<Object> values = new ArrayList<Object>();
            	for(Object value : row) {
            		String valueS = preCheatVal(value);
					values.add(valueS); 
            	}
                fw.write(EasyUtils.list2Str(values));
                fw.write("\r\n");

                if (index++ % 10000 == 0) {
                    fw.flush(); // 每一万行输出一次
                }
            }

            fw.flush();
            fw.close();
            
        } catch (IOException e) {
            throw new BusinessException("export csv error:" + path + ", " + e.getMessage());
        }
    }
    
    public static String preCheatVal(Object value) {
    	if(value == null) {
			value = "";
		}
		String valueS = value.toString().replaceAll(",", "，"); // 导出时字段含英文逗号会错列
		valueS = valueS.replaceAll("\r\n", " ").replaceAll("\n", " ");
		valueS = valueS.replaceAll("\"", "");
		return valueS; 
    }
    
    // 共Web页面上的表格数据直接导出成csv调用
    public static void exportCSV(String path, String data) {
        try {
        	File file = FileHelper.createFile(path);
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), CSV_CHAR_SET );
            BufferedWriter fw = new BufferedWriter(write);   
            
            fw.write(data);
            
            fw.flush();
            fw.close();
            
        } catch (IOException e) {
            throw new BusinessException("export data error:" + path, e);
        }
    }

    /**
     * 使用http请求下载附件。
     * @param sourceFilePath 导出文件路径
     * @param exportName  导出名字
     */
    public static void downloadFileByHttp(HttpServletResponse response, String sourceFilePath) {
        File sourceFile = new File(sourceFilePath);
        if( !sourceFile.exists() ) {
        	log.error("下载附件时，发现文件【" + sourceFilePath + "】不存在");
        	return;
        }
        
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream"); // 设置附件类型
        response.setContentLength((int) sourceFile.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + EasyUtils.toUtf8String(sourceFile.getName()) + "\"");
        
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
//            throw new BusinessException("导出时发生IO异常!", e);
        } finally {
        	sourceFile.delete();  // 删除导出目录下面的临时文件
        	FileHelper.closeSteam(inStream, outStream);        
        }
    }
}