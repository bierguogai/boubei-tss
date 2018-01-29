package com.boubei.tss.portal.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.boubei.tss.cache.extension.workqueue.OutputRecordsManager;
import com.boubei.tss.dm.dml.SQLExcutor;
import com.boubei.tss.framework.persistence.connpool.Output2DBTask;
import com.boubei.tss.portal.entity.FlowRate;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p> FlowrateManager.java </p> 
 * 页面流量访问统计
 */
public class FlowrateManager extends OutputRecordsManager{
    
    private static FlowrateManager manager;
    
    private FlowrateManager(){
    }
    
    public static FlowrateManager getInstanse(){
        if(manager == null)
            manager = new FlowrateManager();
        return manager;
    }
    
    protected void excuteTask(List<Object> temp) {
        OutputFlowrateTask task = new OutputFlowrateTask();
        task.fill(temp);

        tpool.excute(task);
    }
    
    public static class OutputFlowrateTask extends Output2DBTask {

        protected Statement createRecords(Connection conn) throws SQLException {
        	String insertSql = "insert into portal_flowrate(pageId, ip, visitTime) values(?, ?, ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            for (Iterator<?> it = records.iterator(); it.hasNext();) {
                FlowRate temp = (FlowRate) it.next();
                
                if(checkIsTheSameVisitor(conn, temp)) {
                    continue;
                }
                
                int index = 1;
                pstmt.setLong(index++, temp.getPageId());
                pstmt.setString(index++, temp.getIp());
                pstmt.setTimestamp(index++, new Timestamp(temp.getVisitTime().getTime()));
                
                pstmt.execute();
            }
            return pstmt;
        }

        /**
         * 如果数据库中已经有这样一条记录：和当前访问相同的页面且相同的IP，并且相隔时间在三分钟内.
         * 则判定为是重复访问，不记流量。
         */
        private boolean checkIsTheSameVisitor(Connection conn, FlowRate temp) {
            String sql = "select count(*) as num from portal_flowrate t " +
            		" where t.ip =? and t.pageId = ? and t.visitTime > ? ";
            
            String ip = temp.getIp();
			Long pageId = temp.getPageId();
			Date fromTime = new Date(System.currentTimeMillis() - 1*60*1000);
			
			SQLExcutor ex = new SQLExcutor();
			Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
			paramsMap.put(1, ip);
			paramsMap.put(2, pageId);
			paramsMap.put(3, fromTime);
			ex.excuteQuery(sql, paramsMap, conn);
			
			return EasyUtils.obj2Int(ex.getFirstRow("num")) > 0;
        }
    }
}

