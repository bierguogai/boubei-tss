/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

/** 
 * <p> MathUtil.java </p> 
 * 
 * 数字运算工具集
 * 
 */
public class MathUtil {
	
	/**
     * 根据给定的pattern格式化数字类数据为字符串
     */
    public static String formatNumber(Object value, String pattern) {
        if (value == null)  return ""; 
        
        if ( EasyUtils.isNullOrEmpty(pattern) ) {
            return value.toString();
        }
        return new DecimalFormat(pattern).format(value);
    }
    
	/**
	 * 两Double型数据相加
	 * 
	 * @param value
	 * @param addValue
	 * @return
	 */
    public static Double addDoubles(Double value, Double addValue) {
        BigDecimal sum = BigDecimal.ZERO; // new BigDecimal(0)
        if (value != null) {
            sum = BigDecimal.valueOf(value);
        }
        if (addValue != null) {
            sum = sum.add(BigDecimal.valueOf(addValue));
        }
        return sum.doubleValue();
    }

	/**
	 * 两数相乘
	 * 
	 * @param value1
	 * @param value2
	 * @return Double
	 */
	public static Double multiply(Double value1, Double value2) {
		if (value1 == null || value2 == null) {
			return new Double(0);
		}
		BigDecimal val1 = BigDecimal.valueOf(value1);
		BigDecimal val2 = BigDecimal.valueOf(value2);

		return val1.multiply(val2).doubleValue();
	}

	/**
	 * 两Integer对象相加
	 * 
	 * @param value
	 * @param addValue
	 * @return
	 */
	public static Integer addInteger(Integer value, Integer addValue) {
		int sum = 0;
		if (value != null) {
			sum = value;
		}
		if (addValue != null) {
			sum += addValue;
		}
		return new Integer(sum);
	}
	
    public static int randomInt(int factor) {
        Random random = new Random();
        return random.nextInt(factor);
    }
    
    /**
     * 生成一个六位随机数
     */
    public static int randomInt6() {
        return new Random().nextInt(900000) + 100000;
    }
    
    public static int calPercent(float x, float y) {
    	return (y == 0f) ? 0 : Math.round( ( x / y) * 100 );
    }
}
