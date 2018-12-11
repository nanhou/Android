package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;

import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class SettingOrderActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_order);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "订单设置", null);
    }
}
