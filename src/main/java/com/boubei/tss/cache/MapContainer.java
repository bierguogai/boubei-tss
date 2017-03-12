/* ==================================================================   
 * Created [2009-4-27 下午11:32:55] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * 
 * <pre>
 * 为了MapContainer能适用多线程，其每步对 map 的操作都必须锁定 map。
 * 
 * map本身不支持同步，需要调用Collections.synchronizedMap使其支持。
 * ehcache则没有这个问题。
 * </pre>
 */
public class MapContainer extends AbstractContainer {

	private Map<Object, Cacheable> map;

	public MapContainer(String name) {
		super(name);
		map = Collections.synchronizedMap(new LinkedHashMap<Object, Cacheable>());
	}

	public Cacheable get(Object key) {
		synchronized (map) {
			return (Cacheable) map.get(key);
		}
	}

	public Cacheable put(Object key, Cacheable value) {
		synchronized (map) {
			map.put(key, value);
			return value;
		}
	}

	public Cacheable remove(Object key) {
		synchronized (map) {
			return (Cacheable) map.remove(key);
		}
	}

	public Set<Object> keySet() {
	    synchronized (map) {
	    	return map.keySet();
	    }
	}

	public Set<Cacheable> valueSet() {
        synchronized (map) {
        	return new HashSet<Cacheable>(map.values());
        }
    }
	
	public Cacheable getByAccessMethod(int accessMethod) {
	    synchronized (map) {
	    	return super.getByAccessMethod(accessMethod);
        }
	}
	
    public void clear() {
        synchronized (map) {
            map.clear();
        }
    }

    public int size() {
        synchronized (map) {
        	return map.size();
        }
    }
}
