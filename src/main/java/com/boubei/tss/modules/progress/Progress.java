package com.boubei.tss.modules.progress;

import java.text.DecimalFormat;

/** 
 * <p> 记录进度相关信息类。 </p> 
 * 
 * 进度最重要的两个数据：总数 和 当前完成个数 需要由调用进度的对象传入。
 * 
 */
public class Progress {
    public static final String COMPLETE_FLAG = "100"; //任务完成标志

    /**
     * 默认取进度信息时间  2s
     */
    public static final long DEFAULT_LOAD_SCHEDULE_TIME = 2;
    
    /**
     * 取进度信息的最长时间 60s
     */
    public static final long MAX_LOAD_SCHEDULE_TIME = 60;
    
    /**
     * 默认取进度次数
     */
    public static final long DEFAULT_LOAD_TIMES = 100; 
    
    private long total;             //总数
    private long completed;         //完成数
    private long estimateTime;      //估计剩余时间   单位：秒
    private String percent;         //完成百分比
    private long delay;             //下次取进度的时间间隔  单位：秒
    private long previousTime;      //上次取进度的时间
    
    private boolean isConceal = false;//是否取消
    private boolean normal = true;   //线程正常
    private Throwable t;            //异常信息
    
    public Progress(long total) {
        this.total = total;
        this.completed = 0;
        this.delay = DEFAULT_LOAD_SCHEDULE_TIME;
        this.previousTime = System.currentTimeMillis();
        
        DecimalFormat format = new DecimalFormat("0.####");
        this.percent = (total == 0) ? format.format(100) : "0";
    }
    
    public synchronized void add(long change) {
        setCompleted(completed + change);
    }

    private void setCompleted(long completed) {
        completed = completed > total ? total : completed;
        
        // 本次完成总个数 － 上次完成总个数 ＝ 本次完成个数
        long curComplete = completed - this.completed; 
        
        //本次执行结束离上次执行结束的时间
        long curConsumeTime = Math.max(1, (System.currentTimeMillis() - previousTime) / 1000); 
        
        if (curComplete != 0) {
            // 计算剩余完成时间
            this.estimateTime = (total - completed) * curConsumeTime/ curComplete; 

            // 根据上一阶段的时间计算下一次取进度的时间间隔，如果大于60秒，则按60秒取；如果小于2秒，则按2秒取
            long estimateTotalTime = total * curConsumeTime / curComplete;
            long delay = estimateTotalTime / DEFAULT_LOAD_TIMES;
            this.delay = Math.min(delay, MAX_LOAD_SCHEDULE_TIME);
            this.delay = Math.max(this.delay, DEFAULT_LOAD_SCHEDULE_TIME);
        }
        
        DecimalFormat format = new DecimalFormat("0.####");
        this.percent = format.format(total == 0 ?  100D : new Double(100 * completed/total));
        this.previousTime = System.currentTimeMillis();
        this.completed = completed;
    }
    
    /**
     * 向界面返回进度信息，包括当前进度、下一次读取进度延迟时间、剩余时间
     * @return
     */
    public synchronized Object[] getProgressInfo() {
        Object[] obj = new Object[3];
        obj[0] = percent;                //当前进度
        obj[1] = new Long(delay);        //读取进度延迟时间
        obj[2] = new Long(estimateTime); //剩余时间
        return obj;
    }
    
    public boolean isCompleted() {
        return Progress.COMPLETE_FLAG.equals(percent);
    }

    public Throwable getException() {
        return t;
    }

    public void setException(Throwable t) {
        this.t = t;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public boolean isConceal() {
        return isConceal;
    }

    public void setIsConceal(boolean isConceal) {
        this.isConceal = isConceal;
    }
}

