package com.baby.sp.activity;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	protected UMSocialService mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	}
}
