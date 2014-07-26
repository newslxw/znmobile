/**
 * 
 */
package com.wlnet.mobile.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * @author xwlian
 *
 */
public final class Session {

	private static boolean autoSave; //是否自动保存
	private static boolean soundOnSaveSucceed; //是否测量成功,声音提示
	private static boolean soundOnSaveFail; //是否测量失败,声音提示
	private static boolean soundOnUploadSucceed; //是否上传成功,声音提醒
	private static boolean soundOnUploadFail; //是否上传失败,声音提醒
	private static boolean uploadByWIFI; //是否通过WIFI上传
	private static boolean uploadByBluetooth; //是否通过蓝牙上传
	private static String WIFIName; //WIFI服务器地??
	private static String mac; //手机MAC
	public static void exit(Context ctx){
	}

	public static boolean isAutoSave() {
		return autoSave;
	}

	public static void setAutoSave(boolean autoSave) {
		Session.autoSave = autoSave;
	}

	public static boolean isSoundOnSaveSucceed() {
		return soundOnSaveSucceed;
	}

	public static void setSoundOnSaveSucceed(boolean soundOnSaveSucceed) {
		Session.soundOnSaveSucceed = soundOnSaveSucceed;
	}

	public static boolean isSoundOnSaveFail() {
		return soundOnSaveFail;
	}

	public static void setSoundOnSaveFail(boolean soundOnSaveFail) {
		Session.soundOnSaveFail = soundOnSaveFail;
	}

	public static boolean isSoundOnUploadSucceed() {
		return soundOnUploadSucceed;
	}

	public static void setSoundOnUploadSucceed(boolean soundOnUploadSucceed) {
		Session.soundOnUploadSucceed = soundOnUploadSucceed;
	}

	public static boolean isSoundOnUploadFail() {
		return soundOnUploadFail;
	}

	public static void setSoundOnUploadFail(boolean soundOnUploadFail) {
		Session.soundOnUploadFail = soundOnUploadFail;
	}

	public static boolean isUploadByWIFI() {
		return uploadByWIFI;
	}

	public static void setUploadByWIFI(boolean uploadByWIFI) {
		Session.uploadByWIFI = uploadByWIFI;
	}

	public static boolean isUploadByBluetooth() {
		return uploadByBluetooth;
	}

	public static void setUploadByBluetooth(boolean uploadByBluetooth) {
		Session.uploadByBluetooth = uploadByBluetooth;
	}

	public static String getWIFIName() {
		return WIFIName;
	}

	public static void setWIFIName(String wIFIName) {
		WIFIName = wIFIName;
	}


	public static String getMac() {
		return mac;
	}

	public static void setMac(String mac) {
		Session.mac = mac;
	}
	
	
	
}
