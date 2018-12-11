package cn.jinxiit.zebra.fragments.products;

import android.app.Activity;
import android.content.Intent;
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
import cn.jinxiit.zebra.activities.product.GoodsInfoActivity;
import cn.jinxiit.zebra.activities.product.UpdateInventoryPriceAllPlatformActivity;
import cn.jinxiit.zebra.activities.product.UpdateInventoryPriceOnePlatformActivity;
import cn.jinxiit.zebra.adapters.SearchGoodsAdapter;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;

public class ProductStatusFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    Unbinder unbinder;

    private Activity mContext;
    private SearchGoodsAdapter mSearchGoodsAdapter;
    private String mType;
    private int mFrom;
    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final int REQUEST_CODE_UPDA_GOODS_INFO = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();
        if (mContext != null) {
            myApp = (MyApp) mContext.getApplication();
        }
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_UPDA_GOODS_INFO && resultCode == 1) {
            if (data != null) {
                GoodsBean goodsBean = data.getParcelableExtra(MyConstant.STR_BEAN);
                if (goodsBean != null) {
                    mSearchGoodsAdapter.updateItemData(goodsBean);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        initData();
        initRecyclerView();
        initListener();

        mSmartRefreshLayout.autoRefresh();
    }

    private void initListener() {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                httpGetAbNormalGoods();
                refreshLayout.finishLoadMore(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mFrom = 0;
                mSearchGoodsAdapter.clearDataList();
                httpGetAbNormalGoods();
                refreshLayout.finishRefresh(5000);
            }
        });

        mSearchGoodsAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            Intent intent = new Intent(getActivity(), GoodsInfoActivity.class);
            intent.putExtra(MyConstant.STR_BEAN, goodsBean);
            startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
        });

        mSearchGoodsAdapter.setUpdatePriceAndStockClickListener((view, position) -> {
            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            if (goodsBean != null) {
                Intent intent = new Intent(getActivity(),
                        UpdateInventoryPriceAllPlatformActivity.class);
                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
            }
        });

        mSearchGoodsAdapter.setmOnRecyclerViewPlatformClickListener((view, position, platform) -> {
            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            if (goodsBean != null) {
                Intent intent = new Intent(getActivity(),
                        UpdateInventoryPriceOnePlatformActivity.class);
                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                intent.putExtra(MyConstant.STR_PLATFORM, platform);
                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
            }
        });
    }

    private void httpGetAbNormalGoods() {
        if (myApp.mCurrentStoreOwnerBean != null && !TextUtils.isEmpty(mType)) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id)) {
                ApiUtils.getInstance().okgoGetAbNormalGoods(mContext, myApp.mToken, store_id, mType,
                        String.valueOf(mFrom), new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (mSmartRefreshLayout != null) {
                                    mSmartRefreshLayout.finishLoadMore();
                                    mSmartRefreshLayout.finishRefresh();
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    if (jsonObject.has(MyConstant.STR_PRODUCTS)) {
                                        List<GoodsBean> goodsBeanList = mGson.fromJson(
                                                jsonObject.getString(MyConstant.STR_PRODUCTS),
                                                new TypeToken<List<GoodsBean>>() {
                                                }.getType());
                                        if (goodsBeanList != null) {
                                            mSearchGoodsAdapter.addDataList(goodsBeanList);
                                            mFrom += goodsBeanList.size();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                if (mSmartRefreshLayout != null) {
                                    mSmartRefreshLayout.finishRefresh();
                                    mSmartRefreshLayout.finishLoadMore();
                                }
                            }
                        });
            }
        }
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getString(MyConstant.STR_TYPE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(mContext, R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);
        mSearchGoodsAdapter = new SearchGoodsAdapter(R.layout.item_search_goods, false);
        mRecyclerView.setAdapter(mSearchGoodsAdapter);
    }
}
