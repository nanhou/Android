package cn.jinxiit.zebra.activities.sta;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.StatisticBean;
import cn.jinxiit.zebra.components.ChartComponent;
import cn.jinxiit.zebra.components.CirclePbComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class AnalysisBusinessActivity extends BaseActivity
{
    @BindView(R.id.ll_hebdomad)
    LinearLayout mLl_Hebdomad;
    @BindView(R.id.view_chart0)
    View mViewChart0;
    @BindView(R.id.rg_datetop)
    RadioGroup mRg_DateTop;
    @BindView(R.id.view0)
    View mView0;
    @BindView(R.id.view1)
    View mView1;
    @BindView(R.id.view2)
    View mView2;
    @BindView(R.id.view3)
    View mView3;
    @BindView(R.id.ll_calendar)
    LinearLayout mLlCalendar;
    @BindView(R.id.tv_income_today)
    TextView mTvIncomeToday;
    @BindView(R.id.tv_order_today)
    TextView mTvOrderToday;
    @BindView(R.id.tv_income_yu_today)
    TextView mTvAnalysisYuToday;
    @BindView(R.id.tv_income_yesterday)
    TextView mTvIncomeYesterday;
    @BindView(R.id.tv_yu_yesterday)
    TextView mTvYuYesterday;
    @BindView(R.id.tv_finish_order_count_yesterday)
    TextView mTvFinishOrderCountYesterday;
    @BindView(R.id.tv_fail_order_count_yesterday)
    TextView mTvFailOrderCountYesterday;
    @BindView(R.id.tv_income_yesterday_compared)
    TextView mTvIncomeYesterdayCompared;
    @BindView(R.id.tv_income_yu_yesterday_compared)
    TextView mTvIncomeYuYesterdayCompared;
    @BindView(R.id.tv_price_average_yesterday)
    TextView mTvPriceAverageYesterday;
    @BindView(R.id.tv_lose_incoming_yesterday)
    TextView mTvLoseIncomingYesterday;
    @BindView(R.id.tv_analysis_business_title)
    TextView mTvAnalysisBusinessTitle;
    @BindView(R.id.tv_income_compare)
    TextView mTvIncomeCompare;
    @BindView(R.id.tv_yu_compare)
    TextView mTvYuCompare;
    @BindView(R.id.view_circlepb)
    View mViewCirclePb;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.ll_platforms)
    View mViewPlatforms;

    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final String[] mLineCharTitles0 = {"营业额", "预计收入", "有效订单", "预计毛利", "客单价"};
    private static final String[] mLineCharTitlesUnit0 = {"元", "元", "单", "元", "元"};

    private ChartComponent mChartComponent;

    private StatisticBean mYestedayStatisticBean;
    private StatisticBean m7DayStatisticBean;
    private StatisticBean m30DayStatisticBean;
    private StatisticBean mDiydayStatisticBean;

    private long mYesterDayMillis;
    private SimpleDateFormat mDateFormat;
    private MyToolBar myToolBar;
    private String mPlatform = MyConstant.STR_ALLSTATIS;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_business);
        ButterKnife.bind(this);
        myApp = (MyApp) getApplication();
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mChartComponent = null;
        mYestedayStatisticBean = null;
        m7DayStatisticBean = null;
        m30DayStatisticBean = null;
        mDiydayStatisticBean = null;
        myToolBar = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return !disPlatform(ev) && super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.tv_platform0, R.id.tv_platform1, R.id.tv_platform2, R.id.tv_platform3, R.id.tv_start_time, R.id.tv_end_time})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_start_time:
                clickStartTime();
                break;
            case R.id.tv_end_time:
                clickEndTime();
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
        }
    }

    private void choosePlatform(String menu, String platform)
    {
        platformLayoutVisibleOrGone(mViewPlatforms.getVisibility());
        if (mPlatform.equals(platform))
        {
            return;
        }

        myToolBar.setTvMenu(menu);
        mYestedayStatisticBean = null;
        m7DayStatisticBean = null;
        m30DayStatisticBean = null;
        mDiydayStatisticBean = null;
        mPlatform = platform;
        initHttpGetData(MyConstant.STR_TODAY);
        int position = mRg_DateTop.indexOfChild(mRg_DateTop.findViewById(mRg_DateTop.getCheckedRadioButtonId()));
        String dayType = MyConstant.STR_YESTERDAY;
        switch (position)
        {
            case 1:
                dayType = MyConstant.STR_SEVENDAY;
                break;
            case 2:
                dayType = MyConstant.STR_MONTH;
                break;
            case 3:
                dayType = MyConstant.STR_DIYDAY;
                break;
        }
        initHttpGetData(dayType);
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

    private void clickEndTime()
    {
        try
        {
            Date endDate = mDateFormat.parse(mTvEndTime.getText()
                    .toString()
                    .trim());
            Date startDate = mDateFormat.parse(mTvStartTime.getText()
                    .toString()
                    .trim());

            Calendar selectedCalendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            selectedCalendar.setTime(endDate);
            startCalendar.setTime(startDate);

            endCalendar.setTime(startDate);
            endCalendar.add(Calendar.MONTH, 3);
            long timeInMillis = endCalendar.getTimeInMillis();
            if (timeInMillis > mYesterDayMillis)
            {
                endCalendar.setTime(new Date(mYesterDayMillis));
            }

            //正确设置方式 原因：注意事项有说明
            //            startCalendar.set(2013, 0, 1);
            //            endCalendar.set(2020, 11, 31);

            TimePickerView pvTime = new TimePickerBuilder(this, (date, v) -> {//选中事件回调
                mTvEndTime.setText(mDateFormat.format(date));
                initHttpGetData(MyConstant.STR_DIYDAY);
            }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                    //                .setContentSize(18)//滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText("结束时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    //                .setTitleColor(Color.BLACK)//标题文字颜色
                    //                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    //                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startCalendar, endCalendar)//起始终止年月日设定
                    .setLabel("年\t\t", "月\t\t", "日\t\t", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            pvTime.show();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void clickStartTime()
    {
        try
        {
            Date endDate = mDateFormat.parse(mTvEndTime.getText()
                    .toString()
                    .trim());
            Date startDate = mDateFormat.parse(mTvStartTime.getText()
                    .toString()
                    .trim());

            Calendar selectedCalendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            selectedCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
            startCalendar.setTime(endDate);
            startCalendar.add(Calendar.MONTH, -3);

            //正确设置方式 原因：注意事项有说明
            //            startCalendar.set(2013, 0, 1);
            //            endCalendar.set(2020, 11, 31);

            TimePickerView pvTime = new TimePickerBuilder(this, (date, v) -> {//选中事件回调
                mTvStartTime.setText(mDateFormat.format(date));
                initHttpGetData(MyConstant.STR_DIYDAY);
            }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                    //                .setContentSize(18)//滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText("起始时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    //                .setTitleColor(Color.BLACK)//标题文字颜色
                    //                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    //                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startCalendar, endCalendar)//起始终止年月日设定
                    .setLabel("年\t\t", "月\t\t", "日\t\t", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            pvTime.show();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void initHttpGetData(String dataType)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                if (dataType.equals(MyConstant.STR_DIYDAY))
                {
                    String startTime = mTvStartTime.getText()
                            .toString()
                            .trim();
                    String endTime = mTvEndTime.getText()
                            .toString()
                            .trim();
                    httpGetStroeTotalData(store_id, startTime + "-" + endTime);
                } else
                {
                    httpGetStroeTotalData(store_id, dataType);
                }
            }
        }
    }

    private void httpGetStroeTotalData(String storeId, final String dateType)
    {
        ApiUtils.getInstance()
                .okgoGetStoreStatistic(AnalysisBusinessActivity.this, myApp.mToken, mPlatform, storeId, dateType, true, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        StatisticBean statisticBean = mGson.fromJson(response.body(), StatisticBean.class);
                        if (statisticBean != null)
                        {
                            switch (dateType)
                            {
                                case MyConstant.STR_TODAY:
                                    initToDayData(statisticBean);
                                    break;
                                case MyConstant.STR_YESTERDAY:
                                    initChartOtherData(statisticBean, dateType);
                                    mYestedayStatisticBean = statisticBean;
                                    break;
                                case MyConstant.STR_SEVENDAY:
                                    initChartOtherData(statisticBean, dateType);
                                    initChartData(statisticBean);
                                    m7DayStatisticBean = statisticBean;
                                    break;
                                case MyConstant.STR_MONTH:
                                    initChartOtherData(statisticBean, dateType);
                                    initChartData(statisticBean);
                                    m30DayStatisticBean = statisticBean;
                                    break;
                                default:
                                    initChartOtherData(statisticBean, dateType);
                                    initChartData(statisticBean);
                                    mDiydayStatisticBean = statisticBean;
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

    @SuppressLint("LongLogTag")
    private void initChartData(StatisticBean statisticBean)
    {
        ZebraUtils.getInstance()
                .statisticdataSetChart(mChartComponent, mPlatform, mLineCharTitles0, mLineCharTitlesUnit0, statisticBean);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initChartOtherData(StatisticBean statisticBean, String dateType)
    {
        float yesterdayTotalIncoming = statisticBean.getTotalIncoming() * 0.01f;
        float beforeStatisticTotalIncoming = statisticBean.getPreTotalIncoming() * 0.01f;
        mTvIncomeYesterday.setText(String.format("%.2f", yesterdayTotalIncoming));
        ZebraUtils zebraUtils = ZebraUtils.getInstance();
        zebraUtils.jsjComparedProportion(yesterdayTotalIncoming, beforeStatisticTotalIncoming, mTvIncomeYesterdayCompared);

        float yesterdayIncomingYu = yesterdayTotalIncoming - statisticBean.getTotalCost() * 0.01f;
        float beforeYesterdayIncomingYu = beforeStatisticTotalIncoming - statisticBean.getPreTotalCost() * 0.01f;
        mTvYuYesterday.setText(String.format("%.2f", yesterdayIncomingYu));
        zebraUtils.jsjComparedProportion(yesterdayIncomingYu, beforeYesterdayIncomingYu, mTvIncomeYuYesterdayCompared);

        int finishOrder = statisticBean.getFinishOrder();
        mTvFinishOrderCountYesterday.setText(finishOrder + "");

        int failOrder = statisticBean.getFailOrder();
        mTvFailOrderCountYesterday.setText(failOrder + "");

        float average;
        if (finishOrder == 0)
        {
            average = 0;
        } else
        {
            average = yesterdayTotalIncoming / finishOrder;
        }
        mTvPriceAverageYesterday.setText(String.format("¥%.2f", average));
        mTvLoseIncomingYesterday.setText(String.format("¥%.2f", statisticBean.getLoseIncoming() * 0.01f));

        int preTotalOrder = statisticBean.getPreTotalOrder();
        int preFailOrder = statisticBean.getPreFailOrder();

        new CirclePbComponent(mViewCirclePb, MyConstant.STR_ORDER, dateType, statisticBean.getTotalOrder(), preTotalOrder, statisticBean.getFinishOrder(), preTotalOrder - preFailOrder, statisticBean.getFailOrder(), preFailOrder);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initToDayData(StatisticBean statisticBean)
    {
        int totalIncoming = statisticBean.getTotalIncoming();
        mTvIncomeToday.setText(String.format("%.2f", totalIncoming * 0.01));
        mTvOrderToday.setText(statisticBean.getTotalOrder() + "");
        mTvAnalysisYuToday.setText(String.format("%.2f", (totalIncoming - statisticBean.getTotalCost()) * 0.01));

        initStartAndEndTime(statisticBean);
    }

    private void initStartAndEndTime(StatisticBean jdStatisticBean)
    {
        try
        {
            List<StatisticBean.StatisticBean0> statistic = jdStatisticBean.getStatistic();
            StatisticBean.StatisticBean0 statisticBean = statistic.get(0);
            String dayTime = statisticBean.getDayTime();
            Date date = mDateFormat.parse(dayTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            mYesterDayMillis = calendar.getTime()
                    .getTime();
            mTvEndTime.setText(mDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -3);
            mTvStartTime.setText(mDateFormat.format(calendar.getTime()));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void initView()
    {
        initData();
        initChart0();
        initListener();
        initHttpGetData(MyConstant.STR_TODAY);
        initHttpGetData(MyConstant.STR_YESTERDAY);
    }

    @SuppressLint("SimpleDateFormat")
    private void initData()
    {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mViewPlatforms.setVisibility(View.GONE);
        myToolBar = new MyToolBar(this, "营业统计", "全部平台");
        myToolBar.setOnTopMenuClickListener(v -> {
            if (mViewPlatforms.getVisibility() == View.GONE)
            {
                mViewPlatforms.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initListener()
    {
        mRg_DateTop.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (position == 0)
            {
                initYesterdayView();
            } else if (position == 1)
            {
                initSevenDayView();
            } else if (position == 2)
            {
                initMonthView();
            } else
            {
                initDiyDayView();
            }
        });
    }


    private void initDiyDayView()
    {
        mLl_Hebdomad.setVisibility(View.VISIBLE);
        mView0.setVisibility(View.INVISIBLE);
        mView1.setVisibility(View.INVISIBLE);
        mView2.setVisibility(View.INVISIBLE);
        mView3.setVisibility(View.VISIBLE);
        mLlCalendar.setVisibility(View.VISIBLE);
        mTvAnalysisBusinessTitle.setText("自定义 营业分析");
        mTvIncomeCompare.setHint("自定义趋势");
        mTvYuCompare.setHint("自定义趋势");

        if (mDiydayStatisticBean == null)
        {
            initHttpGetData(MyConstant.STR_DIYDAY);
        } else
        {
            initChartOtherData(mDiydayStatisticBean, MyConstant.STR_DIYDAY);
            initChartData(mDiydayStatisticBean);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initMonthView()
    {
        mLl_Hebdomad.setVisibility(View.VISIBLE);
        mView0.setVisibility(View.INVISIBLE);
        mView1.setVisibility(View.INVISIBLE);
        mView2.setVisibility(View.VISIBLE);
        mView3.setVisibility(View.INVISIBLE);
        mLlCalendar.setVisibility(View.GONE);
        mTvAnalysisBusinessTitle.setText("近30日 营业分析");
        mTvIncomeCompare.setHint("较前30日");
        mTvYuCompare.setHint("较前30日");

        if (m30DayStatisticBean == null)
        {
            initHttpGetData(MyConstant.STR_MONTH);
        } else
        {
            initChartOtherData(m30DayStatisticBean, MyConstant.STR_MONTH);
            initChartData(m30DayStatisticBean);
        }
    }

    private void initSevenDayView()
    {
        mLl_Hebdomad.setVisibility(View.VISIBLE);
        mView0.setVisibility(View.INVISIBLE);
        mView1.setVisibility(View.VISIBLE);
        mView2.setVisibility(View.INVISIBLE);
        mView3.setVisibility(View.INVISIBLE);
        mLlCalendar.setVisibility(View.GONE);
        mTvAnalysisBusinessTitle.setText("近7日 营业分析");
        mTvIncomeCompare.setHint("较前7日");
        mTvYuCompare.setHint("较前7日");
        if (m7DayStatisticBean == null)
        {
            initHttpGetData(MyConstant.STR_SEVENDAY);
        } else
        {
            initChartOtherData(m7DayStatisticBean, MyConstant.STR_SEVENDAY);
            initChartData(m7DayStatisticBean);
        }
    }

    private void initYesterdayView()
    {
        mLl_Hebdomad.setVisibility(View.GONE);
        mView0.setVisibility(View.VISIBLE);
        mView1.setVisibility(View.INVISIBLE);
        mView2.setVisibility(View.INVISIBLE);
        mView3.setVisibility(View.INVISIBLE);
        mLlCalendar.setVisibility(View.GONE);
        mTvIncomeCompare.setHint("较前日");
        mTvYuCompare.setHint("较前日");
        if (mYestedayStatisticBean == null)
        {
            initHttpGetData(MyConstant.STR_YESTERDAY);
        } else
        {
            initChartOtherData(mYestedayStatisticBean, MyConstant.STR_YESTERDAY);
        }
    }

    private void initChart0()
    {
        List<String> titles = new ArrayList<>();
        Collections.addAll(titles, mLineCharTitles0);
        mChartComponent = new ChartComponent(mViewChart0, titles);
    }

}
