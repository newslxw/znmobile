package com.wlnet.mobile.common;

import com.wlnet.mobile.utils.DBHelper;

import android.content.Context;

/**
 * dao基类
 * @author xwlian
 *
 */
public abstract class BaseDao {

	
	protected DBHelper helper;
	protected Context ctx;
	public BaseDao(Context ctx){
		this.ctx = ctx;
		helper = DBHelper.getInstance(ctx);
	}
	
}
