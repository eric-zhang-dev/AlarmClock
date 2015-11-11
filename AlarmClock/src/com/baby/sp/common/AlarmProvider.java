package com.baby.sp.common;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AlarmProvider extends ContentProvider {

	//定义SQLiteOpenHelper帮助类的子类DabaseHelper的对象
	private DabaseHelper mDabaseHelper;
	private final static String TAG = "AlarmProvider";
	//标识 URI
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	/*
	 * 添加URI
	 */
	static{
		mUriMatcher.addURI(Alarm.AUTHORITIES, "alarms", ALARMS);
		mUriMatcher.addURI(Alarm.AUTHORITIES, "alarms/#", ALARMS_ID);
	}
	
	@Override
	public boolean onCreate() {
		mDabaseHelper = new DabaseHelper(getContext(), Alarm.DATABASE_NAME);
		return true;
	}

	/*
	 * 设置返回类型
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		int match = mUriMatcher.match(uri);
		switch(match){
		case ALARMS:
			return Alarm.Columns.CONTENT_TYPE;
		case ALARMS_ID:
			return Alarm.Columns.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("找不到URI：" + uri);
		}
	}
	
	/*
	 * 查找数据
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		//相当于拼接查询SQL语句
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		Log.v(TAG, "查询，uri:"+uri);
		int match = mUriMatcher.match(uri);
		switch(match){
			case ALARMS:
				builder.setTables(Alarm.TABLE_NAME);
				break;
			case ALARMS_ID:
				builder.setTables(Alarm.TABLE_NAME);
				builder.appendWhere("_id=" + uri.getPathSegments().get(1));
				break;
			default:
				throw new IllegalArgumentException("找不到URI：" + uri);
		}
		//得到SqliteDatabase对象
		SQLiteDatabase db = mDabaseHelper.getReadableDatabase();
		//得到查询后的游标
		Cursor ret = builder.query(db, projection, selection, 
				selectionArgs, null, null, sortOrder);
		if(ret != null){
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return ret;
	}

	/*
	 * 插入数据
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		if(mUriMatcher.match(uri) == ALARMS){
			SQLiteDatabase db = mDabaseHelper.getWritableDatabase();
			long rowId = db.insert(Alarm.TABLE_NAME, null, values);
			if(rowId > 0){
				Uri newUri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
				Log.v(TAG, "插入，uri:"+newUri);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		}
		return null;
	}

	/*
	 * 删除数据
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		Log.v(TAG, "删除，uri:"+uri);
		int count = 0;
		SQLiteDatabase db = mDabaseHelper.getWritableDatabase();
		switch(mUriMatcher.match(uri)){
		case ALARMS:
			count = db.delete(Alarm.TABLE_NAME, where, whereArgs);
			break;
		case ALARMS_ID:
			long rowId = Long.parseLong(uri.getPathSegments().get(1));
			if(TextUtils.isEmpty(where)){
				where = "_id=" + rowId;
			}else{
				where = "_id=" + rowId + " and (" + where + ")";
			}
			count = db.delete(Alarm.TABLE_NAME, where , whereArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		}
		return count;
	}

	/*
	 * 修改数据
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.v(TAG, "修改，uri:"+uri);
		int count = 0;
		if(mUriMatcher.match(uri) == ALARMS_ID){
			SQLiteDatabase db = mDabaseHelper.getWritableDatabase();
			long rowId = Long.parseLong(uri.getPathSegments().get(1));
			count = db.update(Alarm.TABLE_NAME, values, "_id=" + rowId,null);
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

}
