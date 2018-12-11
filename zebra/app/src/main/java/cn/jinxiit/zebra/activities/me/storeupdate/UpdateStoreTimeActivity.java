package cn.jinxiit.zebra.activities.me.storeupdate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.StoreTimeAdapter;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class UpdateStoreTimeActivity extends BaseActivity
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_add)
    Button mBtnAdd;

    private StoreTimeAdapter mStoreTimeAdapter;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_store_time);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onBackPressed()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                if (extra != null)
                {
                    List<String> business_time = extra.getBusiness_time();
                    if (business_time != null)
                    {
                        List<String> timelist = mStoreTimeAdapter.getmDataList();
                        int size = business_time.size();
                        if (size == timelist.size())
                        {
                            for (int i = 0; i < size; i++)
                            {
                                String businesTime = business_time.get(i);
                                String time = timelist.get(i);
                                if (!TextUtils.isEmpty(businesTime))
                                {
                                    if (businesTime.equals(time))
                                    {
                                        if (i == size - 1)
                                        {
                                            super.onBackPressed();
                                        }
                                    } else
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        rxDialogSureCancel.setTitle("返回");
        rxDialogSureCancel.setContent("是否不保存数据直接退出？");
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            rxDialogSureCancel.cancel();
            UpdateStoreTimeActivity.super.onBackPressed();
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    @OnClick(R.id.btn_add)
    public void onViewClicked()
    {
        mStoreTimeAdapter.addData(null);
        btnAddStatus();
    }

    private void btnAddStatus()
    {
        int itemCount = mStoreTimeAdapter.getItemCount();
        if (itemCount > 2)
        {
            mBtnAdd.setVisibility(View.GONE);
        } else
        {
            mBtnAdd.setVisibility(View.VISIBLE);
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();
    }

    private void initListener()
    {
        mStoreTimeAdapter.setDeleteItemClickListener((view, position) -> btnAddStatus());
    }

    private void initRecyclerView()
    {
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(divider);
        mStoreTimeAdapter = new StoreTimeAdapter();
        mRecyclerView.setAdapter(mStoreTimeAdapter);

        if (myApp.mCurrentStoreOwnerBean != null)
        {
            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                if (extra != null)
                {
                    List<String> business_time = extra.getBusiness_time();
                    if (business_time != null)
                    {
                        mStoreTimeAdapter.addDataList(business_time);
                        if (business_time.size() > 2)
                        {
                            mBtnAdd.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    private void initData()
    {
        new MyToolBar(this, "修改营业时间", "保存").setOnTopMenuClickListener(v -> clickSave());
        myApp = (MyApp) getApplication();
    }

    private void clickSave()
    {
        List<String> timeList = mStoreTimeAdapter.getmDataList();
        if (timeList == null)
        {
            MyToastUtils.error("请添加营业时间段");
            return;
        }

        int size = timeList.size();
        if (size == 0)
        {
            MyToastUtils.error("请添加营业时间段");
            return;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < size; i++)
        {
            String time = timeList.get(i);
            if (TextUtils.isEmpty(time))
            {
                MyToastUtils.error("请补充完整时间段信息");
                return;
            }

            String[] split = time.split("-");
            if (split.length != 2)
            {
                MyToastUtils.error("请补充完整时间段信息");
                return;
            }

            if (TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1]))
            {
                MyToastUtils.error("请补充完整时间段信息");
                return;
            }

            try
            {
                long endTime = dateFormat.parse(split[1])
                        .getTime();
                if (dateFormat.parse(split[0])
                        .getTime() >= endTime)
                {
                    MyToastUtils.error("开始时刻必须小于结束时刻");
                    return;
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
                                return;
                            }
                        }
                    }
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try
        {
            for (int i = 0; i < timeList.size(); i++)
            {
                jsonArray.put(timeList.get(i));
            }
            jsonObject.put(MyConstant.STR_BUSINESS_TIME, jsonArray);
            httpPostUpdateStoreInfo(jsonObject.toString(), timeList);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void httpPostUpdateStoreInfo(String extraJson, List<String> timeList)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoPostUpdateStore(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), null, null, extraJson, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            if (myApp.mCurrentStoreOwnerBean != null)
                            {
                                StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
                                if (store != null)
                                {
                                    StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                                    if (extra == null)
                                    {
                                        extra = new StoreOwnerBean.StoreBean.ExtraBean();
                                    }
                                    extra.setBusiness_time(timeList);
                                }
                                finish();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("修改失败！！");
                        }
                    });
        }
    }
}