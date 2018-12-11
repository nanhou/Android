package cn.jinxiit.zebra.activities.product.creategoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.AddAttrAdapter;
import cn.jinxiit.zebra.beans.AttributeBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class AddAttrActivity extends BaseActivity
{

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private AddAttrAdapter mAddAttrAdapter;

    private ArrayList<AttributeBean> mAttributeBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attr);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        setResult(0);
    }

    @OnClick({R.id.btn_new, R.id.btn_module})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_new:
                clickNew();
                break;
            case R.id.btn_module:
                break;
        }
    }

    private void clickNew()
    {
        if (mAddAttrAdapter != null)
        {
            mAddAttrAdapter.addData(new AttributeBean());
            mRecyclerView.smoothScrollToPosition(mAddAttrAdapter.getItemCount() - 1);
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
        mAddAttrAdapter = new AddAttrAdapter();
        mRecyclerView.setAdapter(mAddAttrAdapter);
        if (mAttributeBeanArrayList != null)
        {
            mAddAttrAdapter.addDataList(mAttributeBeanArrayList);
        } else
        {
            mAddAttrAdapter.addData(new AttributeBean());
        }
    }

    private void initData()
    {
        new MyToolBar(this, "角标", "保存").setOnTopMenuClickListener(v -> clickSave());

        mAttributeBeanArrayList = getIntent().getParcelableArrayListExtra(MyConstant.STR_BEAN_LIST);
    }

    private void clickSave()
    {
        ArrayList<AttributeBean> attributeBeanList = mAddAttrAdapter.getDataList();
        if (attributeBeanList != null && attributeBeanList.size() > 0)
        {
            for (int i = 0; i < attributeBeanList.size(); i++)
            {
                AttributeBean attributeBean = attributeBeanList.get(i);
                List<String> att_label = attributeBean.getAtt_label();
                if (TextUtils.isEmpty(attributeBean.getAtt_name()) || att_label == null)
                {
                    MyToastUtils.error("请补全信息");
                    return;
                } else
                {
                    if (att_label.size() == 3)
                    {
                        if (TextUtils.isEmpty(att_label.get(0)) && TextUtils.isEmpty(att_label.get(1)) && TextUtils.isEmpty(att_label.get(2)))
                        {
                            MyToastUtils.error("请补全信息");
                            return;
                        }
                    }
                }
            }

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(MyConstant.STR_BEAN_LIST, attributeBeanList);
            setResult(1, intent);
            finish();
        } else
        {
            MyToastUtils.error("未发现任何属性信息");
        }
    }
}
