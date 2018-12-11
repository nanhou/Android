package cn.jinxiit.zebra.activities.product.types;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import java.util.ArrayList;
import java.util.List;

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

public class Type2Activity extends BaseActivity {
    @BindView(R.id.tv_father_category)
    TextView mTvFatherCategory;
    @BindView(R.id.et_category)
    EditText mEtCategory;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;

    private MyApp myApp;

    private int mSelect = -1;

    private CategoryBean mCategoryBean;
    private CategoryBean mFatherCategoryBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initData();
    }

    private void initData() {
        myApp = (MyApp) getApplication();
        new MyToolBar(mContext, "二级分类", "保存").setOnTopMenuClickListener(v -> clickSave());

        int[] positions = getIntent().getIntArrayExtra(MyConstant.STR_POSITION);
        if (positions != null && positions.length == 2 && myApp.mMarketCategoryList != null) {
            mFatherCategoryBean = myApp.mMarketCategoryList.get(positions[0]);
            if (mFatherCategoryBean != null) {
                List<CategoryBean> children = mFatherCategoryBean.getChildren();
                if (children != null && children.size() > positions[1]) {
                    mCategoryBean = children.get(positions[1]);
                    if (mCategoryBean != null) {
                        mBtnDelete.setVisibility(View.VISIBLE);
                        ViewSetDataUtils
                                .textViewSetText(mTvFatherCategory, mFatherCategoryBean.getName());
                        ViewSetDataUtils.textViewSetText(mEtCategory, mCategoryBean.getName());
                        mEtCategory.setSelection(mEtCategory.getText().toString().length());
                        mSelect = 0;
                    }
                }
            }
        }
    }

    private void clickSave() {
        if (mSelect == -1) {
            MyToastUtils.error("请选择一级分类");
            return;
        }

        String name = mEtCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MyToastUtils.error("请输入二级分类");
            return;
        }

        if (myApp.mUser != null) {
            UserBean.ExtraBean extra = myApp.mUser.getExtra();
            if (extra != null) {
                String market_id = extra.getMarket_id();
                if (!TextUtils.isEmpty(market_id)) {
                    if (mCategoryBean == null) {
                        httpPostNewCategory(name, market_id);
                    } else {
                        httpPostUpdateCategory(name, market_id);
                    }
                }
            }
        }
    }

    private void httpPostNewCategory(String name, String market_id) {
        CategoryBean categoryBean = myApp.mMarketCategoryList.get(mSelect);
        ApiUtils.getInstance().okgoPostCreateMarketCategory(this, myApp.mToken, market_id, name,
                categoryBean.getId(), new ApiResultListener() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        CategoryBean childenCategoryBean = new Gson()
                                .fromJson(response.body(), CategoryBean.class);
                        if (childenCategoryBean != null) {
                            List<CategoryBean> children = categoryBean.getChildren();
                            if (children == null) {
                                children = new ArrayList<>();
                            }
                            children.add(childenCategoryBean);
                            categoryBean.setChildren(children);
                            MyToastUtils.success("添加成功");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        MyToastUtils.error("添加失败");
                    }
                });
    }

    private void httpPostUpdateCategory(String name, String market_id) {
        if (mCategoryBean != null) {
            ApiUtils.getInstance().okgoPostUpdateMarketCategory(this, myApp.mToken, market_id,
                    mCategoryBean.getId(), name, new ApiResultListener() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            if (response.body().contains(MyConstant.STR_OK)) {
                                mCategoryBean.setName(name);
                                MyToastUtils.success("修改成功");
                                finish();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {

                        }
                    });
        }
    }

    @OnClick({R.id.ll_father_category, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_father_category:
                if (mCategoryBean == null) {
                    clickLlFatherCategory();
                } else {
                    MyToastUtils.error("一级分类不可修改");
                }
                break;
            case R.id.btn_delete:
                clickDelete();
                break;
        }
    }

    private void clickLlFatherCategory() {
        if (myApp.mMarketCategoryList != null && myApp.mMarketCategoryList.size() > 0) {
            List<String> itemlist = new ArrayList<>();
            for (int i = 0; i < myApp.mMarketCategoryList.size(); i++) {
                itemlist.add(myApp.mMarketCategoryList.get(i).getName());
            }

            //条件选择器
            OptionsPickerView pvOptions = new OptionsPickerBuilder(this,
                    (options1, option2, options3, v) -> {
                        //返回的分别是三个级别的选中位置
                        CategoryBean categoryBean = myApp.mMarketCategoryList.get(options1);
                        if (categoryBean != null) {
                            ViewSetDataUtils
                                    .textViewSetText(mTvFatherCategory, categoryBean.getName());
                            mSelect = options1;
                        }
                    }).setTitleText("一级分类").isDialog(true).build();

            pvOptions.setPicker(itemlist, null, null);
            pvOptions.show();
        }
    }

    private void clickDelete() {
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

    private void deletType() {
        if (myApp.mUser != null) {
            UserBean.ExtraBean extra = myApp.mUser.getExtra();
            if (extra != null && mCategoryBean != null) {
                ApiUtils.getInstance()
                        .okgoDeleteMarketCategory(this, myApp.mToken, extra.getMarket_id(),
                                mCategoryBean.getId(), new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        if (response.body().contains(MyConstant.STR_OK)) {
                                            if (mFatherCategoryBean != null) {
                                                List<CategoryBean> children = mFatherCategoryBean
                                                        .getChildren();
                                                if (children != null) {
                                                    if (children.size() == 1) {
                                                        if (myApp.mMarketCategoryList != null) {
                                                            myApp.mMarketCategoryList
                                                                    .remove(mFatherCategoryBean);
                                                        }
                                                    } else {
                                                        children.remove(mCategoryBean);
                                                    }
                                                }
                                            }
                                            MyToastUtils.success("删除分类成功");
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        MyToastUtils.error("删除失败");
                                    }
                                });
            }
        }
    }
}
