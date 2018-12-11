package cn.jinxiit.zebra.activities.product.types;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class Type1Activity extends BaseActivity
{
    @BindView(R.id.et_category)
    EditText mEtCategory;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;

    private MyApp myApp;

    private CategoryBean mCategoryBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type1);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick(R.id.btn_delete)
    public void onViewClicked()
    {
        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
        rxDialogSureCancel.getTitleView().setTextSize(17);
        rxDialogSureCancel.getContentView().setTextSize(14);
        rxDialogSureCancel.setTitle("是否删除该分类");
        rxDialogSureCancel.setContent("如果该分类下存在商品，应先移除商品后才能删除分类");
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            rxDialogSureCancel.cancel();
            deletType();
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    private void deletType()
    {
        if (myApp.mUser != null)
        {
            UserBean.ExtraBean extra = myApp.mUser.getExtra();
            if (extra != null && mCategoryBean != null)
            {
                ApiUtils.getInstance().okgoDeleteMarketCategory(this, myApp.mToken, extra.getMarket_id(), mCategoryBean.getId(), new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        if (response.body().contains(MyConstant.STR_OK))
                        {
                            myApp.mMarketCategoryList.remove(mCategoryBean);
                            MyToastUtils.success("删除分类成功");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        MyToastUtils.error("删除失败");
                    }
                });
            }
        }
    }

    private void initView()
    {
        initData();
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        new MyToolBar(mContext, "一级分类", "保存").setOnTopMenuClickListener(v -> clickSave());

        int[] positions = getIntent().getIntArrayExtra(MyConstant.STR_POSITION);

        if (positions != null && positions.length == 1 && myApp.mMarketCategoryList != null)
        {
            mCategoryBean = myApp.mMarketCategoryList.get(positions[0]);
            if (mCategoryBean != null)
            {
                ViewSetDataUtils.textViewSetText(mEtCategory, mCategoryBean.getName());
                mEtCategory.setSelection(mEtCategory.getText()
                        .toString()
                        .length());
                mBtnDelete.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clickSave()
    {
        String name = mEtCategory.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(name))
        {
            MyToastUtils.error("请输入一级分类");
            return;
        }

        if (myApp != null)
        {
            if (myApp.mUser != null)
            {
                UserBean.ExtraBean extra = myApp.mUser.getExtra();
                if (extra != null)
                {
                    String market_id = extra.getMarket_id();
                    if (!TextUtils.isEmpty(market_id))
                    {
                        if (mCategoryBean == null)
                        {
                            httpPostNewCategory(name, market_id);
                        } else
                        {
                            httpPostUpdateCategory(name, market_id);
                        }
                    }
                }
            }
        }
    }

    private void httpPostUpdateCategory(String name, String market_id)
    {
        if (mCategoryBean != null)
        {
            ApiUtils.getInstance()
                    .okgoPostUpdateMarketCategory(this, myApp.mToken, market_id, mCategoryBean.getId(), name, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            if (response.body()
                                    .contains(MyConstant.STR_OK))
                            {
                                MyToastUtils.success("修改成功");
                                mCategoryBean.setName(name);
                                finish();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {

                        }
                    });
        }
    }

    private void httpPostNewCategory(String name, String market_id)
    {
        ApiUtils.getInstance()
                .okgoPostCreateMarketCategory(this, myApp.mToken, market_id, name, null, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        CategoryBean categoryBean = new Gson().fromJson(response.body(), CategoryBean.class);
                        if (categoryBean != null && myApp.mMarketCategoryList != null)
                        {
                            myApp.mMarketCategoryList.add(categoryBean);
                            MyToastUtils.success("添加成功");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        MyToastUtils.error("添加失败");
                    }
                });
    }
}
