package cn.jinxiit.zebra.activities.product.batchupdate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.BatchUpdateStockAdapter;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class BatchUpdateStockActivity extends BaseActivity
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private BatchUpdateStockAdapter mBatchUpdateStockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_update_stock);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy()
    {
        setResult(0);
        super.onDestroy();
    }

    private void initView()
    {
        initData();
        initRecyclerView();
    }

    private void initRecyclerView()
    {
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(divider);

        ArrayList<GoodsBean> goodsBeanArrayList = getIntent().getParcelableArrayListExtra(MyConstant.STR_BEAN);
        mBatchUpdateStockAdapter = new BatchUpdateStockAdapter(goodsBeanArrayList);
        mRecyclerView.setAdapter(mBatchUpdateStockAdapter);
    }

    private void initData()
    {
        new MyToolBar(this, "自定义库存", "保存").setOnTopMenuClickListener(v -> {
            try
            {
                clickSave();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        });
    }

    private void clickSave() throws JSONException
    {
        List<GoodsBean> goodsBeanList = mBatchUpdateStockAdapter.getDataList();
        if (goodsBeanList != null)
        {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < goodsBeanList.size(); i++)
            {
                GoodsBean goodsBean = goodsBeanList.get(i);
                if (goodsBean != null)
                {
                    String product_id = goodsBean.getProduct().getId();
                    if (!TextUtils.isEmpty(product_id))
                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(MyConstant.STR_ID, product_id);
                        GoodsBean.ProductBean product = goodsBean.getProduct();
                        if (product != null)
                        {
                            List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product.getPriceAndStock();
                            if (priceAndStockBeanList != null)
                            {
                                for (int j = 0; j < priceAndStockBeanList.size(); j++)
                                {
                                    GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList.get(j);
                                    if (priceAndStockBean != null && priceAndStockBean.getBinded() == 1)
                                    {
                                        String third_type = priceAndStockBean.getThird_type();
                                        if (third_type != null)
                                        {
                                            long stock = priceAndStockBean.getStock();
                                            switch (third_type)
                                            {
                                                case MyConstant.STR_EN_JDDJ:
                                                    jsonObject.put(MyConstant.STR_EN_JDDJ_STOCK, stock);
                                                    break;
                                                case MyConstant.STR_EN_MT:
                                                    jsonObject.put(MyConstant.STR_EN_MT_STOCK, stock);
                                                    break;
                                                case MyConstant.STR_EN_ELEME:
                                                    jsonObject.put(MyConstant.STR_EN_ELEME_STOCK, stock);
                                                    break;
                                                case MyConstant.STR_EN_EBAI:
                                                    jsonObject.put(MyConstant.STR_EN_EBAI_STOCK, stock);
                                                    break;
                                            }
                                        }
                                    }
                                }
                                jsonArray.put(jsonObject);
                            }
                        }
                    }
                }
            }
            if (jsonArray.length() > 0)
            {
                Intent intent = new Intent();
                intent.putExtra(MyConstant.STR_JSON, jsonArray.toString());
                setResult(1, intent);
            }
        }
        onBackPressed();
    }
}
