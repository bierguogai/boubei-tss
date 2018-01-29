/* ==================================================================   
 * Created [2007-5-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */
package com.boubei.tss.framework.persistence.connpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.hibernate.util.NamingHelper;

import com.boubei.tss.EX;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.ConfigurableContants;
import com.boubei.tss.util.EasyUtils;

/**
 * 管理数据库连接的provider，以及创建或释放掉连接.
 * 如果连接池定义了自己的数据源信息，则采用；否则从默认的系统配置文件里加载。
 */
public class _Connection extends ConfigurableContants {

	protected final Logger log = Logger.getLogger(this.getClass());

	private IConnectionProvider provider;
	
	/**
	 * 如果配置的是数据源，则优先从数据源获取连接；否则手动创建一个连接。
	 */
	private _Connection(String propertiesX) {
		if(propertiesX.endsWith(".properties")) {
			Properties dbProperties = super.init(propertiesX);
		    
			if (dbProperties.getProperty(Environment.DATASOURCE) != null) {
				provider = new DatasourceConnectionProvider(dbProperties);
			} else {
				provider = new DriverManagerConnectionProvider(dbProperties);
			}
		}
		else {
			provider = new DriverManagerConnectionProvider(propertiesX);
		}
	}

	static Map<String, _Connection> _connectionMap = new HashMap<String, _Connection>();
 
    public static _Connection getInstanse(String propertiesFile) {
        propertiesFile = (String) EasyUtils.checkNull(propertiesFile, DEFAULT_PROPERTIES);
        _Connection _connection = _connectionMap.get(propertiesFile);
        if (_connection == null) {
            _connectionMap.put(propertiesFile, _connection = new _Connection(propertiesFile));
        }
        return _connection;
    }

	public Connection getConnection() {
		return provider.getConnection();
	}

	public void releaseConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			log.fatal("销毁数据库连接时候出错", e);
		}
	}
 
	/*************************************** 获取连接接口  ***************************/
	interface IConnectionProvider {
		Connection getConnection();
	}

	/**
	 * <pre>
	 *  ## JNDI Datasource 
	 *  hibernate.connection.datasource jdbc/tss  
	 *  hibernate.connection.username db2  
	 *  hibernate.connection.password db2  
	 * </pre>
	 */
	static class DatasourceConnectionProvider implements IConnectionProvider {
		
		Properties p;
		
		public DatasourceConnectionProvider(Properties p) {
			this.p = p;
		}
		
		public Connection getConnection() {
			String user = p.getProperty(Environment.USER);
			String pass = p.getProperty(Environment.PASS);
			String jndiName = p.getProperty(Environment.DATASOURCE);
			
			try {
				DataSource ds = (DataSource) NamingHelper.getInitialContext(p).lookup(jndiName);
				return ds.getConnection(user, pass);
			} 
			catch (Exception e) {
				throw new BusinessException("Error when get connection from datasource: " + jndiName, e);
			}
		}
	}
 
	class DriverManagerConnectionProvider implements IConnectionProvider {
 
	    String driver, url, user, pwd;
		
		public DriverManagerConnectionProvider(Properties p) {
			driver = p.getProperty("db.connection.driver_class").trim();
			url    = p.getProperty("db.connection.url").trim();
			user   = p.getProperty("db.connection.username").trim();
			pwd    = p.getProperty("db.connection.password").trim();
		}
		
		public DriverManagerConnectionProvider(String config) {
			String[] infos = config.split(",");
			driver = infos[0].trim();
			url    = infos[1].trim();
			user   = infos[2].trim();
			pwd    = infos[3].trim();
		}
		
		public Connection getConnection() {
			Connection conn = null;
	        try {
	            Class.forName(driver);
	            DriverManager.setLoginTimeout(30);
				conn = DriverManager.getConnection(url, user, pwd);
	        } 
	        catch (Exception e) {
	        	log.error(EX.parse(EX.F_03, url, user, e.getMessage()));
	            throw new BusinessException( EX.parse(EX.F_03, driver, user, e.getMessage()) );
	        } 
	        return conn;
		}
	}
}