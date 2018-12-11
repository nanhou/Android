package cn.jinxiit.zebra.activities.product.creategoods;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.AddSpecificationAdapter;
import cn.jinxiit.zebra.toolbars.MyToolBar;

public class AddSpecificationsActivity extends BaseActivity
{

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private AddSpecificationAdapter mAddSpecificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_specifications);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick({R.id.btn_add})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_add:
                clickAdd();
                break;
        }
    }

    private void clickAdd()
    {
        if (mAddSpecificationAdapter != null)
        {
            mAddSpecificationAdapter.addData(1);
            mRecyclerView.smoothScrollToPosition(mAddSpecificationAdapter.getItemCount() - 1);
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
        mAddSpecificationAdapter = new AddSpecificationAdapter();
        mRecyclerView.setAdapter(mAddSpecificationAdapter);
    }

    private void initData()
    {
        new MyToolBar(this, "添加规格", "保存").setOnTopMenuClickListener(new MyToolBar.OnTopMenuClickListener()
        {
            @Override
            public void onMenuClick(View v)
            {

            }
        });
    }
}
