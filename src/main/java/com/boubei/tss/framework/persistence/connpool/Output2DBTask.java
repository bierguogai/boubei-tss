/* ==================================================================   
 * Created [2007-2-15] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018  
 * ================================================================== 
 */

package com.boubei.tss.framework.persistence.connpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.workqueue.AbstractTask;

/**
 * <pre>
 * 输出记录到数据库的任务抽象超类。
 * 类似日志输出、访问量输出可以通过继承该超类实现。
 * </pre>
 */
public abstract class Output2DBTask extends AbstractTask {

	public void excute() {
		if (records == null) return;

		Pool connectionPool = JCache.getInstance().getConnectionPool();
		Cacheable connItem = connectionPool.checkOut(0);
		Connection conn = (Connection) connItem.getValue();
		Statement statement = null;
		try {
			statement = createRecords(conn);
		} 
		catch (SQLException e) {
			log.error("写入记录到数据库时候出错。records = " + records, e);
		} 
		finally {
			try { statement.close(); } catch (SQLException e) { }
			
			connectionPool.checkIn(connItem);
		}
	}
	
	protected abstract Statement createRecords(Connection conn) throws SQLException;

}
