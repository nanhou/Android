package cn.jinxiit.zebra.activities.product.creategoods;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.AnglesAdapter;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class AnglesActivity extends BaseActivity
{
    @BindView(R.id.rv_angles)
    RecyclerView mRvAngles;

    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angles);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        initData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRvAngles.setLayoutManager(new GridLayoutManager(this, 3));
        AnglesAdapter adapter = new AnglesAdapter();
        adapter.addData(mDataList);
        mRvAngles.setAdapter(adapter);
    }

    private void initData()
    {
        new MyToolBar(this, "角标", "保存");
        mDataList = new ArrayList<>();
        for (int i = 0; i < 9; i++)
        {
            mDataList.add("夏季新款" + i);
        }
    }
}
