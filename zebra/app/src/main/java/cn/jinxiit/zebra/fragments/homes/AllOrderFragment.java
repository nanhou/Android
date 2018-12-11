package cn.jinxiit.zebra.fragments.homes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.orders.OderOfDayActivity;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.fragments.orders.OrderListFragment;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class AllOrderFragment extends Fragment {
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    Unbinder unbinder;

    private Activity mActivity;
    private MyApp myApp;
    private Gson mGson = new Gson();

    private boolean mIsRun = true;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRun) {
                httpGetOrderNum();
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
    };
    private static Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mIsRun = false;
    }

    private void initView() {
        initData();
        initSmartTabLayout();
        mHandler.post(mRunnable);
    }

    private void initData() {
        mActivity = getActivity();
        if (mActivity != null) {
            myApp = (MyApp) mActivity.getApplication();
            mViewPagerTab.setDistributeEvenly(false);
        }
    }

    private void httpGetOrderNum() {
        if (myApp.mCurrentStoreOwnerBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id)) {
                ApiUtils.getInstance()
                        .okgoGetOrderCount(mActivity, myApp.mToken, store_id, null, null,
                                new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        CountBean countBean = mGson
                                                .fromJson(response.body(), CountBean.class);
                                        updateTab(countBean);
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        MyToastUtils.error("获取数据失败");
                                    }
                                });
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTab(CountBean countBean) {
        if (countBean != null) {
            mViewPagerTab.setCustomTabView((container, position, adapter) -> {
                LayoutInflater inflater = LayoutInflater.from(container.getContext());
                View tab = inflater.inflate(R.layout.custom_tab, container, false);
                TextView customText = tab.findViewById(R.id.custom_text);
                CharSequence title = adapter.getPageTitle(position);
                switch (position) {
                    case 0:
                        customText
                                .setText(String.format("%s(%d)", title, countBean.getNew_order()));
                        break;
                    case 1:
                        customText.setText(String.format("%s(%d)", title, countBean.getPicking()));
                        break;
                    case 2:
                        customText.setText(
                                String.format("%s(%d)", title, countBean.getDistribution()));
                        break;
                    case 3:
                        customText.setText(
                                String.format("%s(%d)", title, countBean.getDistributioning()));
                        break;
                    case 4:
                        customText.setText(String.format("%s(%d)", title, countBean.getAbnormal()));
                        break;
                    case 5:
                        customText
                                .setText(String.format("%s(%d)", title, countBean.getAfter_sale()));
                        break;
                    default:
                        break;
                }
                return tab;
            });
            mViewPagerTab.setViewPager(mViewPager);
        }
    }

    private void initSmartTabLayout() {
        @SuppressLint("DefaultLocale") FragmentPagerItemAdapter mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                FragmentPagerItems.with(getActivity()).add("新订单", OrderListFragment.class,
                        new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_NEW_ORDER)
                                .get()).add("拣货", OrderListFragment.class,
                        new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_PICKING).get())
                        .add("待配送", OrderListFragment.class, new Bundler()
                                .putString(MyConstant.STR_TYPE, MyConstant.STR_DISTRIBUTION).get())
                        .add("配送中", OrderListFragment.class, new Bundler()
                                .putString(MyConstant.STR_TYPE, MyConstant.STR_DISTRIBUTIONING)
                                .get()).add("异常单", OrderListFragment.class,
                        new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_ABNORMAL).get())
                        .add("售后单", OrderListFragment.class, new Bundler()
                                .putString(MyConstant.STR_TYPE, MyConstant.STR_AFTER_SALE).get())
                        .create());
        mViewPager.setAdapter(mFragmentPagerItemAdapter);
        mViewPagerTab.setViewPager(mViewPager);
    }

    @OnClick({R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                MyActivityUtils.skipActivity(Objects.requireNonNull(getActivity()),
                        OderOfDayActivity.class);
                break;
        }
    }
}
