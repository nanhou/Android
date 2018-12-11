package cn.jinxiit.zebra.activities.product.batchadds;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.CategoryOfProductAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class CategoryOfProductListActivity extends BaseActivity
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.tv_batch_num)
    TextView mTvBatchNum;

    private CategoryBean mCategoryBean;
    private CategoryOfProductAdapter mCategoryOfProductAdapter;
    private int mFrom;
    private MyApp myApp;
    private Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_of_product_list);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.rl_batch_product})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.rl_batch_product:
                MyActivityUtils.skipActivity(mContext, BatchCreateAllProductActivity.class);
                break;
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();
        mSmartRefreshLayout.autoRefresh();
    }

    @SuppressLint("SetTextI18n")
    private void initListener()
    {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                httpGetProductByCategoryId();
                refreshLayout.finishLoadMore(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                mCategoryOfProductAdapter.clearDataList();
                httpGetProductByCategoryId();
                refreshLayout.finishRefresh(5000);
            }
        });

        mCategoryOfProductAdapter.setOnRecyclerViewItemClickListener((view, position) -> mTvBatchNum.setText(myApp.mBatchProductOwnerDataBeanList.size() + ""));
    }

    private void httpGetProductByCategoryId()
    {
        if (mCategoryBean != null)
        {
            String categoryBeanId = mCategoryBean.getId();
            if (!TextUtils.isEmpty(categoryBeanId))
            {
                ApiUtils.getInstance()
                        .okgoGetProductList(this, myApp.mToken, null, null, categoryBeanId, mFrom, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                if(mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.finishRefresh();
                                    mSmartRefreshLayout.finishLoadMore();
                                }
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    if (jsonObject.has(MyConstant.STR_PRODUCTS))
                                    {
                                        List<ProductOwnerDataBean> productOwnerDataBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<ProductOwnerDataBean>>()
                                        {
                                        }.getType());
                                        if (productOwnerDataBeanList != null && productOwnerDataBeanList.size() > 0)
                                        {
                                            mCategoryOfProductAdapter.addDataList(productOwnerDataBeanList);
                                            mFrom += productOwnerDataBeanList.size();
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
                                if(mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.finishRefresh();
                                    mSmartRefreshLayout.finishLoadMore();
                                }
                            }
                        });
            }
        }
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(this);
        mRecyclerView.addItemDecoration(divider);
        mCategoryOfProductAdapter = new CategoryOfProductAdapter(myApp);
        mRecyclerView.setAdapter(mCategoryOfProductAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void initData()
    {
        myApp = (MyApp) getApplication();
        mCategoryBean = getIntent().getParcelableExtra(MyConstant.STR_CATEGORY);
        if (mCategoryBean != null)
        {
            String name = mCategoryBean.getName();
            if (!TextUtils.isEmpty(name))
            {
                new MyToolBar(this, name, null);
            }
        }

        mTvBatchNum.setText(myApp.mBatchProductOwnerDataBeanList.size() + "");
    }

}
