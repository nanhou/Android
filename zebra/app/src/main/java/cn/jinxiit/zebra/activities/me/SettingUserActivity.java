package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.me.accounts.BindPhoneActivity;
import cn.jinxiit.zebra.activities.me.accounts.UpdatePasswordActivity;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.MyActivityUtils;

public class SettingUserActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "当前账号", null);
    }

    @OnClick({R.id.tv_passwd, R.id.tv_phone})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_passwd:
                MyActivityUtils.skipActivity(mContext, UpdatePasswordActivity.class);
                break;
            case R.id.tv_phone:
                MyActivityUtils.skipActivity(mContext, BindPhoneActivity.class);
                break;
        }
    }
}
