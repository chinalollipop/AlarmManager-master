package com.coder80.timer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.coder80.timer.TimerApplication;
import com.coder80.timer.utils.Constants;
import com.coder80.timer.utils.ServiceUtil;


/**
 * Created by Daniel on 2015/8/18.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "BootBroadcastReceiver";
    private Context mContext;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("BootBroadcastReceiver", "BroadcastReceiver onReceive here.... ");
            Handler handler = new Handler(Looper.getMainLooper());
            //after reboot the device,about 2 minutes later,upload the POI info to server
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!ServiceUtil.isServiceRunning(mContext,Constants.POI_SERVICE)){
                       // ServiceUtil.invokeTimerPOIService1(mContext,100);
                    }
                }
            }, Constants.BROADCAST_ELAPSED_TIME_DELAY);
        }else if(intent.getAction().equals("android.intent.action.cq")){
        	if(TimerApplication.application.cq!=0){
        		Log.w(TAG, "倒计时一【"+--TimerApplication.application.cq+"】");
        		Intent intent1 = new Intent("intent.action.cq");
        		intent1.putExtra("endTime", TimerApplication.application.cq);
        		mContext.sendBroadcast(intent1);
        	}else{
        		ServiceUtil.cancleAlarmManager1(mContext);
        		TimerApplication.application.cq = 100;
        	}
        }else if(intent.getAction().equals("android.intent.action.hn")){
        	if(TimerApplication.application.hn!=0){
        		Log.w(TAG, "倒计时一【"+--TimerApplication.application.hn+"】");
        		Intent intent2 = new Intent("intent.action.hn");
        		intent2.putExtra("endTime", TimerApplication.application.hn);
        		mContext.sendBroadcast(intent2);
        	}else{
        		ServiceUtil.cancleAlarmManager2(mContext);
        		TimerApplication.application.hn=50;
        	}
        }
    }
}

