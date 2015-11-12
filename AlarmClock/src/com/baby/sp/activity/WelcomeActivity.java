package com.baby.sp.activity;


import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baby.sp.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout launch;
    private Animation fadeIn;
    private Animation fadeInScale;
    private LinearLayout mLoginLayout;
    private LinearLayout mLoginForWechat;
    private LinearLayout mLoginForQQ;
    private LinearLayout mLoginForSina;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 设置无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
     
        setContentView(R.layout.welcome);
        init();
        setListener();
    }
    private void setListener() {
        // TODO Auto-generated method stub
        fadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                launch.setAnimation(fadeInScale);
            }
        });
        fadeInScale.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                //动画完成后跳转到主界面
                mLoginLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init() {
        // TODO Auto-generated method stub
    	addForQQ();
        launch = (RelativeLayout) findViewById(R.id.launch);
        mLoginLayout = (LinearLayout) findViewById(R.id.login_layout);
        mLoginForWechat = (LinearLayout) findViewById(R.id.login_for_wechat);
        mLoginForQQ = (LinearLayout) findViewById(R.id.login_for_qq);
        mLoginForSina = (LinearLayout) findViewById(R.id.login_for_sina);
        mLoginForQQ.setOnClickListener(this);
        mLoginForSina.setOnClickListener(this);
        mLoginForWechat.setOnClickListener(this);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        fadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
        fadeInScale.setDuration(3000);
        fadeInScale.setFillAfter(true);
        launch.startAnimation(fadeIn);
    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_for_wechat:
                Toast mToast;
                mToast = Toast.makeText(WelcomeActivity.this,"Yet opened!",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER,0,0);
                mToast.show();
                break;
            case R.id.login_for_qq:
                login(SHARE_MEDIA.QQ);
                break;
            case R.id.login_for_sina:
            	login(SHARE_MEDIA.SINA);
                break;
        }
    }
    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(WelcomeActivity.this, platform, new UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(WelcomeActivity.this, "start", 0).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(WelcomeActivity.this, "onComplete", 0).show();
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(WelcomeActivity.this, "授权失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(WelcomeActivity.this, platform, new UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if(status == 200 && info != null){
                    StringBuilder sb = new StringBuilder();
                    Set<String> keys = info.keySet();
                    for(String key : keys){
                       sb.append(key+"="+info.get(key).toString()+"\r\n");
                    }
                    Log.e("TestData",sb.toString());
                    new AlertDialog.Builder(WelcomeActivity.this)
                    .setTitle("TestData") 
                    .setMessage(sb.toString())
                     	.setPositiveButton("确定", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								 goMain();
							}
						})
                     	.show();
                }else{
                   Log.d("TestData","发生错误："+status);
               }
                if (info != null) {
                    Toast.makeText(WelcomeActivity.this, info.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */  
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
           ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
	private void addForQQ() {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104912430",
                "gjOmKSDDiIM2Ht3Z");
        qqSsoHandler.addToSocialSDK();
//        mController.doOauthVerify(WelcomeActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
//            @Override
//            public void onStart(SHARE_MEDIA platform) {
//                Toast.makeText(WelcomeActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onError(SocializeException e, SHARE_MEDIA platform) {
//                Toast.makeText(WelcomeActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                Toast.makeText(WelcomeActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
//                //获取相关授权信息
//                mController.getPlatformInfo(WelcomeActivity.this, SHARE_MEDIA.QQ, new UMDataListener() {
//            @Override
//            public void onStart() {
//                Toast.makeText(WelcomeActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
//            }                                              
//            @Override
//                public void onComplete(int status, Map<String, Object> info) {
//                    if(status == 200 && info != null){
//                        StringBuilder sb = new StringBuilder();
//                        Set<String> keys = info.keySet();
//                        for(String key : keys){
//                           sb.append(key+"="+info.get(key).toString()+"\r\n");
//                        }
//                        Log.e("TestData",sb.toString());
//                        goMain();
//                    }else{
//                       Log.d("TestData","发生错误："+status);
//                   }
//                }
//        });
//            }
//            @Override
//            public void onCancel(SHARE_MEDIA platform) {
//                Toast.makeText(WelcomeActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
//            }
//        } );
    }
    private void goMain() {
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    	
    }
}
