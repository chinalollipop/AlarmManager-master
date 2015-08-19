package com.coder80.timer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by Daniel on 2015/8/18.
 */
public class UploadPOIService extends Service implements Runnable{
    private String TAG = UploadPOIService.class.getSimpleName();
    private int mtime = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        uploadPOIInfo();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.w(TAG, "UploadPOIService onDestroy here.... ");
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub
    	mtime =  intent.getIntExtra("endTime",0);
    	Log.w(TAG, "UploadPOIService1-->onStartCommand1  Action 【"+intent.getAction()+"】 time 【" +mtime+"】"); 
    	return super.onStartCommand(intent, flags, startId);
    }
    private void uploadPOIInfo() {
    	//simulation HTTP request to server 
    	//Log.w(TAG, "uploadPOIInfo beign to upload POI to server ");
    	new Thread(this).start();
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Log.i(TAG, "UploadPOIService beign to upload POI to server ");
		
//		mtime--;
		//Log.w(TAG, "Thread .... have time " +mtime); 
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopSelf();
	}

}
