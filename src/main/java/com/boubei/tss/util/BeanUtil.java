/* ==================================================================   
 * Created [2006-4-27] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */

package com.boubei.tss.util;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * 定义一些用来操作实体的工具方法
 */
public class BeanUtil {

    private static Logger log = Logger.getLogger(BeanUtil.class);

    /**
     * 对象属性复制.
     * 
     * @param to
     *            目标拷贝对象
     * @param from
     *            拷贝源
     */
    public static void copy(Object to, Object from) {
        try {
            PropertyUtils.copyProperties(to, from);
        } catch (Exception e) {
            throw new RuntimeException("拷贝时出错!", e);
        } 
    }

    /**
     * 对象属性复制工具
     * 
     * @param to
     *            目标拷贝对象
     * @param from
     *            拷贝源
     * @param ignore
     *            需要忽略的属性
     */
    public static void copy(Object to, Object from, String[] ignore) {
        List<String> list = Arrays.asList(ignore);
        PropertyDescriptor[] descr = PropertyUtils.getPropertyDescriptors(to);
        for (int i = 0; i < descr.length; i++) {
            PropertyDescriptor d = descr[i];
            
            if (d.getWriteMethod() == null)  continue;
            
            if (list.contains(d.getName())) continue;
            
            try {
                Object value = PropertyUtils.getProperty(from, d.getName());
                PropertyUtils.setProperty(to, d.getName(), value);
            } catch (Exception e) {
                throw new RuntimeException("属性名：" + d.getName() + " 在实体间拷贝时出错", e);
            }
        }
    }
 
    /**
     * 将对象中的属性按属性名/属性值的方式存入到Map中。
     * 
     * @param bean
     * @param map
     * @param ignore
     */
    public static void addBeanProperties2Map(Object bean, Map<String, Object> map, String...ignore){
        List<String> list = Arrays.asList(ignore);
        PropertyDescriptor[] descr = PropertyUtils.getPropertyDescriptors(bean);
        for (int i = 0; i < descr.length; i++) {
            PropertyDescriptor d = descr[i];
            String propertyName = d.getName();
            
            // 既有get又有set方法的属性的值才被读取 
            if ( d.getWriteMethod() == null || d.getReadMethod() == null) continue;
            
            if (list.contains(propertyName)) continue;
            
            try { // put value into Map
                map.put(propertyName, PropertyUtils.getProperty(bean, propertyName));
            } catch (Exception e) {
                log.info("获取属性名为：" + propertyName + " 的值时出错", e);
            }
        }
    }

    /**
     * 按照Map中的key和bean中的属性名一一对应，将Map中数据设定到实体对象bean中
     * 
     * @param bean
     * @param attrsMap
     */
    public static void setDataToBean(Object bean, Map<String, ?> attrsMap) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor d : descriptors ) {
            if (d.getWriteMethod() == null) continue;
            
            Class<?> clazz = d.getPropertyType();
            
            String propertyName = d.getName();
            Object value = attrsMap.get(propertyName); // value一般为前台传入，类型多为String型
            
            if (value == null || (!String.class.equals(clazz) && "".equals(value))) {
                continue;
            }
            if (clazz.equals(Date.class) && (value instanceof String)) {
                value = DateUtil.parse((String) value);
            }
            
            try {
            	if (value.getClass().equals(clazz) || clazz.isAssignableFrom(value.getClass())) { 
                    PropertyUtils.setProperty(bean, propertyName, value);
                } 
                else {
                    PropertyUtils.setProperty(bean, propertyName, 
                    		clazz.getConstructor( String.class ).newInstance( value ));
                }
            } catch (Exception e) {
                throw new RuntimeException( "属性名：" + propertyName + " 设置到实体中时出错", e);
            }
        }
    }

    /**
     * <p>
     * 获取实体对象的所有属性
     * </p>
     * @param bean 实体对象
     * @return Map 属性集合
     */
    public static Map<String, Object> getProperties(Object bean){
        return getProperties(bean, new HashSet<String>());
    }
    
    /**
     * <p>
     * 获取实体对象的某些属性
     * </p>
     * @param bean 实体对象
     * @param ignores Set 忽略的属性名集合
     * @return Map 属性集合
     */
    public static Map<String, Object> getProperties(Object bean, Set<String> ignores) {
        Map<String, Object> map = new HashMap<String, Object>();
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor d : descriptors ) {
            if (d.getReadMethod() == null) continue;
            
            String propertyName = d.getName();
            if (ignores.contains(propertyName)) continue;
            
            map.put(propertyName, getPropertyValue(bean, propertyName));
        }
        return map;
    }

    /**
     * 根据对象的class名，创建相应的Class对象
     * 
     * @param className
     * @return
     */
    public static Class<?> createClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("实体: " + className + " 无法加载", e);
        }
    }

    /**
     * 根据对象的class名，创建相应的对象
     * 
     * @param className
     * @return
     */
    public static Object newInstanceByName(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("实例化失败：" + className, e);
        } 
    }

    /**
     * <p>
     * 通过带参数的构造函数实例化对象
     * </p>
     * 
     * @param className
     *            String
     * @param clazzes
     *            Class[]
     * @param args
     *            Object[]
     * @return Object
     */
    public static Object newInstanceByName(String className, Class<?>[] clazzes, Object[] args) {
        Class<?> clazz = createClassByName(className);
        try {
            return clazz.getConstructor(clazzes).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("实例化失败：" + className, e);
        } 
    }

    /**
     * 根据Class对象创建Object对象
     * 
     * @param clazz
     * @return
     */
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("实例化失败：" + clazz.getName(), e);
        }
    }

    /**
     * 判断属性是否在实体中
     * 
     * @param bean
     * @param propertyName
     * @return
     */
    public static boolean isPropertyInBean(Object bean, String propertyName) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor d : descriptors ) {
            if (propertyName.equals(d.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否继承某个接口.
     * 
     * TODO 需要改进，如果接口继承了其它接口，或者超类继承了接口，getInterfaces()将取不到，它只能取到向上一层的接口
     * 
     * @param clazz
     * @param interfaceClazz
     * @return true/false
     */
    public static boolean isImplInterface(Class<?> clazz, Class<?> interfaceClazz) {
        return Arrays.asList(clazz.getInterfaces()).contains(interfaceClazz);
    }

    /**
     * <p>
     * 将对象格式化为XML字符串
     * </p>
     * @param bean Object Java对象
     * @return String XML字符串
     */
    public static String toXml(Object bean) {
        XStream xs = new XStream();
        return xs.toXML(bean);
    }

    /**
     * 获取对象中指定属性的值
     * @param obj
     * @param propertyName
     * @return
     */
    public static Object getPropertyValue(Object obj, String propertyName) {
        try {
            return PropertyUtils.getProperty(obj, propertyName);
        } catch (Exception e) {
            throw new RuntimeException("访问属性：" + propertyName + "时出错", e);
        } 
    }
}
