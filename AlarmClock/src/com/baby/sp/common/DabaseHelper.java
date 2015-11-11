package com.baby.sp.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author sjz
 * 继承SQLiteOpenHelper帮助类，通过getReadableDatabase()或 getWritableDatabase()方法得到SqliteDabase对象。
 * 
 */
public class DabaseHelper extends SQLiteOpenHelper {

	private final static int VERSION = 5;
	
	//SQLiteOpenHelper的子类必须有该构造函数
	public DabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public DabaseHelper(Context context, String name,int version) {
		this(context, name, null,version);
		// TODO Auto-generated constructor stub
	}
	
	public DabaseHelper(Context context, String name) {
		this(context, name, VERSION);
		// TODO Auto-generated constructor stub
	}

	/*
	 * 该函数是在第一次创建数据库的时候调用，实际上是在得到SqliteDabase对象的时候才调用。
	 * 即调用getReadableDatabase()或 getWritableDatabase()时才调用此方法。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table alarms("
				  + "_id INTEGER PRIMARY KEY, hour INTEGER,"
				  + "minutes INTEGER,repeat varchar(20),"
				  + "bell varchar(50),vibrate INTEGER,"
				  + "label varchar(50),enabled INTEGER,nextMillis INTEGER)");
		System.out.println("create");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
