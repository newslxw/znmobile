package com.wlnet.mobile.ui;

import com.lsd.smartconfig.lib.ConfigStatus;
import com.lsd.smartconfig.lib.SmartConfigActivity;
import com.wlnet.mobile.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ZNDevConfigActivity extends SmartConfigActivity {
	private ZNDevConfigActivity me = this;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_zndev_config);
		// Show the Up button in the action bar.
		//setupActionBar();
	}
	
	/**
	 * 
	 * 可以在此自定义布局文件、更改提示文字。
	 */
	@Override
	protected void renderView(Bundle savedInstanceState) {
		//超时设置
		this.SMART_CONFIG_TIMEOUT = 30000;
		//提示信息设置
		this.TIP_CONFIGURING_DEVICE = me.getString(R.string.tip_configuring_device);
		this.TIP_DEVICE_CONFIG_SUCCESS= me.getString(R.string.tip_device_config_success);
		this.TIP_WIFI_NOT_CONNECTED=me.getString(R.string.tip_wifi_not_connected);
		
		setContentView(R.layout.activity_zndev_config);
		//ssidEt,pwdEt,showPwd,deviceCountGroup为约定的控件实例变量，不可更改。
		connectBtn = (Button)findViewById(R.id.connect);
		ssidEt = (TextView)findViewById(R.id.ssid);
		pwdEt = (EditText)findViewById(R.id.pwd);
		showPwd = (CheckBox)findViewById(R.id.showPwd);
		deviceCountGroup = (RadioGroup)findViewById(R.id.deviceCountGroup);
		deviceCountGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.deviceCountSingle){
					me.deviceCountMode = ZNDevConfigActivity.DEVICE_COUNT_ONE;
				}else{
					me.deviceCountMode = ZNDevConfigActivity.DEVICE_COUNT_MULTIPLE;
				}
			}
		});
	}

	@Override
	protected void onSingleConfigTimeout() {
		Toast.makeText(me, me.getString(R.string.tip_timeout),Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onSuccess(ConfigStatus arg0) {
		if(me.deviceCountMode == DEVICE_COUNT_ONE){
			Toast.makeText(me, String.format(me.getString(R.string.tip_device_config_success), configStatus.mac),Toast.LENGTH_SHORT).show();
		}else if(me.deviceCountMode == DEVICE_COUNT_MULTIPLE){
			Toast.makeText(me, me.getString(R.string.tip_timeout),Toast.LENGTH_LONG).show();
		}
		
	}

}
