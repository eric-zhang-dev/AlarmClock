package com.baby.sp.common;

import android.app.Application;

public class MyApplication extends Application{

	@Override
    public void onCreate() {
        super.onCreate();
        AlarmExceptionHandler crashHandler = AlarmExceptionHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
