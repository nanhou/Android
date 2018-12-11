package cn.jinxiit.zebra.fragments.orders;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.adapters.OrderAdapter;
import cn.jinxiit.zebra.beans.OrderUpBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;

public class OrderListFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefresh;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    Unbinder unbinder;

    private Activity mContext;
    private OrderAdapter mAdapter;
    private int mFrom = 0;
    private String mType;
    private String mDayTime;
    private MyApp myApp;
    private Gson mGson = new Gson();
    private String mPlatform;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mAdapter != null) {
            mFrom = 0;
            mAdapter.clearDataList();
            httpGetStatusOrderList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mAdapter != null) {
//            mFrom = 0;
//            mAdapter.clearDataList();
//            httpGetStatusOrderList();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        initData();
        initRecyclerView();
        initListener();
    }

    private void initData() {
        mContext = getActivity();
        if (mContext != null) {
            myApp = (MyApp) mContext.getApplication();
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getString(MyConstant.STR_TYPE);
            mDayTime = arguments.getString(MyConstant.STR_DAY_TIME);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new OrderAdapter(mType);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                httpGetStatusOrderList();
                refreshLayout.finishRefresh(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mFrom = 0;
                mAdapter.clearDataList();
                httpGetStatusOrderList();
                refreshLayout.finishRefresh(5000);
            }
        });

        mAdapter.setmOnRecyclerViewBottomOffClick(
                (view, position) -> mRecyclerView.scrollToPosition(position));

        mAdapter.setmOnRecyclerViewPrinterOrderClick((View view, int position) -> {
            //            OrderUpBean itemData = mAdapter.getItemData(position);
            //            httpPrinterOrder(itemData);
            switch (mType) {
                case MyConstant.STR_NEW_ORDER:
                case MyConstant.STR_PICKING:
                case MyConstant.STR_DISTRIBUTION:
                case MyConstant.STR_ABNORMAL:
                    break;
                default:
                    mFrom = 0;
                    mAdapter.clearDataList();
                    httpGetStatusOrderList();
                    break;
            }
        });
    }

    private void httpGetStatusOrderList() {
        if (myApp.mCurrentStoreOwnerBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id)) {
                ApiUtils.getInstance()
                        .okgoGetStatusOrderList(mContext, myApp.mToken, store_id, mType, mDayTime,
                                mPlatform, null, String.valueOf(mFrom), new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        if (mSmartRefresh != null) {
                                            mSmartRefresh.finishRefresh();
                                            mSmartRefresh.finishLoadMore();
                                        }
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body());
                                            if (jsonObject.has(MyConstant.STR_ORDERS)) {
                                                List<OrderUpBean> orderUpBeanList = mGson.fromJson(
                                                        jsonObject.getString(MyConstant.STR_ORDERS),
                                                        new TypeToken<List<OrderUpBean>>() {
                                                        }.getType());
                                                if (orderUpBeanList != null) {
                                                    int size = orderUpBeanList.size();
                                                    if (mFrom == 0 && size == 0 && mTvTip != null) {
                                                        mTvTip.setVisibility(View.VISIBLE);
                                                    } else if (size > 0 && mTvTip != null) {
                                                        mTvTip.setVisibility(View.GONE);
                                                        mFrom += orderUpBeanList.size();
                                                        mAdapter.addDataList(orderUpBeanList);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        if (mSmartRefresh != null) {
                                            mSmartRefresh.finishRefresh();
                                            mSmartRefresh.finishLoadMore();
                                        }
                                    }
                                });
            }
        }
    }

    public void refreshDayTime(String dayTime) {
        if (dayTime != null) {
            if (!dayTime.equals(mDayTime)) {
                mDayTime = dayTime;
                mSmartRefresh.autoRefresh();
            }
        }
    }

    public void refreshPlatform(String platform) {
        if (platform == null) {
            if (mPlatform != null) {
                mPlatform = null;
                mSmartRefresh.autoRefresh();
            }
        } else {
            if (!platform.equals(mPlatform)) {
                mPlatform = platform;
                mSmartRefresh.autoRefresh();
            }
        }
    }


}
