package com.wlnet.mobile.utils;

import java.util.regex.Pattern;

public final class StringUtils {

	/**
	 * 是否空字符串
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str==null)return true;
		return Pattern.matches("^\\s*$", str);
	}
	
	/**
	 * 去掉空格，如果str==null则返回空字符
	 * @param str
	 * @return
	 */
	public static String trim(String str){
		if(isEmpty(str)) return "";
		return str.trim();
	}
	
	
	
}
