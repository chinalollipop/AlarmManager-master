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
