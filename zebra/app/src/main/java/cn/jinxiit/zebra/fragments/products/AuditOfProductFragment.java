package cn.jinxiit.zebra.fragments.products;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import cn.jinxiit.zebra.activities.product.UpdateOrAddGoodsActivity;
import cn.jinxiit.zebra.adapters.AuditOfProductAdapter;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;

public class AuditOfProductFragment extends Fragment
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    Unbinder unbinder;

    private Activity mContext;
    private AuditOfProductAdapter mAuditOfProductAdapter;
    private String mType;
    private int mFrom;
    private MyApp myApp;
    private Gson mGson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();
        initView();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void refreshData()
    {
        if (mSmartRefreshLayout != null)
        {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();

        mSmartRefreshLayout.autoRefresh();
    }

    private void initListener()
    {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                httpGetGoodsOfAudit();
                refreshLayout.finishLoadMore(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                mAuditOfProductAdapter.clearDataList();
                httpGetGoodsOfAudit();
                refreshLayout.finishRefresh(5000);
            }
        });

        mAuditOfProductAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            GoodsBean goodsBean = mAuditOfProductAdapter.getItemData(position);
            if (goodsBean != null)
            {
                if (MyConstant.STR_FAILED.equals(goodsBean.getStatus()))
                {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(MyConstant.STR_IS, true);
                    bundle.putParcelable(MyConstant.STR_BEAN, goodsBean);
                    bundle.putString(MyConstant.STR_TYPE, MyConstant.STR_FAILED);
                    MyActivityUtils.skipActivityForResult(mContext, UpdateOrAddGoodsActivity.class, bundle, 1);
                }
            }
        });
    }

    private void httpGetGoodsOfAudit()
    {
        if (myApp.mCurrentStoreOwnerBean != null && !TextUtils.isEmpty(mType))
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetExamineGoods(mContext, myApp.mToken, store_id, mType, String.valueOf(mFrom), new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                if (mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.finishLoadMore();
                                    mSmartRefreshLayout.finishRefresh();
                                }
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    if (jsonObject.has(MyConstant.STR_PRODUCTS))
                                    {
                                        List<GoodsBean> goodsBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<GoodsBean>>()
                                        {
                                        }.getType());
                                        if (goodsBeanList != null)
                                        {
                                            mAuditOfProductAdapter.addDataList(goodsBeanList);
                                            mFrom += goodsBeanList.size();
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
                                    mSmartRefreshLayout.finishLoadMore();
                                    mSmartRefreshLayout.finishRefresh();
                                }
                            }
                        });
            }
        }
    }

    private void initData()
    {
        myApp = (MyApp) mContext.getApplication();
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mType = arguments.getString(MyConstant.STR_TYPE);
        }
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);
        mAuditOfProductAdapter = new AuditOfProductAdapter();
        mRecyclerView.setAdapter(mAuditOfProductAdapter);
    }
}
