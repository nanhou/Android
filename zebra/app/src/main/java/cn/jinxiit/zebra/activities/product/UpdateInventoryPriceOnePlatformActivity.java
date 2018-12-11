package cn.jinxiit.zebra.activities.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.components.UpdatePriceAndInventoryComponent;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyDailogUtils;

public class UpdateInventoryPriceOnePlatformActivity extends BaseActivity {
    @BindView(R.id.view0)
    View mView0;

    private GoodsBean mGoodsBean;
    private String mPlatform;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponent;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_inventory_price_one_platform);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initData();
    }

    private void initData() {
        myApp = (MyApp) getApplication();
        Intent intent = getIntent();
        mGoodsBean = intent.getParcelableExtra(MyConstant.STR_BEAN);
        mPlatform = intent.getStringExtra(MyConstant.STR_PLATFORM);
        if (!TextUtils.isEmpty(mPlatform)) {
            new MyToolBar(this, "修改平台库存价格", "保存").setOnTopMenuClickListener(v -> clickSave());
        }
        mUpdatePriceAndInventoryComponent = new UpdatePriceAndInventoryComponent(mView0, mGoodsBean,
                mPlatform);
    }

    private void clickSave() {
        if (mUpdatePriceAndInventoryComponent != null && !TextUtils.isEmpty(mPlatform)) {
            List<String> updatePriceAndStockList = mUpdatePriceAndInventoryComponent
                    .updatePriceAndStock();
            if (updatePriceAndStockList != null && updatePriceAndStockList.size() == 2) {
                String jd_price = null;
                String jd_stock = null;
                String meituan_price = null;
                String meituan_stock = null;
                String eleme_price = null;
                String eleme_stock = null;
                String ebai_price = null;
                String ebai_stock = null;

                switch (mPlatform) {
                    case MyConstant.STR_EN_JDDJ:
                        jd_price = updatePriceAndStockList.get(0);
                        jd_price = createPrice(jd_price);
                        jd_stock = updatePriceAndStockList.get(1);
                        jd_stock = createStock(jd_stock);
                        break;
                    case MyConstant.STR_EN_MT:
                        meituan_price = updatePriceAndStockList.get(0);
                        meituan_price = createPrice(meituan_price);
                        meituan_stock = updatePriceAndStockList.get(1);
                        meituan_stock = createStock(meituan_stock);
                        break;
                    case MyConstant.STR_EN_ELEME:
                        eleme_price = updatePriceAndStockList.get(0);
                        eleme_price = createPrice(eleme_price);
                        eleme_stock = updatePriceAndStockList.get(1);
                        eleme_stock = createStock(eleme_stock);
                        break;
                    case MyConstant.STR_EN_EBAI:
                        ebai_price = updatePriceAndStockList.get(0);
                        ebai_price = createPrice(ebai_price);
                        ebai_stock = updatePriceAndStockList.get(1);
                        ebai_stock = createStock(ebai_stock);
                        break;
                }
                MyGoodsBean myGoodsBean = new MyGoodsBean();
                myGoodsBean.setJd_price(jd_price);
                myGoodsBean.setJd_stock(jd_stock);
                myGoodsBean.setMeituan_price(meituan_price);
                myGoodsBean.setMeituan_stock(meituan_stock);
                myGoodsBean.setEleme_price(eleme_price);
                myGoodsBean.setEleme_stock(eleme_stock);
                myGoodsBean.setEbai_price(ebai_price);
                myGoodsBean.setEbai_stock(ebai_stock);

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
                                        UpdateInventoryPriceOnePlatformActivity.super
                                                .onBackPressed();
                                    }
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                UpdateInventoryPriceOnePlatformActivity.super.onBackPressed();
                            }
                        });

            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String createPrice(String price) {
        if (!checkCanUpdateStackAndPrice(null, price)) {
            return null;
        }
        if (TextUtils.isEmpty(price)) {
            price = "0";
        } else {
            price = String.format("%.0f", Double.parseDouble(price) * 100);
        }
        return price;
    }

    private String createStock(String stock) {
        if (!checkCanUpdateStackAndPrice(stock, null)) {
            return null;
        }
        if (TextUtils.isEmpty(stock)) {
            stock = "0";
        }
        return stock;
    }

    private boolean checkCanUpdateStackAndPrice(String stock, String price) {
        if (mGoodsBean != null && mPlatform != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockList = product
                        .getPriceAndStock();
                if (priceAndStockList != null) {
                    GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = null;
                    for (int i = 0; i < priceAndStockList.size(); i++) {
                        priceAndStockBean = priceAndStockList.get(i);
                        if (mPlatform.equals(priceAndStockBean.getThird_type())) {
                            break;
                        }
                        priceAndStockBean = null;
                    }
                    if (priceAndStockBean != null) {
                        if (stock != null) {
                            return priceAndStockBean.getStock() != Double.parseDouble(stock);
                        } else if (price != null) {
                            return priceAndStockBean.getPrice() != Double.parseDouble(price) * 100;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (backIsShowDialog()) {
            RxDialogSureCancel reverseCanSureCancelDialog = MyDailogUtils
                    .createReverseCanSureCancelDialog(mContext, "提示", "信息还未保存，是否保存？", "不保存", "保存");
            reverseCanSureCancelDialog.setCancelListener(v -> {
                reverseCanSureCancelDialog.cancel();
                clickSave();
            });
            reverseCanSureCancelDialog.setSureListener(v -> {
                reverseCanSureCancelDialog.cancel();
                UpdateInventoryPriceOnePlatformActivity.super.onBackPressed();
            });
            reverseCanSureCancelDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean backIsShowDialog() {
        if (mUpdatePriceAndInventoryComponent != null && !TextUtils.isEmpty(mPlatform)) {
            List<String> updatePriceAndStockList = mUpdatePriceAndInventoryComponent
                    .updatePriceAndStock();
            if (updatePriceAndStockList != null && updatePriceAndStockList.size() == 2) {
                return checkCanUpdateStackAndPrice(null,
                        updatePriceAndStockList.get(0)) || checkCanUpdateStackAndPrice(
                        updatePriceAndStockList.get(1), null);
            }
        }
        return false;
    }
}