package cn.jinxiit.zebra.fragments.homes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.sta.AnalysisBusinessActivity;
import cn.jinxiit.zebra.activities.sta.AnalysisCustomerActivity;
import cn.jinxiit.zebra.activities.sta.AnalysisGoodsActivity;
import cn.jinxiit.zebra.activities.sta.AnalysisTrafficActivity;
import cn.jinxiit.zebra.beans.StatisticBean;
import cn.jinxiit.zebra.components.ChartComponent;
import cn.jinxiit.zebra.components.CirclePbComponent;
import cn.jinxiit.zebra.components.TrafficComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;
import cn.jinxiit.zebra.views.ObservableScrollView;

public class StaticFragment extends Fragment
{
    Unbinder unbinder;
    @BindView(R.id.radiogroup)
    RadioGroup mRadiogroup;
    @BindView(R.id.ll_data0)
    LinearLayout mllData0;
    @BindView(R.id.ll_data1)
    LinearLayout mllData1;
    @BindView(R.id.scrollview)
    ObservableScrollView mObservableScrollView;
    @BindView(R.id.view0)
    View mView0;
    @BindView(R.id.view1)
    View mView1;
    @BindView(R.id.tv_income_today)
    TextView mTvTodayIncome;
    @BindView(R.id.tv_order_count_today)
    TextView mTvTodayOrderCount;
    @BindView(R.id.tv_income_yu_today)
    TextView mTvTodayIncomeYu;
    @BindView(R.id.view_chart0)
    View mViewChart0;
    @BindView(R.id.view_chart1)
    View mViewChart1;
    @BindView(R.id.tv_income_yesterday)
    TextView mTvYesterdayIncome;
    @BindView(R.id.tv_income_yesterday_compared)
    TextView mTvYesterdayIncomeCompared;
    @BindView(R.id.tv_income_yu_yesterday)
    TextView mTvYesterdayIncomeYu;
    @BindView(R.id.tv_income_yu_yesterday_compared)
    TextView mTvYesterdayIncomeYuCompared;
    @BindView(R.id.tv_finish_order_count_yesterday)
    TextView mTvYesterdayFinishOrderCount;
    @BindView(R.id.tv_price_average_yesterday)
    TextView mTvYesterdayPriceAverage;
    @BindView(R.id.tv_fail_order_count_yesterday)
    TextView mTvYesterdayFailOrderCount;
    @BindView(R.id.tv_lose_incoming_yesterday)
    TextView mTvYesterdayLoseIncoming;
    @BindView(R.id.view_yesterday_traffic)
    View mViewYesterdayTraffic;
    @BindView(R.id.view_7day_traffic)
    View mView7dayTraffic;
    @BindView(R.id.view_yesteday_circlepb_data)
    View mViewYesterdayCirclepbData;

    private ChartComponent mChartComponent0;
    private ChartComponent mChartComponent1;

    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final String[] mLineCharTitles0 = {"营业额", "预计收入", "有效订单", "预计毛利", "客单价"};
    private static final String[] mLineCharTitlesUnit0 = {"元", "元", "单", "元", "元"};

    private static final String[] mLineCharTitles1 = {"全部顾客", "新顾客", "老顾客"};
    private static final String[] mLineCharTitlesUnit1 = {"人", "人", "人"};

