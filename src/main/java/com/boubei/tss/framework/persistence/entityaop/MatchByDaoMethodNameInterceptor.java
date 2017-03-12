package com.boubei.tss.framework.persistence.entityaop;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/** 
 * 根据DAO类的方法进行模糊匹配从而实现拦截的超类，
 * 一般为拦截get、save、update、delete等类似方法。
 * 
 */
public abstract class MatchByDaoMethodNameInterceptor implements MethodInterceptor {
    
    public static final String SWITCH_CLOSE_TAG = "close";  // 拦截器关闭标记
    
    public static final int SAVE   = 1;
    public static final int GET    = 2;
    public static final int UPDATE = 3;
    public static final int DELETE = 4;
    
    protected String saveKind = "save,insert,create";
    protected String getKind  = "get,load,find";
    protected String updateKind = "save,update,modify,move";
    protected String deleteKind = "delete,remove"; // 注意remove跟move，删除判断要放前面
    
    public abstract Object invoke(MethodInvocation invocation) throws Throwable;
    
    protected int judgeManipulateKind(String methodName){
        if(match(methodName, Arrays.asList(deleteKind.split(","))))
            return DELETE;
        if(match(methodName, Arrays.asList(saveKind.split(","))))
            return SAVE;
        if(match(methodName, Arrays.asList(getKind.split(","))))
            return GET;
        if(match(methodName, Arrays.asList(updateKind.split(","))))
            return UPDATE;
        return 0;
    }
    
    protected boolean match(String methodName, List<String> list){
        for(Iterator<String> it = list.iterator(); it.hasNext();){
            if(methodName.indexOf((String)it.next()) != -1)
                return true;
        }
        return false;
    }

    public void setDeleteKind(String deleteKind) {
        this.deleteKind = deleteKind == null ? this.deleteKind : this.deleteKind + "," + deleteKind;
    }
    public void setGetKind(String getKind) {
        this.getKind = getKind == null ? this.getKind : this.getKind + "," + getKind;
    }
    public void setSaveKind(String saveKind) {
        this.saveKind = saveKind == null ? this.saveKind : this.saveKind + "," + saveKind;
    }
    public void setUpdateKind(String updateKind) {
        this.updateKind = updateKind == null ? this.updateKind : this.updateKind + "," + updateKind;
    }
}

