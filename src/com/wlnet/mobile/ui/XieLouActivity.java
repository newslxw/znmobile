package com.wlnet.mobile.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.wlnet.mobile.R;
import com.wlnet.mobile.common.Const;
import com.wlnet.mobile.dao.DevDao;
import com.wlnet.mobile.dao.RevDao;
import com.wlnet.mobile.net.Receive;
import com.wlnet.mobile.net.TcpReceive;
import com.wlnet.mobile.pojo.Dev;
import com.wlnet.mobile.pojo.Rev;
import com.wlnet.mobile.ui.DevActivity.Refresh;
import com.wlnet.mobile.utils.DateUtil;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class XieLouActivity extends Activity {

	private ImageView imgXielou;
	private Bundle bundle;
	private String devUuid;
	private Refresh refreshHandle;
	private List<Rev> listRev;
	private Rev queryRev ;
	private RevDao revDao;
	private Rev curRev;
	private boolean bPause;
	private String strOpen ;
	private String strClose ;
	private Thread revThread;
	private CountDownLatch countDownLatch;
	private final static String strSafe = "安全"; 
	private final static String strDanger = "泄露"; 
	private  TextView lbInfo;
    public synchronized boolean isbPause() {
		return bPause;
	}

	public synchronized void setbPause(boolean bPause) {
		this.bPause = bPause;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xie_lou);
		bundle = getIntent().getExtras();
		devUuid = bundle.getString("DevUuid");
		queryRev = new Rev();
		queryRev.setDevUuid(devUuid);
		strOpen = TcpReceive.CMD_OPEN;
		strClose = TcpReceive.CMD_CLOSE;
		revDao = new RevDao(this);
		curRev = new Rev();
		WifiManager manager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
		refreshHandle = new Refresh(manager);
		listRev = revDao.query(queryRev);
		imgXielou = (ImageView) this.findViewById(R.id.imgXielou);
		lbInfo = (TextView) this.findViewById(R.id.labInfo);
		imgXielou.setTag(R.drawable.xielouclose);
		if(listRev!=null&&listRev.size()>0){
			curRev = listRev.get(0);
			if(strOpen.equals(curRev.getMeasureValue())){
				imgXielou.setImageResource(R.drawable.xielouopen);
				imgXielou.setTag(R.drawable.xielouopen);
				lbInfo.setText(strDanger);
			}
		}
		
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

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Rev rev = (Rev) msg.obj;
				if(rev==null) break;
				String value = rev.getMeasureValue();
				if(strOpen.equals(value)){
					imgXielou.setImageResource(R.drawable.xielouopen);
					imgXielou.setTag(R.drawable.xielouopen);
					lbInfo.setText(strDanger);
					
				}else if(strClose.equals(value)){
					imgXielou.setImageResource(R.drawable.xielouclose);
					imgXielou.setTag(R.drawable.xielouclose);
					lbInfo.setText(strSafe);
				}
				break;
			case -1:
				Toast.makeText(XieLouActivity.this.getApplicationContext(),
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
				if(!XieLouActivity.this.isbPause())
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
						msg.obj = rev;
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
    
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menulink:
			Intent i = new Intent(XieLouActivity.this, ZNDevConfigActivity.class);
			startActivity(i);
		default:
			break;
		}
		return false;
        
   };
    
}
