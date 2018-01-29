package com.boubei.tss.um.syncdata;

import java.util.Map;
 
public interface ISyncService {

    /**
     * 获取完全同步组时候需要用到的数据
     * 
     * @param mainGroupId   
     *              选中进行同步的组ID
     * @param applicationId  
     *              应用ID
     * @param fromGroupId 
     *              选中进行同步的组对应外部应用的ID
     * @return
     */
    Map<String, Object> getCompleteSyncGroupData(Long mainGroupId, String applicationId, String fromGroupId);
 

}
