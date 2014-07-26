package com.wlnet.mobile.pojo;

import java.io.Serializable;
import java.util.List;

import com.wlnet.mobile.common.BaseTable;


/**
 * The persistent class for the m_rev database table.
 * 
 */
public class Rev extends BaseTable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "m_rev";
	public static final String REV_ID = "rev_id";
	public static final String DEV_UUID = "dev_uuid";
	public static final String MEASURE_VALUE = "measure_value";
	public static final String REV_TIME = "rev_time";
	
	private Long revId;
	private String devUuid;
	private String measureValue;
	private String revTime;

	
	
	public String getDevUuid() {
		return devUuid;
	}

	public void setDevUuid(String devUuid) {
		this.devUuid = devUuid;
	}


	public Rev() {
	}

	public Rev(String measureValue) {
		this.measureValue = measureValue;
	}
	
	public Long getRevId() {
		return this.revId;
	}

	public void setRevId(Long revId) {
		this.revId = revId;
	}


	public String getMeasureValue() {
		return this.measureValue;
	}

	public void setMeasureValue(String measureValue) {
		this.measureValue = measureValue;
	}

	public String getRevTime() {
		return this.revTime;
	}

	public void setRevTime(String revTime) {
		this.revTime = revTime;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

}