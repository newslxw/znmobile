package com.wlnet.mobile.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wlnet.mobile.R;
import com.wlnet.mobile.dao.RevDao;
import com.wlnet.mobile.net.TcpReceive;
import com.wlnet.mobile.pojo.Rev;
import com.wlnet.mobile.utils.GpsUtils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class RevMapActivity extends Activity implements  OnGetGeoCoderResultListener {

	private static final String LTAG = RevMapActivity.class.getSimpleName();
	protected Refresh refreshHandle;
	protected List<Rev> listRev;
	protected Rev queryRev ;
	protected RevDao revDao;
	protected Rev curRev;
	protected boolean bPause;
	protected Thread revThread;
	protected CountDownLatch countDownLatch;
	
	private MapView mMapView;
	private TextView tvMapWZ;
	private Bundle bundle;
	private String devUuid;
	private BaiduMap mBaiduMap;
	private LatLng curPoint;
	private LocationClient mLocClient;
	private MyLocationListener locListener;
	private LocationApplication myApplication;
	private LocationClientOption locOption;
	private BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位
	//protected GeoCoder mSearch;
	private PoiSearch mPoiSearch ;
	private ArrayList<LatLng> oldPoints = new ArrayList<LatLng>();
	private int maxPoint = 20;
	private String curWZ ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rev_map);

		bundle = getIntent().getExtras();

		double latitude = bundle.getDouble("latitude");
		double longitude = bundle.getDouble("longitude");
		devUuid = bundle.getString("DevUuid");
		queryRev = new Rev();
		queryRev.setDevUuid(devUuid);
		revDao = new RevDao(this);
		curRev = new Rev();
		WifiManager manager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
		refreshHandle = new Refresh(manager);
		listRev = revDao.query(queryRev);
		if(listRev!=null&&listRev.size()>0){
			curRev = listRev.get(0);
		}
		
		mMapView = (MapView) findViewById(R.id.bmapView); 
		tvMapWZ = (TextView) findViewById(R.id.tvMapWZ);  
        mBaiduMap = mMapView.getMap();  
        mMapView.removeViewAt(1);
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_en); 
	    //普通地图  
	    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
	    mBaiduMap.setMyLocationEnabled(true);
	    myApplication = (LocationApplication)getApplicationContext();
		locListener = new MyLocationListener();
		LocationClientOption locOption = new LocationClientOption();
		locOption.setOpenGps(true);// 打开gps
		locOption.setCoorType("bd09ll"); // 设置坐标类型
		//myApplication.startLoc(locListener, locOption);
		// 初始化搜索模块，注册事件监听
		myApplication.getmSearch().setOnGetGeoCodeResultListener(this);
		
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
		
		if(latitude>1 && longitude>1){
			LatLng point = new LatLng(latitude, longitude); 
			updateMapCenter(point, null);
			updateEndPos(point);
		}
	}

	public void updateEndPos(LatLng point){
		if(point == null || mBaiduMap == null ) return;
		if(curPoint!=null&&curPoint.latitude == point.latitude && curPoint.longitude == point.longitude) return;
		curPoint = point;
		mBaiduMap.clear();  
		myApplication.getmSearch().reverseGeoCode(new ReverseGeoCodeOption().location(point));
		OverlayOptions option = new MarkerOptions().position(point).icon(mCurrentMarker);  
		Overlay overlay = mBaiduMap.addOverlay(option);
		oldPoints.add(point);
		if(oldPoints.size() > maxPoint) oldPoints.remove(0);
		if(oldPoints.size()>2){
			PolylineOptions polylineOption = new PolylineOptions().points(oldPoints)
				    .color(0xFF000000);  
					mBaiduMap.addOverlay(polylineOption);
		}
		
	}
	

	private LatLng getRevPoint(Rev rev){
		String value = rev.getMeasureValue();
		if(value==null)value="";
		String arr[] = value.split(",");
		if(arr.length>6){
			LatLng point2 = GpsUtils.gpsPointToBaiduMap(arr[5], arr[6]);
			return point2;
		}
		return null;
	}
	
	
	private Handler myHandler = new Handler() {
		int tag = 2;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Rev rev = (Rev) msg.obj;
				LatLng point = getRevPoint(rev);
				updateEndPos(point);
				break;
			case -1:
				Toast.makeText(RevMapActivity.this.getApplicationContext(),
						"连接服务器超时，请检查网络", Toast.LENGTH_SHORT).show();
				Exception e = (Exception)msg.obj;
				e.printStackTrace();
				break;
			}
			
			
			/**
			 * Toast.makeText(activity.getApplicationContext(),
					"无法连接服务器", Toast.LENGTH_SHORT).show();
			 */
		}
	};
    public synchronized boolean isbPause() {
		return bPause;
	}

	public synchronized void setbPause(boolean bPause) {
		this.bPause = bPause;
	}

	class Refresh implements Runnable{
    	//private WifiManager.MulticastLock lock;
    	public Refresh(WifiManager manager) {
          //  this.lock= manager.createMulticastLock("UDPwifi"); 
       }
		@Override
		public void run() {
			Rev rev=null;
			while(true){
				if(!RevMapActivity.this.isbPause())
				{
					try {
						//this.lock.acquire();
						rev = TcpReceive.requestRev(queryRev);
						if(rev!=null&&rev.getDevUuid()!=null&&rev.getRevTime()!=null){
							if(curRev!=null&&!rev.getRevTime().equals(curRev.getRevTime())) {
								curRev = rev;
								revDao.insert(curRev);
							}
						}
						Message msg = Message.obtain(); 
						msg.obj = curRev;
						msg.what = 1;
						myHandler.sendMessage(msg);//向Handler发送消息，更新UI
					} catch (IOException e) {
						Message msg = Message.obtain();
						msg.what=-1;
						msg.obj = e;
						myHandler.sendMessage(msg);
					}/*finally{
						this.lock.release();
					}*/
					
				}else{
					countDownLatch.countDown();
					return;
				}
	            try {
					Thread.sleep(2000); //60s
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
	
	/**
	 * 重置地图中心点
	 * @param cenpt
	 */
	public void updateMapCenter(LatLng cenpt, LocationMode mode){
		MapStatus mMapStatus = new MapStatus.Builder()
	    .target(cenpt)
	    .zoom(18)
	    .build();
	    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
	    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
	    //改变地图状态
	    mBaiduMap.setMapStatus(mMapStatusUpdate);
		//mBaiduMap.animateMapStatus(mMapStatusUpdate);
	    if(mode != null) {
	    	mode = LocationMode.FOLLOWING;
		    mCurrentMarker = null;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
							mode, true, null));
	    }
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;
			LatLng point = new LatLng(location.getLatitude() ,location.getLongitude());
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(100).latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData); 
			if (isFirstLoc) {
				isFirstLoc = false;
				updateMapCenter(point, null);
			}
		}
	}
	
	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if(keyCode == KeyEvent.KEYCODE_BACK){
			this.getIntent().putExtra("latitude", curPoint.latitude);
			this.getIntent().putExtra("longitude", curPoint.longitude);
			this.setResult(0, this.getIntent());
		}*/
		return super.onKeyDown(keyCode, event);
	}

	@Override  
    protected void onDestroy() {  
		// 退出时销毁定位
		myApplication.stopLoc();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mBaiduMap = null;
		//mSearch.destroy();
		mPoiSearch.destroy();
        super.onDestroy();  
    }  
    @Override  
    protected void onResume() { 
		super.onResume();
		this.setbPause(false);
		countDownLatch = new CountDownLatch(1);
		//super.onStart();
		revThread = new Thread(refreshHandle);
		revThread.start();
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() { 
    	this.setbPause(true);
		if(revThread!=null){
			try {
				revThread.interrupt();
				countDownLatch.await(100,TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			revThread= null;
		} 
		mMapView.onPause();  
        super.onPause();  
    }

    @Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RevMapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(RevMapActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			/*Toast.makeText(RevMapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();*/
			tvMapWZ.setText("未能找到地名");
			curWZ = null;
		}else{
			curWZ = result.getAddress();
			if(!tvMapWZ.getText().equals(result.getAddress())){
				tvMapWZ.setText(result.getAddress());
				/*Toast.makeText(RevMapActivity.this, result.getAddress(),
						Toast.LENGTH_LONG).show();*/
				
				/*LatLngBounds.Builder builder = new Builder(); 
				builder.include(curPoint);
				LatLngBounds bound = builder.build();
				PoiBoundSearchOption popt = new PoiBoundSearchOption();
				popt = popt.bound(bound).keyword(result.getAddress());
				mPoiSearch.searchInBound(popt);*/
				
			}
		}

	}
	
	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){  
	    public void onGetPoiResult(PoiResult result){  
	    //获取POI检索结果  
	    	PoiOverlay overlay = new PoiOverlay(mBaiduMap);
	    	overlay.addToMap();
	    	overlay.zoomToSpan();
	    	
	    }  
	    public void onGetPoiDetailResult(PoiDetailResult result){  
	    //获取Place详情页检索结果  
	    }  
	};
	
}
