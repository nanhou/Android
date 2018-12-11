package cn.jinxiit.zebra.activities.product.creategoods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.AnalysisiGoodsAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class ChooseTypesActivity extends BaseActivity
{
    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.rv_type0)
    RecyclerView mRvType0;
    @BindView(R.id.rv_type1)
    RecyclerView mRvType1;
    @BindView(R.id.rv_type2)
    RecyclerView mRvType2;

    private List<CategoryBean> mDataList = new ArrayList<>();//模拟数据

    private AnalysisiGoodsAdapter mAnalysisiGoodsAdapter0;
    private AnalysisiGoodsAdapter mAnalysisiGoodsAdapter1;
    private AnalysisiGoodsAdapter mAnalysisiGoodsAdapter2;

    private boolean mIsCategoryMarket;
    private MyApp myApp;
    private Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_types);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mDataList = null;
        mGson = null;
        setResult(0);
    }

    private void initView()
    {
        initRecyclerView();
        initData();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    private void initListener()
    {
        mAnalysisiGoodsAdapter0.setOnRecyclerViewItemClickListener((view, position0) -> {

            if (!mIsCategoryMarket)
            {
                CategoryBean categoryBean0 = mAnalysisiGoodsAdapter0.getItemData(position0);
                if (categoryBean0 != null)
                {
                    String id0 = categoryBean0.getId();
                    if (!TextUtils.isEmpty(id0))
                    {
                        mAnalysisiGoodsAdapter1.setmCurrentPosition(-1);
                        mAnalysisiGoodsAdapter2.clearDataList();
                        httpGetProductCategory(id0, mAnalysisiGoodsAdapter1);
                    }
                    ViewSetDataUtils.textViewSetText(mTvShow, categoryBean0.getName());
                }
            } else
            {
                CategoryBean goodsTypeBean0 = mDataList.get(position0);
                if (goodsTypeBean0 != null)
                {
                    List<CategoryBean> sub_types = goodsTypeBean0.getChildren();
                    if (sub_types != null)
                    {
                        mAnalysisiGoodsAdapter1.setmCurrentPosition(-1);
                        mAnalysisiGoodsAdapter1.setDataList(sub_types);
                        mAnalysisiGoodsAdapter2.clearDataList();
                    }
                    ViewSetDataUtils.textViewSetText(mTvShow, goodsTypeBean0.getName());
                }
            }
        });

        mAnalysisiGoodsAdapter1.setOnRecyclerViewItemClickListener((view, position1) -> {

            if (!mIsCategoryMarket)
            {
                CategoryBean categoryBean0 = mAnalysisiGoodsAdapter0.getItemData(mAnalysisiGoodsAdapter0.getmCurrentPosition());
                CategoryBean categoryBean1 = mAnalysisiGoodsAdapter1.getItemData(position1);
                if (categoryBean1 != null && categoryBean0 != null)
                {
                    String id1 = categoryBean1.getId();
                    if (!TextUtils.isEmpty(id1))
                    {
                        mAnalysisiGoodsAdapter2.setmCurrentPosition(-1);
                        httpGetProductCategory(id1, mAnalysisiGoodsAdapter2);
                    }
                    ViewSetDataUtils.textViewSetText(mTvShow, categoryBean0.getName());
                    mTvShow.append(">");
                    if (!TextUtils.isEmpty(categoryBean1.getName()))
                    {
                        mTvShow.append(categoryBean1.getName());
                    }
                }
            } else
            {
                int position0 = mAnalysisiGoodsAdapter0.getmCurrentPosition();
                CategoryBean goodsTypeBean0 = mDataList.get(position0);

                CategoryBean goodsTypeBean1 = goodsTypeBean0.getChildren()
                        .get(position1);

                List<CategoryBean> sub_types = goodsTypeBean1.getChildren();
                if (sub_types != null)
                {
                    mAnalysisiGoodsAdapter2.setmCurrentPosition(-1);
                    mAnalysisiGoodsAdapter2.setDataList(sub_types);
                }

                ViewSetDataUtils.textViewSetText(mTvShow, goodsTypeBean0.getName());
                mTvShow.append(">");
                mTvShow.append(goodsTypeBean1.getName());
            }
        });

        mAnalysisiGoodsAdapter2.setOnRecyclerViewItemClickListener((view, position2) -> {

            if (!mIsCategoryMarket)
            {
                CategoryBean categoryBean0 = mAnalysisiGoodsAdapter0.getItemData(mAnalysisiGoodsAdapter0.getmCurrentPosition());
                CategoryBean categoryBean1 = mAnalysisiGoodsAdapter1.getItemData(mAnalysisiGoodsAdapter1.getmCurrentPosition());
                CategoryBean categoryBean2 = mAnalysisiGoodsAdapter2.getItemData(position2);

                if (categoryBean0 != null && categoryBean1 != null && categoryBean2 != null)
                {
                    String name0 = categoryBean0.getName();
                    String name1 = categoryBean1.getName();
                    String name2 = categoryBean2.getName();
                    if (!TextUtils.isEmpty(name0) && !TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2))
                    {
                        mTvShow.setText(name0 + ">" + name1 + ">" + name2);
                    }
                }
            } else
            {
                int position0 = mAnalysisiGoodsAdapter0.getmCurrentPosition();
                int position1 = mAnalysisiGoodsAdapter1.getmCurrentPosition();
                CategoryBean goodsTypeBean0 = mDataList.get(position0);
                ViewSetDataUtils.textViewSetText(mTvShow, goodsTypeBean0.getName());
                CategoryBean goodsTypeBean1 = goodsTypeBean0.getChildren()
                        .get(position1);
                mTvShow.append(">");
                mTvShow.append(goodsTypeBean1.getName());
                CategoryBean goodsTypeBean2 = goodsTypeBean1.getChildren()
                        .get(position2);
                mTvShow.append(">");
                mTvShow.append(goodsTypeBean2.getName());
            }
        });
    }

    private void initRecyclerView()
    {
        mRvType0.setLayoutManager(new LinearLayoutManager(this));
        mAnalysisiGoodsAdapter0 = new AnalysisiGoodsAdapter();
        mRvType0.setAdapter(mAnalysisiGoodsAdapter0);

        mRvType1.setLayoutManager(new LinearLayoutManager(this));
        mAnalysisiGoodsAdapter1 = new AnalysisiGoodsAdapter();
        mRvType1.setAdapter(mAnalysisiGoodsAdapter1);

        mRvType2.setLayoutManager(new LinearLayoutManager(this));
        mAnalysisiGoodsAdapter2 = new AnalysisiGoodsAdapter();
        mRvType2.setAdapter(mAnalysisiGoodsAdapter2);

//        mAnalysisiGoodsAdapter0.setDataList(mDataList);
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();

        mIsCategoryMarket = getIntent().getBooleanExtra(MyConstant.STR_IS, true);
        if (mIsCategoryMarket && myApp.mMarketCategoryList != null)
        {
            mDataList = myApp.mMarketCategoryList;
            mAnalysisiGoodsAdapter0.setDataList(mDataList);
            new MyToolBar(this, "选择店内分类", "确定").setOnTopMenuClickListener(v -> clickConfirm());
        } else if (!mIsCategoryMarket)
        {
            new MyToolBar(this, "选择商品分类", "确定").setOnTopMenuClickListener(v -> clickConfirm());
            httpGetProductCategory("0", mAnalysisiGoodsAdapter0);
        }
    }

    private void httpGetProductCategory(String fatherId, AnalysisiGoodsAdapter adapter)
    {
        ApiUtils.getInstance()
                .okgoGetProductCategoryList(this, myApp.mToken, fatherId, true, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        List<CategoryBean> categoryBeanList = mGson.fromJson(response.body(), new TypeToken<List<CategoryBean>>()
                        {
                        }.getType());
                        if (categoryBeanList != null && categoryBeanList.size() > 0)
                        {
                            adapter.setDataList(categoryBeanList);
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }

    private void clickConfirm()
    {
        if (mIsCategoryMarket)
        {
            int position = mAnalysisiGoodsAdapter1.getmCurrentPosition();
            if (position == -1)
            {
                MyToastUtils.error("请选择正确的二级分类");
                return;
            }
            CategoryBean categoryBean = mAnalysisiGoodsAdapter1.getItemData(position);
            if (categoryBean != null)
            {
                String categoryId = categoryBean.getId();
                String categoryName = mTvShow.getText()
                        .toString()
                        .trim();
                if (!TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(categoryName))
                {
                    Intent intent = new Intent();
                    intent.putExtra(MyConstant.STR_CATID, categoryId);
                    intent.putExtra(MyConstant.STR_NAME, categoryName);
                    setResult(1, intent);
                    finish();
                    return;
                }
            }
            MyToastUtils.error("请选择正确的二级分类");
        }
        else
        {
            int position = mAnalysisiGoodsAdapter2.getmCurrentPosition();
            if (position == -1)
            {
                MyToastUtils.error("请选择正确的三级分类");
                return;
            }

            CategoryBean categoryBean = mAnalysisiGoodsAdapter2.getItemData(position);
            String categoryId = categoryBean.getId();
            String categoryNames = mTvShow.getText()
                    .toString()
                    .trim();
            if (!TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(categoryNames))
            {
                Intent intent = new Intent();
                intent.putExtra(MyConstant.STR_CATID, categoryId);
                intent.putExtra(MyConstant.STR_NAME, categoryNames);
                setResult(1, intent);
                finish();
                return;
            }
            MyToastUtils.error("请选择正确的商品分类");
        }
    }
}