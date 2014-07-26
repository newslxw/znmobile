package com.wlnet.mobile.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wlnet.mobile.R;
import com.wlnet.mobile.dao.DevDao;
import com.wlnet.mobile.net.Receive;
import com.wlnet.mobile.net.TcpReceive;
import com.wlnet.mobile.pojo.Dev;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DevActivity extends ListActivity {

	private List<Dev> listDev ;
	private DevDao devDao;
	private Refresh refreshHandle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		devDao = new DevDao(this);
		WifiManager manager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
		refreshHandle = new Refresh(manager);
		Thread thread1 = new Thread(refreshHandle);
		
		listDev = devDao.query(null);
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>(); 
		for(int i=0; i<listDev.size(); i++){
			Map<String, Object> map = new HashMap<String, Object>();
			Dev dev = listDev.get(i);
			if("chazuo".equals(dev.getAppType())){
				map.put("img", R.drawable.czico);
			}else if("xielou".equals(dev.getAppType())){
				map.put("img", R.drawable.xielou);
			}else{
				map.put("img", R.drawable.sidu);
			}
    		map.put("text", dev.getDevName());
    		map.put("data", dev);
    		listData.add(map);
		}
		
		setListAdapter(new SimpleAdapter(this, listData, R.layout.lvitem_dev,
				new String[] { "img", "text" }, new int[] { R.id.imageDev,
						R.id.tvDevname }));
		ListView lv = getListView();
		// lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String, Object> item = (Map<String, Object>) parent
						.getItemAtPosition(pos);
				Dev dev = (Dev)item.get("data");
				if(dev==null)return;
				Bundle bundle = new Bundle();
				bundle.putString("DevUuid", dev.getDevUuid());
				String appType = dev.getAppType();
				if("chazuo".equals(appType)){
					Intent revIntent = new Intent(DevActivity.this,ChaZuoActivity.class); 
					revIntent.putExtras(bundle);
					startActivity(revIntent);
				}else if("xielou".equals(appType)){
					Intent revIntent = new Intent(DevActivity.this,XieLouActivity.class); 
					revIntent.putExtras(bundle);
					
					startActivity(revIntent);
				}else{
					Intent revIntent = new Intent(DevActivity.this,RevActivity.class); 
					revIntent.putExtras(bundle);
					
					startActivity(revIntent);
				}
				
			}
		});
		
		thread1.start();

    }
    
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				List<Map<String, Object>> listData = (List<Map<String, Object>>) msg.obj;
				DevActivity.this.setListAdapter(new SimpleAdapter(DevActivity.this, listData, R.layout.lvitem_dev,
						new String[] { "img", "text" }, new int[] { R.id.imageDev,
								R.id.tvDevname }));
				break;
			case -1:
				Toast.makeText(DevActivity.this.getApplicationContext(),
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
    	private WifiManager.MulticastLock lock;
    	public Refresh(WifiManager manager) {
            this.lock= manager.createMulticastLock("UDPwifi"); 
       }
		@Override
		public void run() {
			List<Dev> list=null;
			try {
				this.lock.acquire();
				list = TcpReceive.requestDevList();
			} catch (IOException e) {
				Message msg = Message.obtain();
				msg.what=-1;
				msg.obj = e;
	            myHandler.sendMessage(msg);
			}finally{
				this.lock.release();
			}
			if(list!=null&&list.size()>0){
				devDao.insertOrUpdate(list);
			}
			List<Dev> list2 = devDao.query(null);
			if(list2==null) return;
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>(); 
			for(int i=0; i<list2.size(); i++){
				Map<String, Object> map = new HashMap<String, Object>();
				Dev dev = list2.get(i);
				if("chazuo".equals(dev.getAppType())){
					map.put("img", R.drawable.czico);
				}else if("xielou".equals(dev.getAppType())){
					map.put("img", R.drawable.xielou);
				}else{
					map.put("img", R.drawable.sidu);
				}
	    		map.put("text", dev.getDevName());
	    		map.put("data", dev);
	    		listData.add(map);
			}
			Message msg = Message.obtain(); 
			msg.obj = listData;
			msg.what = 1;
            myHandler.sendMessage(msg);//向Handler发送消息，更新UI
		}
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dev, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menulink:
			Intent i = new Intent(DevActivity.this, ZNDevConfigActivity.class);
			startActivity(i);
		default:
			break;
		}
		return false;
        
   };
    
}
