/* ==================================================================   
 * Created [2016-06-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.modules.log;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.boubei.tss.framework.persistence.connpool.Output2DBTask;
import com.boubei.tss.util.EasyUtils;

/** 
 *　日志输出任务
 */
public class LogOutputTask extends Output2DBTask implements Serializable {

	private static final long serialVersionUID = 4414166319649429147L;

	protected Statement createRecords(Connection conn) throws SQLException {
    	String insertSql = "insert into component_log" +
                "(operatorId, operatorName, operatorIP, operationCode, operateTable, operateTime, content, methodExcuteTime, operatorBrowser) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

        PreparedStatement pstmt = conn.prepareStatement(insertSql); 
        for ( Object temp : records ) {
            Log dto = (Log) temp;
            
            int index = 1;
            pstmt.setLong  (index++, (Long) EasyUtils.checkNull(dto.getOperatorId(), 0L));
            pstmt.setString(index++, dto.getOperatorName());
            pstmt.setString(index++, dto.getOperatorIP());
            pstmt.setString(index++, dto.getOperationCode());
            pstmt.setString(index++, dto.getOperateTable());
            pstmt.setTimestamp(index++, new java.sql.Timestamp(dto.getOperateTime().getTime()));
            pstmt.setString(index++, dto.getContent());
            pstmt.setInt(index++, dto.getMethodExcuteTime() == null ? 0 : dto.getMethodExcuteTime());
            pstmt.setString(index++, dto.getOperatorBrowser());
            
            pstmt.execute();
        }
        
        pstmt.close();
        
        return pstmt;
    }
}


