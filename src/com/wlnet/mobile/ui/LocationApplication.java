package com.wlnet.mobile.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.geocode.GeoCoder;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

/**
 * 主Application
 */
public class LocationApplication extends Application {
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	protected GeoCoder mSearch;
	//public MyLocationListener mMyLocationListener;
	
	public Vibrator mVibrator; //android 震动
	
	
	public Vibrator getmVibrator() {
		return mVibrator;
	}


	public void setmVibrator(Vibrator mVibrator) {
		this.mVibrator = mVibrator;
	}


	public LocationClient getmLocationClient() {
		return mLocationClient;
	}


	public void setmLocationClient(LocationClient mLocationClient) {
		this.mLocationClient = mLocationClient;
	}


	public GeofenceClient getmGeofenceClient() {
		return mGeofenceClient;
	}


	public void setmGeofenceClient(GeofenceClient mGeofenceClient) {
		this.mGeofenceClient = mGeofenceClient;
	}
	
	/**
	 * 启动定位服务
	 * 
	 */
	public void startLoc(BDLocationListener listener, LocationClientOption option){
		if(listener != null)mLocationClient.registerLocationListener(listener);
		if(option !=null ) mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	
	public void stopLoc(){
		if(mLocationClient.isStarted()) mLocationClient.stop();
	}


	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		mLocationClient = new LocationClient(this);
		//mMyLocationListener = new MyLocationListener();
		//mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(this);
		mSearch =  GeoCoder.newInstance();		
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
	}


	public GeoCoder getmSearch() {
		return mSearch;
	}


	public void setmSearch(GeoCoder mSearch) {
		this.mSearch = mSearch;
	}

	/*
	*//**
	 * 实现实位回调监听
	 *//*
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			logMsg(sb.toString());
			Log.i("BaiduLocationApiDem", sb.toString());
		}


	}
	
	
	*//**
	 * 显示请求字符串
	 * @param str
	 *//*
	public void logMsg(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 高精度地理围栏回调
	 * @author jpren
	 *
	 */
	
}
