package com.coder80.timer;

import com.coder80.timer.utils.ServiceUtil;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * Created by coder80 on 2014/10/31.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{
	private TextView mtvOne,mtvTwo;
	private Button mBtnStart1,mBtnStart2;
	private Button mBtnStop1,mBtnStop2;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		IntentFilter filter = new IntentFilter(); 
		filter.addAction("intent.action.hn");
		filter.addAction("intent.action.cq");
		this.registerReceiver(mBroadcastReceiver, filter);
		mtvOne = (TextView) findViewById(R.id.tvOne);
		mtvTwo = (TextView) findViewById(R.id.tvTwo);
		mBtnStart1 = (Button) findViewById(R.id.button1);
		mBtnStart2 = (Button) findViewById(R.id.button2);
		mBtnStop1 = (Button) findViewById(R.id.button3);
		mBtnStop2 = (Button) findViewById(R.id.button4);
		mBtnStart1.setOnClickListener(this);
		mBtnStart2.setOnClickListener(this);
		mBtnStop1.setOnClickListener(this);
		mBtnStop2.setOnClickListener(this);
		
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action= arg1.getAction();
			if(action.equals("intent.action.cq")){
				mtvOne.setText("状态一时间是："+arg1.getIntExtra("endTime", 0));
			}else if(action.equals("intent.action.hn")){
				mtvTwo.setText("状态二时间是："+arg1.getIntExtra("endTime", 0));
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch(id){
		case R.id.button1:
			ServiceUtil.invokeTimerPOIService1(mContext,1000);
			break;
		case R.id.button2:
			ServiceUtil.invokeTimerPOIService2(mContext,150);
			break;
		case R.id.button3:
			ServiceUtil.cancleAlarmManager1(mContext);
			TimerApplication.application.cq = 100;
			break;
		case R.id.button4:
			ServiceUtil.cancleAlarmManager2(mContext);
			TimerApplication.application.hn = 50;
			break;
		}
		
	}
}
