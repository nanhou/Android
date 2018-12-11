package cn.jinxiit.zebra.activities.orders;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.product.SearchActivity;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.fragments.orders.OrderListFragment;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.MyUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class OderOfDayActivity extends BaseActivity
{
    @BindView(R.id.radiogroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.rb1)
    RadioButton mRbChooseTime;
    @BindView(R.id.rb0)
    RadioButton mRbToday;
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;
    private MyApp myApp;
    private Gson mGson = new Gson();

    //当前评跟时间
    private String mPlatform;
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_of_day);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick({R.id.iv_back, R.id.iv_screening, R.id.iv_search})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_screening:
                clickScreening();
                break;
            case R.id.iv_search:
                Bundle bundle = new Bundle();
                bundle.putString(MyConstant.STR_TYPE, MyConstant.STR_SEARCH_ORDER);
                MyActivityUtils.skipActivity(this, SearchActivity.class, bundle);
                break;
        }
    }

    private void clickScreening()
    {
        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_choose_platform, null);
        dialogContent.findViewById(R.id.tv_platform_all)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    choosePlatform(null);
                });
        dialogContent.findViewById(R.id.tv_platform_jd)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    choosePlatform(MyConstant.STR_EN_JDDJ);
                });
        dialogContent.findViewById(R.id.tv_platform_mt)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    choosePlatform(MyConstant.STR_EN_MT);
                });
        dialogContent.findViewById(R.id.tv_platform_eleme)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    choosePlatform(MyConstant.STR_EN_ELEME);
                });
        dialogContent.findViewById(R.id.tv_platform_ebai)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    choosePlatform(MyConstant.STR_EN_EBAI);
                });
        dialogContent.findViewById(R.id.tv_cancel)
                .setOnClickListener(v -> dialog.cancel());

        dialog.setContentView(dialogContent);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window == null)
        {
            return;
        }
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources()
                        .getColor(R.color.colorTm));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        dialogContent.measure(0, 0);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        lp.width = view.getMeasuredWidth();
        lp.width = WindowUtils.dip2px(mContext, 280);
        lp.alpha = 5f; // 透明度
        window.setAttributes(lp);
        dialog.show();
    }

    private void choosePlatform(String platform)
    {
        mPlatform = platform;
        httpGetOrderNum();
        for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++)
        {
            OrderListFragment orderListFragment = (OrderListFragment) mFragmentPagerItemAdapter.getPage(i);
            orderListFragment.refreshPlatform(platform);
        }
    }

    private void initView()
    {
        initData();
        initSmartTabLayout();
        initListener();
        httpGetOrderNum();
    }

    private void httpGetOrderNum()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetOrderCount(mContext, myApp.mToken, store_id, mTime, mPlatform, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                CountBean countBean = mGson.fromJson(response.body(), CountBean.class);
                                updateTab(countBean);
                            }

                            @Override
                            public void onError(Response<String> response)
                            {
                                MyToastUtils.error("获取数据失败");
                            }
                        });
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTab(CountBean countBean)
    {
        if (countBean != null)
        {
            mViewPagerTab.setCustomTabView((container, position, adapter) -> {
                LayoutInflater inflater = LayoutInflater.from(container.getContext());
                View tab = inflater.inflate(R.layout.custom_tab, container, false);
                TextView customText = tab.findViewById(R.id.custom_text);
                CharSequence title = adapter.getPageTitle(position);
                switch (position)
                {
                    case 0:
                        customText.setText(String.format("%s(%d)", title, countBean.getAll()));
                        break;
                    case 1:
                        customText.setText(String.format("%s(%d)", title, countBean.getSuccess()));
                        break;
                    case 2:
                        customText.setText(String.format("%s(%d)", title, countBean.getCancel()));
                        break;
                    case 3:
                        customText.setText(String.format("%s(%d)", title, countBean.getAfter_sale()));
                        break;

                    default:
                        break;
                    //                        throw new IllegalStateException("Invalid position: " + position);
                }
                return tab;
            });
            mViewPagerTab.setViewPager(mViewPager);
        }
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        mViewPager.setOffscreenPageLimit(4);

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mTime = dateFormat.format(calendar.getTime());
    }

    private void initSmartTabLayout()
    {
        mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(this.getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("全部", OrderListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, null)
                        .putString(MyConstant.STR_DAY_TIME, mTime)
                        .get())
                .add("已完成", OrderListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_SUCCESS)
                        .putString(MyConstant.STR_DAY_TIME, mTime)
                        .get())
                .add("已取消", OrderListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_CANCEL)
                        .putString(MyConstant.STR_DAY_TIME, mTime)
                        .get())
                .add("售后单", OrderListFragment.class, new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_AFTER_SALE)
                        .putString(MyConstant.STR_DAY_TIME, mTime)
                        .get())
                .create());
        mViewPager.setAdapter(mFragmentPagerItemAdapter);
        mViewPagerTab.setViewPager(mViewPager);
    }

    private void initListener()
    {
        mRbChooseTime.setOnClickListener(v -> showCalenderDialog());

        mRbToday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            chooseDate(dateFormat.format(calendar.getTime()));
        });

    }

    private void chooseDate(String dayTime)
    {
        if (dayTime != null)
        {
            mTime = dayTime;
            httpGetOrderNum();
            for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++)
            {
                OrderListFragment orderListFragment = (OrderListFragment) mFragmentPagerItemAdapter.getPage(i);
                orderListFragment.refreshDayTime(dayTime);
            }
        }
    }

    private void showCalenderDialog()
    {
        String chooseTime = mRbChooseTime.getText()
                .toString()
                .trim();
        Calendar chooseCalendar = Calendar.getInstance();
        chooseCalendar.add(Calendar.DAY_OF_MONTH, -1);
        if (MyUtils.isValidDate(chooseTime, "yyyy-MM-dd"))
        {
            chooseDate(chooseTime);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                chooseCalendar.setTime(dateFormat.parse(chooseTime));
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        Calendar limitStartCalendar = Calendar.getInstance();
        limitStartCalendar.add(Calendar.YEAR, -3);
        Calendar limitEndCalendar = Calendar.getInstance();
        limitEndCalendar.add(Calendar.DAY_OF_MONTH, -1);

        TimePickerView timePickerView = new TimePickerBuilder(this, (date, v) -> {//选中事件回调

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = dateFormat.format(date);
            if (!TextUtils.isEmpty(strDate))
            {
                mRbChooseTime.setText(strDate);
                chooseDate(strDate);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                //                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                //                .setTitleText("起始时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                //                .setTitleColor(Color.BLACK)//标题文字颜色
                //                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                //                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setDate(chooseCalendar)// 如果不设置的话，默认是系统时间*/
                .setRangDate(limitStartCalendar, limitEndCalendar)//起始终止年月日设定
                .setLabel("年", "月", "日", null, null, null)//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

        timePickerView.setOnDismissListener(o -> {
            String str = mRbChooseTime.getText()
                    .toString()
                    .trim();
            if ("选择日期".equals(str))
            {
                RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(0);
                radioButton.setChecked(true);
            }
        });
        timePickerView.show();
    }
}
