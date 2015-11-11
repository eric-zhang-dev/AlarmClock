package com.baby.sp.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baby.sp.R;
import com.baby.sp.common.AlarmPreference;
import com.tencent.tauth.Tencent;

public class SettingActivity extends Activity implements View.OnClickListener{

	private final static int SHOW_CANCEL_MODE = 1;
	private final static int SHOW_NUM_TIMES = 2;
	private final static int SHOW_SHAKE_ITEM = 3;
	private final static int CANCEL = 4;
	
	private Context context;
	
	private LinearLayout ll_num_times;
	private LinearLayout ll_shake;
	private CheckBox cb_check;
	private TextView tv_cancel_mode;
	private TextView tv_num_time;
	private TextView tv_shake;
	
	private boolean isBell = true;
	private int cancelAlaemMode = 0;
	private String times = "3";
	
	private int[] shakeValues = {4000,5000,6000};
	private int shakeValue = 0;
	private int shakeItemIndex = 0;
	private Map<String, Object> settings;
	private Tencent mTencent; 
	private TextView mTextView;
	private LinearLayout mLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();
	}

	/*
	 * 初始化组件
	 */
	private void init() {
		context = this;
		mTencent = Tencent.createInstance("1104912430", context);
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		findViewById(R.id.tv_ok).setOnClickListener(this);
		findViewById(R.id.ll_bell_mode).setOnClickListener(this);
		findViewById(R.id.ll_cancel_mode).setOnClickListener(this);
		ll_num_times = (LinearLayout) findViewById(R.id.ll_num_times);
		mLayout = (LinearLayout) findViewById(R.id.feed_back);
		mLayout.setOnClickListener(this);
		ll_num_times.setOnClickListener(this);
		ll_shake = (LinearLayout) findViewById(R.id.ll_shake);
		ll_shake.setOnClickListener(this);
		cb_check = (CheckBox) findViewById(R.id.cb_check);
		tv_cancel_mode = (TextView) findViewById(R.id.tv_cancel_mode);
		tv_num_time = (TextView) findViewById(R.id.tv_num_time);
		tv_shake = (TextView) findViewById(R.id.tv_shake);
		settings = new HashMap<String, Object>();
		//获得原设置的值
		isBell = (boolean) AlarmPreference.getSettingValue(context, AlarmPreference.BELL_MODE_KEY);
		cancelAlaemMode = (int) AlarmPreference.getSettingValue(context, AlarmPreference.CANCEL_MODE_KEY);
		times = ""+(int) AlarmPreference.getSettingValue(context, AlarmPreference.NUM_TIMES_KEY);
		shakeValue = (int) AlarmPreference.getSettingValue(context, AlarmPreference.SHAKE_ITEM_KEY);
		cb_check.setChecked(isBell);
		tv_cancel_mode.setText(getResources().getStringArray(R.array.cancel_bell_mode)[cancelAlaemMode]);
		if(cancelAlaemMode == 0){
			ll_num_times.setVisibility(View.VISIBLE);
			ll_shake.setVisibility(View.GONE);
		}else{
			ll_num_times.setVisibility(View.GONE);
			ll_shake.setVisibility(View.VISIBLE);
		}
		if("3".equals(times)){
		}else{
			tv_num_time.setText(times);			
		}
		System.out.println("init"+shakeValue);
		for (int i = 0; i < shakeValues.length; i++) {
			if(shakeValues[i] == shakeValue){
				tv_shake.setText(""+getResources().getStringArray(R.array.shake_item)[i]);
				shakeItemIndex = i;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_cancel:
			showDialog(CANCEL);
			break;
		case R.id.tv_ok:
			settings.put(AlarmPreference.BELL_MODE_KEY, isBell);
			settings.put(AlarmPreference.CANCEL_MODE_KEY, cancelAlaemMode);
			settings.put(AlarmPreference.NUM_TIMES_KEY, Integer.parseInt(times));
			settings.put(AlarmPreference.SHAKE_ITEM_KEY, shakeValue);
			System.out.println("ok"+shakeValue);
			AlarmPreference.saveSetting(context, settings);
			finish();
			break;
		case R.id.ll_bell_mode:
			if(isBell){
				isBell = false;
			}else{
				isBell = true;
			}
			cb_check.setChecked(isBell);
			break;
		case R.id.ll_cancel_mode:
			showDialog(SHOW_CANCEL_MODE);
			break;
		case R.id.ll_num_times:
			showDialog(SHOW_NUM_TIMES);
			break;
		case R.id.ll_shake:
			showDialog(SHOW_SHAKE_ITEM);
			break;
			case R.id.feed_back:
				mTencent.startWPAConversation(SettingActivity.this,"1585210845", "美女，你好！");
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		Dialog dialog = null;
		switch(id){
		case CANCEL:
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.action_settings)
			.setMessage("是否放弃本次设置？")
			.setNegativeButton("否", null)
			.setPositiveButton("是", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).create();
			break;
		case SHOW_CANCEL_MODE:
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.cancel_alarm_mode)
			.setSingleChoiceItems(R.array.cancel_bell_mode, cancelAlaemMode, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					tv_cancel_mode.setText(getResources().getStringArray(R.array.cancel_bell_mode)[which]);
					cancelAlaemMode = which;
					if(which == 0){
						ll_num_times.setVisibility(View.VISIBLE);
						ll_shake.setVisibility(View.GONE);
					}else{
						ll_num_times.setVisibility(View.GONE);
						ll_shake.setVisibility(View.VISIBLE);
					}
					dialog.dismiss();
				}
			})
			.setNegativeButton("取消", null).create();
			break;
		case SHOW_NUM_TIMES:
			LayoutInflater inflater = LayoutInflater.from(context);
			final View view = inflater.inflate(R.layout.num_label, null);
			final EditText et_times = (EditText) view.findViewById(R.id.et_times);
			et_times.setText(times);
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.num_time)
			.setView(view)
			.setNegativeButton("取消", null)
			.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!TextUtils.isEmpty(et_times.getText()) && !"0".equals(et_times.getText().toString().trim())){
						times = et_times.getText().toString().trim();
					}else{
						times = "3";
					}
					if("3".equals(times)){
						tv_num_time.setText(times + "(默认)");
					}else{
						tv_num_time.setText(times);
					}
				}
			}).create();
			break;
		case SHOW_SHAKE_ITEM:
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.shake_title)
			.setSingleChoiceItems(R.array.shake_item, shakeItemIndex, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					tv_shake.setText(getResources().getStringArray(R.array.shake_item)[which]);
					shakeValue = shakeValues[which];
					System.out.println(shakeValue);
					dialog.dismiss();
				}
			})
			.setNegativeButton("取消", null).create();
			break;
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
}
