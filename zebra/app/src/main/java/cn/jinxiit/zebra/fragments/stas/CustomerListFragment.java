package cn.jinxiit.zebra.fragments.stas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.StatisticBean;
import cn.jinxiit.zebra.components.ChartComponent;
import cn.jinxiit.zebra.components.CirclePbComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class CustomerListFragment extends Fragment
{
    Unbinder unbinder;
    @BindView(R.id.view_chart)
    View mViewChart;
    @BindView(R.id.view_circlepb)
    View mViewCirclePb;
    @BindView(R.id.ll_analysis_customer)
    LinearLayout mllAnalysis_Customer;
    @BindView(R.id.tv_tip)
    TextView mTvTip;

    private String mType;
    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final String[] mLineCharTitles1 = {"全部顾客", "新顾客", "老顾客"};
    private static final String[] mLineCharTitlesUnit1 = {"人", "人", "人"};

    private ChartComponent mChartComponent1;
    private String mPlatform = MyConstant.STR_ALLSTATIS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        unbinder = ButterKnife.bind(this, view);
        myApp = (MyApp) Objects.requireNonNull(getActivity())
                .getApplication();
        assert getArguments() != null;
        mType = getArguments().getString(MyConstant.STR_TYPE);
        initView();
        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initView()
    {
        initData();
        initViewChart0();
    }

    @SuppressLint("SetTextI18n")
    private void initData()
    {
        initViewChart0();

        if (TextUtils.isEmpty(mType))
        {
            return;
        }

        httpGetData();
    }

    private void httpGetData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                switch (mType)
                {
                    case MyConstant.STR_YESTERDAY:
                        mllAnalysis_Customer.setVisibility(View.GONE);
                        break;
                    case MyConstant.STR_SEVENDAY:
                        break;
                    case MyConstant.STR_MONTH:
                        mTvTip.setText("近30日 顾客分析");
                        break;
                }
                httpGetStroeTotalData(store_id, mType);
            }
        }
    }

    private void httpGetStroeTotalData(String storeId, final String type)
    {
        ApiUtils.getInstance()
                .okgoGetStoreStatistic(getActivity(), myApp.mToken, mPlatform, storeId, type, true, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        StatisticBean statisticBean = mGson.fromJson(response.body(), StatisticBean.class);
                        if (statisticBean != null)
                        {
                            initCirclePb(statisticBean, type);
                            switch (type)
                            {
                                case MyConstant.STR_SEVENDAY:
                                    initLineChart(statisticBean);
                                    break;
                                case MyConstant.STR_MONTH:
                                    initLineChart(statisticBean);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                    }
                });
    }

    private void initCirclePb(StatisticBean statisticBean, String type)
    {
        int newUser = statisticBean.getNewUser();
        int oldUser = statisticBean.getOldUser();
        int preNewUser = statisticBean.getPreNewUser();
        int preOldUser = statisticBean.getPreOldUser();
        new CirclePbComponent(mViewCirclePb, MyConstant.STR_CUSTOMER, type, newUser + oldUser, preNewUser + preOldUser, newUser, preNewUser, oldUser, preOldUser);
    }

    private void initLineChart(StatisticBean statisticBean)
    {
        ZebraUtils.getInstance()
                .customerdataSetChart(mChartComponent1, mPlatform, mLineCharTitles1, mLineCharTitlesUnit1, statisticBean);
    }

    private void initViewChart0()
    {
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < mLineCharTitles1.length; i++)
        {
            titles.add(mLineCharTitles1[i]);
        }
        mChartComponent1 = new ChartComponent(mViewChart, titles);
    }

    public void refreshData(String platform)
    {
        if (mPlatform.equals(platform))
        {
            return;
        }
        this.mPlatform = platform;
        httpGetData();
    }

}
