package cn.jinxiit.zebra.activities.me.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.lzy.okgo.model.Response;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.NotificationBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class NotificationInfoActivity extends BaseActivity
{
    @BindView(R.id.tv_notification_title)
    TextView mTvTitle;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    private NotificationBean mNotificationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        initData();
    }

    private void initData()
    {
        new MyToolBar(this, "消息详情", null);
        mNotificationBean = getIntent().getParcelableExtra(MyConstant.STR_BEAN);
        MyApp myApp = (MyApp) getApplication();
        if (mNotificationBean != null)
        {
            ViewSetDataUtils.textViewSetText(mTvTitle, mNotificationBean.getTitle());
            ViewSetDataUtils.textViewSetText(mTvContent, mNotificationBean.getContent());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mTvTime.setText(dateFormat.format(new Date(mNotificationBean.getCreated_at() * 1000)));

            if (myApp.mCurrentStoreOwnerBean != null)
            {
                ApiUtils.getInstance()
                        .okgoPostReadNotification(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), mNotificationBean.getId(), new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {

                            }

                            @Override
                            public void onError(Response<String> response)
                            {

                            }
                        });
            }
        }
    }
}
