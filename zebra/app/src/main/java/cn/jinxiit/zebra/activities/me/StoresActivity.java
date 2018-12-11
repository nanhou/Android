package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.gcssloop.widget.RCImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.StoresAdapter;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class StoresActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.rciv_logo)
    RCImageView mRcivLogo;
    @BindView(R.id.tv_name)
    TextView mTvName;

    private StoresAdapter mStoresAdapter;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        ButterKnife.bind(this);
        myApp = (MyApp) getApplication();

        new MyToolBar(mContext, "门店列表", "创建门店").setOnTopMenuClickListener(
                v -> MyActivityUtils.skipActivity(mContext, CreateStoreActivity.class));

        initView();
    }

    private void initView() {
        initData();
        initRecyclerView();
        initListener();
    }

    private void initData() {
        StoreOwnerBean storeOwnerBean = myApp.mCurrentStoreOwnerBean;
        if (storeOwnerBean != null) {
            StoreOwnerBean.StoreBean store = storeOwnerBean.getStore();
            if (store != null) {
                String name = store.getName();
                if (!TextUtils.isEmpty(name)) {
                    mTvName.setText(name);
                }
                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                if (extra != null) {
                    String logo = extra.getLogo();
                    if (!TextUtils.isEmpty(logo)) {
                        String url = MyConstant.URL_GET_FILE + logo + ".jpg";
                        int width = WindowUtils.dip2px(mContext, 80);
                        MyPicassoUtils.downSizeImageForUrl(width, width, url, mRcivLogo);
                    } else {
                        mRcivLogo.setImageResource(R.drawable.img_default);
                    }
                }
            }
        }
    }

    private void initRecyclerView() {
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview
                .addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mStoresAdapter = new StoresAdapter();
        mRecyclerview.setAdapter(mStoresAdapter);

        if (myApp.mStoreOwnerBeanList != null && myApp.mStoreOwnerBeanList.size() > 0) {
            mStoresAdapter.addDataList(myApp.mStoreOwnerBeanList);
        }
    }

    private void initListener() {
        mStoresAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            myApp.mCurrentStoreOwnerBean = mStoresAdapter.getItemData(position);
            ZebraUtils.getInstance().sharedPreferencesSaveString(mContext, myApp.mUser.getId(),
                    myApp.mCurrentStoreOwnerBean.getStore_id());
            initData();
            setResult(99);
            finish();
        });
    }
}
