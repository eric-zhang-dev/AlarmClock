package com.baby.sp.common;

import java.io.Serializable;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("serial")
public class Alarm implements Serializable{

	//取消闹方式
	//解题
	public final static int CANCEL_NUM_MODE = 0;
	//摇晃手机
	public final static int CANCEL_SHAKE_MODE = 1;
	//闹铃重复方式
	//响一次
	public final static int ALARM_ONCE = 0;
	//周一到周五
	public final static int ALARM_MON_FRI = 1;
	//每天
	public final static int ALARM_EVERYDAY = 2;
	
	//访问的数据库名
	public static final String DATABASE_NAME = "clock.db";
	public static final String AUTHORITIES="com.baby.sp.common.AlarmProvider";
	public static final String TABLE_NAME = "alarms";
	//新建或编辑标志
	public static final int UPDATE_ALARM = 100;
	public static final int DELETE_ALARM = 200;
	
	public static class Columns implements BaseColumns{
		//AlarmProvider的访问Uri
		public static final Uri CONTENT_URI = 
				Uri.parse("content://" + AUTHORITIES + "/" + TABLE_NAME);
		//AlarmProvider返回的数据类型
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/alarms";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/alarms";
		
		//表中的列名
		public static final String HOUR = "hour";
		public static final String MINUTES = "minutes";
		public static final String REPEAT = "repeat";
		public static final String BELL = "bell";
		public static final String VIBRATE = "vibrate";
		public static final String LABEL = "label";
		public static final String ENABLED = "enabled";
		public static final String NEXTMILLIS = "nextMillis";
//		
		//默认排序
		public static final String DEFAULT_SORT_ORDER = HOUR + ", " + MINUTES + " ASC";
		//有效闹钟排序
		public static final String ENABLED_SORT_ORDER = NEXTMILLIS + " ASC";
		
		//有效
		public static final String ENABLED_WHER = "enabled = 1";
		
		public static final String[] ALARM_QUERY_COLUMNS = {
            _ID, HOUR, MINUTES, REPEAT,BELL,VIBRATE,
            LABEL,ENABLED,NEXTMILLIS}; 
		
		public static final int ID_INDEX = 0;
		public static final int HOUR_INDEX = 1;
		public static final int MINUTES_INDEX = 2;
		public static final int REPEAT_INDEX = 3;
		public static final int BELL_INDEX = 4;
		public static final int VIBRATE_INDEX = 5;
		public static final int LABEL_INDEX = 6;
		public static final int ENABLED_INDEX = 7;
		public static final int NEXTMILLIS_INDEX = 8;
	}
		
	//表中的列名
	public int id;
	public int hour;
	public int minutes;
	public String repeat;
	public String bell;
	public int vibrate;
	public String label;
	public int enabled;
	public long nextMillis;
	
	//默认构造器
	public Alarm() {}
	
	//构造器，将游标转换为Alarm对象
	public Alarm(Cursor cursor) {
		id = cursor.getInt(Alarm.Columns.ID_INDEX);
		hour = cursor.getInt(Alarm.Columns.HOUR_INDEX);
		minutes = cursor.getInt(Alarm.Columns.MINUTES_INDEX);
		repeat = cursor.getString(Alarm.Columns.REPEAT_INDEX);
		bell = cursor.getString(Alarm.Columns.BELL_INDEX);
		vibrate = cursor.getInt(Alarm.Columns.VIBRATE_INDEX);
		label = cursor.getString(Alarm.Columns.LABEL_INDEX);
		enabled = cursor.getInt(Alarm.Columns.ENABLED_INDEX);
		nextMillis = cursor.getLong(Alarm.Columns.NEXTMILLIS_INDEX);
	}
}
