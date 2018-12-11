package cn.jinxiit.zebra.fragments.mes;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.me.notifications.NotificationInfoActivity;
import cn.jinxiit.zebra.adapters.NotificationAdapter;
import cn.jinxiit.zebra.beans.NotificationBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class NotificationFragment extends Fragment
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    Unbinder unbinder;

    private NotificationAdapter mNotificationAdapter;
    private int mFrom;
    private String mType;
    private Activity mActivity;
    private MyApp myApp;
    private Gson mGson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();

        mSmartRefreshLayout.autoRefresh();
    }

    private void initData()
    {
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mType = arguments.getString(MyConstant.STR_TYPE);
        }
        mActivity = getActivity();
        if (mActivity != null)
        {
            myApp = (MyApp) mActivity.getApplication();
        }
    }

    private void initListener()
    {
        mNotificationAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            NotificationBean notificationBean = mNotificationAdapter.getItemData(position);
            if (notificationBean != null)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MyConstant.STR_BEAN, notificationBean);
                MyActivityUtils.skipActivity(Objects.requireNonNull(getActivity()), NotificationInfoActivity.class, bundle);
            }
        });

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                httpGetNotification();
                refreshLayout.finishLoadMore(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                mNotificationAdapter.clearDataList();
                httpGetNotification();
                refreshLayout.finishRefresh(5000);
            }
        });
    }

    private void httpGetNotification()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoGetNotification(mActivity, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), mType, String.valueOf(mFrom), new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            if (mSmartRefreshLayout != null)
                            {
                                mSmartRefreshLayout.finishRefresh();
                                mSmartRefreshLayout.finishLoadMore();
                            }
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_MESSAGES))
                                {
                                    List<NotificationBean> notificationBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_MESSAGES), new TypeToken<List<NotificationBean>>()
                                    {
                                    }.getType());
                                    if (notificationBeanList != null)
                                    {
                                        mNotificationAdapter.addDataList(notificationBeanList);
                                        mFrom += notificationBeanList.size();
                                    }
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            if (mSmartRefreshLayout != null)
                            {
                                mSmartRefreshLayout.finishRefresh();
                                mSmartRefreshLayout.finishLoadMore();
                            }
                        }
                    });
        }
    }

    private void initRecyclerView()
    {
        FragmentActivity activity = Objects.requireNonNull(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(activity);
        mRecyclerView.addItemDecoration(divider);
        mNotificationAdapter = new NotificationAdapter();
        mRecyclerView.setAdapter(mNotificationAdapter);
    }
}
