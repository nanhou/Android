package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class HelpAndFeedbackActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feedback);
        new MyToolBar(this, "帮助与反馈", null);
    }
}
