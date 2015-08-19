# AlarmManager-master
AlarmManager计时器【倒计时】

 
在实际讨论之前，我们首先看一个实例之后，然后再讨论Android中Timer和AlarmManager的具体区别

在Android上常用的定时器有两种，一种是Java.util.Timer，一种就是系统的AlarmService了。

实验1：使用Java.util.Timer。

在onStart()创创建Timer，每5秒更新一次计数器，并启动。

Java代码

1mTimer = new Timer();
2mTimer.schedule(new TimerTask() {
3 @Override
4 public void run() {
5 ++mCount;
6 mHandler.sendEmptyMessage(0);
7 }
8 }, 5*1000, 5*1000);

当连接USB线进行调试时，会发现一切工作正常，每5秒更新一次界面，即使是按下电源键，仍然会5秒触发一次。
当拔掉USB线，按下电源键关闭屏幕后，过一段时间再打开，你会发现定时器明显没有继续计数，停留在了关闭电源键时的数字。
甚至，你可能会发现屏幕显示的时间有时候在跳帧或者调频【Timer之间再抢夺资源导致的】

实验2：使用AlarmService：
2.1通过AlarmService每个5秒发送一个广播，setRepeating时的类型为AlarmManager.ELAPSED_REALTIME。

Java代码
1AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
2am.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, 5*1000, sender);

拔掉USB线，按下电源键，过一段时间再次打开屏幕，发现定时器没有继续计数。

AlarmManager的setRepeating()相当于Timer的Schedule(task,delay,peroid);

有点差异的地方时Timer这个方法是指定延迟多长时间，以后开始周期性的执行task;

2.2setRepeating是的类型设置为AlarmManager.ELAPSED_REALTIME_WAKEUP

Java代码

1AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
2am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 5*1000, sender);

拔掉USB线，按下电源键，过一点时间再次打开屏幕，发现定时器一直在计数。

如此看来，使用WAKEUP才能保证自己想要的定时器一直工作，但是肯定会引起耗电量的增加。



接下来，我们可以讨论Android中Timer和AlarmManager的区别

首先看一下AlarmManager的使用传递的几个方法以及参数

AlarmManger启动定时任务，其常用的方法

(1)set(int type，long startTime，PendingIntent pi)； 
        该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。 
(2)setRepeating(int type，long startTime，long intervalTime，PendingIntent pi)；

        该方法用于设置重复闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟首次执行时间，第三个参数表示闹钟两次执行的间隔时间，第四个参数表示闹钟响应动作。类似JAVA的Timer里面scheduleAtFixedRate(TimerTask task, long delay, long period)：以近似固定的时间间隔（由指定的周期分隔）进行后续执行。在固定速率执行中，根据已安排的初始执行时间来安排每次执行。如果由于任何原因（如垃圾回收或其他后台活动）而延迟了某次执行，则将快速连续地出现两次或更多的执行，从而使后续执行能够“追赶上来”。从长远来看，执行的频率将正好是指定周期的倒数（假定 Object.wait(long) 所依靠的系统时钟是准确的）。

(3)setInexactRepeating（int type，long startTime，long intervalTime，PendingIntent pi）； 
        该方法也用于设置重复闹钟，与第二个方法相似，不过其两个闹钟执行的间隔时间不是固定的而已。它相对而言更节能（power-efficient）一些，因为系统可能会将几个差不多的闹钟合并为一个来执行，减少设备的唤醒次数。 有点类似JAVA的Timer里面schedule(TimerTask task, Date firstTime, long period)：根据前一次执行的实际执行时间来安排每次执行。如果由于任何原因（如垃圾回收或其他后台活动）而延迟了某次执行，则后续执行也将被延迟。在长期运行中，执行的频率一般要稍慢于指定周期的倒数（假定 Object.wait(long) 所依靠的系统时钟是准确的）。

(4)cancel(PendingIntent operation) 
       取消一个设置的闹钟
       其中需要注意的是取消的Intent必须与启动Intent保持绝对一致才能支持取消AlarmManager
(5)setTimeZone(String timeZone) 
       设置系统的默认时区。需要android.permission.SET_TIME_ZONE权限

注解：使用alarmManager启动service时，用service的action（service在manifest中声明多个action）来启动，防止用service.class导致pendingintent相互覆盖影响的情况！重要！


主要分析这两个方法

1、public void setInexactRepeating (int type, long triggerAtTime, long interval, PendingIntent operation)

设置不精准重复周期
不精准重复周期：有很多类似的警报，类型范围比较大的时候，这些警报就会合并为一个警报，这样可以不用每次都执行警报，是一种节能型
参数的意思
 
2、public void setRepeating (int type, long triggerAtTime, long interval, PendingIntent operation)

