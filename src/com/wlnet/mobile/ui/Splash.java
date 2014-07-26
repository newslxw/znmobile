package com.wlnet.mobile.ui;

import com.wlnet.mobile.R;
import com.wlnet.mobile.utils.DBHelper;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class Splash extends Activity {

	   private final int SPLASH_DISPLAY_LENGHT = 1000; //延迟三秒 

	    @Override 
	    public void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState); 
	      //  requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        setContentView(R.layout.activity_splash); 
	        //创建数据库
	      //  DBHelper.getInstance(this).runTestData();
	        new Handler().postDelayed(new Runnable(){ 

	         @Override 
	         public void run() { 
	             Intent mainIntent = new Intent(Splash.this,DevActivity.class); 
	             Splash.this.startActivity(mainIntent); 
	                 Splash.this.finish(); 
	         } 
	            
	        }, SPLASH_DISPLAY_LENGHT); 
	    } 
}
