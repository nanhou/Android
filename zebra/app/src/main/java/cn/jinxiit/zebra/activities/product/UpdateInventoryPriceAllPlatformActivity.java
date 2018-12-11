package cn.jinxiit.zebra.activities.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.fragments.products.UpdateInventoryPriceFragment;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyDailogUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class UpdateInventoryPriceAllPlatformActivity extends BaseActivity {
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;
    private GoodsBean mGoodsBean;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_inventory_price_all_platform);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initData();
        initSmartTabLayout();
    }

    private void initData() {
        myApp = (MyApp) getApplication();
        Intent intent = getIntent();
        mGoodsBean = intent.getParcelableExtra(MyConstant.STR_BEAN);
        new MyToolBar(this, "修改库存与价格", "保存").setOnTopMenuClickListener(v -> backSave());
    }

    private void initSmartTabLayout() {
        if (mGoodsBean != null) {
            mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                    FragmentPagerItems.with(this).add("全部平台", UpdateInventoryPriceFragment.class,
                            new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_All)
                                    .putParcelable(MyConstant.STR_BEAN, mGoodsBean).get())
                            .add("单个平台", UpdateInventoryPriceFragment.class,
                                    new Bundler().putString(MyConstant.STR_TYPE, MyConstant.STR_ONE)
                                            .putParcelable(MyConstant.STR_BEAN, mGoodsBean).get())
                            .create());
            mViewPager.setAdapter(mFragmentPagerItemAdapter);
            mViewPagerTab.setViewPager(mViewPager);
        }
    }

    private void backSave() {
        UpdateInventoryPriceFragment updateInventoryPriceFragment = (UpdateInventoryPriceFragment) mFragmentPagerItemAdapter
                .getPage(mViewPager.getCurrentItem());
        List<String> updatePriceAndStockList = updateInventoryPriceFragment.updatePriceAndStock();
        if (updatePriceAndStockList != null) {
            clickSure(updatePriceAndStockList);
        }
    }

    @SuppressLint("DefaultLocale")
    private void clickSure(List<String> updatePriceAndStockList) {
        if (updatePriceAndStockList != null && myApp.mCurrentStoreOwnerBean != null && mGoodsBean != null) {

            String price = null;
            String stock = null;
            String cost_price = null;
            String purchase_price = null;
            String jd_price = null;
            String jd_stock = null;
            String meituan_price = null;
            String meituan_stock = null;
            String eleme_price = null;
            String eleme_stock = null;
            String ebai_price = null;
            String ebai_stock = null;

            int size = updatePriceAndStockList.size();
            if (size == 4) {
                price = updatePriceAndStockList.get(0);
                price = createPrice(price);

                stock = updatePriceAndStockList.get(1);
                stock = createStock(stock, MyConstant.STR_EN_ALL);

                purchase_price = updatePriceAndStockList.get(2);
                purchase_price = createPrice(purchase_price);

                cost_price = updatePriceAndStockList.get(3);
                cost_price = createPrice(cost_price);
            } else if (size == 8) {
                jd_price = updatePriceAndStockList.get(0);
                jd_price = createPrice(jd_price);
                jd_stock = updatePriceAndStockList.get(1);
                jd_stock = createStock(jd_stock, MyConstant.STR_EN_JDDJ);

                meituan_price = updatePriceAndStockList.get(2);
                meituan_price = createPrice(meituan_price);
                meituan_stock = updatePriceAndStockList.get(3);
                meituan_stock = createStock(meituan_stock, MyConstant.STR_EN_MT);

                eleme_price = updatePriceAndStockList.get(4);
                eleme_price = createPrice(eleme_price);
                eleme_stock = updatePriceAndStockList.get(5);
                eleme_stock = createStock(eleme_stock, MyConstant.STR_EN_ELEME);

                ebai_price = updatePriceAndStockList.get(6);
                ebai_price = createPrice(ebai_price);
                ebai_stock = updatePriceAndStockList.get(7);
                ebai_stock = createStock(ebai_stock, MyConstant.STR_EN_EBAI);
            }

            MyGoodsBean myGoodsBean = new MyGoodsBean();
            myGoodsBean.setPrice(price);
            myGoodsBean.setStock(stock);
            myGoodsBean.setJd_price(jd_price);
            myGoodsBean.setJd_stock(jd_stock);
            myGoodsBean.setMeituan_price(meituan_price);
            myGoodsBean.setMeituan_stock(meituan_stock);
            myGoodsBean.setEleme_price(eleme_price);
            myGoodsBean.setEleme_stock(eleme_stock);
            myGoodsBean.setEbai_price(ebai_price);
            myGoodsBean.setEbai_stock(ebai_stock);
            myGoodsBean.setCost_price(cost_price);
            myGoodsBean.setPurchase_price(purchase_price);

            ApiUtils.getInstance().okgoPostUpdateStoreGoodsInfo(this, myApp.mToken,
                    myApp.mCurrentStoreOwnerBean.getStore_id(), mGoodsBean.getProduct().getId(),
                    myGoodsBean, new ApiResultListener() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            if (mGoodsBean != null) {
                                GoodsBean goodsBean = new Gson()
                                        .fromJson(response.body(), GoodsBean.class);
                                if (goodsBean != null) {
                                    mGoodsBean.setProduct(goodsBean.getProduct());
                                    Intent intent = new Intent();
                                    intent.putExtra(MyConstant.STR_BEAN, mGoodsBean);
                                    setResult(1, intent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            MyToastUtils.error("更新失败");
                            UpdateInventoryPriceAllPlatformActivity.super.onBackPressed();
                        }
                    });
        }
    }

    @SuppressLint("DefaultLocale")
    private String createPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            price = "0";
        } else {
            price = String.format("%.0f", Double.parseDouble(price) * 100);
        }
        return price;
    }

    private String createStock(String stock, String platform) {
        if (!checkCanUpdateStock(stock, platform)) {
            return null;
        }
        if (TextUtils.isEmpty(stock)) {
            stock = "0";
        }
        return stock;
    }

    private boolean checkCanUpdateStock(String stock, String platform) {
        if (mGoodsBean != null && stock != null && platform != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockList = product
                        .getPriceAndStock();
                if (priceAndStockList != null) {
                    if (MyConstant.STR_EN_ALL.equals(platform)) {
                        long countStock = 0;
                        for (int i = 0; i < priceAndStockList.size(); i++) {
                            GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockList
                                    .get(i);
                            if (priceAndStockBean != null) {
                                if (priceAndStockBean.getBinded() == 1) {
                                    countStock += priceAndStockList.get(i).getStock();
                                }
                            }
                        }
                        return !(countStock == Double.parseDouble(stock));
                    } else {
                        for (int i = 0; i < priceAndStockList.size(); i++) {
                            GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockList
                                    .get(i);
                            if (priceAndStockBean != null) {
                                if (platform.equals(priceAndStockBean.getThird_type())) {
                                    if (priceAndStockBean.getStock() == Double.parseDouble(stock)) {
                                        return false;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (checkCanBackPressed()) {
            super.onBackPressed();
        } else {
            RxDialogSureCancel reverseCanSureCancelDialog = MyDailogUtils
                    .createReverseCanSureCancelDialog(mContext, "提示", "信息还未保存，是否保存？", "不保存", "保存");
            reverseCanSureCancelDialog.setCancelListener(v -> {
                reverseCanSureCancelDialog.cancel();
                backSave();
            });
            reverseCanSureCancelDialog.setSureListener(v -> {
                reverseCanSureCancelDialog.cancel();
                UpdateInventoryPriceAllPlatformActivity.super.onBackPressed();
            });
            reverseCanSureCancelDialog.show();
        }
    }

    private boolean checkCanBackPressed() {
        UpdateInventoryPriceFragment updateInventoryPriceFragment = (UpdateInventoryPriceFragment) mFragmentPagerItemAdapter
                .getPage(mViewPager.getCurrentItem());
        List<String> updatePriceAndStockList = updateInventoryPriceFragment.updatePriceAndStock();
        if (updatePriceAndStockList != null && myApp.mCurrentStoreOwnerBean != null && mGoodsBean != null) {
            int size = updatePriceAndStockList.size();
            if (size == 4) {
                return !checkCanUpdateStock(updatePriceAndStockList.get(1), MyConstant.STR_EN_ALL);
            } else if (size == 8) {
                return !checkCanUpdateStock(updatePriceAndStockList.get(1),
                        MyConstant.STR_EN_JDDJ) && !checkCanUpdateStock(
                        updatePriceAndStockList.get(3),
                        MyConstant.STR_EN_MT) && !checkCanUpdateStock(
                        updatePriceAndStockList.get(5),
                        MyConstant.STR_EN_ELEME) && !checkCanUpdateStock(
                        updatePriceAndStockList.get(7), MyConstant.STR_EN_EBAI);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(0);
    }

    private void clickSave() {
        UpdateInventoryPriceFragment updateInventoryPriceFragment = (UpdateInventoryPriceFragment) mFragmentPagerItemAdapter
                .getPage(mViewPager.getCurrentItem());
        List<String> updatePriceAndStockList = updateInventoryPriceFragment.updatePriceAndStock();
        if (updatePriceAndStockList != null) {
            createUpdateInfoDialog(updatePriceAndStockList);
        } else {
            MyToastUtils.error("数据错误");
        }
    }

    private void createUpdateInfoDialog(List<String> updatePriceAndStockList) {
        if (mGoodsBean == null) {
            MyToastUtils.error("未知商品");
            return;
        }

        StringBuilder builder = new StringBuilder(1024);
        GoodsBean.ProductBean product = mGoodsBean.getProduct();
        if (product != null) {
            String name = product.getName();
            if (!TextUtils.isEmpty(name)) {
                builder.append(name);
                builder.append("\n");
            }
        }

        if (builder.length() == 0) {
            MyToastUtils.error("未知商品");
            return;
        }

        createContent(updatePriceAndStockList, builder);

        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
        rxDialogSureCancel.setTitle("确认修改价格与库存");
        rxDialogSureCancel.setContent(builder.toString());
        rxDialogSureCancel.setSureListener(v -> {
            clickSure(updatePriceAndStockList);
            rxDialogSureCancel.cancel();
        });
        rxDialogSureCancel.setCancelListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.setCanceledOnTouchOutside(true);
        rxDialogSureCancel.show();
        builder.reverse();
    }

    private void createContent(List<String> updatePriceAndStockList, StringBuilder builder) {
        int size = updatePriceAndStockList.size();
        if (size == 2) {
            createContentAll(updatePriceAndStockList, builder);
        } else if (size == 6) {
            createContentPlatform(updatePriceAndStockList, builder);
        } else {
            builder.append("数据错误！！");
        }
    }

    private void createContentAll(List<String> updatePriceAndStockList, StringBuilder builder) {
        builder.append("全部平台修改");
        builder.append("\n");
        builder.append("价格：¥");
        String allPrice = updatePriceAndStockList.get(0);
        if (TextUtils.isEmpty(allPrice)) {
            allPrice = "0.00";
        }
        builder.append(allPrice);
        builder.append("\n");
        builder.append("平分总库存: ");
        String allStock = updatePriceAndStockList.get(1);
        if (TextUtils.isEmpty(allStock)) {
            allStock = "0";
        }
        builder.append(allStock);
    }

    private void createContentPlatform(List<String> updatePriceAndStockList, StringBuilder builder) {
        if (mGoodsBean != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockList = product
                        .getPriceAndStock();
                if (priceAndStockList != null) {
                    for (int i = 0; i < priceAndStockList.size(); i++) {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockList
                                .get(i);
                        if (priceAndStockBean.getBinded() == 1) {
                            String third_type = priceAndStockBean.getThird_type();
                            switch (third_type) {
                                //0, 1
                                case MyConstant.STR_EN_JDDJ:
                                    String jdPrice = updatePriceAndStockList.get(0);
                                    if (TextUtils.isEmpty(jdPrice)) {
                                        jdPrice = "0.00";
                                    }
                                    String jdStock = updatePriceAndStockList.get(1);
                                    if (TextUtils.isEmpty(jdStock)) {
                                        jdStock = "0";
                                    }
                                    builder.append(String.format("京东\n价格: ¥%s\t库存: %s\n", jdPrice,
                                            jdStock));
                                    break;
                                //2, 3
                                case MyConstant.STR_EN_MT:
                                    String mtPrice = updatePriceAndStockList.get(2);
                                    if (TextUtils.isEmpty(mtPrice)) {
                                        mtPrice = "0.00";
                                    }
                                    String mtStock = updatePriceAndStockList.get(3);
                                    if (TextUtils.isEmpty(mtStock)) {
                                        mtStock = "0";
                                    }
                                    builder.append(String.format("美团\n价格: ¥%s\t库存: %s\n", mtPrice,
                                            mtStock));
                                    break;
                                //4, 5
                                case MyConstant.STR_EN_ELEME:
                                    String elemePrice = updatePriceAndStockList.get(4);
                                    if (TextUtils.isEmpty(elemePrice)) {
                                        elemePrice = "0.00";
                                    }
                                    String elemeStock = updatePriceAndStockList.get(5);
                                    if (TextUtils.isEmpty(elemeStock)) {
                                        elemeStock = "0";
                                    }
                                    builder.append(
                                            String.format("饿了么\n价格: ¥%s\t库存: %s\n", elemePrice,
                                                    elemeStock));
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void refreshShangjiaStatus(GoodsBean goodsBean) {
        if (mFragmentPagerItemAdapter != null) {
            for (int i = 0; i < mFragmentPagerItemAdapter.getCount(); i++) {
                UpdateInventoryPriceFragment updateInventoryPriceFragment = (UpdateInventoryPriceFragment) mFragmentPagerItemAdapter
                        .getPage(i);
                if (updateInventoryPriceFragment != null) {
                    updateInventoryPriceFragment.refreshShangjiaStatus(goodsBean);
                }
            }
        }
    }
}
