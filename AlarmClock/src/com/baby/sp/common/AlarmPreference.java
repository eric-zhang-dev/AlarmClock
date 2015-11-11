package com.baby.sp.common;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AlarmPreference {

	//文件名
	private final static String ALARM_SETTING = "alarm_setting";
	//key
	public final static String BELL_MODE_KEY = "bell_mode";
	public final static String CANCEL_MODE_KEY = "cancel_mode";
	public final static String NUM_TIMES_KEY = "num_times";
	public final static String SHAKE_ITEM_KEY = "shake_item";
	
	/**
	 * 保存设置
	 * @param context Context
	 * @param settings Map键值对
	 */
	public static void saveSetting(Context context , Map<String,Object> settings){
		if(settings != null && settings.size() > 0){
			/*
			 * Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，
			 *     在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中
			 * Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
			 * Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
			 *     MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；
			 *     MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
			 */
			SharedPreferences alarmSettings = 
					context.getSharedPreferences(ALARM_SETTING, Activity.MODE_PRIVATE);
			Editor editor = alarmSettings.edit();
			for (String key : settings.keySet()) {
				switch(key){
				case BELL_MODE_KEY:
					editor.putBoolean(BELL_MODE_KEY, (Boolean)settings.get(key));
					break;
				case CANCEL_MODE_KEY:
					editor.putInt(CANCEL_MODE_KEY, (int)settings.get(key));
					break;
				case NUM_TIMES_KEY:
					editor.putInt(NUM_TIMES_KEY, (int)settings.get(key));
					break;
				case SHAKE_ITEM_KEY:
					editor.putInt(SHAKE_ITEM_KEY, (int)settings.get(key));
					break;
				}				
			}
			//提交保存
			editor.commit();
		}
	}
	
	/**
	 * 获得设置的值
	 * @param context Context
	 * @param key 
	 * @return
	 */
	public static Object getSettingValue(Context context,String key){
		SharedPreferences alarmSettings = 
				context.getSharedPreferences(ALARM_SETTING, Activity.MODE_PRIVATE);
		Object o = null;
		switch(key){
		case BELL_MODE_KEY:
			o = alarmSettings.getBoolean(key, true);
			break;
		case CANCEL_MODE_KEY:
			o = alarmSettings.getInt(key, 0);
			break;
		case NUM_TIMES_KEY:
			o = alarmSettings.getInt(key, 3);
			break;
		case SHAKE_ITEM_KEY:
			o = alarmSettings.getInt(key, 5000);
			break;
		}
		return o;
	}
}
