package cn.jinxiit.zebra.activities.sta;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.fragments.stas.CustomerListFragment;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class AnalysisCustomerActivity extends BaseActivity
{
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mVp_Traffic;
    @BindView(R.id.ll_platforms)
    View mViewPlatforms;

    private MyToolBar myToolBar;
    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_customer);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        myToolBar = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return !disPlatform(ev) && super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.tv_platform0, R.id.tv_platform1, R.id.tv_platform2, R.id.tv_platform3})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_platform0:
                choosePlatform("全部平台", MyConstant.STR_ALLSTATIS);
                break;
            case R.id.tv_platform1:
                choosePlatform("京东到家", MyConstant.STR_JDDJ);
                break;
            case R.id.tv_platform2:
                choosePlatform("饿了么", MyConstant.STR_ELEME);
                break;
            case R.id.tv_platform3:
                choosePlatform("美团外卖", MyConstant.STR_MT);
                break;
        }
    }

    private void choosePlatform(String menu, String platform)
    {
        myToolBar.setTvMenu(menu);
        platformLayoutVisibleOrGone(mViewPlatforms.getVisibility());
        if (mFragmentPagerItemAdapter != null)
        {
            for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++)
            {
                CustomerListFragment customerListFragment = (CustomerListFragment) mFragmentPagerItemAdapter.getPage(i);
                if (customerListFragment != null)
                {
                    customerListFragment.refreshData(platform);
                }
            }
        }
    }

    private boolean disPlatform(MotionEvent ev)
    {
        if (mViewPlatforms.getVisibility() == View.VISIBLE)
        {
            Rect viewRect = new Rect();
            mViewPlatforms.getGlobalVisibleRect(viewRect);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
            {
                platformLayoutVisibleOrGone(mViewPlatforms.getVisibility());
                return true;
            }
        }
        return false;
    }

    private void platformLayoutVisibleOrGone(int visible)
    {
        if (visible == View.VISIBLE)
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
            translateAnimation.setDuration(200);
            mViewPlatforms.setAnimation(translateAnimation);
            mViewPlatforms.setVisibility(View.GONE);
        } else
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
            translateAnimation.setDuration(200);
            mViewPlatforms.setAnimation(translateAnimation);
            mViewPlatforms.setVisibility(View.VISIBLE);
        }
    }

    private void initView()
    {
        initData();
        initViewPager();
        initSmartTabLayout();
    }

    private void initData()
    {
        mViewPlatforms.setVisibility(View.GONE);
        myToolBar = new MyToolBar(this, "顾客分析", "全部平台");
        myToolBar.setOnTopMenuClickListener(v -> {
            if (mViewPlatforms.getVisibility() == View.GONE)
            {
                mViewPlatforms.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initViewPager()
    {
        mVp_Traffic.setOffscreenPageLimit(3);
    }

    private void initSmartTabLayout()
    {
        mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.toptitle_yesterday, CustomerListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_YESTERDAY)
                        .get())
                .add(R.string.toptitle_hebdomad, CustomerListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_SEVENDAY)
                        .get())
                .add(R.string.toptitle_month, CustomerListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_MONTH)
                        .get())
                .create());
        mVp_Traffic.setAdapter(mFragmentPagerItemAdapter);
        mViewPagerTab.setViewPager(mVp_Traffic);
    }
}
