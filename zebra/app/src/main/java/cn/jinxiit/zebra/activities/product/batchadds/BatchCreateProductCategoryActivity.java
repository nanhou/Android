package cn.jinxiit.zebra.activities.product.batchadds;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.product.SearchActivity;
import cn.jinxiit.zebra.adapters.BatchCreateLeftAdapter;
import cn.jinxiit.zebra.adapters.BatchCreateRightAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;

public class BatchCreateProductCategoryActivity extends BaseActivity
{
    @BindView(R.id.recyclerview_left)
    RecyclerView mRecyclerViewLeft;
    @BindView(R.id.recyclerview_right)
    RecyclerView mRecyclerViewRight;
    @BindView(R.id.tv_batch_num)
    TextView mTvBatchNum;

    private MyApp myApp;
    private BatchCreateLeftAdapter mBatchCreateLeftAdapter;
    private BatchCreateRightAdapter mBatchCreateRightAdapter;
    private Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_create_product);
        ButterKnife.bind(this);

        initView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume()
    {
        super.onResume();
        mTvBatchNum.setText(myApp.mBatchProductOwnerDataBeanList.size() + "");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        myApp.mBatchProductOwnerDataBeanList.clear();
        myApp = null;
        mBatchCreateLeftAdapter = null;
        mBatchCreateRightAdapter = null;
    }

    @OnClick({R.id.iv_search, R.id.rl_batch_product})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_search:
                Bundle bundle = new Bundle();
                bundle.putString(MyConstant.STR_TYPE, MyConstant.STR_SEARCH_BATCH_CREATE);
                MyActivityUtils.skipActivity(this, SearchActivity.class, bundle);
                break;
            case R.id.rl_batch_product:
                MyActivityUtils.skipActivity(mContext, BatchCreateAllProductActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (myApp.mBatchProductOwnerDataBeanList.size() != 0)
        {
            final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
            rxDialogSureCancel.setTitle("取消创建商品");
            rxDialogSureCancel.setContent("退出当前界面将清空所添加的商品，是否退出？");
            rxDialogSureCancel.getTitleView()
                    .setTextSize(17);
            rxDialogSureCancel.getContentView()
                    .setTextSize(14);
            TextView sureView = rxDialogSureCancel.getSureView();
            TextView cancelView = rxDialogSureCancel.getCancelView();
            sureView.setText("取消");
            cancelView.setText("确认退出");
            cancelView.setOnClickListener(v -> BatchCreateProductCategoryActivity.super.onBackPressed());
            sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
            rxDialogSureCancel.show();
        } else
        {
            super.onBackPressed();
        }
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();
        httpGetCategoryList("0", true);
    }

    private void initListener()
    {
        mBatchCreateLeftAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            mBatchCreateRightAdapter.clearDataList();
            CategoryBean categoryBean = mBatchCreateLeftAdapter.getCurrentData();
            httpGetCategoryList(categoryBean.getId(), false);
        });
    }

    private void httpGetCategoryList(String fatherId, boolean is1ji)
    {
        ApiUtils.getInstance()
                .okgoGetProductCategoryList(mContext, myApp.mToken, fatherId, is1ji, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        String result = response.body();
                        if ("0".equals(fatherId))
                        {
                            dataTo1Category(result);
                        } else
                        {
                            dataTo23Category(result);
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }

    private void dataTo23Category(String result)
    {
        List<CategoryBean> categoryBeanList = mGson.fromJson(result, new TypeToken<List<CategoryBean>>()
        {
        }.getType());

        if (categoryBeanList != null && categoryBeanList.size() > 0)
        {
            mBatchCreateRightAdapter.addDataList(categoryBeanList);
        }
    }

    private void dataTo1Category(String result)
    {
        List<CategoryBean> categoryBeanList = mGson.fromJson(result, new TypeToken<List<CategoryBean>>()
        {
        }.getType());
        if (categoryBeanList != null && categoryBeanList.size() > 0)
        {
            mBatchCreateLeftAdapter.addDataList(categoryBeanList);
            CategoryBean categoryBean = categoryBeanList.get(0);
            httpGetCategoryList(categoryBean.getId(), false);
        }
    }

    private void initRecyclerView()
    {
        mRecyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        mBatchCreateLeftAdapter = new BatchCreateLeftAdapter();
        mRecyclerViewLeft.setAdapter(mBatchCreateLeftAdapter);

        mRecyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        mBatchCreateRightAdapter = new BatchCreateRightAdapter();
        mRecyclerViewRight.setAdapter(mBatchCreateRightAdapter);
    }

    private void initData()
    {
        new MyToolBar(this, "批量创建", null);
        myApp = (MyApp) getApplication();
    }

}
