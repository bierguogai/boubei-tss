/* ==================================================================   
 * Created [2007-1-3] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
*/

package com.boubei.tss.cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.sso.Environment;

/** 
 * 对象池抽象基类，定义通用的方法。
 */
public abstract class AbstractPool implements Pool {
	
    protected Logger log = Logger.getLogger(this.getClass());
    
    protected void logDebug(String msg) {
    	log.debug(msg + "，" + Environment.threadID());
    }
 
    protected void logError(String msg) {
    	log.error(msg + "，" + Environment.threadID());
    }
    
    // 对象池属性
    
    /** 池中对象个数 */
    protected Integer size = 0;
    
    /** 请求数 */
    protected long requests;
    
    /** 命中数 */
    protected long hits;
    
    /** 命中时间 */
    protected long hitLong;
    
    /** 是否已释放 */
    protected boolean released = false;
    
    /** 缓存策略 */
    protected CacheStrategy strategy;
    
    /** 缓存自定义类 */
    protected CacheCustomizer customizer;

    /** 监听器列表 */
    protected Set<Listener> listeners = new HashSet<Listener>();
    
    public String getName() { 
    	return strategy.name; 
    }
    
	/**
	 * 返回空闲状态的对象池
	 */
    public abstract Container getFree();

	/**
	 * 返回使用状态的对象池
	 */
	public abstract Container getUsing();
	
	public String toString() {
		String display = "\n pool[" +this.getName()+ "，" +this.getHitRate()+ "]'s lastest snapshot: ";
        display += getFree();
	    display += getUsing();
		    
        return display + "\n";
	}
 
    public Cacheable getObject(Object key) {
        if(key == null) { 
            return null;
        }
        
        Cacheable item = getObjectOnly(key);
        addRequests();
        if(item != null) { // 命中则增加命中数
            addHits(); 
            item.addHit();
            item.updateAccessed();
        } 
        else {
            // 调用ICacheCustomizer来载入需要的缓存项，取到则放入缓存池中
            item = reload(new TimeWrapper(key, null));
            if(item != null) {
                putObject(item.getKey(), item.getValue());
            }
        }       
        return item;
    }
    
    public boolean contains(Object key) {
    	return getObjectOnly(key) != null;
    }
    
	/**
	 * 根据key值从缓存池中获取一个对象，不会增加点击率、请求数等信息。<br>
	 * 因为getObject(Object key)方法会增加缓存项的点击率，所以实现本方法以供缓存池内部维护调用。<br>
	 * 
	 * @param key
	 * @return
	 */
    protected Cacheable getObjectOnly(Object key) {
        Cacheable item = getFree().get(key);
        if( item == null ) {
        	item = getUsing().get(key);
        }
        if (item == null && released) {
            log.debug("getObjectOnly faild，because [" + getName() + "] has released, all items was gone.");
        }
        
		return item; 
    }

    public synchronized Cacheable putObject(Object key, Object value) {
		if (key == null) {
			return null;
		}

		Cacheable oldItem = getObjectOnly(key);
		if (oldItem != null) {
			oldItem.update(value);
			return oldItem;
		} 
		else {
			// 缓存项放入缓存池的同时也设置了其生命周期
			Cacheable newItem = new TimeWrapper(key, value, strategy.cyclelife);
			newItem = getFree().put(key, newItem);
        	synchronized(size) { 
        		size ++; 
        	}
			
			// 事件监听器将唤醒所有等待中的线程，包括cleaner线程，checkout，remove等方法的等待线程
			firePoolEvent(PoolEvent.PUT_IN);
 
			return newItem;
		}
	}

    // 从缓存池中移除一个对象。
    private Cacheable removeObject(Object key) {
        Cacheable item = getFree().remove(key);
        if(item == null) {
        	item = getUsing().remove(key);
        }
        
        firePoolEvent(PoolEvent.REMOVE);
        return item;
    }

    public void flush() {
        release(true);
        resetHitCounter();
        log.debug(this.getName() + " flushed.");
    }
    
    public Cacheable reload(final Cacheable obj) throws RuntimeException {
        Cacheable newObj = customizer.reloadCacheObject(obj);
        
        // 如果重新加载的缓存项为空，则将原先的缓存项从池中移除并销毁，否则则覆盖原有的缓存项。
        Object key = obj.getKey();
        if(newObj == null) {
			destroyByKey(key);
        } else {
            newObj = putObject(key, newObj.getValue());
        }
        return newObj;
    }
    
