package com.baby.sp.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.baby.sp.R;
import com.baby.sp.common.Alarm;
import com.baby.sp.common.AlarmClockManager;
import com.baby.sp.common.AlarmHandle;

public class NewClockActivity extends Activity implements View.OnClickListener{

	//打开对话框的标志
	private final static int SHOW_REPEAT = 1;
	private final static int SHOW_LABEL = 2;
	private final static int DEL_ALARM = 3;
	
	private TimePicker timePicker;
	private TextView tv_repeat;
	private TextView tv_bell;
	private TextView tv_label;
	private CheckBox cb_vibration;
	private Button bt_del;
	
	private Alarm alarm;
	
	private boolean isNew = false;
	
	private Context context;
	
	private String bellPath;
	
	//是否打开震动
	private int vibration = 1;
	//记录重复方式 0只响一次，1周一到周五，2每天
	private int repeat = 0;
	private int repeatOld = 0;
	private int hour;
	private int minute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_clock);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		context = this;
		timePicker = (TimePicker) findViewById(R.id.clock);
		//设置24小时制
		timePicker.setIs24HourView(true);
		//设置禁止键盘输入
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		tv_repeat = (TextView) findViewById(R.id.tv_repeat);
		tv_bell = (TextView) findViewById(R.id.tv_bell);
		tv_label = (TextView) findViewById(R.id.tv_label);
		cb_vibration = (CheckBox) findViewById(R.id.cb_offon);
		bt_del = (Button) findViewById(R.id.bt_del);
		//判断是新建还是编辑
		alarm = (Alarm) getIntent().getSerializableExtra("alarm");
		if(alarm == null ){
			//新建
			isNew = true;
			((TextView)findViewById(R.id.tv_title)).setText("新建闹钟");
			Calendar calendar = Calendar.getInstance();
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
			tv_repeat.setText("只响一次");
			bellPath = getDefaultbell();
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			cb_vibration.setChecked(true);
			tv_label.setText("闹钟");
			//隐藏删除按钮
			bt_del.setVisibility(View.GONE);
		}else{
			//编辑
			isNew = false;
			((TextView)findViewById(R.id.tv_title)).setText("编辑闹钟");
			timePicker.setCurrentHour(alarm.hour);
			timePicker.setCurrentMinute(alarm.minutes);
			hour = alarm.hour;
			minute = alarm.minutes;
			tv_repeat.setText(alarm.repeat);
			repeatOld = alarm.repeat.equals("只响一次") ? 0 :alarm.repeat.equals("周一到周五") ? 1 :2;
			repeat = repeatOld;
			bellPath = alarm.bell;
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			cb_vibration.setChecked(alarm.vibrate == 1 ? true : false);
			tv_label.setText(alarm.label);
			//显示删除按钮
			bt_del.setVisibility(View.VISIBLE);
			bt_del.setOnClickListener(this);
		}
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		findViewById(R.id.tv_ok).setOnClickListener(this);
		findViewById(R.id.ll_repeat).setOnClickListener(this);
		findViewById(R.id.ll_bell).setOnClickListener(this);
		findViewById(R.id.ll_label).setOnClickListener(this);
		cb_vibration.setOnClickListener(this);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int mit) {
				// TODO Auto-generated method stub
				hour = hourOfDay;
				minute = mit;
			}
		});
		
	}

	private String getDefaultbell() {
		String ret = "";
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
				null, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				ret = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
			}
			cursor.close();
		}
		return ret;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_ok:
			Intent intent = new Intent();
			if(isNew){
				alarm = new Alarm();
				alarm.hour = hour;
				alarm.minutes = minute;
				alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天";
				alarm.bell = bellPath;
				alarm.vibrate = vibration;
				alarm.label = TextUtils.isEmpty(tv_label.getText()) ? "" : tv_label.getText().toString();
				alarm.enabled = 1;	
				alarm.nextMillis = 0;	
				//插入
				AlarmHandle.addAlarm(context, alarm);
				intent.putExtra("alarm", alarm);
			}else{
				ContentValues values = new ContentValues(); 
				if(alarm.hour != hour){
					values.put(Alarm.Columns.HOUR, hour);
					alarm.hour = hour;
				}
				if(alarm.minutes != minute){
					values.put(Alarm.Columns.MINUTES, minute);
					alarm.minutes = minute;
				}
				if(repeatOld != repeat){
					values.put(Alarm.Columns.REPEAT, repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天");
					alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天";
				}
				if(!TextUtils.isEmpty(bellPath) && !alarm.bell.equals(bellPath)){
					values.put(Alarm.Columns.BELL, bellPath);
				}
				if(vibration != alarm.vibrate){
					values.put(Alarm.Columns.VIBRATE, vibration);
				}
				if(!TextUtils.isEmpty(tv_label.getText()) && !alarm.label.equals(tv_label.getText())){
					values.put(Alarm.Columns.LABEL, tv_label.getText().toString());
				}
				if(alarm.enabled != 1){
					values.put(Alarm.Columns.ENABLED,1);
					alarm.enabled = 1;
				}
				if(values.size() > 0){
					AlarmHandle.updateAlarm(context, values, alarm.id);
					intent.putExtra("alarm", alarm);
				}
			}
			//返回更新
			setResult(Alarm.UPDATE_ALARM,intent);
			finish();
			break;
		case R.id.tv_cancel:
			finish();
			break;
		case R.id.ll_repeat:
			showDialog(SHOW_REPEAT);
			break;
		case R.id.ll_bell:
			// TODO Auto-generated method stub  
            Intent selectBell = new Intent(NewClockActivity.this,SelectBellActivity.class);
            selectBell.putExtra("bellPath", bellPath);
            selectBell.putExtra("bellName", tv_bell.getText());
            startActivityForResult(selectBell, 1);  
			break;
		case R.id.cb_offon:
			if(cb_vibration.isChecked()){
				vibration = 1;
			}else{
				vibration = 0;
			}
			break;
		case R.id.ll_label:
			showDialog(SHOW_LABEL);
			break;
		case R.id.bt_del:
			//弹出删除闹钟对话框
			showDialog(DEL_ALARM);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		switch(id){
		case SHOW_REPEAT:
			dialog = new AlertDialog.Builder(context)
				.setTitle(getResources().getText(R.string.repeat_text))
				.setSingleChoiceItems(R.array.repeat_item, repeatOld, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv_repeat.setText(getResources().getStringArray(R.array.repeat_item)[which]);
						repeat = which;
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", null).create();
			break;
		case SHOW_LABEL:
			final View view = inflater.inflate(R.layout.label_dialog, null);
			final EditText et = (EditText) view.findViewById(R.id.et_label);
			et.setText(tv_label.getText());
			dialog = new AlertDialog.Builder(context)
				.setTitle(getResources().getText(R.string.label_text))
				.setView(view)
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv_label.setText(et.getText().toString());						
					}
				}).create();			
			break;
		case DEL_ALARM:
			dialog = new AlertDialog.Builder(context)
			.setTitle(getResources().getText(R.string.del_clock))
			.setMessage("是否删除此闹钟？")
			.setNegativeButton("取消", null)
			.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(alarm.enabled == 1){
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}
					AlarmHandle.deleteAlarm(context, alarm.id);
					setResult(Alarm.DELETE_ALARM);
					finish();
				}
			}).create();	
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	//获得选择铃声的名称和路径
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			tv_bell.setText(data.getStringExtra("name"));
			bellPath = data.getStringExtra("path");
		}
	}

}
