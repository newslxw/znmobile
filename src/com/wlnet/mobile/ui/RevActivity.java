package com.wlnet.mobile.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.wlnet.mobile.R;
import com.wlnet.mobile.dao.DevDao;
import com.wlnet.mobile.dao.RevDao;
import com.wlnet.mobile.net.Receive;
import com.wlnet.mobile.net.TcpReceive;
import com.wlnet.mobile.pojo.Dev;
import com.wlnet.mobile.pojo.Rev;
import com.wlnet.mobile.ui.DevActivity.Refresh;
import com.wlnet.mobile.utils.DateUtil;
import com.wlnet.mobile.utils.GpsUtils;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RevActivity extends Activity implements  OnGetGeoCoderResultListener {
	private Bundle bundle;
	protected String devUuid;
	protected Refresh refreshHandle;
	protected List<Rev> listRev;
	protected Rev queryRev ;
	protected RevDao revDao;
	protected Rev curRev;
	protected boolean bPause;
	protected Thread revThread;
	private TextView tvUpdTime, tvWD, tvSD, tvYQ, tvKQ, tvJQ, tvWZ;
	private ImageView ibWZ;
	protected CountDownLatch countDownLatch;
	protected LocationApplication myApplication ;
	private LatLng point ;//= new LatLng(23.1200490000,113.3076500000);
	private LatLng gpsPoint;
	protected GeoCoder mSearch;
    public synchronized boolean isbPause() {
		return bPause;
	}

	public synchronized void setbPause(boolean bPause) {
		this.bPause = bPause;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rev);
		bundle = getIntent().getExtras();
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
		this.tvJQ = (TextView) this.findViewById(R.id.tvJQ);
		this.tvKQ = (TextView) this.findViewById(R.id.tvKQ);
		this.tvYQ = (TextView) this.findViewById(R.id.tvYQ);
		this.tvSD = (TextView) this.findViewById(R.id.tvSD);
		this.tvWD = (TextView) this.findViewById(R.id.tvWD);
		this.tvWZ = (TextView) this.findViewById(R.id.tvMapWZ);
		this.tvUpdTime = (TextView) this.findViewById(R.id.tvUpdTime);
		ibWZ = (ImageView)this.findViewById(R.id.ibWZ);
		ibWZ.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
					showMap(point);	
			}
			
		});
		tvWZ.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			 showMap(point);				
			}
			
		});
		mSearch =  GeoCoder.newInstance();
		// 初始化搜索模块，注册事件监听
		mSearch.setOnGetGeoCodeResultListener(this);
		
		updateView(curRev);
    }
	
	private void updateView(Rev rev){
		this.tvUpdTime.setText(DateUtil.formatDBdate(rev.getRevTime()));
		String value = rev.getMeasureValue();
		if(value==null)value="";
		String arr[] = value.split(",");
		this.tvWD.setText(arr.length>0?arr[0]+"℃":"");
		this.tvSD.setText(arr.length>1?arr[1]+"%":"");
		this.tvJQ.setText(arr.length>2?arr[2]+"ppm  标准:0.10ppm":"       标准:0.10ppm");
		this.tvYQ.setText(arr.length>3?arr[3]+"%  标准:21%":"无      标准:21%");
		String kq = "";
		if(arr.length>4){
			if("1".equals(arr[4])){
				kq="优";
			}else if("2".equals(arr[4])){
				kq="良";
			}else if("3".equals(arr[4])){
				kq="一般";
				
			}else if("4".equals(arr[4])){
				kq="差";
			}else{
				kq="一般";
			}  
		}
		this.tvKQ.setText(kq);
		if(arr.length>6){
			if(null != gpsPoint){
				LatLng point2 = GpsUtils.gpsPointToMap(arr[5], arr[6]);//new LatLng(Double.parseDouble(arr[5]), Double.parseDouble(arr[6]));
				if(!gpsPoint.equals(point2)){
					// 将GPS设备采集的原始GPS坐标转换成百度坐标  
					gpsPoint = point2;
					point = GpsUtils.gpsPointToBaiduMap(gpsPoint);
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
				}
			}else{
				gpsPoint = GpsUtils.gpsPointToMap(arr[5], arr[6]);//new LatLng(Double.parseDouble(arr[5]), Double.parseDouble(arr[6]));
				point = GpsUtils.gpsPointToBaiduMap(gpsPoint);
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));	
			}
			
			//this.tvWZ.setText("正在计算...");
		}
	}
	
	public void showMap(LatLng point){
		if(null == point){
			Toast.makeText(RevActivity.this.getApplicationContext(),
					"无定位信息，无法显示地图", Toast.LENGTH_SHORT).show();
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putDouble("latitude", point.latitude);
		bundle.putDouble("longitude", point.longitude);
		bundle.putString("DevUuid", devUuid);
		Intent revIntent = new Intent(RevActivity.this,RevMapActivity.class); 
		revIntent.putExtras(bundle);
		startActivity(revIntent);
	}

	protected void onDestroy() {
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.setbPause(false);
		countDownLatch = new CountDownLatch(1);
		super.onStart();
		revThread = new Thread(refreshHandle);
		revThread.start();
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
		super.onPause();
	}
	public Handler getHandler(){
		return myHandler;
	}

	private Handler myHandler = new Handler() {
		int tag = 1;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Rev rev = (Rev) msg.obj;
				updateView(rev);
				break;
			case -1:
				Toast.makeText(RevActivity.this.getApplicationContext(),
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
	
	
    


	class Refresh implements Runnable{
    	//private WifiManager.MulticastLock lock;
    	public Refresh(WifiManager manager) {
          //  this.lock= manager.createMulticastLock("UDPwifi"); 
       }
		@Override
		public void run() {
			Rev rev=null;
			while(true){
				if(!RevActivity.this.isbPause())
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
						//List<Map<String, Object>> listData = revToLv(curRev);
						Message msg = Message.obtain(); 
						msg.obj = curRev;
						msg.what = 1;
						getHandler().sendMessage(msg);//向Handler发送消息，更新UI
					} catch (IOException e) {
						Message msg = Message.obtain();
						msg.what=-1;
						msg.obj = e;
			            getHandler().sendMessage(msg);
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
    
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menulink:
			Intent i = new Intent(RevActivity.this, ZNDevConfigActivity.class);
			startActivity(i);
		default:
			break;
		}
		return false;
        
   };
   
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RevActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(RevActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RevActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			tvWZ.setText("未能找到结果");
		}else{
			if(!tvWZ.getText().equals(result.getAddress())){
				tvWZ.setText(result.getAddress());
				Toast.makeText(RevActivity.this, result.getAddress(),
						Toast.LENGTH_LONG).show();
			}
		}

	}

}
