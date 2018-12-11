package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.fragments.mes.AllStoreStateFragment;
import cn.jinxiit.zebra.fragments.mes.AloneStoreStateFragment;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class BusinessStateActivity extends BaseActivity {

    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.vp_state)
    ViewPager mVpState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_state);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "营业状态", null);
        initView();
    }

    private void initView() {
        initSmartTabLayout();
    }

    private void initSmartTabLayout() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("全部平台", AllStoreStateFragment.class)
                .add("单个平台", AloneStoreStateFragment.class)
                .create());
        mVpState.setAdapter(adapter);
        mViewPagerTab.setViewPager(mVpState);
    }
}
