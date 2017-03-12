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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 时间、字符串转换工具
 * 注： SimpleDateFormat为非线程安全。
 * </p>
 */
public class DateUtil {
    
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    private static final String SDF_1_REG = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
    private static final String SDF_2_REG = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2}$";
    private static final String SDF_3_REG = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
    private static final String SDF_4_REG = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2}$";
    private static final String SDF_5_REG = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}$";
    private static final String SDF_6_REG = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}$";

    /**
     * <p>
     * 将日期字符串解析成日期对象，支持以下格式
     * <li>yyyy-MM-dd HH:mm:ss
     * <li>yyyy-MM-dd
     * <li>yyyy/MM/dd HH:mm:ss
     * <li>yyyy/MM/dd
     * <li>yyyy-MM-dd HH:mm
     * <li>yyyy/MM/dd HH:mm
     * </p>
     * 
     * @param str
     * @return
     */
    public static Date parse(String str) {
    	if(EasyUtils.isNullOrEmpty(str)) return null;
    	
    	if(str.indexOf(".") > 0) {
    		str = str.substring(0, str.indexOf(".")); // 截掉微秒
    	}
    	
    	List<String> sdfRegArray = Arrays.asList(SDF_1_REG, SDF_2_REG, SDF_3_REG, SDF_4_REG, SDF_5_REG, SDF_6_REG);
    	
    	SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat SDF_2 = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        SimpleDateFormat SDF_3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat SDF_4 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat SDF_5 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat SDF_6 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	List<SimpleDateFormat> sdfArray = Arrays.asList(SDF_1, SDF_2, SDF_3, SDF_4, SDF_5, SDF_6);
    	
        Date date = null;
        try {
        	for(int index = 0; index < sdfRegArray.size(); index ++) {
        		 if (Pattern.compile(sdfRegArray.get(index)).matcher(str).matches()) {
                     return sdfArray.get(index).parse(str);
                 } 
        	}
        } catch (ParseException e) {
        }
        return date;
    }

    /**
     * <p>
     * 将日期格式化成字符串：yyyy-MM-dd
     * </p>
     * 
     * @param date
     * @return
     */
    public static String format(Date date) {
        if(date == null) return "";
        
        return new SimpleDateFormat(DateUtil.DEFAULT_DATE_PATTERN).format(date);
    }
    
    /**
     * <p>
     * 将日期格式化成字符串：yyyy-MM-dd HH:mm:ss
     * </p>
     * 
     * @param date
     * @return
     */
    public static String formatCare2Second(Date date) {
        if(date == null) return "";
        
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * <p>
     * 将日期格式化成相应格式的字符串，如：
     * <li>yyyy-MM-dd HH:mm:ss
     * <li>yyyy-MM-dd
     * <li>yyyy/MM/dd HH:mm:ss
     * <li>yyyy/MM/dd
     * </p>
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if(date == null) return "";
        
        if (pattern == null || "".equals(pattern)) {
            pattern = DateUtil.DEFAULT_DATE_PATTERN; 
        }
        
        return new SimpleDateFormat(pattern).format(date);
    }
    
    /**
     * 获取今天零点。
     */
    public static Date today() {
        return noHMS(new Date());
    }
    
    /**
     * 去掉时分秒
     */
    public static Date noHMS(Date pointedDate) {
    	Calendar c = Calendar.getInstance();
    	c.setTime(pointedDate);
    	
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    public static Date addDays(Date now, double days) {
        long nowLong = now.getTime(); // 将参考日期转换为毫秒时间
        Date time = new Date(nowLong + (long) (days * 24 * 60 * 60 * 1000)); // 加上时间差毫秒数
        return time;
    }
    
    public static Date subDays(Date now, double days) {
        return addDays(now, days * -1);
    }
    
	public static List<Date> daysBetweenFromAndTo(Date fromDate, Date toDate) {
		List<Date> rltList = new ArrayList<Date>();
		while (fromDate.before(toDate)) {
			rltList.add(fromDate);
			fromDate = addDays(fromDate, 1);
		}
		rltList.add(toDate);

		return rltList;
	}
	
    public static int getYear(Date now) {
    	Calendar pointedDay = Calendar.getInstance();
    	pointedDay.setTime(now);
        return pointedDay.get(Calendar.YEAR);
    }
    
    public static int getMonth(Date now) {
    	Calendar pointedDay = Calendar.getInstance();
    	pointedDay.setTime(now);
    	return pointedDay.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
    }
    
    public static int getDay(Date now) {
    	Calendar pointedDay = Calendar.getInstance();
    	pointedDay.setTime(now);
        return pointedDay.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }
    
    /**
     * 判断是否是月末
     */
    public static boolean isMonthEnd(Date day){
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        return c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    public static String toYYYYMM(Object year, int monthOfYear, String seperator){
    	return year + seperator + (monthOfYear < 10 ? "0" : "") + monthOfYear;
    }
    
    public static String toYYYYMM(Date time, String seperator) {
    	return toYYYYMM(getYear(time), getMonth(time), seperator);
    }
}
