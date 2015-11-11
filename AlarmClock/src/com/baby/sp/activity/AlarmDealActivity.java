package com.baby.sp.activity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baby.sp.R;
import com.baby.sp.common.Alarm;
import com.baby.sp.common.AlarmClockManager;
import com.baby.sp.common.AlarmHandle;
import com.baby.sp.common.AlarmPreference;
import com.baby.sp.common.AlarmService;
import com.baby.sp.common.ShakeDetector;
import com.baby.sp.common.ShakeDetector.OnShakeListener;

public class AlarmDealActivity extends Activity {

	private Context context;
	private LinearLayout ll_num;
	private LinearLayout ll_shake;
	private TextView tv_info;
	private TextView tv_tip;
	private TextView tv_num;
	private EditText et_result;
	
	private TextView tv_test;
	
	//数字键盘布局
	private GridView gv_nums;
	//数字键盘数据存放
	private List<Map<String, Integer>> lists;
	//键盘对应的图片
	private int nums[] = {R.drawable.num_1,R.drawable.num_2,R.drawable.num_3,
			R.drawable.num_4,R.drawable.num_5,R.drawable.num_6,R.drawable.num_7,
			R.drawable.num_8,R.drawable.num_9,R.drawable.num_del,R.drawable.num_0,R.drawable.num_ok};
	//图片对应的值
	private String [] tags = {"1","2","3","4","5","6","7","8","9","del","0","ok"};
	//闹钟内容
	private Alarm alarm;
	//随机数
	private Random random;
	//结果
	private int result;
	//做题总数
	private int times;
	private String numberStr = "";
	
	//摇晃值
	private int shakeValue = 0 ;
	
	//取消闹钟模式
	private int cancelAlaemMode;
	
	private float y1 = 0;
	private float y2 = 0;
	private float y3 = 0;
	
