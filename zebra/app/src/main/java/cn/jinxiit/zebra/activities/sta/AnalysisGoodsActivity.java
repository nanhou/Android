package cn.jinxiit.zebra.activities.sta;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.AnalysisiGoodsAdapter;
import cn.jinxiit.zebra.adapters.TextCenterAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.fragments.stas.GoodsListFragment;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class AnalysisGoodsActivity extends BaseActivity
{
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mVp_Traffic;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_classification)
    ConstraintLayout mLayoutClassification;
    @BindView(R.id.recyclerview0)
    RecyclerView mRecyclerview0;
    @BindView(R.id.recyclerview1)
    RecyclerView mRecyclerview1;
    @BindView(R.id.cb_goods_all)
    CheckBox mCbGoodsAll;
    @BindView(R.id.ll_platforms)
    View mViewPlatforms;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;

    private AnalysisiGoodsAdapter mAnalysisiGoodsAdapter;
    private TextCenterAdapter mTextCenterAdapter;

    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_goods);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return !disClassification(ev) && !disPlatform(ev) && super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.tv_menu, R.id.tv_platform0, R.id.tv_platform1, R.id.tv_platform2, R.id.tv_platform3, R.id.iv_back, R.id.tv_title})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_title:
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                break;
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
            case R.id.tv_menu:
                if (mViewPlatforms.getVisibility() == View.GONE)
                {
                    platformLayoutVisibleOrGone(mViewPlatforms.getVisibility());
                }
                break;
        }
    }

    private void choosePlatform(String menu, String platform)
    {
        mTvMenu.setText(menu);
        platformLayoutVisibleOrGone(mViewPlatforms.getVisibility());

        if (mFragmentPagerItemAdapter != null)
        {
            for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++)
            {
                GoodsListFragment goodsListFragment = (GoodsListFragment) mFragmentPagerItemAdapter.getPage(i);
                if (goodsListFragment != null)
                {
                    goodsListFragment.refreshData(platform);
                }
            }
        }
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

    private boolean disClassification(MotionEvent ev)
    {
        if (mLayoutClassification.getVisibility() == View.VISIBLE)
        {
            Rect viewRect = new Rect();
            mLayoutClassification.getGlobalVisibleRect(viewRect);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
            {
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                return true;
            }
        }
        return false;
    }

    private void classificationLayoutVisibleOrGone(int visible)
    {
        if (visible == View.VISIBLE)
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
            translateAnimation.setDuration(200);
            mLayoutClassification.setAnimation(translateAnimation);
            mLayoutClassification.setVisibility(View.GONE);
        } else
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
            translateAnimation.setDuration(200);
            mLayoutClassification.setAnimation(translateAnimation);
            mLayoutClassification.setVisibility(View.VISIBLE);
        }
    }

    private void initView()
    {
        initData();
        initViewPager();
        initRecyclerView();
        initSmartTabLayout();
        initListener();
    }

    private void initData()
    {
        mViewPlatforms.setVisibility(View.GONE);
    }

    private void initViewPager()
    {
        mVp_Traffic.setOffscreenPageLimit(3);
    }

    private void initListener()
    {
        mCbGoodsAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                mAnalysisiGoodsAdapter.clearSelected();
            }
        });

        mCbGoodsAll.setOnClickListener(v -> {
            mCbGoodsAll.setChecked(true);
            mTextCenterAdapter.clearDataList();
            mTvTitle.setText("全部分类");
            classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
        });

        mAnalysisiGoodsAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            mCbGoodsAll.setChecked(false);
            CategoryBean data = mAnalysisiGoodsAdapter.getItemData(position);
            if (data != null)
            {
                List<CategoryBean> sub_types = data.getChildren();
                if (sub_types != null && sub_types.size() > 0)
                {
                    List<String> datalist = new ArrayList<>();
                    datalist.add(data.getName());
                    for (CategoryBean subTypesBean : sub_types)
                    {
                        datalist.add(subTypesBean.getName());
                    }
                    mTextCenterAdapter.setDataList(datalist);
                }
            }
        });

        mTextCenterAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            CategoryBean currentData = mAnalysisiGoodsAdapter.getItemData(mAnalysisiGoodsAdapter.getmCurrentPosition());
            if (currentData != null)
            {
                String fatherType = currentData.getName();
                StringBuilder buffer = new StringBuilder(1024);
                buffer.append(fatherType);
                if (position != 0)
                {
                    buffer.append(" l ");
                    buffer.append(currentData.getChildren()
                            .get(position - 1)
                            .getName());
                }
                ViewSetDataUtils.textViewSetText(mTvTitle, buffer.toString());
                buffer.reverse();
            }
            classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
        });
    }

    private void initRecyclerView()
    {
        mRecyclerview0.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));
        mRecyclerview0.addItemDecoration(divider);

        //        mRecyclerview0.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAnalysisiGoodsAdapter = new AnalysisiGoodsAdapter();
        mRecyclerview0.setAdapter(mAnalysisiGoodsAdapter);
        List<CategoryBean> dataList = new ArrayList<>();
        for (int i = 0; i <= 10; i++)
        {
            CategoryBean subTypesBean = new CategoryBean("淡水鱼类" + i, null);
            CategoryBean subTypesBean1 = new CategoryBean("冰鲜类" + i, null);
            List<CategoryBean> subTypesBeanList = new ArrayList<>();
            subTypesBeanList.add(subTypesBean);
            subTypesBeanList.add(subTypesBean1);
            CategoryBean goodsTypeBean = new CategoryBean("水产鲜活" + i, subTypesBeanList);
            dataList.add(goodsTypeBean);
        }
        mAnalysisiGoodsAdapter.addDataList(dataList);

        mRecyclerview1.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview1.addItemDecoration(divider);
        //        mRecyclerview1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mTextCenterAdapter = new TextCenterAdapter();
        mRecyclerview1.setAdapter(mTextCenterAdapter);
    }

    private void initSmartTabLayout()
    {
        mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.toptitle_yesterday, GoodsListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_YESTERDAY)
                        .get())
                .add(R.string.toptitle_hebdomad, GoodsListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_SEVENDAY)
                        .get())
                .add(R.string.toptitle_month, GoodsListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_MONTH)
                        .get())
                .create());
        mVp_Traffic.setAdapter(mFragmentPagerItemAdapter);
        mViewPagerTab.setViewPager(mVp_Traffic);
    }
}
