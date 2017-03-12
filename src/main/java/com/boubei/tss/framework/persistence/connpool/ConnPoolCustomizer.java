/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */
package com.boubei.tss.framework.persistence.connpool;

import java.sql.Connection;
import java.sql.SQLException;

import com.boubei.tss.cache.CacheStrategy;
import com.boubei.tss.cache.DefaultCacheCustomizer;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.TimeWrapper;
import com.boubei.tss.framework.exception.BusinessException;

/**
 * <pre>
 * 数据库连接池自定义类。<br/>
 * 
 * 在本类中定义了如何创建、验证、销毁数据库连接。
 * </pre>
 */
public class ConnPoolCustomizer extends DefaultCacheCustomizer {
    
	public Cacheable create() {
		if( CacheStrategy.TRUE.equals(strategy.disabled) ) {
			throw new BusinessException("数据库存在异常，【" + strategy.name + "】已被停用，请稍后再访问");
		}
		
		Connection conn = _Connection.getInstanse(strategy.paramFile).getConnection();
		String cacheKey = TimeWrapper.createSequenceKey("Connection");
		
		// 包装新创建的Connection，赋予其生命周期。
		return new TimeWrapper(cacheKey, conn, strategy.cyclelife);
	}

	public boolean isValid(Cacheable o) {
		Connection conn = (Connection) o.getValue();
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	public void destroy(Cacheable o) {
		if (o == null)
			return;

		Connection conn = (Connection) o.getValue();
		_Connection.getInstanse(strategy.paramFile).releaseConnection(conn);
		o = null;
	}

	public Cacheable reloadCacheObject(Cacheable item) {
		return create();
	}
}
