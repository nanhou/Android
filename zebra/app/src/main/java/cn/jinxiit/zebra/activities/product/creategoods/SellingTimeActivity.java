package cn.jinxiit.zebra.activities.product.creategoods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.StoreTimeAdapter;
import cn.jinxiit.zebra.beans.LimitDateBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class SellingTimeActivity extends BaseActivity
{
    @BindView(R.id.rg_sell_date)
    RadioGroup mRgSellDate;
    @BindView(R.id.rg_sell_time)
    RadioGroup mRgSellTime;
    @BindView(R.id.ll_limit_date)
    LinearLayout mLlLimitDate;
    @BindView(R.id.ll_limit_time)
    LinearLayout mLlLimitTime;
    @BindView(R.id.tv_start_date)
    TextView mTvStartDate;
    @BindView(R.id.tv_end_date)
    TextView mTvEndDate;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private LimitDateBean mLimitDateBean;

    private StoreTimeAdapter mStoreTimeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_time);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        setResult(0);
    }

    @OnClick({R.id.tv_add_time, R.id.ll_start_date, R.id.ll_end_date})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_start_date:
                clickChooseDate(true);
                break;
            case R.id.ll_end_date:
                clickChooseDate(false);
                break;
            case R.id.tv_add_time:
                mStoreTimeAdapter.addData(null);
                break;
        }
    }

    private void clickChooseDate(boolean isStart)
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar limitStartCalendar = Calendar.getInstance();
            Calendar limitEndCalendar = Calendar.getInstance();
            limitEndCalendar.add(Calendar.YEAR, 10);

            TimePickerBuilder timePickerBuilder = new TimePickerBuilder(this, (date, v) -> {//选中事件回调

                choosedDate(isStart, date);

            }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                    //                .setContentSize(18)//滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    //                .setTitleText("起始时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    //                .setTitleColor(Color.BLACK)//标题文字颜色
                    //                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    //                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    //                .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(limitStartCalendar, limitEndCalendar)//起始终止年月日设定
                    .setLabel("年\t\t", "月\t\t", "日\t\t", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false);//是否显示为对话框样式

            if (isStart)
            {
                timePickerBuilder.setTitleText("起始时间");

                String strStartDate = mTvStartDate.getText()
                        .toString()
                        .trim();
                if (!TextUtils.isEmpty(strStartDate))
                {
                    Date startDate = dateFormat.parse(strStartDate);
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);
                    timePickerBuilder.setDate(startCalendar);
                }
            } else
            {
                timePickerBuilder.setTitleText("结束时间");

                String strEndDate = mTvEndDate.getText()
                        .toString()
                        .trim();
                if (!TextUtils.isEmpty(strEndDate))
                {
                    Date endDate = dateFormat.parse(strEndDate);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);
                    timePickerBuilder.setDate(endCalendar);
                }
            }

            timePickerBuilder.build()
                    .show();

        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void choosedDate(boolean isStart, Date date)
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String strDate = dateFormat.format(date);

            if (isStart)
            {
                String strEndDate = mTvEndDate.getText()
                        .toString()
                        .trim();
                if (!TextUtils.isEmpty(strEndDate))
                {
                    Date endDate = dateFormat.parse(strEndDate);
                    if (!strDate.equals(strEndDate) && date.getTime() > endDate.getTime())
                    {
                        MyToastUtils.error("开始时间早于结束时间");
                        return;
                    }
                }
                ViewSetDataUtils.textViewSetText(mTvStartDate, strDate);
            } else
            {
                String strStartDate = mTvStartDate.getText()
                        .toString()
                        .trim();
                if (!TextUtils.isEmpty(strStartDate))
                {
                    Date startDate = dateFormat.parse(strStartDate);
                    if (!strDate.equals(strStartDate) && date.getTime() < startDate.getTime())
                    {
                        MyToastUtils.error("结束时间晚于开始时间");
                        return;
                    }
                }
                ViewSetDataUtils.textViewSetText(mTvEndDate, strDate);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void initView()
    {
        initRecyclerView();
        initData();
        initListener();
    }

    private void initRecyclerView()
    {
        mRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(divider);
        mStoreTimeAdapter = new StoreTimeAdapter();
        mRecyclerView.setAdapter(mStoreTimeAdapter);
    }

    private void initListener()
    {
        mRgSellDate.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (position == 0)
            {
                mLlLimitDate.setVisibility(View.GONE);
            } else
            {
                mLlLimitDate.setVisibility(View.VISIBLE);
            }
        });

        mRgSellTime.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (position == 0)
            {
                mLlLimitTime.setVisibility(View.GONE);
            } else
            {
                mLlLimitTime.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initData()
    {
        new MyToolBar(this, "售卖时间", "保存").setOnTopMenuClickListener(v -> clickSave());
        mLlLimitDate.setVisibility(View.GONE);
        mLlLimitTime.setVisibility(View.GONE);

        mLimitDateBean = getIntent().getParcelableExtra(MyConstant.STR_BEAN);
        if (mLimitDateBean != null)
        {
            if (!mLimitDateBean.isFull_time())
            {
                mRgSellDate.check(mRgSellDate.getChildAt(1)
                        .getId());
                mLlLimitDate.setVisibility(View.VISIBLE);
                ViewSetDataUtils.textViewSetText(mTvStartDate, mLimitDateBean.getStart_date());
                ViewSetDataUtils.textViewSetText(mTvEndDate, mLimitDateBean.getEnd_date());
                if (!mLimitDateBean.isFull_hour())
                {
                    mRgSellTime.check(mRgSellTime.getChildAt(1)
                            .getId());
                    mLlLimitTime.setVisibility(View.VISIBLE);
                    List<String> limit_hour = mLimitDateBean.getLimit_hour();
                    mStoreTimeAdapter.addDataList(limit_hour);
                }
            }
        }
    }

    private void clickSave()
    {
        if (createBean())
        {
            if (mLimitDateBean != null)
            {
                Intent intent = new Intent();
                intent.putExtra(MyConstant.STR_BEAN, mLimitDateBean);
                setResult(1, intent);
                finish();
            }
        }
    }

    private boolean createBean()
    {
        if (mLimitDateBean == null)
        {
            mLimitDateBean = new LimitDateBean();
        } else
        {
            mLimitDateBean.initData();
        }
        int datePosition = mRgSellDate.indexOfChild(mRgSellDate.findViewById(mRgSellDate.getCheckedRadioButtonId()));
        if (datePosition == 0)
        {
            mLimitDateBean.setFull_time(true);
            return true;
        } else
        {
            mLimitDateBean.setFull_time(false);
        }
        String startDate = mTvStartDate.getText()
                .toString()
                .trim();
        String endDate = mTvEndDate.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate))
        {
            MyToastUtils.error("请选择开始时间与结束时间");
            return false;
        }
        mLimitDateBean.setStart_date(startDate);
        mLimitDateBean.setEnd_date(endDate);

        int timePosition = mRgSellTime.indexOfChild(mRgSellTime.findViewById(mRgSellTime.getCheckedRadioButtonId()));
        if (timePosition == 0)
        {
            mLimitDateBean.setFull_hour(true);
            return true;
        } else
        {
            mLimitDateBean.setFull_hour(false);
        }

        List<String> list = checkLimitHours();
        if (list == null)
        {
            return false;
        }
        mLimitDateBean.setLimit_hour(list);
        return true;
    }

    private List<String> checkLimitHours()
    {
        List<String> timeList = mStoreTimeAdapter.getmDataList();
        if (timeList == null)
        {
            MyToastUtils.error("请添加营业时间段");
            return null;
        }

        int size = timeList.size();
        if (size == 0)
        {
            MyToastUtils.error("请添加营业时间段");
            return null;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < size; i++)
        {
            String time = timeList.get(i);
            if (TextUtils.isEmpty(time))
            {
                MyToastUtils.error("请补充完整时间段信息");
                return null;
            }

            String[] split = time.split("-");
            if (split.length != 2)
            {
                MyToastUtils.error("请补充完整时间段信息");
                return null;
            }

            if (TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1]))
            {
                MyToastUtils.error("请补充完整时间段信息");
                return null;
            }

            try
            {
                long endTime = dateFormat.parse(split[1])
                        .getTime();
                if (dateFormat.parse(split[0])
                        .getTime() >= endTime)
                {
                    MyToastUtils.error("开始时刻必须小于结束时刻");
                    return null;
                }

                if (i < size - 1)
                {
                    String nextTime = timeList.get(i + 1);
                    if (!TextUtils.isEmpty(nextTime))
                    {
                        String[] nextSplit = nextTime.split("-");
                        if (nextSplit.length > 0 && !TextUtils.isEmpty(nextSplit[0]))
                        {
                            if (endTime > dateFormat.parse(nextSplit[0])
                                    .getTime())
                            {
                                MyToastUtils.error("时间段之间不可交叉");
                                return null;
                            }
                        }
                    }
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return timeList;
    }

    static class TimeViewHolder
    {
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;
        @BindView(R.id.tv_end_time)
        TextView tvEndTime;
        @BindView(R.id.tv_delete)
        TextView tvDelete;

        TimeViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
