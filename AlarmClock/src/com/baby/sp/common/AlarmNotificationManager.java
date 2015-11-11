package com.baby.sp.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.baby.sp.R;
import com.baby.sp.activity.MainActivity;

public class AlarmNotificationManager {

	private static NotificationManager notificationManager;
	
	/*
	 * 显示状态栏通知图标
	 */
	public static void showNotification(Context context, Alarm alarm){
//		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification();
//		//设置图标
//		notification.icon = R.drawable.icon;
//		// 表明在点击了通知栏中的"清除通知"后，此通知不清除， 经常与FLAG_ONGOING_EVENT一起使用
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(context, MainActivity.class);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
////		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		String title = context.getResources().getString(R.string.app_name);
		String hourStr = (alarm.hour+"").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
		String minutesStr = (alarm.minutes+"").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
		String str = hourStr + ":" + minutesStr + "\t" + alarm.label + "\t" + alarm.repeat;
//		notification.setLatestEventInfo(context, title, str, pi);
//		notificationManager.notify(0, notification);
		Notification notification = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(str)
				.setContentIntent(pi)
				.setSmallIcon(R.drawable.icon)
				.setWhen(System.currentTimeMillis())
				.build();
	}
	
	
	/*
	 * 取消状态栏通知图标
	 */
	public static void cancelNotification(Context context){
//		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		if(notificationManager != null){
			notificationManager.cancelAll();
		}
	}
	
}
