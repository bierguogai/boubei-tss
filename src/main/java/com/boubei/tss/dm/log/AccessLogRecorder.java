package com.boubei.tss.dm.log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.sqlquery.SqlConfig;
import com.boubei.tss.cache.extension.workqueue.AbstractTask;
import com.boubei.tss.cache.extension.workqueue.OutputRecordsManager;

public class AccessLogRecorder extends OutputRecordsManager {

    private static AccessLogRecorder instance;

    private AccessLogRecorder() {}

    public static AccessLogRecorder getInstanse() {
        if (instance == null) {
            instance = new AccessLogRecorder();
        }
        return instance;
    }

    protected int getMaxSize() {
        return 20;
    }

    protected int getMaxTime() {
        return 5 * 60 * 1000;
    }

    protected void excuteTask(List<Object> logs) {
        AbstractTask task = new AccessLogOutputTask();
        task.fill(logs);

        tpool.excute(task);
    }

    private final class AccessLogOutputTask extends AbstractTask {
        public void excute() {
            List<Map<Integer, Object>> paramsMapList = new ArrayList<Map<Integer, Object>>();
            for (Object temp : records) {
                AccessLog log = (AccessLog) temp;
                Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
                int index = 1;
                paramsMap.put(index++, log.getClassName());
                paramsMap.put(index++, log.getMethodName());
                paramsMap.put(index++, log.getMethodCnName());
                paramsMap.put(index++, new Timestamp(log.getAccessTime().getTime()));
                paramsMap.put(index++, log.getRunningTime());
                paramsMap.put(index++, log.getParams());
                paramsMap.put(index++, log.getUserId());
                paramsMap.put(index++, log.getIp());

                paramsMapList.add(paramsMap);
            }

            String script = SqlConfig.getScript("saveAccessLog", 1);
            if(script == null) {
            	script = "insert into dm_access_log " +
            			"(className, methodName, methodCnName, accessTime, runningTime, params, userId, ip) " +
            			"values (?, ?, ?, ?, ?, ?, ?, ?)";
            }
            SQLExcutor.excuteBatch(script, paramsMapList, DMConstants.LOCAL_CONN_POOL);
        }
    }
}
