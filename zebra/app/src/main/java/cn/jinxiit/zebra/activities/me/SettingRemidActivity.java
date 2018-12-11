package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class SettingRemidActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_remid);
        new MyToolBar(mContext, "订单提醒", null);
    }
}
