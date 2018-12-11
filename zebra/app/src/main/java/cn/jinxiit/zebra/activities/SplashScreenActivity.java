package cn.jinxiit.zebra.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.logins.LoginActivity;
import cn.jinxiit.zebra.utils.MyActivityUtils;

public class SplashScreenActivity extends Activity
{
    private Handler mHandler = new Handler();
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        steepStatusBar();
        mContext = this;
//        MyApp.mFlag = 0;
        mHandler.postDelayed(() -> MyActivityUtils.skipActivityAndFinish(mContext, LoginActivity.class), 2000);
    }

    private void steepStatusBar()
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorMain2));
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
