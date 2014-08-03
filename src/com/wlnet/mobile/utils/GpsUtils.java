package com.wlnet.mobile.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class GpsUtils {
	

	public static LatLng gpsPointToMap(String nstr, String estr){
		int pos1 = nstr.indexOf(".");
		int pos2 = estr.indexOf(".");
		if(pos1==-1) pos1 = nstr.length();
		if(pos2 == -1) pos2  = nstr.length();
		String nstr1 = nstr.substring(0, pos1-2);
		String estr1 = estr.substring(0, pos2-2);
		String nstr2 = nstr.substring(pos1-2);
		String estr2 = estr.substring(pos2-2);
		double n2 = Double.parseDouble(nstr2);
		double e2 = Double.parseDouble(estr2);
		n2 = n2 / 60;
		e2 = e2 / 60;
		double n1 = Double.parseDouble(nstr1);
		double e1 = Double.parseDouble(estr1);
		n1 = n1 + n2;
		e1 = e1 + e2;
		LatLng point = new LatLng(n1, e1);
		return point;
	}

	public static LatLng gpsPointToBaiduMap(LatLng point){
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.GPS);  
		converter.coord(point);  
		LatLng ret = converter.convert();
		return ret;
	}
	
	public static LatLng gpsPointToBaiduMap(String nstr, String estr){
		return GpsUtils.gpsPointToBaiduMap(GpsUtils.gpsPointToMap(nstr, estr));
	}
}