设置精准重复周期
这里有四个参数
第一个参数：即警报的类型，一般取值是AlarmManager.RTC和AlarmManager.RTC_WAKEUP，如果为RTC表示为一个正常的定时器，如果是RTC_WAKEUP则除了有定时器外还可以有震动或者响铃。另外就是RTC在手机睡眠的时候不发射警报，而RTC_WAKEUP则在睡眠的时候也会发射警报
第二个参数：第一次运行时要等待的时间，也就是执行延迟时间，单位是毫秒。例如：现在7点，你设置8点提醒，那么就是7点到8点的时差
第三个参数：表示执行的时间间隔，单位是毫秒，也就是每过多久发射一次警报，一般都是以天为单位
第四个参数：一个PendingIntent对象，即到时间后要执行的操作

2.Type
AlarmManager.RTC                  硬件闹钟，不唤醒手机（也可能是其它设备）休眠；当手机休眠时不发射闹钟。
AlarmManager.RTC_WAKEUP   硬件闹钟，当闹钟发躰时唤醒手机休眠；
AlarmManager.ELAPSED_REALTIME      真实时间流逝闹钟，不唤醒手机休眠；当手机休眠时不发射闹钟。
AlarmManager.ELAPSED_REALTIME_WAKEUP     真实时间流逝闹钟，当闹钟发躰时唤醒手机休眠；
时间纬度：绝对时间（常规的年月日）  or  相对时间（相对于开机之后，例如开机10分钟）

CPU唤起： 唤起 OR 不唤起

绝对时间+ 唤起CPU=AlarmManager.RTC_WAKEUP   System.currentTimeMillis()

绝对时间+不唤起CPU=AlarmManager.RTC         System.currentTimeMillis()

相对时间+ 唤起CPU=AlarmManager.ELAPSED_REALTIME_WAKEUP     SystemClock.elapsedRealtime()

相对时间+不唤起CPU=AlarmManager.ELAPSED_REALTIME           SystemClock.elapsedRealtime()


3.PendingIntent 对象
在使用AlarmManager时一般都会用到PendingIntent，所以在这里进行扩展Activity、Service、BroadcastReceiver,
想对应的有PendingIntent.getActivity（）、PendingIntent.getService（）、PendingIntent.getBroadcast
PendingIntent与Intent和很相似，Intent是意图的意思，想要做什么，而PendingIntent相对于它来说，就是计划将要做什么，比如闹钟、每天发送一次当前位置给服务器等等

pendingintent使用的三种情况

你可以通过getActivity(Context context, int requestCode, Intent intent, int flags)系列方法从系统取得一个用于启动一个Activity的PendingIntent对象,

可以通过getService(Context context, int requestCode, Intent intent, int flags)方法从系统取得一个用于启动一个Service的PendingIntent对象

可以通过getBroadcast(Context context, int requestCode, Intent intent, int flags)方法从系统取得一个用于向BroadcastReceiver的Intent广播的PendingIntent对象

 
pendingIntent的第4个参数

FLAG_CANCEL_CURRENT:如果当前系统中已经存在一个相同的PendingIntent对象，那么就将先将已有的PendingIntent取消，然后重新生成一个PendingIntent对象。

FLAG_NO_CREATE:如果当前系统中不存在相同的PendingIntent对象，系统将不会创建该PendingIntent对象而是直接返回null。

FLAG_ONE_SHOT:该PendingIntent只作用一次。在该PendingIntent对象通过send()方法触发过后，PendingIntent将自动调用cancel()进行销毁，那么如果你再调用send()方法的话，系统将会返回一个SendIntentException。

FLAG_UPDATE_CURRENT:如果系统中有一个和你描述的PendingIntent对等的PendingInent，那么系统将使用该PendingIntent对象，但是会使用新的Intent来更新之前PendingIntent中的Intent对象数据，例如更新Intent中的Extras。

 
 看了这么多介绍，接下来我们说需求，以及为什么要使用到他
 
 项目需求：
 1、计时器需要多个，并且相互之间不受影响，比如计时器一的运算不会影响计时器二的运算，相互的显示也不受影响；
 2、考虑手机的电量使用情况【此处是一个性能优化的问题，再保证程序稳定性的时候，才会考虑其健壮性】
 思考：由于项目的需要，我的计时器可能需要多个，并且相互之间没有影响，通过AlarmService和Timer的对比，无论从性能还是程序的健壮性上，因为Timer在实际的开发中不安全，比如手机关闭电源键，他有时间不会跳动，一直停留在原来的时间，并且受程序代码的影响，比如系统资源不足时，会把当前的计时器回收掉。而AlarmService却可以屏蔽这一点，因为他调用的是系统CPU的资源，不受其他影响，除非手机关机了，或者重启了，没有打开此APP等等
 
 认真思考之后，我的构思是这样的：
 第一、使用一个注册广播【全局性的】来监听AlarmService每次发出的请求并且根据不同的AlarmService【绑定的requestCode、Action不同】,做出像对应的处理和界面显示
 第二、每添加一个计时器，开辟一个相对应的AlarmManager，
 
 回顾：其实可以使用Serive来做此问题，综合考虑，感觉不合适，后来废弃了，如果有其他用户使用Service来实现此功能，可以提供参考。
 
 接着就是代码实现了。
 
 
 首先增加一个全局的广播
 java代码：
 
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


