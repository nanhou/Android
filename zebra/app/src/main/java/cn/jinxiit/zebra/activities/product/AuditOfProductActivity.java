package cn.jinxiit.zebra.activities.product;

import android.content.Intent;
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
import cn.jinxiit.zebra.fragments.products.AuditOfProductFragment;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class AuditOfProductActivity extends BaseActivity
{
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_of_product);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1 && resultCode == 1)
        {
            for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++)
            {
                AuditOfProductFragment auditOfProductFragment = (AuditOfProductFragment) mFragmentPagerItemAdapter.getPage(i);
                if (auditOfProductFragment != null)
                {
                    auditOfProductFragment.refreshData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView()
    {
        initData();

        httpGetCountTip();
        //        initSmartTabLayout();
    }

    private void httpGetCountTip()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoGetCountTip(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), MyConstant.STR_EXAMIN, true, new ApiResultListener()
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
            mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("全部(" + countBean.getAll() + ")", AuditOfProductFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_All)
                            .get())
                    .add("审核中(" + countBean.getApplying() + ")", AuditOfProductFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_APPLYING)
                            .get())
                    .add("已通过(" + countBean.getSuccess() + ")", AuditOfProductFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_SUCCESS)
                            .get())
                    .add("已驳回(" + countBean.getFailed() + ")", AuditOfProductFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_FAILED)
                            .get())
                    .create());
            mViewPager.setAdapter(mFragmentPagerItemAdapter);
            mViewPagerTab.setViewPager(mViewPager);
        }
    }

    private void initData()
    {
        mViewPager.setOffscreenPageLimit(4);
        new MyToolBar(this, "商品审核", null);
        myApp = (MyApp) getApplication();
    }

}
