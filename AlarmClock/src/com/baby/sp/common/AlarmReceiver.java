package com.baby.sp.common;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baby.sp.activity.AlarmDealActivity;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//开机
//			Intent service = new Intent(context, AlarmService.class);
//			context.startService(service);
			AlarmClockManager.setNextAlarm(context);
		}else{
			//转到闹铃界面
			Intent deal = new Intent(context, AlarmDealActivity.class);
			deal.putExtra(Alarm.Columns._ID, intent.getIntExtra(Alarm.Columns._ID, 0));
			deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deal);
		}
	}
}
