package cn.jinxiit.zebra.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.fragments.products.ProductStatusFragment;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class ProductStatusActivity extends BaseActivity
{
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_status);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        initData();
        initSmartTabLayout();
    }

    private void initSmartTabLayout()
    {
        Intent intent = getIntent();
        CountBean countBean = intent.getParcelableExtra(MyConstant.STR_BEAN);
        if (countBean != null)
        {
            long total = countBean.getAll();
            long lowerFrame = countBean.getLowerFrame();
            long lessStock = countBean.getLessStock();
            long outStock = countBean.getOutStock();

            mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("全部(" + total + ")", ProductStatusFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_All)
                            .get())
                    .add("无货(" + outStock + ")", ProductStatusFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_OUT_STOCK)
                            .get())
                    .add("下架(" + lowerFrame + ")", ProductStatusFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_LOWER_FRAME)
                            .get())
                    .add("库存紧张(" + lessStock + ")", ProductStatusFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_LESS_STOCK)
                            .get())
                    .create());
            mViewPager.setAdapter(mFragmentPagerItemAdapter);
            mViewPagerTab.setViewPager(mViewPager);
        }
    }

    private void initData()
    {
        new MyToolBar(this, "商品状态", null);
        mViewPager.setOffscreenPageLimit(4);
    }
}