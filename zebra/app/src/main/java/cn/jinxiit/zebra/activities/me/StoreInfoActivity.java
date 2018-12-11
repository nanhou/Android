package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class StoreInfoActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);
        new MyToolBar(this, "门店信息", null);
    }
}
