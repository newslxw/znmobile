package com.wlnet.mobile.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wlnet.mobile.common.BaseTable;


/**
 * The persistent class for the m_dev database table.
 * 
 */
public class Dev extends BaseTable  implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "m_dev";
	public static final String DEV_UUID = "dev_uuid";
	public static final String DEV_NAME = "dev_name";
	public static final String INET_IP = "inet_ip";
	public static final String INET_PORT = "inet_port";
	public static final String DEV_TYPE = "dev_type";
	public static final String APP_TYPE = "app_type";

	private String devMac;
	private String devName;
	private String devUuid;
	private String inetIp;
	private String devType;	
	private String appType;	
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	private Integer inetPort;

	private Short locked;

	private String lockedPhone;

	private Date lockedTime;

	private String wifiMac;
	
	private List<Rev> revList;
	
	private String measureTime;

	public String getDevUuid() {
		return devUuid;
	}

	public void setDevUuid(String devUuid) {
		this.devUuid = devUuid;
	}


	public String getDevType() {
		return devType;
	}

	public void setDevType(String devType) {
		this.devType = devType;
	}


	
	

	public String getMeasureTime() {
		return measureTime;
	}

	public void setMeasureTime(String measureTime) {
		this.measureTime = measureTime;
	}

	public List<Rev> getRevList() {
		return revList;
	}

	public void setRevList(List<Rev> revList) {
		this.revList = revList;
	}

	public Dev() {
	}


	public Dev(String devUuid, String ip, int sendPort) {
		this.devUuid = devUuid;
		this.inetIp = ip;
		this.inetPort = sendPort;
	}

	public String getDevMac() {
		return this.devMac;
	}

	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}

	public String getDevName() {
		return this.devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getInetIp() {
		return this.inetIp;
	}

	public void setInetIp(String inetIp) {
		this.inetIp = inetIp;
	}

	public Integer getInetPort() {
		return this.inetPort;
	}

	public void setInetPort(Integer inetPort) {
		this.inetPort = inetPort;
	}

	public Short getLocked() {
		return this.locked;
	}

	public void setLocked(Short locked) {
		this.locked = locked;
	}

	public String getLockedPhone() {
		return this.lockedPhone;
	}

	public void setLockedPhone(String lockedPhone) {
		this.lockedPhone = lockedPhone;
	}

	public Date getLockedTime() {
		return this.lockedTime;
	}

	public void setLockedTime(Date lockedTime) {
		this.lockedTime = lockedTime;
	}

	public String getWifiMac() {
		return this.wifiMac;
	}

	public void setWifiMac(String wifiMac) {
		this.wifiMac = wifiMac;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

}