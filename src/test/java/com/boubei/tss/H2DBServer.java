package com.boubei.tss;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.h2.tools.Server;
import org.springframework.stereotype.Component;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

@Component
public class H2DBServer {  
    
    static Logger log = Logger.getLogger(H2DBServer.class);
    
    private Server server;  
    
    public static int DEFAULT_PORT = 9081;  
    
    public String URL = "jdbc:h2:mem:h2db;DB_CLOSE_DELAY=-1;LOCK_MODE=0"; // Connection关闭时不停用H2 server
    public String user = "sa";  
    public String password = "123";  
    
    boolean isPrepareed = false;
    public int port; 
    
    Connection conn;
    
    public H2DBServer() {
    	port = DEFAULT_PORT; 
    	log.info("正在启动H2 database, 尝试端口号：" + port);  
    	
    	/* 
    	 * 此时H2数据库只起来了服务，没有实例。支持部署多个web应用时，启动多个不同端口的H2实例 
    	 */
    	while(server == null && port <= 12000) {
    		try {  
                server = Server.createTcpServer("-tcpPort", String.valueOf(port)).start();  
            } catch (Exception e) {  
                log.warn("启动H2（createTcpServer）时出错：" + e.getMessage() );  
                port ++;
            } 
    	} 
    	
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
    	
    	log.info("启动H2 成功 ，端口号：" + port);  
        
        try {  
	    	// 在以URL取得连接以后，数据库实例h2db才创建完成
	        Class.forName("org.h2.Driver");  
	        conn = DriverManager.getConnection(URL, user, password);  
    	} 
        catch (Exception e) {  
            log.error("建立H2连接时出错：" + e.toString());  
        } 
    }
 
    
    public void stopServer() {  
        if (server != null) {  
            log.info("正在关闭H2 database...端口号：" + port);  
            
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException("关闭H2 database连接出错：" + e.toString(), e);  
            }  
            server.shutdown();
            server.stop();
            
            log.info("关闭H2 database成功...端口号：" + port);  
        }  
    }  
 
	public Connection getH2Connection() {
		return conn;
	}  
	
	public static void excuteSQL(String sqlDir) {  
        log.info("正在执行目录：" + sqlDir+ "下的SQL脚本。。。。。。");  
        
        Pool connePool = JCache.getInstance().getConnectionPool();
		Cacheable connItem = connePool.checkOut(0);
		
        try {  
        	Connection conn = (Connection) connItem.getValue();
            Statement stmt = conn.createStatement();  
            
            List<File> sqlFiles = FileHelper.listFilesByTypeDeeply(".sql", new File(sqlDir));
            for(File sqlFile : sqlFiles) {
            	String fileName = sqlFile.getName();

            	log.info("开始执行SQL脚本：" + fileName+ "。");  
            	
                String sqls = FileHelper.readFile(sqlFile, "UTF-8");
                String[] sqlArray = sqls.split(";");
                for(String sql : sqlArray) {
                	if( EasyUtils.isNullOrEmpty(sql) ) continue;
                	
                	log.debug(sql);  
                	stmt.execute(sql);
                }
				
                log.info("SQL脚本：" + fileName+ " 执行完毕。");  
            }
 
            log.info("成功执行目录：" + sqlDir+ "下的SQL脚本!");
            stmt.close(); 
            
        } catch (Exception e) {  
            throw new RuntimeException("目录：" + sqlDir+ "下的SQL脚本执行出错：", e);
        } finally {
        	connePool.checkIn(connItem);
        }
    }
}  
