package com.boubei.tss.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.cache.extension.workqueue.AbstractTask;
import com.boubei.tss.cache.extension.workqueue.OutputRecordsManager;
import com.boubei.tss.framework.persistence.connpool.Output2DBTask;
import com.boubei.tss.util.EasyUtils;

/** 
 * 记录（文章、附件等）点击率统计
 */
public class HitRateManager extends OutputRecordsManager{
    
    private static Map<String, HitRateManager> managers = new HashMap<String, HitRateManager>();
    
    private String updateSQL;
    private HitRateManager(String table) { 
    	this.updateSQL = "update " +table+ " t set t.hitCount = IFNULL(t.hitCount,0) + 1 where t.id = ?";
    }
    
    public static HitRateManager getInstanse(String table) {
    	HitRateManager manager = managers.get(table);
        if(manager == null) {
        	managers.put(table, manager = new HitRateManager(table));
        }
        return manager;
    }
    
    protected void excuteTask(List<Object> temp) {
        AbstractTask task = new Output2DBTask() {
            protected Statement createRecords(Connection conn) throws SQLException {
                PreparedStatement pstmt = conn.prepareStatement(updateSQL);
                for ( Object temp : records ) {
                    pstmt.setLong(1, EasyUtils.obj2Long(temp));
                    pstmt.execute();
                }
                
                return pstmt;
            }
        };
        task.fill(temp);

        tpool.excute(task);
    }
    
    public void output(Object record){
        super.output(record);
    }
    
    protected int getMaxSize(){ return 10; }
}

