package com.baby.sp.common;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AlarmHandle {

	private final static String TAG = "AlarmHandle";
	
	public AlarmHandle() {
		// TODO Auto-generated constructor stub
	}
	//增加一个闹钟
	public static void addAlarm(Context context , Alarm alarm){
		
		ContentValues values = alarm2ContentValues(alarm);
		Uri uri = context.getContentResolver().insert(Alarm.Columns.CONTENT_URI, values);
		alarm.id = (int) ContentUris.parseId(uri);
		Log.v(TAG, "增加了一条闹钟");
	}
	
	//删除一个闹钟
	public static void deleteAlarm(Context context , int alarmId){
		Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		context.getContentResolver().delete(uri, null, null);
		Log.v(TAG, "删除了一条闹钟");
	}
	
	//删除一个闹钟
	public static void deleteAllAlarm(Context context){
		context.getContentResolver().delete(Alarm.Columns.CONTENT_URI, null, null);
		Log.v(TAG, "删除了所有闹钟");
	}
	
	//更新指定ID的闹钟的相关属性
	public static void updateAlarm(Context context , ContentValues values , int alarmId){
		Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		context.getContentResolver().update(uri, values,null, null);
		Log.v(TAG, "修改了一条闹钟");
		
	}
	
	//根据ID号获得闹钟的信息
	public static Alarm getAlarm(Context context , int alarmId){
		Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		Cursor cursor = context.getContentResolver().query(uri, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
		Alarm alarm = null;
		if(cursor != null){
			if(cursor.moveToFirst()){
				alarm = new Alarm(cursor);
			}
		}
		cursor.close();
		return alarm;
	}
	
	//根据ID号获得闹钟的信息
	public static Alarm getNextAlarm(Context context){
		Cursor cursor = context.getContentResolver()
				.query(Alarm.Columns.CONTENT_URI, 
					   Alarm.Columns.ALARM_QUERY_COLUMNS,
					   Alarm.Columns.ENABLED_WHER,null, 
					   Alarm.Columns.ENABLED_SORT_ORDER);
		Alarm alarm = null;
		if(cursor != null){
//			while(cursor.moveToNext()){
//				alarm = new Alarm(cursor);
//				String repeat = alarm.repeat;
//				String [] repeats = context.getResources().getStringArray(R.array.repeat_item);
//				if(repeats[Alarm.ALARM_ONCE].equals(repeat)){
//					break;
//				}else if(repeats[Alarm.ALARM_MON_FRI].equals(repeat)){
//					Calendar calendar = Calendar.getInstance();
//					if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
//							|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
//						continue;
//					}
//				}else if(repeats[Alarm.ALARM_EVERYDAY].equals(repeat)){
//					break;
//				}
//			}
			if(cursor.moveToFirst()){
				alarm = new Alarm(cursor);
			}
		}
		cursor.close();
		return alarm;
	}
	
	//获得所有闹钟
	public static List<Alarm> getAlarms(Context context){
		List<Alarm> alarms = new ArrayList<Alarm>();
		Cursor cursor = context.getContentResolver()
				.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, 
						null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
		if(cursor != null){
			while(cursor.moveToNext()){
				alarms.add(new Alarm(cursor));
			}
		}
		cursor.close();
		return alarms;
	}
	
	private static ContentValues alarm2ContentValues(Alarm alarm) {
		ContentValues values = new ContentValues();
		values.put(Alarm.Columns.HOUR, alarm.hour);
		values.put(Alarm.Columns.MINUTES, alarm.minutes);
		values.put(Alarm.Columns.REPEAT, alarm.repeat);
		values.put(Alarm.Columns.BELL, alarm.bell);
		values.put(Alarm.Columns.VIBRATE, alarm.vibrate);
		values.put(Alarm.Columns.LABEL, alarm.label);
		values.put(Alarm.Columns.ENABLED, alarm.enabled);
		return values;
	}
}
