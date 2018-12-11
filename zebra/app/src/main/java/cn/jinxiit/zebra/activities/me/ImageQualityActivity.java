package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class ImageQualityActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_quality);
        new MyToolBar(this, "图片质量", null);
    }
}
