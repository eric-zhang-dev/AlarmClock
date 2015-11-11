package com.baby.sp.common;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baby.sp.R;

public class AlarmClockManager {

	private final static String TAG = "AlarmClockManager";
	//日历
	private static Calendar calendar = Calendar.getInstance();
	//闹铃管理
	private static AlarmManager am;
	
	/**
	 * 设置提示信息
	 * @param context 上下文
	 * @param hour 小时
	 * @param minute 分钟
	 */
	public static void setAlarm(Context context , Alarm alarm){
		String[] repeats = context.getResources().getStringArray(R.array.repeat_item);
		long timeMillis = time2Millis(alarm.hour , alarm.minutes , alarm.repeat , repeats);
		//将下次响铃时间的毫秒数存到数据库
		ContentValues values = new ContentValues();
		values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
		AlarmHandle.updateAlarm(context, values, alarm.id);
		Toast.makeText(context, fomatTip(timeMillis) , Toast.LENGTH_SHORT).show();
		System.out.println(fomatTip(timeMillis));
		//设置闹钟
		setNextAlarm(context);
	}
	
	/**
	 * 设置闹钟
	 * @param context 上下文
	 */
	public static void setNextAlarm(Context context){
		Log.v(TAG, "获得最近有效的闹钟");
		Alarm alarm = AlarmHandle.getNextAlarm(context);
		if(alarm != null){
			Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
			intent.putExtra(Alarm.Columns._ID, alarm.id);
			PendingIntent pi = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis, pi);
			//显示通知
			AlarmNotificationManager.showNotification(context,alarm);
		}else{
			AlarmNotificationManager.cancelNotification(context);
		}
	}
	
	public static void cancelAlarm(Context context , int id ){
		Log.v(TAG, "cancelAlarm");
		Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
		PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		setNextAlarm(context);
	}
	
	private static String fomatTip(long timeMillis) {
	    long delta = timeMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" : days+"天";

        String hourSeq = (hours == 0) ? "" : hours + "小时";
        	
        String minSeq = (minutes == 0) ? "1分钟" : minutes + "分钟";
        
		return "已将闹钟设置为从现在起"+daySeq+hourSeq+minSeq+"后提醒";
	}

	public static Long time2Millis(int hour , int minute , String repeat , String[] repeats){
		calendar.setTimeInMillis(System.currentTimeMillis()); 
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//闹钟重复模式为 只响一次或每天
		if(repeat.equals(repeats[Alarm.ALARM_ONCE]) || repeat.equals(repeats[Alarm.ALARM_EVERYDAY])){
			//若时间已经过去，则推迟一天
			if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
				System.out.println("过时延迟一天");
				calendar.roll(Calendar.DATE, 1);
			}
		}else if(repeat.equals(repeats[Alarm.ALARM_MON_FRI])){
			//闹钟重复模式为 周一到周五
			if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
				//周五若时间已经过去，则推迟3天
				if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
					calendar.roll(Calendar.DATE, 3);
				}
			}else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				//周六
				calendar.roll(Calendar.DATE, 2);
			} else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				//周日
				calendar.roll(Calendar.DATE, 1);
			}else{
				//若时间已经过去，则推迟一天
				if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
					System.out.println("过时延迟一天");
					calendar.roll(Calendar.DATE, 1);
				}
			}
		}
		return calendar.getTimeInMillis();
	}
	
}
