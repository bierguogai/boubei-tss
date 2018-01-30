/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
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

import com.boubei.tss.EX;
import com.boubei.tss.cache.CacheStrategy;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.DefaultCacheCustomizer;
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
			throw new BusinessException( EX.parse(EX.F_04, strategy.name) );
		}
		
		Connection conn = _Connection.getInstanse(strategy.paramFile).getConnection();
		String cacheKey = TimeWrapper.createSequenceKey("Connection");
		
		// 包装新创建的Connection，赋予其生命周期。
		return new TimeWrapper(cacheKey, conn, strategy.cyclelife);
	}

	public boolean isValid(Cacheable o) {
		Connection conn = (Connection) o.getValue();
		boolean result = false;
		try {
			result = !conn.isClosed();
		} catch (SQLException e) {
		}
		return result;
	}

	public void destroy(Cacheable o) {
		Connection conn = (Connection) o.getValue();
		_Connection.getInstanse(strategy.paramFile).releaseConnection(conn);
		o = null;
	}

	public Cacheable reloadCacheObject(Cacheable item) {
		return create();
	}
}