然后是一个实体类【启动AlarmService】

 package com.coder80.timer.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.List;

import com.coder80.timer.receiver.BootBroadcastReceiver;
import com.coder80.timer.service.UploadPOIService;
import com.coder80.timer.service.UploadPOIService2;
/**
 * Created by Daniel on 2015/8/18.
 */

public class ServiceUtil {

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(Constants.RETRIVE_SERVICE_COUNT);

        if(null == serviceInfos || serviceInfos.size() < 1) {
            return false;
        }

        for(int i = 0; i < serviceInfos.size(); i++) {
            if(serviceInfos.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        Log.i("ServiceUtil-AlarmManager", className + " isRunning =  " + isRunning);
        return isRunning;
    }

    public static void invokeTimerPOIService1(Context context,int curMillis){
        Log.i("ServiceUtil-AlarmManager1", "invokeTimerPOIService1 wac called.." );
        PendingIntent alarmSender = null;
        Intent startIntent = new Intent(context, BootBroadcastReceiver.class);
        startIntent.setAction("android.intent.action.cq");
        startIntent.putExtra("endTime", curMillis);
        try {
            alarmSender = PendingIntent.getBroadcast(context, 1, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("ServiceUtil-AlarmManager", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.ELAPSED_TIME, alarmSender);
        //am.setExact(AlarmManager.RTC_WAKEUP,  Constants.ELAPSED_TIME, alarmSender);
    }
    
    public static void invokeTimerPOIService2(Context context,int curMillis){
        Log.i("ServiceUtil-AlarmManager2", "invokeTimerPOIService2 wac called.." );
        PendingIntent alarmSender = null;
        Intent startIntent = new Intent(context, BootBroadcastReceiver.class);
        startIntent.setAction("android.intent.action.hn");
        startIntent.putExtra("endTime", curMillis);
        try {
            alarmSender = PendingIntent.getBroadcast(context, 2, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("ServiceUtil-AlarmManager", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.ELAPSED_TIME, alarmSender);
        //am.setExact(AlarmManager.RTC_WAKEUP,  Constants.ELAPSED_TIME, alarmSender);
    }
    

    public static void cancleAlarmManager1(Context context){
        Log.i("ServiceUtil-AlarmManager1", "cancleAlarmManager1 to start ");
        Intent intent = new Intent(context,BootBroadcastReceiver.class);
    	intent.setAction("android.intent.action.cq");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }
    
    public static void cancleAlarmManager2(Context context){
        Log.i("ServiceUtil-AlarmManager2", "cancleAlarmManager2 to start ");
        Intent intent = new Intent(context,BootBroadcastReceiver.class);
    	intent.setAction("android.intent.action.hn");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 2, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }
    
}



显示界面：

package com.coder80.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coder80.timer.utils.ServiceUtil;
/**
 * Created by Daniel on 2015/8/18.
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


项目清单文件：

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.coder80.timer"
    android:versionCode="1"
    android:versionName="1.0" >

    <uses-sdk
        android:minSdkVersion="8"
        android:targetSdkVersion="15" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        android:name="com.coder80.timer.TimerApplication"
         >
        <activity
            android:name=".MainActivity"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- <service android:name="com.coder80.timer.service.UploadPOIService" >
            <intent-filter>
                <action android:name="com.coder80.timer.service.UploadPOIService" />
            </intent-filter>
        </service> -->
        <service android:name="com.coder80.timer.service.UploadPOIService2" >
            <intent-filter>
                <action android:name="com.coder80.timer.service.UploadPOIService2" />
            </intent-filter>
        </service>
        <receiver
            android:name="com.coder80.timer.receiver.BootBroadcastReceiver"
            android:exported="false" >
            <intent-filter android:priority="1000">
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
                <action android:name="android.intent.action.USER_PRESENT"/>
                <action android:name="android.intent.action.cq"/>
                <action android:name="android.intent.action.hn"/>
            </intent-filter>
        </receiver>
    </application>

</manifest>



最后就是运行效果了，可以看到，基本满足我的需求 了。，谢谢。


 
 
 
 
 
 
 
 



参考资料：
http://blog.csdn.net/Coder80/article/details/40742877
