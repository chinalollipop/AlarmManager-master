package com.coder80.timer;

import android.app.Application;

/**
 * Created by Daniel on 2015/8/18.
 */
public class TimerApplication extends Application {

	public static TimerApplication application;
	
	public int cq=100;
	public int hn=50;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		application =this;
	}
}