    /**
     * 销毁指定对象（如果有必要的话可采用异步）；
     */
    public synchronized void destroyObject(final Cacheable o) {
        if (o != null) {
    		customizer.destroy(o);
        	logDebug(" Object[" + o + "] was destroyed.");
        	
        	synchronized(size) { 
        		size --; 
        		checkSize();
        	}
        }
    }
    
    private void checkSize() {
    	int realSize = getFree().size() + getUsing().size();
    	if( realSize != size ) {
    		log.debug(this);
    		log.info( "[" +this.getName()+ "] realSize != size(), realSize = " +realSize+ ", size() = " +size());
    		size = realSize; // 以实际个数为准
    	}
    }
    
    public synchronized boolean destroyByKey(Object key) {
    	Cacheable item = removeObject(key);
        destroyObject(item);
        return item == null;
    }
    
    public final void releaseAsync(final boolean forced) {
        Thread t = new Thread(new Runnable(){
            public void run() { 
                release(forced); 
            }
        });
        t.start();
    }
    
    protected Cacheable checkOut() {
        Cacheable item = getFree().getByAccessMethod(strategy.accessMethod);
        if(item != null) {
            item.addHit();
            getFree().remove(item.getKey());
            getUsing().put(item.getKey(), item);
        }
        return item;
    }
    
