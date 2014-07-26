package com.wlnet.mobile.common;

/**
 * 给Spinner使用的item
 * @author xwlian
 *
 */
public class SpinnerItem {

	private String ID = "";
	private String Value = "";

	public SpinnerItem() {
		ID = "";
		Value = "";
	}

	public SpinnerItem(String _ID, String _Value) {
		ID = _ID;
		Value = _Value;
	}

	@Override
	public String toString() { // 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对�?toString()
		// TODO Auto-generated method stub
		return Value;
	}

	public String GetID() {
		return ID;
	}

	public String GetValue() {
		return Value;
	}
}