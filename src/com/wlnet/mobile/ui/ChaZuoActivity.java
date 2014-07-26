package com.wlnet.mobile.ui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.wlnet.mobile.R;
import com.wlnet.mobile.R.drawable;
import com.wlnet.mobile.R.id;
import com.wlnet.mobile.R.layout;
import com.wlnet.mobile.R.menu;
import com.wlnet.mobile.common.Const;
import com.wlnet.mobile.dao.RevDao;
import com.wlnet.mobile.net.TcpReceive;
import com.wlnet.mobile.pojo.Rev;
import com.wlnet.mobile.ui.RevActivity.Refresh;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ChaZuoActivity extends Activity {
	private ImageView imgChazuo;

	private Bundle bundle;
	private String devUuid;
	private Refresh refreshHandle;
	private Rev queryRev ;
	private boolean bPause;
	private Thread revThread;
	private String strOpen ;
	private String strClose ;
	private CountDownLatch countDownLatch;
	
    public synchronized boolean isbPause() {
		return bPause;
	}

	public synchronized void setbPause(boolean bPause) {
		this.bPause = bPause;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cha_zuo);
		bundle = getIntent().getExtras();
		devUuid = bundle.getString("DevUuid");
		queryRev = new Rev();
		queryRev.setDevUuid(devUuid);
		strOpen = devUuid+Const.Sym.cmd+TcpReceive.CMD_OPEN;
		strClose = devUuid+Const.Sym.cmd+TcpReceive.CMD_CLOSE;
		WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		refreshHandle = new Refresh(manager);
		imgChazuo = (ImageView) this.findViewById(R.id.imgChazuo);
		imgChazuo.setTag(R.drawable.czclose);
		imgChazuo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Integer src = (Integer) imgChazuo.getTag();
				switch(src){
				case R.drawable.czopen:
					Thread thread1 = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								Rev rev = TcpReceive.switchCZ(queryRev, strClose);
								Message msg = Message.obtain(); 
								msg.obj = rev;
								msg.what = 1;
					            myHandler.sendMessage(msg);//向Handler发送消息，更新UI
							} catch (IOException e) {
								Message msg = Message.obtain();
								msg.what=-1;
								msg.obj = e;
					            myHandler.sendMessage(msg);
							}
							
						}});
					thread1.start();
					break;
				case R.drawable.czclose:
					Thread thread2 = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								Rev rev = TcpReceive.switchCZ(queryRev, strOpen);
								Message msg = Message.obtain(); 
								msg.obj = rev;
								msg.what = 1;
					            myHandler.sendMessage(msg);//向Handler发送消息，更新UI
							} catch (IOException e) {
								Message msg = Message.obtain();
								msg.what=-1;
								msg.obj = e;
					            myHandler.sendMessage(msg);
							}
							
						}});
					thread2.start();
					break;
				}
			}
			
		});
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
					imgChazuo.setImageResource(R.drawable.czopen);
					imgChazuo.setTag(R.drawable.czopen);
					
				}else if(strClose.equals(value)){
					imgChazuo.setImageResource(R.drawable.czclose);
					imgChazuo.setTag(R.drawable.czclose);
				}
				break;
			case -1:
				Exception e = (Exception)msg.obj;
				String str = e.getMessage();
				if(str==null) str= "服务器没响应";
				Toast.makeText(ChaZuoActivity.this.getApplicationContext(),
						e.getMessage(), Toast.LENGTH_SHORT).show();
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
           // this.lock= manager.createMulticastLock("UDPwifi"); 
       }
		@Override
		public void run() {
			Rev rev=null;
			//ChaZuoActivity.this.setbPause(true);
			while(true){
				if(!ChaZuoActivity.this.isbPause())
				{
					try {
						//this.lock.acquire();
						rev = TcpReceive.requestCZ(queryRev);
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
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cha_zuo, menu);
		return true;
	}

}
