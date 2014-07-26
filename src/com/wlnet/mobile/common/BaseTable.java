package com.wlnet.mobile.common;

/**
 *
 * @author xwlian
 *
 */
public abstract class BaseTable implements Table {

	protected Integer id;
	@Override
	public Integer getId() {
		return this.id; 
	}
	@Override
	public void setId(Integer id){
		this.id = id;
	}

}
