package com.baby.sp.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.baby.sp.R;


public class LightActivity extends Activity {
	private Button lightBtn = null;
	private Camera camera = null;
	private Parameters parameters = null;
	public static boolean kaiguan = true; // 定义开关状态，状态为false，打开状态，状态为true，关闭状态
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置，隐藏窗口所有装饰
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置屏幕显示无标题，必须启动就要设置好，否则不能再次被设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		lightBtn = (Button) findViewById(R.id.btn_light);
		lightBtn.setOnClickListener(new Mybutton());
	}

	class Mybutton implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (kaiguan) {

				lightBtn.setBackgroundResource(R.drawable.shou_on);
				camera = Camera.open();
				parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
				camera.setParameters(parameters);
				camera.startPreview();
				kaiguan = false;
			} else {
				// addContentView(adView, new ViewGroup.LayoutParams(-1, -2));
				lightBtn.setBackgroundResource(R.drawable.shou_off);
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
				camera.setParameters(parameters);
				camera.stopPreview();
				kaiguan = true;
				camera.release();
			}

			// AdView构造函数可以接收三个参数：context(上下文), AdSize类型(广告样式),
			// 广告位ID(非高级广告位填null即可)
			// AdView adView = new AdView(this, AdSize.Square, null);
			// AdView adView = new AdView(this, AdSize.Banner, null);

			// 设置adView为当前Activity的View

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void Myback() { // 关闭程序
		if (kaiguan) {// 开关关闭时
			LightActivity.this.finish();
			android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
		} else if (!kaiguan) {// 开关打开时
			camera.release();
			LightActivity.this.finish();
			android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
			kaiguan = true;// 避免，打开开关后退出程序，再次进入不打开开关直接退出时，程序错误
		}
	}
}