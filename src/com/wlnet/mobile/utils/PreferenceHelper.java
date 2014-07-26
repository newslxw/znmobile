package com.wlnet.mobile.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**操作preferences的工具类
 * @author xwlian
 *
 */
public final class PreferenceHelper {


	public final static String PREFS_NAME ="zndbMobile";  //Preferences名称
	
	/**
	 * 获取Preference
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); 
		return settings;
	}
	
	
	/**
	 * 获取preference中的
	 * @param keys
	 * @return map
	 */
	public static Map<String,String> getPreferenceValues(Context ctx,String[] keys){
		SharedPreferences settings = getSharedPreferences(ctx);
		Map<String,String> map = new HashMap<String,String>();
		if(settings!=null){
			for(String key:keys){
				map.put(key, settings.getString(key,null));
			}
		}
		return map;
	}
	
	/**
	 * 更新perference中的
	 * @param keys   更新的key数组
	 * @param values 对于的value数组
	 */
	public static void updatePreferenceValues(Context ctx,String[] keys,String[] values){
		SharedPreferences settings = getSharedPreferences(ctx);
		Editor editor = settings.edit();
		for(int i=0; i<keys.length; i++){
			editor.putString(keys[i], values[i]);
		}
		editor.commit();
	}
	
}
