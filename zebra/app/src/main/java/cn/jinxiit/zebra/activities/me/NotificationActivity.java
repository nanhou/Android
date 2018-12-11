package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.fragments.mes.NotificationFragment;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class NotificationActivity extends BaseActivity
{
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mVp_Traffic;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        new MyToolBar(this, "消息通知", null);

        initView();
    }

    private void initView()
    {
        initData();
        httpCountTip();
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        mVp_Traffic.setOffscreenPageLimit(4);
    }

    private void httpCountTip()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoGetCountTip(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), MyConstant.STR_MESSAGE, true, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            CountBean countBean = new Gson().fromJson(response.body(), CountBean.class);
                            if (countBean != null)
                            {
                                initSmartTabLayout(countBean);
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("初始化失败");
                        }
                    });
        }
    }

    private void initSmartTabLayout(CountBean countBean)
    {
        if (countBean != null)
        {
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("全部(" + countBean.getAll() + ")", NotificationFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_All)
                            .get())
                    .add("商品(" + countBean.getProduct() + ")", NotificationFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_PRODUCT)
                            .get())
                    .add("公告(" + countBean.getNotice() + ")", NotificationFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_NOTICE)
                            .get())
                    .add("其他(" + countBean.getOther() + ")", NotificationFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_OTHER)
                            .get())
                    .create());
            mVp_Traffic.setAdapter(adapter);
            mViewPagerTab.setViewPager(mVp_Traffic);
        }
    }
}
