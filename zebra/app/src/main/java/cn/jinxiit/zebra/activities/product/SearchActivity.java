package cn.jinxiit.zebra.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.OrderAdapter;
import cn.jinxiit.zebra.adapters.CategoryOfProductAdapter;
import cn.jinxiit.zebra.adapters.SearchBrandAdapter;
import cn.jinxiit.zebra.adapters.SearchCreateAdapter;
import cn.jinxiit.zebra.adapters.SearchGoodsAdapter;
import cn.jinxiit.zebra.beans.BrandBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.OrderUpBean;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class SearchActivity extends BaseActivity implements View.OnLayoutChangeListener
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.ll_new)
    LinearLayout mLlNew;
    @BindView(R.id.cl_root)
    View mViewRoot;

    private SearchGoodsAdapter mSearchGoodsAdapter;
    private SearchCreateAdapter mSearchCreateAdapter;
    private SearchBrandAdapter mSearchBrandAdapter;
    private OrderAdapter mOrderAdapter;
    private CategoryOfProductAdapter mCategoryOfProductAdapter;

    private String mType;
    private String mUpcCode;

    private MyApp myApp;
    private int mFrom = 0;
    private Gson mGson = new Gson();

    private static final int REQUEST_CODE_UPDA_GOODS_INFO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onDestroy()
    {
        setResult(0);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDA_GOODS_INFO && resultCode == 1)
        {
            if (data != null)
            {
                GoodsBean goodsBean = data.getParcelableExtra(MyConstant.STR_BEAN);
                if (goodsBean != null)
                {
                    mSearchGoodsAdapter.updateItemData(goodsBean);
                }
            }
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
    {
        int keyHeight = WindowUtils.dip2px(this, 150);
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight))
        {
            //软件盘开启
            mLlNew.setVisibility(View.GONE);
            if (mSearchCreateAdapter != null)
            {
                mSearchCreateAdapter.notifyDataSetChanged();
            }
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight))
        {
            //软件盘消失
            if (mSearchCreateAdapter != null)
            {
                mLlNew.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick({R.id.btn_new, R.id.iv_back, R.id.btn_search})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_search:
                doSearch();
                break;
            case R.id.btn_new:
                MyActivityUtils.skipActivity(this, UpdateOrAddGoodsActivity.class);
                break;
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();

        Intent intent = getIntent();
        String upc = intent.getStringExtra(MyConstant.STR_UPC_CODE);
        if (!TextUtils.isEmpty(upc))
        {
            mEtSearch.setText(upc);
            mSmartRefreshLayout.autoRefresh();
        }
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        Intent intent = getIntent();
        mType = intent.getStringExtra(MyConstant.STR_TYPE);
    }

    private void initListener()
    {
        mEtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                doSearch();
                return true;
            }
            return false;
        });

        if (mOrderAdapter != null){
            mOrderAdapter.setmOnRecyclerViewPrinterOrderClick((view, position) -> {
                mOrderAdapter.clearDataList();
                doSearch();
            });
        }

        if (mSearchCreateAdapter != null)
        {
            mSearchCreateAdapter.setOnRecyclerViewItemClickListener((view, position) -> {

                ProductOwnerDataBean productOwnerDataBean = mSearchCreateAdapter.getItemData(position);
                if (productOwnerDataBean != null)
                {
                    httpCheckProductExist(productOwnerDataBean);
                }
            });
        }

        if (mSearchGoodsAdapter != null)
        {
            mSearchGoodsAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
                GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
                Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
            });

            mSearchGoodsAdapter.setUpdatePriceAndStockClickListener((view, position) -> {
                GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
                if (goodsBean != null)
                {
                    Intent intent = new Intent(mContext, UpdateInventoryPriceAllPlatformActivity.class);
                    intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                    startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
                }
            });

            mSearchGoodsAdapter.setmOnRecyclerViewPlatformClickListener((view, position, platform) -> {
                GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
                if (goodsBean != null)
                {
                    Intent intent = new Intent(mContext, UpdateInventoryPriceOnePlatformActivity.class);
                    intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                    intent.putExtra(MyConstant.STR_PLATFORM, platform);
                    startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
                }
            });
        }

        if (mSearchBrandAdapter != null)
        {
            mSearchBrandAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
                BrandBean brandBean = mSearchBrandAdapter.getItemData(position);
                if (brandBean != null)
                {
                    Intent intent = getIntent();
                    intent.putExtra(MyConstant.STR_BEAN, brandBean);
                    setResult(1, intent);
                    finish();
                }
            });
        }

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                if (mSearchCreateAdapter != null || mCategoryOfProductAdapter != null)
                {
                    httpGetProducData();
                } else if (mSearchGoodsAdapter != null)
                {
                    mUpcCode = null;
                    httpGetGoodsOfStore();
                } else if (mSearchBrandAdapter != null)
                {
                    httpGetSearchBrand();
                } else if (mOrderAdapter != null)
                {
                    httpGetSearchOrder();
                }
                refreshLayout.finishLoadMore(5000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                if (mSearchCreateAdapter != null)
                {
                    mSearchCreateAdapter.clearDataList();
                    httpGetProducData();
                } else if (mSearchGoodsAdapter != null)
                {
                    mSearchGoodsAdapter.clearDataList();
                    httpGetGoodsOfStore();
                } else if (mSearchBrandAdapter != null)
                {
                    mSearchBrandAdapter.clearDataList();
                    httpGetSearchBrand();
                } else if (mOrderAdapter != null)
                {
                    mOrderAdapter.clearDataList();
                    httpGetSearchOrder();
                } else if (mCategoryOfProductAdapter != null)
                {
                    mCategoryOfProductAdapter.clearDataList();
                    httpGetProducData();
                }
                refreshLayout.finishRefresh(5000);
            }
        });
        mViewRoot.addOnLayoutChangeListener(this);
    }

    private void httpGetSearchOrder()
    {
        String q = mEtSearch.getText()
                .toString()
                .trim();
        if (q.length() < 2)
        {
            MyToastUtils.error("至少输入两个字");
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.finishRefresh();
                mSmartRefreshLayout.finishLoadMore();
            }
            return;
        }

        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetStatusOrderList(mContext, myApp.mToken, store_id, null, null, null, q, String.valueOf(mFrom), new ApiResultListener()
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
                                    if (jsonObject.has(MyConstant.STR_ORDERS))
                                    {
                                        List<OrderUpBean> orderUpBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_ORDERS), new TypeToken<List<OrderUpBean>>()
                                        {
                                        }.getType());
                                        if (orderUpBeanList != null)
                                        {
                                            mOrderAdapter.addDataList(orderUpBeanList);
                                            mFrom += orderUpBeanList.size();
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
    }

    private void httpGetSearchBrand()
    {
        String q = mEtSearch.getText()
                .toString()
                .trim();
        if (q.length() < 2)
        {
            MyToastUtils.error("至少输入两个字");
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.finishRefresh();
                mSmartRefreshLayout.finishLoadMore();
            }
            return;
        }

        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoGetSearchBrand(this, myApp.mToken, q, String.valueOf(mFrom), new ApiResultListener()
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
                                if (jsonObject.has(MyConstant.STR_BRANDS))
                                {
                                    List<BrandBean> brandBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_BRANDS), new TypeToken<List<BrandBean>>()
                                    {
                                    }.getType());
                                    if (brandBeanList != null)
                                    {
                                        mSearchBrandAdapter.addDataList(brandBeanList);
                                        mFrom += brandBeanList.size();
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

    private void httpCheckProductExist(ProductOwnerDataBean productOwnerDataBean)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();

            ApiUtils.getInstance()
                    .okgoPostCheckProductExisit(this, myApp.mToken, store_id, productOwnerDataBean.getProduct_id(), new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_RESULT))
                                {
                                    boolean isUnExisit = jsonObject.getBoolean(MyConstant.STR_RESULT);
                                    if (isUnExisit)
                                    {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(MyConstant.STR_BEAN, productOwnerDataBean);
                                        MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
                                    } else
                                    {
                                        productExisitDo(productOwnerDataBean);
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
                            MyToastUtils.error("检测失败");
                        }
                    });
        }
    }

    private void productExisitDo(ProductOwnerDataBean productOwnerDataBean)
    {
        ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
        if (product != null)
        {
            ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
            if (extra != null)
            {
                String upcCode = extra.getUpcCode();
                if (!TextUtils.isEmpty(upcCode))
                {
                    RxDialogSure rxDialogSure = new RxDialogSure(this);
                    rxDialogSure.setTitle("提示");
                    rxDialogSure.setContent("该店铺已存在该标品，不可重复创建");
                    rxDialogSure.getSureView()
                            .setOnClickListener(v -> rxDialogSure.cancel());
                    rxDialogSure.show();
                    return;
                }
            }
        }

        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);
        rxDialogSureCancel.setTitle("提示");
        rxDialogSureCancel.setContent("该店铺已有类似的商品，是否继续创建？");
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MyConstant.STR_BEAN, productOwnerDataBean);
            MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
            rxDialogSureCancel.cancel();
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    private void doSearch()
    {
        mSmartRefreshLayout.autoRefresh();

        String query = mEtSearch.getText()
                .toString()
                .trim();
        if (query.length() > 1)
        {
            WindowUtils.hideInput(mContext, mEtSearch);
        }
    }

    private void httpGetGoodsOfStore()
    {
        String q = mEtSearch.getText()
                .toString()
                .trim();

        if (q.length() < 2)
        {
            MyToastUtils.error("至少输入两个字");
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.finishRefresh();
                mSmartRefreshLayout.finishLoadMore();
            }
            return;
        }

        if (myApp.mCurrentStoreOwnerBean != null)
        {
            ApiUtils.getInstance()
                    .okgoGetSearchGoodsOfStore(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), q, mUpcCode, String.valueOf(mFrom), new ApiResultListener()
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
                                if (jsonObject.has(MyConstant.STR_PRODUCTS))
                                {
                                    List<GoodsBean> goodsBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<GoodsBean>>()
                                    {
                                    }.getType());
                                    if (goodsBeanList != null && goodsBeanList.size() > 0)
                                    {
                                        mFrom += goodsBeanList.size();
                                        mSearchGoodsAdapter.addDataList(goodsBeanList);
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

    private void httpGetProducData()
    {
        String q = mEtSearch.getText()
                .toString()
                .trim();
        if (q.length() < 2)
        {
            MyToastUtils.error("至少输入两个字");
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.finishRefresh();
                mSmartRefreshLayout.finishLoadMore();
            }
            return;
        }

        ApiUtils.getInstance()
                .okgoGetProductList(this, myApp.mToken, q, null, null, mFrom, new ApiResultListener()
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
                            if (jsonObject.has(MyConstant.STR_TOTAL))
                            {
                                int total = jsonObject.getInt(MyConstant.STR_TOTAL);
                                if (mFrom == 0)
                                {
                                    if (mSearchCreateAdapter != null)
                                    {
                                        mSearchCreateAdapter.clearDataList();
                                    }
                                    if (mCategoryOfProductAdapter != null)
                                    {
                                        mCategoryOfProductAdapter.clearDataList();
                                    }
                                }
                                if (total != 0)
                                {
                                    if (jsonObject.has(MyConstant.STR_PRODUCTS))
                                    {
                                        List<ProductOwnerDataBean> productOwnerDataBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<ProductOwnerDataBean>>()
                                        {
                                        }.getType());
                                        if (productOwnerDataBeanList != null)
                                        {
                                            int size = productOwnerDataBeanList.size();
                                            if (size > 0)
                                            {
                                                if (mSearchCreateAdapter != null)
                                                {
                                                    mSearchCreateAdapter.addDataList(productOwnerDataBeanList);
                                                }
                                                if (mCategoryOfProductAdapter != null)
                                                {
                                                    mCategoryOfProductAdapter.addDataList(productOwnerDataBeanList);
                                                }
                                                mFrom += size;
                                            }
                                        }
                                    }
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

    private void initRecyclerView()
    {
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.addItemDecoration(divider);
        if (mType == null)
        {
            mSearchGoodsAdapter = new SearchGoodsAdapter(R.layout.item_search_goods, false);
            mRecyclerview.setAdapter(mSearchGoodsAdapter);
        } else if (MyConstant.STR_SEARCH_CREATE.equals(mType))
        {
            mSearchCreateAdapter = new SearchCreateAdapter();
            mRecyclerview.setAdapter(mSearchCreateAdapter);
        } else if (MyConstant.STR_SEARCH_BRAND.equals(mType))
        {
            mSearchBrandAdapter = new SearchBrandAdapter();
            mRecyclerview.setAdapter(mSearchBrandAdapter);
        } else if (MyConstant.STR_SEARCH_ORDER.equals(mType))
        {
            mOrderAdapter = new OrderAdapter(mType);
            mRecyclerview.setAdapter(mOrderAdapter);
        } else if (MyConstant.STR_SEARCH_BATCH_CREATE.equals(mType))
        {
            mCategoryOfProductAdapter = new CategoryOfProductAdapter(myApp);
            mRecyclerview.setAdapter(mCategoryOfProductAdapter);
        }
    }
}