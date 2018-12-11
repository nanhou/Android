package cn.jinxiit.zebra.fragments.stas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.adapters.GoodsTop5Adapter;
import cn.jinxiit.zebra.beans.ProductStatusComparedBean;
import cn.jinxiit.zebra.components.ChartComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class GoodsListFragment extends Fragment
{
    Unbinder unbinder;
    private View mViewChart0;
    private Spinner mSpinnerSort;
    private TextView mTvPrice;
    private TextView mTvPriceCompared;
    private TextView mTvCount;
    private TextView mTvCountCompared;
    @BindView(R.id.lv_goodstop5)
    ListView mLvGoodstop5;

    private Activity mContext;
    private String mType;
    private MyApp myApp;
    private GoodsTop5Adapter mGoodsTop5Adapter;
    private ProductStatusComparedBean mProductStatusComparedBean;

    private static final String[] mLineCharTitles1 = {"销售数量", "销售额"};
    private static final String[] mLineCharTitlesUnit1 = {"份", "元"};

    private ChartComponent mChartComponent1;

    private String mPlatform = MyConstant.STR_ALLSTATIS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_goods, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();
        if (mContext != null)
        {
            myApp = (MyApp) mContext.getApplication();
        }
        if (getArguments() != null)
        {
            mType = getArguments().getString(MyConstant.STR_TYPE);
        }
        initView();
        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initView()
    {
        initListView();
        initSpinner();
        initViewChart0();

        initListener();

        httpGetProductStatus();
    }

    private void initListener()
    {
        mSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (mProductStatusComparedBean != null)
                {
                    List<ProductStatusComparedBean.ProductStatusBean> productStats;
                    boolean isPrice;
                    if (position == 0)
                    {
                        productStats = mProductStatusComparedBean.getProductStatsByCount();
                        isPrice = false;
                    } else
                    {
                        productStats = mProductStatusComparedBean.getProductStatsByPrice();
                        isPrice = true;
                    }
                    mGoodsTop5Adapter.setmDataList(productStats, isPrice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });
    }

    private void httpGetProductStatus()
    {
        if (myApp.mCurrentStoreOwnerBean != null && mType != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetStoreProductStatus(mContext, myApp.mToken, mPlatform, store_id, mType, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                mProductStatusComparedBean = new Gson().fromJson(response.body(), ProductStatusComparedBean.class);
                                if (mProductStatusComparedBean != null)
                                {
                                    if (!MyConstant.STR_YESTERDAY.equals(mType))
                                    {
                                        dataSetChart(mProductStatusComparedBean);
                                    }
                                    dataSetOtherOfChart(mProductStatusComparedBean);
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {}
                        });
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void dataSetOtherOfChart(ProductStatusComparedBean productStatusComparedBean)
    {
        if (productStatusComparedBean == null)
        {
            return;
        }
        ZebraUtils zebraUtils = ZebraUtils.getInstance();
        int price = productStatusComparedBean.getPrice();
        int count = productStatusComparedBean.getCount();
        int prePrice = productStatusComparedBean.getPrePrice();
        int preCount = productStatusComparedBean.getPreCount();

        mTvPrice.setText(String.format("%.2f", price * 0.01));
        zebraUtils.jsjComparedProportion(price, prePrice, mTvPriceCompared);
        mTvCount.setText(count + "");
        zebraUtils.jsjComparedProportion(count, preCount, mTvCountCompared);

        List<ProductStatusComparedBean.ProductStatusBean> productStatsByCountList = productStatusComparedBean.getProductStatsByCount();
        List<ProductStatusComparedBean.ProductStatusBean> productStatsByPriceList = productStatusComparedBean.getProductStatsByPrice();
        if (productStatsByCountList != null && productStatsByPriceList != null && productStatsByCountList.size() > 0 && mGoodsTop5Adapter != null)
        {
            boolean isPrice = false;
            if (mSpinnerSort != null)
            {
                if (mSpinnerSort.getSelectedItemPosition() != 0)
                {
                    isPrice = true;
                    mGoodsTop5Adapter.setmDataList(productStatsByPriceList, isPrice);
                    return;
                }
            }
            mGoodsTop5Adapter.setmDataList(productStatsByCountList, isPrice);
        }
    }

    private void dataSetChart(ProductStatusComparedBean productStatusComparedBean)
    {
        ZebraUtils.getInstance()
                .productdataSetChart(mChartComponent1, mPlatform, mLineCharTitles1, mLineCharTitlesUnit1, productStatusComparedBean);
    }

    private void initSpinner()
    {
        List<String> list_Sort = new ArrayList<>();
        list_Sort.add("按销量排序");
        list_Sort.add("按销售额排序");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.layout_textview0, list_Sort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSort.setAdapter(adapter);
    }

    private void initListView()
    {
        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_goods_header, null);
        mViewChart0 = headerView.findViewById(R.id.view_chart0);
        mTvPrice = headerView.findViewById(R.id.tv_price);
        mTvPriceCompared = headerView.findViewById(R.id.tv_price_compared);
        mTvCount = headerView.findViewById(R.id.tv_count);
        mTvCountCompared = headerView.findViewById(R.id.tv_count_compared);
        mSpinnerSort = headerView.findViewById(R.id.spinner_sort);
        LinearLayout mllAnalysis_Goods = headerView.findViewById(R.id.ll_analysis_goods);
        if (mType.equals(MyConstant.STR_YESTERDAY))
        {
            mllAnalysis_Goods.setVisibility(View.GONE);
        }
        mLvGoodstop5.addHeaderView(headerView);

        mGoodsTop5Adapter = new GoodsTop5Adapter(mContext);
        mLvGoodstop5.setAdapter(mGoodsTop5Adapter);
    }

    private void initViewChart0()
    {
        List<String> titles = new ArrayList<>(Arrays.asList(mLineCharTitles1));
        mChartComponent1 = new ChartComponent(mViewChart0, titles);
    }

    public void refreshData(String platform)
    {
        if (mPlatform.equals(platform))
        {
            return;
        }
        this.mPlatform = platform;
        mGoodsTop5Adapter.clearDataList();
        httpGetProductStatus();
    }
}
