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
import cn.jinxiit.zebra.components.TrafficComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;


public class TrafficListFragment extends Fragment
{
    Unbinder unbinder;
    @BindView(R.id.view_chart0)
    View mViewChart0;
    @BindView(R.id.ll_analysis_traffic)
    LinearLayout mllAnalysis_Traffic;
    @BindView(R.id.view_traffic)
    View mViewTraffic;
    @BindView(R.id.tv_traffic_tip)
    TextView mTvTrafficTip;

    private String mType;
    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final String[] mLineCharTitles0 = {"曝光人数", "访问人数", "下单人数", "访问转化", "下单转化"};
    private static final String[] mLineCharTitlesUnit0 = {"人", "人", "人", "%", "%"};
    private ChartComponent mChartComponent;
    private String mPlatform = MyConstant.STR_ALLSTATIS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_traffic, container, false);
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
    }

    @SuppressLint("SetTextI18n")
    private void initData()
    {
        if (TextUtils.isEmpty(mType))
        {
            return;
        }

        initViewChart0();

        httpGetStoreData();
    }

    private void httpGetStoreData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                switch (mType)
                {
                    case MyConstant.STR_YESTERDAY:
                        mllAnalysis_Traffic.setVisibility(View.GONE);
                        break;
                    case MyConstant.STR_SEVENDAY:
                        break;
                    case MyConstant.STR_MONTH:
                        mTvTrafficTip.setText("近30日 顾客分析");
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
                            switch (type)
                            {
                                case MyConstant.STR_YESTERDAY:
                                    initViewTraffic(statisticBean, MyConstant.STR_YESTERDAY);
                                    break;
                                case MyConstant.STR_SEVENDAY:
                                    initViewTraffic(statisticBean, MyConstant.STR_SEVENDAY);
                                    initLineChart(statisticBean);
                                    break;
                                case MyConstant.STR_MONTH:
                                    initViewTraffic(statisticBean, MyConstant.STR_MONTH);
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

    private void initLineChart(StatisticBean jdStatisticBean)
    {
        ZebraUtils.getInstance().trafficdataSetChart(mChartComponent, mPlatform, mLineCharTitles0, mLineCharTitlesUnit0, jdStatisticBean);
    }

    private void initViewTraffic(StatisticBean statisticBean, String type)
    {
        new TrafficComponent(mViewTraffic, type, statisticBean.getExposure(), statisticBean.getPreExposure(), statisticBean.getVisiter(), statisticBean.getPreVisiter(), statisticBean.getTotalOrder(), statisticBean.getPreTotalOrder());
    }

    private void initViewChart0()
    {
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < mLineCharTitles0.length; i++)
        {
            titles.add(mLineCharTitles0[i]);
        }
        mChartComponent = new ChartComponent(mViewChart0, titles);
    }

    public void refreData(String platform)
    {
        if (mPlatform.equals(platform)) return;
        this.mPlatform = platform;
        httpGetStoreData();
    }
}
