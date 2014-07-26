/**
 * 
 */
package com.wlnet.mobile.common;

import android.provider.BaseColumns;

/**
 * @author xwlian
 *
 */
public interface Table extends BaseColumns {


	
	//public String getCreateSql();
	
	public String getTableName();
	public Integer getId();
	public void setId(Integer id);
}
