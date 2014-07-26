package com.wlnet.mobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

	public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	/**
	 * 默认的时间格
	 */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
	
	/**
	 * 获取当前时间24位字符串
	 * @return
	 */
	public static String getDateTimeStr(){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);    
        return format.format(new Date()); 
	}
	
	public static String getDateTimeStr(Date date){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);    
        return format.format(date); 
	}
	
	/**
	 * 24位字符串解析成Date类型
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date toDateTime(String strDate) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		Date d = null;
		d =format.parse(strDate);
		return d;
	}
    
	/**
	 * 把数据库存储24位字符串格式的时间转换成以下格式的字符串 yyyy-MM-dd HH:mm:ss
	 * @param d
	 * @return
	 */
	public static String formatDBdate(String d){
		if(d==null) return d;
		if(d.length() != 8 && d.length() !=14) return d;
		String ret = d.substring(0,4)+"-" + d.substring(4,6)+"-"+d.substring(6,8);
		if(d.length()==14) ret=ret+" "+d.substring(8,10)+":"+d.substring(10,12)+":"+d.substring(12,14);
		return ret;
	}
}
