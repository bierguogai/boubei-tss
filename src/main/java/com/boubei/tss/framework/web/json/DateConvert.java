package com.boubei.tss.framework.web.json;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.boubei.tss.util.DateUtil;

public class DateConvert implements Converter<String, Date> {

    public Date convert(String source) {
    	return DateUtil.parse(source);
    }

}
