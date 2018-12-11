package cn.jinxiit.zebra.activities.product.batchadds;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.MainActivity;
import cn.jinxiit.zebra.adapters.BatchCreateProductListAdapter;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class BatchCreateAllProductActivity extends BaseActivity
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private BatchCreateProductListAdapter mBatchCreateProductListAdapter;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_create_all_product);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.btn_confirm})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_confirm:
                try
                {
                    clickConfirm();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void clickConfirm() throws JSONException
    {
        if (mBatchCreateProductListAdapter != null)
        {
            List<ProductOwnerDataBean> dataList = mBatchCreateProductListAdapter.getDataList();
            if (dataList != null && dataList.size() > 0)
            {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < dataList.size(); i++)
                {
                    JSONObject jsonObject = new JSONObject();
                    ProductOwnerDataBean productOwnerDataBean = dataList.get(i);
                    ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
                    if (product != null)
                    {
                        jsonObject.put(MyConstant.STR_ID, product.getId());
                        ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                        if (extra != null)
                        {
                            jsonObject.put(MyConstant.STR_PRICE, extra.getPrice());
                        }
                        jsonArray.put(jsonObject);
                    }
                }
                if (jsonArray.length() > 0 && myApp.mCurrentStoreOwnerBean != null)
                {
                    ApiUtils.getInstance()
                            .okgoPostBatchAddGoods(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), jsonArray.toString(), new ApiResultListener()
                            {
                                @Override
                                public void onSuccess(Response<String> response)
                                {
                                    if (response.body()
                                            .contains(MyConstant.STR_OK))
                                    {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(MyConstant.STR_POSITION, 1);
                                        MyActivityUtils.skipActivityAndFinishAll(mContext, MainActivity.class, bundle);
                                    }
                                }

                                @Override
                                public void onError(Response<String> response)
                                {
                                    MyToastUtils.error("批量创建失败");
                                }
                            });
                }
            }
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(this);
        mRecyclerView.addItemDecoration(divider);
        mBatchCreateProductListAdapter = new BatchCreateProductListAdapter(myApp.mBatchProductOwnerDataBeanList);
        mRecyclerView.setAdapter(mBatchCreateProductListAdapter);
    }

    private void initData()
    {
        new MyToolBar(this, "批量发布商品", null);
        myApp = (MyApp) getApplication();
    }
}