    public synchronized Cacheable checkOut(long timeout) {
        if(timeout <= 0) {
            timeout = strategy.interruptTime;
        }
        
        long time = System.currentTimeMillis();
        Cacheable item =  checkOut();
        while (item == null  &&  (System.currentTimeMillis() - time < timeout)) {
            try {
                log.debug("[" + getName() + "] has no available item......waiting " + timeout + "(ms)");
                wait(timeout); /* wait需要结合synchronized，线程wait后会先释放对象锁，待wait时间（timeout）到了或其他线程notify后再收回对象锁，继续执行。 */
                item = checkOut();
            } 
            catch (InterruptedException e) { 
                log.error("checkOut时等待被中断", e); 
            }
        }
        
        if(item == null) {
			/* 如果没有清理出任何对象，说明对象可能都集中在using池中，且无法释放，
			 * 此时池已接近奔溃边缘，尝试清除using里的僵尸对象（最后访问时间距今已超过了其cyclelife的对象） */
        	purge();
        	
            String errorMsg = "[" + getName() + "] is overflow，waiting time " + timeout + ", " + getFree().size() + "/"
            		+ getUsing().size() + "/" +  this.getCacheStrategy().poolSize;
            log.info(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return item;
    }
    
    public synchronized void checkIn(Cacheable item) {
        if (item == null) {
        	logError("attempt to checkIn a null item");
            return;
        }
        Object key = item.getKey();
        
        // 判断对象是否存在using池中，是的话将对象从using池中移出，否则抛出异常
		Cacheable temp = getUsing().remove(key);
		if( !item.equals(temp) ) {
            logError("attempt to checkIn a item not from using pool, failed！" + getName() + "[" + item + " --> " + temp + "]");
            return;
        }
		
        Object value = item.getValue();
        
        // 记录对象被using的时间
        long _hitLong = item.addHitLong();
        this.addHitLong(_hitLong);
        
        //如果池已满，则销毁对象，否则则放回池中
        int maxSize = strategy.poolSize;
        if (maxSize > 0 && size() >= maxSize) {
        	// logDebug(getName() + "【" + item + "】 checkIn时池已满了，即将被销毁。");
        	destroyObject(item);
        } 
        else {
            try{ 
                // 如果对象实现了Reusable接口，则执行回收操作
                if(value instanceof Reusable) {
                    ((Reusable)value).recycle(); 
                }
                
                // 需重新放入free池中, 其点击率等属性已改变
                getFree().put(key, item); 
                
                //事件监听器将唤醒所有等待中的线程，包括cleaner线程，checkout，remove等操作的等待线程
                firePoolEvent(PoolEvent.CHECKIN);
                
                logDebug("cache-item[" + item.getKey() + "] was checkIn）！");
            } 
            catch (Exception e) {        
            	// 如果不能回收则销毁
                destroyObject(item); 
                log.error(" failed checkIn cache-item[" + item.getKey() + "], destroyed.",  e);
            }
        }
    }   
    
    public synchronized Cacheable remove() {
        Cacheable item = getFree().getByAccessMethod(strategy.accessMethod);
        
        //如果free池中取不到，则要等using池中的缓存对象返回到free中。线程等待
        long timeout = strategy.interruptTime;
        long time = System.currentTimeMillis();
        while (item == null  &&  (System.currentTimeMillis() - time < timeout)) {
            try {
            	logDebug("[" + this.getName() + "]'s free container has no available item......waiting " + timeout + " ms");
                wait(timeout);
                item = getFree().getByAccessMethod(strategy.accessMethod);
            } 
            catch (InterruptedException e) { 
                log.error("remove pool item failed", e); 
            }
        }
        
        if( item != null ) {
        	removeObject(item.getKey());        
        }
        
        return item;
    }
    
    public boolean purge() {
        log.debug("starting to clean [" + this.getName() + "] expired item ....... ");
       
        int count = 0;
        for (Cacheable item : getFree().valueSet()) {
            if ( item != null && item.isExpired() ) {
                removeObject(item.getKey());
                destroyObject(item);
                count++;
            }
        }
        
        /* 当池容量超过55%时，着手清除using里的僵尸对象（最后访问时间距今已超过了其cyclelife*2的对象)，直接销毁，
         * 不宜放回checkIn回Free池（比如：using中DBConn可能还卡死在执行某个SQL里，这样的Conn无法再被利用） */
        for (Cacheable item : getUsing().valueSet()) {
            long delta = System.currentTimeMillis() - item.getAccessed();
            long maxWait  = strategy.getMaxWait();  // 默认1小时 
            float percent = strategy.calPoolLoadPercent( this.size() );
			if ( delta > maxWait && percent > 0.55 ) {
                removeObject(item.getKey());
                destroyObject(item);
                count++;
            }
        }
        
        if(count > 0) {
        	log.debug("total clean[" + this.getName() + "] [" + count + "] items。");
        }
        log.debug("purge end.");
        
        return (getFree().size() == 0  && count == 0);
    }
    
    public long getRequests() {
        return requests;
    }
    
    public long getHitLong() {
    	return hitLong;
    }
    
    /**
     * 池请求数加一
     */
    protected void addRequests() {  
    	requests++; 
    }

    /**
     * 池命中数加一
     */
    protected void addHits() { 
    	hits++; 
    }
    
    /**
     * 池命中数加一
     */
    protected void addHitLong(long _hitLong) { 
    	this.hitLong += _hitLong; 
    }
    
    /**
     * 重置请求数和点击数
     * requests = hits = hitLong = 0
     */
    protected final void resetHitCounter() { 
    	requests = hits = hitLong = 0; 
    }  
    
    public final float getHitRate() {
        return (requests == 0) ? 0 : Math.round(((float) hits / requests) * 100f);
    }
    
    public Set<Cacheable> listItems() {
        Set<Cacheable> values = new HashSet<Cacheable>();
        try {
            values.addAll(getFree().valueSet());
            values.addAll(getUsing().valueSet());
    	} catch(Exception e) {}
        
        return values;
    }
    
    public Set<Object> listKeys() {
        Set<Object> keys = new HashSet<Object>();
        try{
            keys.addAll(getFree().keySet());
            keys.addAll(getUsing().keySet());
        } catch(Exception e) {}
        
        return keys;
    }
    
    public CacheStrategy getCacheStrategy() { 
    	return this.strategy; 
    }
    
    /**
     * 判断是否是修改缓存策略还是在初始化缓存池，初始化的话strategy为null
     * @param strategy
     */
    public void setCacheStrategy(CacheStrategy strategy) { 
        if( this.strategy != null ) { // 缓存策略改变则触发事件
            this.strategy.fireEventIfChanged(strategy); 
        } else {
        	this.strategy = strategy; 
        }
    }
    
    public CacheCustomizer getCustomizer() { 
    	return this.customizer; 
    }
    
    public void setCustomizer(CacheCustomizer customizer) { 
    	this.customizer = customizer; 
    }
 
    public final void firePoolEvent(int eventType) {
        PoolEvent poolEvent = new PoolEvent(this, eventType);
        for ( Listener listener : listeners){
        	listener.dealwithPoolEvent(poolEvent);
        }
    }
    
    public final void addPoolListener(Listener x){
        listeners.add(x);
    }
}