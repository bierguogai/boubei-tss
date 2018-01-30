/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache;

import com.boubei.tss.util.BeanUtil;

/**
 * 池容器工厂类。
 */
public class ContainerFactory {
	
	private static ContainerFactory factory;

	private ContainerFactory() {
	}

	public static ContainerFactory getInstance() {
		if (factory == null) {
			factory = new ContainerFactory();
		}
		return factory;
	}

	public Container create(String className, String containerName) {
		// 调用构造方法：XXContainer(String name)
		Object instance = BeanUtil.newInstanceByName(className,
				new Class[] { String.class }, new Object[] { containerName });
		
		if (!Container.class.isAssignableFrom(instance.getClass())) {
			throw new RuntimeException(className + " did not implements Container interface.");
		}

		return (Container) instance;
	}
}