	//服务
	private AlarmService alarmService;
	//服务连接
	private ServiceConnection SConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			alarmService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 链接服务成功得到服务
			alarmService = ((AlarmService.MyBinder)service).getService();
			//得到服务中的震动器
//			vibrator = alarmService.mVibrator;
//			if(vibrator != null && alarm != null && alarm.vibrate == 1){
//				vibrator.vibrate(new long[]{500,500}, 0);
//			}
		}
	}; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final Window win = getWindow();
		//四个参数，锁屏显示，解锁，保持屏幕常亮，打开屏幕
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.alarm_deal);
		init();
	}

	/*
	 * 初始化
	 */
	private void init() {
		// TODO Auto-generated method stub
		context = this;
		ll_num = (LinearLayout) findViewById(R.id.ll_num);
		ll_shake = (LinearLayout) findViewById(R.id.ll_shake);
		int id = getIntent().getIntExtra(Alarm.Columns._ID, 0);
		if(id != 0){
			//根据ID获得闹钟的详细信息
			alarm = AlarmHandle.getAlarm(context, id);
			//开启服务，监听电话状态和音乐播放
			Intent intent = new Intent(this, AlarmService.class);
			intent.putExtra("alarm", alarm);
			bindService(intent, SConn, Context.BIND_AUTO_CREATE);
			//获得公共设置
			cancelAlaemMode = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.CANCEL_MODE_KEY);
			times = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.NUM_TIMES_KEY);
			//判断取消闹钟的模式
			switch(cancelAlaemMode){
			//做题模式
			case Alarm.CANCEL_NUM_MODE:
				ll_num.setVisibility(View.VISIBLE);
				ll_shake.setVisibility(View.GONE);
				break;
			//摇晃模式
			case Alarm.CANCEL_SHAKE_MODE:
				ll_num.setVisibility(View.GONE);
				ll_shake.setVisibility(View.VISIBLE);
				//获得设置的摇晃阀值
				final int shakeThreshold = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.SHAKE_ITEM_KEY);
				System.out.println("shakeThreshold:"+shakeThreshold);
				final ShakeDetector shakeDetector = new ShakeDetector(context);
				shakeDetector.registerOnShakeListener(new OnShakeListener() {
					@Override
					public void onShake() {
						shakeValue = shakeDetector.shakeValue;
						if(shakeValue - shakeThreshold >= 0){
							release();
							tv_test.setText("解除闹铃，清醒值："+shakeValue);
							shakeDetector.stop();
							alarmFinish(alarm);
							finish();
						}else{
							NumberFormat nt = NumberFormat.getPercentInstance();
							nt.setMinimumFractionDigits(2);
							tv_test.setText("摇晃手机取消闹铃\n\n当前清醒值："+ nt.format((double)shakeValue / shakeThreshold));
						}
						
					}
				});
				shakeDetector.start();
				break;
			}
		}else{
			finish();
		}
		tv_test = (TextView) findViewById(R.id.tv_test);
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_num = (TextView) findViewById(R.id.tv_num);
		et_result = (EditText) findViewById(R.id.et_result);
		tv_info.setText("完成下面的数学题取消闹钟，还剩" + times + "道题！");
		random = new Random();
		showNextNumber();

		//初始化adapter的数据list
		lists = new ArrayList<Map<String,Integer>>();
		for(int i=0;i<nums.length; i++){
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("num", nums[i]);
			lists.add(map);
		}
		gv_nums = (GridView) findViewById(R.id.gv_nums);
		//设置adapter
		gv_nums.setAdapter(new SimpleAdapter(context, lists,
				R.layout.grid_item, new String[]{"num"}, new int[]{R.id.iv_item}));
		//设置按键点击事件
		gv_nums.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(tags[position]){
				case "0":
					numberStr += tags[position];
					break;
				case "1":
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
				case "7":
				case "8":
				case "9":
					if("0".equals(numberStr)){
						numberStr = "";
					}
					numberStr += tags[position];
					break;
				case "del":
					if(!TextUtils.isEmpty(numberStr) && numberStr.length() > 0){
						numberStr = numberStr.substring(0, numberStr.length() - 1);
					}
					break;
				case "ok":
					if(!TextUtils.isEmpty(et_result.getText()) 
							&& Integer.parseInt(et_result.getText().toString()) == result){
						numberStr = "";
						tv_tip.setText("恭喜做对了，加油！");
						times--;
					}else{
						numberStr = "";
						tv_tip.setText("很抱歉做错了，别灰心，GO ！");
					}
					if(times == 0){
						release();
						alarmFinish(alarm);
						finish();
					}
					showNextNumber();
					tv_info.setText("完成下面的数学题取消闹钟，还剩" + times + "道题！");
					break;
				}
				et_result.setText(numberStr);
				et_result.setSelection(numberStr.length());
			}
		});
	}

	/*
	 * 随机产生两个数，做加、减、乘运算 
	 */
	private void showNextNumber() {
		int a = random.nextInt(100);
		int b = random.nextInt(100);
		int c = random.nextInt(3);
		switch(c){
		case 0:
			//加法
			tv_num.setText(a + " + " + b + "=");
			result = a + b;
			break;
		case 1:
			//减法
			if(a > b){
				tv_num.setText(a + " - " + b + "=");
				result = a - b;
			}else{
				tv_num.setText(b + " - " + a + "=");
				result = b - a;
			}
			break;
		case 2:
			//乘法
			b = random.nextInt(11);
			tv_num.setText(a + " * " + b + "=");
			result = a * b;
			break;
		}
		
	}

	//释放铃声和震动资源
	private void release(){
		//关闭铃声和震动
		if(alarmService != null){
			alarmService.stop();
		}
	}
	
	private void alarmFinish(Alarm alarm){
		//若只响一次则设置enabled为0
		String[] repeats = context.getResources().getStringArray(R.array.repeat_item);
		if(alarm.repeat.equals(repeats[Alarm.ALARM_ONCE])){
			ContentValues values = new ContentValues();
			values.put(Alarm.Columns.ENABLED, 0);
			//更新数据库
			AlarmHandle.updateAlarm(context, values, alarm.id);
		}else{
			long timeMillis = AlarmClockManager.time2Millis(alarm.hour , alarm.minutes , alarm.repeat , repeats);
			//将下次响铃时间的毫秒数存到数据库
			ContentValues values = new ContentValues();
			values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
			AlarmHandle.updateAlarm(context, values, alarm.id);
		}
		AlarmClockManager.setNextAlarm(context);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		release();
		//销毁指定服务
		if(alarmService != null){
			unbindService(SConn);
		}
	}
	
	/*
	 * 屏蔽返回键、菜单键、home键、音量键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_HOME:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			return true;
		default:
			return false;
		}
	}

	/*
	 * 触摸事件,若三个手指同时下滑距离大于200则取消闹钟
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			System.out.println(event.getPointerCount());
			if(event.getPointerCount() == 3){
				System.out.println("下滑取消闹钟");
				y1 = event.getY(0);
				y2 = event.getY(1);
				y3 = event.getY(2);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if(event.getPointerCount() == 3){
				System.out.println("释放");
				if(event.getY(0) - y1 > 200 && event.getY(1) - y2 > 200 
						&& event.getY(2) - y3 > 200){
					//取消闹钟
					alarmFinish(alarm);
					System.out.println("取消闹钟");
					finish();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
}