    //避免重复请求
    private String mCurrentStoreId;
    private boolean mIsYesterdayData;
    private boolean mIs7Data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        unbinder = ButterKnife.bind(this, view);
        myApp = (MyApp) Objects.requireNonNull(getActivity())
                .getApplication();
        initView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initResumeData();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_business_tip0, R.id.ll_business_tip1, R.id.ll_traffic_tip0, R.id.ll_traffic_tip1, R.id.ll_customer_tip0, R.id.ll_customer_tip1, R.id.btn_analysis_business, R.id.btn_analysis_traffic, R.id.btn_analysis_goods, R.id.btn_analysis_customer})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_business_tip0:
            case R.id.ll_business_tip1:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisBusinessActivity.class);
                break;
            case R.id.ll_traffic_tip0:
            case R.id.ll_traffic_tip1:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisTrafficActivity.class);
                break;
            case R.id.ll_customer_tip0:
            case R.id.ll_customer_tip1:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisCustomerActivity.class);
                break;
            case R.id.btn_analysis_business:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisBusinessActivity.class);
                break;
            case R.id.btn_analysis_traffic:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisTrafficActivity.class);
                break;
            case R.id.btn_analysis_goods:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisGoodsActivity.class);
                break;
            case R.id.btn_analysis_customer:
                MyActivityUtils.skipActivity(getOwnerActivity(), AnalysisCustomerActivity.class);
                break;
        }
    }

    private FragmentActivity getOwnerActivity()
    {
        return Objects.requireNonNull(getActivity());
    }

    private void initResumeData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                httpGetStroeJDTotalData(store_id, MyConstant.STR_TODAY);

                if (store_id.equals(mCurrentStoreId))
                {
                    if (!mIsYesterdayData)
                    {
                        httpGetStroeJDTotalData(store_id, MyConstant.STR_YESTERDAY);
                    }

                    if (!mIs7Data)
                    {
                        httpGetStroeJDTotalData(store_id, MyConstant.STR_SEVENDAY);
                    }
                } else
                {
                    mCurrentStoreId = store_id;
                    httpGetStroeJDTotalData(store_id, MyConstant.STR_YESTERDAY);
                    httpGetStroeJDTotalData(store_id, MyConstant.STR_SEVENDAY);
                }
            }
        }
    }

    private void initView()
    {
        initViewData();
        initViewChart0();
        initViewChart1();
        initListener();
    }

    private void initViewData()
    {
        mllData0.setVisibility(View.VISIBLE);
        mllData1.setVisibility(View.GONE);
    }

    private void initViewChart1()
    {
        List<String> titles = new ArrayList<>(Arrays.asList(mLineCharTitles1));
        mChartComponent1 = new ChartComponent(mViewChart1, titles);
    }

    private void initViewChart0()
    {
        List<String> titles = new ArrayList<>(Arrays.asList(mLineCharTitles0));
        mChartComponent0 = new ChartComponent(mViewChart0, titles);
    }

    private void httpGetStroeJDTotalData(String storeId, final String type)
    {
        ApiUtils.getInstance()
                .okgoGetStoreStatistic(getActivity(), myApp.mToken, MyConstant.STR_ALLSTATIS, storeId, type, false, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        StatisticBean statisticBean = mGson.fromJson(response.body(), StatisticBean.class);
                        if (statisticBean != null)
                        {
                            switch (type)
                            {
                                case MyConstant.STR_TODAY:
                                    initTodayData(statisticBean);
                                    break;
                                case MyConstant.STR_YESTERDAY:
                                    mIsYesterdayData = true;
                                    initYesterdayData(statisticBean);
                                    break;
                                case MyConstant.STR_SEVENDAY:
                                    mIs7Data = true;
                                    init7Data(statisticBean);
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

    private void init7Data(StatisticBean statisticBean)
    {
        ZebraUtils zebraUtils = ZebraUtils.getInstance();

        //营业额之类的
        zebraUtils.statisticdataSetChart(mChartComponent0, MyConstant.STR_ALLSTATIS, mLineCharTitles0, mLineCharTitlesUnit0, statisticBean);

        //用户数据表
        zebraUtils.customerdataSetChart(mChartComponent1, MyConstant.STR_ALLSTATIS, mLineCharTitles1, mLineCharTitlesUnit1, statisticBean);

        new TrafficComponent(mView7dayTraffic, MyConstant.STR_SEVENDAY, statisticBean.getExposure(), statisticBean.getPreExposure(), statisticBean.getVisiter(), statisticBean.getPreVisiter(), statisticBean.getTotalOrder(), statisticBean.getPreTotalOrder());
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initYesterdayData(StatisticBean statisticBean)
    {
        if (statisticBean == null)
        {
            return;
        }

        float yesterdayTotalIncoming = statisticBean.getTotalIncoming() * 0.01f;
        float beforeYesterdayTotalIncoming = statisticBean.getPreTotalIncoming() * 0.01f;
        mTvYesterdayIncome.setText(String.format("%.2f", yesterdayTotalIncoming));
        ZebraUtils zebraUtils = ZebraUtils.getInstance();
        zebraUtils.jsjComparedProportion(yesterdayTotalIncoming, beforeYesterdayTotalIncoming, mTvYesterdayIncomeCompared);

        float yesterdayIncomingYu = yesterdayTotalIncoming - statisticBean.getTotalCost() * 0.01f;
        float beforeYesterdayIncomingYu = beforeYesterdayTotalIncoming - statisticBean.getPreTotalCost() * 0.01f;
        mTvYesterdayIncomeYu.setText(String.format("%.2f", yesterdayIncomingYu));
        zebraUtils.jsjComparedProportion(yesterdayIncomingYu, beforeYesterdayIncomingYu, mTvYesterdayIncomeYuCompared);

        int finishOrderCount = statisticBean.getFinishOrder();
        mTvYesterdayFinishOrderCount.setText(finishOrderCount + "");

        float average;
        if (finishOrderCount == 0)
        {
            average = 0;
        } else
        {
            average = yesterdayTotalIncoming / finishOrderCount;
        }
        mTvYesterdayPriceAverage.setText(String.format("¥%.2f", average));
        mTvYesterdayFailOrderCount.setText(statisticBean.getFailOrder() + "");
        mTvYesterdayLoseIncoming.setText(String.format("¥%.2f", statisticBean.getLoseIncoming() * 0.01f));

        new TrafficComponent(mViewYesterdayTraffic, MyConstant.STR_YESTERDAY, statisticBean.getExposure(), statisticBean.getPreExposure(), statisticBean.getVisiter(), statisticBean.getPreVisiter(), statisticBean.getTotalOrder(), statisticBean.getPreTotalOrder());

        int newUser = statisticBean.getNewUser();
        int oldUser = statisticBean.getOldUser();
        int preNewUser = statisticBean.getPreNewUser();
        int preOldUser = statisticBean.getPreOldUser();

        new CirclePbComponent(mViewYesterdayCirclepbData, MyConstant.STR_CUSTOMER, MyConstant.STR_YESTERDAY, newUser + oldUser, preNewUser + preOldUser, newUser, preNewUser, oldUser, preOldUser);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initTodayData(StatisticBean statisticBean)
    {
        int totalIncoming = statisticBean.getTotalIncoming();
        mTvTodayIncome.setText(String.format("%.2f", totalIncoming * 0.01));
        mTvTodayOrderCount.setText(statisticBean.getTotalOrder() + "");
        mTvTodayIncomeYu.setText(String.format("%.2f", (totalIncoming - statisticBean.getTotalCost()) * 0.01));
    }

    private void initListener()
    {
        mRadiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (position == 0)
            {
                mllData0.setVisibility(View.VISIBLE);
                mllData1.setVisibility(View.GONE);
                mView0.setVisibility(View.VISIBLE);
                mView1.setVisibility(View.INVISIBLE);
            } else if (position == 1)
            {
                mllData0.setVisibility(View.GONE);
                mllData1.setVisibility(View.VISIBLE);
                mView0.setVisibility(View.INVISIBLE);
                mView1.setVisibility(View.VISIBLE);
            }
        });
    }
}
