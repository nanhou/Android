package cn.jinxiit.zebra.activities.product;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.model.ActionItem;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;
import com.vondear.rxtools.view.popupwindows.RxPopupSingleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.codescanner.ScannerCodeActivity;
import cn.jinxiit.zebra.activities.product.creategoods.AddAttrActivity;
import cn.jinxiit.zebra.activities.product.creategoods.AddSpecificationsActivity;
import cn.jinxiit.zebra.activities.product.creategoods.ChooseTypesActivity;
import cn.jinxiit.zebra.activities.product.creategoods.SellingTimeActivity;
import cn.jinxiit.zebra.adapters.GridImageAdapter;
import cn.jinxiit.zebra.adapters.ImageDefaultAdapter;
import cn.jinxiit.zebra.beans.AttributeBean;
import cn.jinxiit.zebra.beans.BrandBean;
import cn.jinxiit.zebra.beans.FileBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.LimitDateBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;
import cn.jinxiit.zebra.views.widget.FullyGridLayoutManager;

public class UpdateOrAddGoodsActivity extends BaseActivity
{
    @BindView(R.id.recyclerview_imgs_default)
    RecyclerView mRecyclerViewImgsDefault;
    @BindView(R.id.recyclerview_imgs)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.tv_upc_code)
    TextView mTvUpcCode;
    @BindView(R.id.tv_category_market)
    TextView mTvCategoryMarket;
    @BindView(R.id.et_price)
    EditText mEtPrice;
    @BindView(R.id.et_weight)
    EditText mEtWeight;
    @BindView(R.id.et_stock)
    EditText mEtStock;
    @BindView(R.id.et_pack_fee)
    EditText mEtPackFee;
    @BindView(R.id.et_min_purchase)
    EditText mEtMinPurchase;
    @BindView(R.id.et_summary)
    EditText mEtSummary;
    @BindView(R.id.tv_category_product)
    TextView mTvCategoryProduct;
    @BindView(R.id.ll_attrs)
    LinearLayout mLlAttrs;
    @BindView(R.id.ll_selling_times)
    LinearLayout mLlSellingTimes;
    @BindView(R.id.ll_place)
    LinearLayout mLlPlace;
    @BindView(R.id.ll_father_name)
    LinearLayout mLlFatherName;
    @BindView(R.id.ll_extra_summary)
    LinearLayout mLlEaxtraSummary;
    @BindView(R.id.ll_upc_code)
    LinearLayout mLlUpcCode;
    @BindView(R.id.ll_brand)
    LinearLayout mLlBrand;
    @BindView(R.id.ll_category_product)
    LinearLayout mLlCategoryProduct;
    @BindView(R.id.tv_brand)
    TextView mTvBrand;
    @BindView(R.id.et_father_name)
    EditText mEtFataherName;
    @BindView(R.id.ll_qr)
    View mLlQr;
    @BindView(R.id.tv_weight_unit)
    TextView mTvWeightUnit;
    @BindView(R.id.tv_unit)
    TextView mTvUnit;
    @BindView(R.id.et_place)
    EditText mEtPlace;
    @BindView(R.id.et_extra_summary)
    EditText mEtExtraSummary;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    private static final int REQUEST_CODE_CHOOSE_CATEGORY_MARKET = 0;
    private static final int REQUEST_CODE_ADD_ATTRS = 1;
    private static final int REQUEST_CODE_SELLING_TIME = 2;
    private static final int REQUEST_CODE_CHOOSE_CATEGORY_PRODUCT = 3;
    private static final int REQUEST_CODE_CHOOSE_BRAND = 4;

    private ArrayList<AttributeBean> mAttributeBeanList;
    private LimitDateBean mLimitDateBean;
    private String mMarketCategoryId;
    private String mProdctCategoryId;
    private List<String> mImageList;
    private BrandBean mBrandBean;

    private MyApp myApp;
    private Gson mGson = new Gson();

    //images
    private int mThemeId;
    private List<LocalMedia> mSelectList = new ArrayList<>();
    private GridImageAdapter mGridImageAdapter;
    private boolean mode = true;

    private ProductOwnerDataBean mProductOwnerDataBean;

    private boolean mIsUpdate;//是否更新数据
    private String mType;//是否审核失败的修改
    private GoodsBean mGoodsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_or_add_goods);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy()
    {
        setResult(0);
        super.onDestroy();
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MyConstant.REQUEST_CAMERA_PERMISSION)
        {
            Log.e("", grantResults[0] + "");
            if (PackageManager.PERMISSION_GRANTED == grantResults[0] && Manifest.permission.CAMERA.equals(permissions[0]))
            {
                saoyisao();
            } else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("需开启相机权限后才能使用扫码功能，去设置？")
                        .setPositiveButton("去设置", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
                            startActivity(intent);
                        })
                        .setNegativeButton("取消", (dialogInterface, i) -> {

                        })
                        .create();
                alertDialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            activityResultCamera(requestCode, data);
        } else if (requestCode == MyConstant.REQUEST_CODE_QR && resultCode == 1)
        {
            activityQR(data);
        } else if (requestCode == REQUEST_CODE_ADD_ATTRS && resultCode == 1)
        {
            if (data != null)
            {
                mAttributeBeanList = data.getParcelableArrayListExtra(MyConstant.STR_BEAN_LIST);
            }
            activityResultAttrs();
        } else if (requestCode == REQUEST_CODE_SELLING_TIME && resultCode == 1)
        {
            if (data != null)
            {
                mLimitDateBean = data.getParcelableExtra(MyConstant.STR_BEAN);
            }
            activityResultTimes();
        } else if (requestCode == REQUEST_CODE_CHOOSE_CATEGORY_MARKET && resultCode == 1)
        {
            activityResultMarketCategory(data);
        } else if (requestCode == REQUEST_CODE_CHOOSE_CATEGORY_PRODUCT && resultCode == 1)
        {
            activityResultProductCategory(data);
        } else if (requestCode == REQUEST_CODE_CHOOSE_BRAND && resultCode == 1)
        {
            activityResultBrand(data);
        }
    }

    @OnClick({R.id.tv_weight_unit, R.id.ll_brand, R.id.ll_qr, R.id.ll_category_product, R.id.tv_unit, R.id.btn_confirm, R.id.ll_selling_times, R.id.ll_attrs, R.id.ll_add_specifications, R.id.ll_category_market})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_brand:
                Bundle bundleBrand = new Bundle();
                bundleBrand.putString(MyConstant.STR_TYPE, MyConstant.STR_SEARCH_BRAND);
                MyActivityUtils.skipActivityForResult(this, SearchActivity.class, bundleBrand, REQUEST_CODE_CHOOSE_BRAND);
                break;
            case R.id.ll_qr:
                saoyisao();
                break;
            case R.id.ll_category_product:
                if (mProductOwnerDataBean == null)
                {
                    Bundle bundleProduct = new Bundle();
                    bundleProduct.putBoolean(MyConstant.STR_IS, false);
                    MyActivityUtils.skipActivityForResult(this, ChooseTypesActivity.class, bundleProduct, REQUEST_CODE_CHOOSE_CATEGORY_PRODUCT);
                }
                break;
            case R.id.tv_unit:
                clickTvUnit();
                break;
            case R.id.tv_weight_unit:
                clickTvWeightUnit();
                break;
            case R.id.btn_confirm:
                clickConfirm();
                break;
            case R.id.ll_selling_times:
                clickLlSellingTimes();
                break;
            case R.id.ll_attrs:
                clickLlAttrs();
                break;
            case R.id.ll_add_specifications:
                MyActivityUtils.skipActivity(this, AddSpecificationsActivity.class);
                break;
            case R.id.ll_category_market:
                Bundle bundleMarket = new Bundle();
                bundleMarket.putBoolean(MyConstant.STR_IS, true);
                MyActivityUtils.skipActivityForResult(this, ChooseTypesActivity.class, bundleMarket, REQUEST_CODE_CHOOSE_CATEGORY_MARKET);
                break;
        }
    }

    private void clickTvWeightUnit()
    {
        RxPopupSingleView titlePopup = new RxPopupSingleView(mContext, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.popupwindow_definition_layout);
        titlePopup.setColorItemText(ContextCompat.getColor(mContext, R.color.black));
        titlePopup.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.white));
        titlePopup.addAction(new ActionItem("  g"));
        titlePopup.addAction(new ActionItem("  kg"));
        titlePopup.setItemOnClickListener((item, position) -> {
            if (!titlePopup.getAction(position).mTitle.equals(mTvWeightUnit.getText()))
            {
                if (position >= 0)
                {
                    mTvWeightUnit.setText(titlePopup.getAction(position).mTitle);
                }
            }
        });
        titlePopup.show(mTvWeightUnit, 0);
    }

    private boolean activityQR(Intent data)
    {
        //处理扫描结果（在界面上显示）
        if (null != data)
        {
            String result = data.getStringExtra(MyConstant.STR_RESULT);
            if (result != null)
            {
                httpgetProdcutByUpcCode(result);
            }
        }
        return false;
    }

    private void httpgetProdcutByUpcCode(String upcCode)
    {
        if (NumberUtils.isInteger(upcCode))
        {
            ApiUtils.getInstance()
                    .okgoGetProductList(mContext, myApp.mToken, null, upcCode, null, 0, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_PRODUCTS))
                                {
                                    List<ProductOwnerDataBean> productOwnerDataBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<ProductOwnerDataBean>>()
                                    {
                                    }.getType());
                                    if (productOwnerDataBeanList != null)
                                    {
                                        int size = productOwnerDataBeanList.size();
                                        if (size == 1)
                                        {
                                            httpCheckProductExist(productOwnerDataBeanList.get(0));
                                        } else if (size == 0)
                                        {
                                            mLlUpcCode.setVisibility(View.VISIBLE);
                                            mTvUpcCode.setText(upcCode);
                                            mLlBrand.setVisibility(View.VISIBLE);
                                            mLlPlace.setVisibility(View.GONE);
                                            mLlFatherName.setVisibility(View.GONE);
                                            mLlEaxtraSummary.setVisibility(View.GONE);
                                            mEtName.setEnabled(true);
                                        }
                                    }
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {

                        }
                    });
        } else
        {
            MyToastUtils.error("条形码错误:" + upcCode);
        }
    }

    private void httpCheckProductExist(ProductOwnerDataBean productOwnerDataBean)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();

            ApiUtils.getInstance()
                    .okgoPostCheckProductExisit(mContext, myApp.mToken, store_id, productOwnerDataBean.getProduct_id(), new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_RESULT))
                                {
                                    boolean isUnExisit = jsonObject.getBoolean(MyConstant.STR_RESULT);
                                    if (isUnExisit)
                                    {
                                        mProductOwnerDataBean = productOwnerDataBean;
                                        if (mProductOwnerDataBean != null)
                                        {
                                            //标品  不可修改name  et price获取焦点
                                            mEtName.setEnabled(false);
                                            mEtPrice.requestFocus();

                                            mLlQr.setVisibility(View.GONE);
                                            mLlUpcCode.setVisibility(View.VISIBLE);
                                            mLlBrand.setVisibility(View.VISIBLE);
                                            mEtWeight.setFocusable(false);
                                            mTvCategoryProduct.setText("其他类型");
                                            viewSetByProductOwnerDataBean();
                                        }
                                    } else
                                    {
                                        productExisitDo(productOwnerDataBean);
                                    }
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("检测失败");
                        }
                    });
        }
    }

    private void productExisitDo(ProductOwnerDataBean productOwnerDataBean)
    {
        ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
        if (product != null)
        {
            ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
            if (extra != null)
            {
                String upcCode = extra.getUpcCode();
                if (!TextUtils.isEmpty(upcCode))
                {
                    RxDialogSure rxDialogSure = new RxDialogSure(mContext);
                    rxDialogSure.setTitle("提示");
                    rxDialogSure.setContent("该店铺已存在该标品，不可重复创建");
                    rxDialogSure.getSureView()
                            .setOnClickListener(v -> rxDialogSure.cancel());
                    rxDialogSure.show();
                    return;
                }
            }
        }

        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);
        rxDialogSureCancel.setTitle("提示");
        rxDialogSureCancel.setContent("该店铺已有类似的商品，是否继续创建？");
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MyConstant.STR_BEAN, productOwnerDataBean);
            MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
            rxDialogSureCancel.cancel();
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    private void saoyisao()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                MyToastUtils.error("使用扫一扫功能需相机权限!!");
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MyConstant.REQUEST_CAMERA_PERMISSION);
                return;
            }
        }

        Intent data = new Intent(mContext, ScannerCodeActivity.class);
        startActivityForResult(data, MyConstant.REQUEST_CODE_QR);
    }

    private void clickTvUnit()
    {
        RxPopupSingleView titlePopup = new RxPopupSingleView(mContext, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.popupwindow_definition_layout);
        titlePopup.setColorItemText(ContextCompat.getColor(mContext, R.color.black));
        titlePopup.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.white));
        //        份/个/箱/盒/袋/把/包/根/块/套/条/扎/只/组/片
        titlePopup.addAction(new ActionItem("  份"));
        titlePopup.addAction(new ActionItem("  个"));
        titlePopup.addAction(new ActionItem("  箱"));
        titlePopup.addAction(new ActionItem("  盒"));
        titlePopup.addAction(new ActionItem("  袋"));
        titlePopup.addAction(new ActionItem("  把"));
        titlePopup.addAction(new ActionItem("  包"));
        titlePopup.addAction(new ActionItem("  根"));
        titlePopup.addAction(new ActionItem("  块"));
        titlePopup.addAction(new ActionItem("  套"));
        titlePopup.addAction(new ActionItem("  条"));
        titlePopup.addAction(new ActionItem("  扎"));
        titlePopup.addAction(new ActionItem("  只"));
        titlePopup.addAction(new ActionItem("  组"));
        titlePopup.addAction(new ActionItem("  片"));
        titlePopup.setItemOnClickListener((item, position) -> {
            if (!titlePopup.getAction(position).mTitle.equals(mTvUnit.getText()))
            {
                if (position >= 0)
                {
                    mTvUnit.setText(titlePopup.getAction(position).mTitle);
                }
            }
        });
        titlePopup.show(mTvUnit, 0);
    }

    @SuppressLint("DefaultLocale")
    private void clickConfirm()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            if (mIsUpdate)
            {
                updateStoreGoods();
            } else
            {
                addStoreGoods();
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateStoreGoods()
    {
        MyGoodsBean myGoodsBean = new MyGoodsBean();
        String name = getPruductName();
        if (TextUtils.isEmpty(name))
        {
            MyToastUtils.error("请输入商品的名称");
            return;
        }
        myGoodsBean.setName(name);
        myGoodsBean.setPlace(mEtPlace.getText()
                .toString()
                .trim());
        myGoodsBean.setExtra_summary(mEtExtraSummary.getText()
                .toString()
                .trim());
        myGoodsBean.setCat_id(mMarketCategoryId);

        String strPrice = mEtPrice.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(strPrice))
        {
            MyToastUtils.error("请输入商品价格");
            return;
        }
        myGoodsBean.setPrice(String.format("%.0f", Double.parseDouble(strPrice) * 100));
        String weight = mEtWeight.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(weight))
        {
            MyToastUtils.error("请输入商品毛重");
            return;
        }
        myGoodsBean.setWeight(weight);
        myGoodsBean.setUnit(mTvUnit.getText()
                .toString()
                .replace(" ", ""));
        myGoodsBean.setWeight_unit(mTvWeightUnit.getText()
                .toString()
                .replace(" ", ""));
        String stock = mEtStock.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(stock))
        {
            stock = "0";
        }
        myGoodsBean.setStock(stock);
        String strPackFee = mEtPackFee.getText()
                .toString()
                .trim();
        if (!TextUtils.isEmpty(strPackFee))
        {
            myGoodsBean.setPack_fee(String.format("%.0f", Double.parseDouble(strPackFee) * 100));
        }
        myGoodsBean.setMin_purchase(mEtMinPurchase.getText()
                .toString()
                .trim());

        String attrsJson = null;
        if (mAttributeBeanList != null)
        {
            attrsJson = mGson.toJson(mAttributeBeanList, new TypeToken<List<AttributeBean>>()
            {
            }.getType());
        }
        myGoodsBean.setAttribute(attrsJson);

        String saleTimeJson = null;
        if (mLimitDateBean != null)
        {
            saleTimeJson = mGson.toJson(mLimitDateBean, LimitDateBean.class);
        }
        myGoodsBean.setSale_time(saleTimeJson);

        String imagesJson = null;
        if (mImageList != null && mImageList.size() > 0)
        {
            List<String> tempImage = new ArrayList<>(mImageList);

            tempImage.removeAll(Collections.singleton(""));
            tempImage.removeAll(Collections.singleton(null));

            if (tempImage.size() > 0)
            {
                imagesJson = mGson.toJson(tempImage, new TypeToken<List<String>>()
                {
                }.getType());
            }
        }
        myGoodsBean.setImages(imagesJson);

        if (MyConstant.STR_FAILED.equals(mType))
        {
            ApiUtils.getInstance()
                    .okgoPostUpdateFailedGoods(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), mGoodsBean.getProduct()
                            .getId(), myGoodsBean, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            MyToastUtils.success("重新提交审核成功");
                            setResult(1);
                            finish();
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("修改失败");
                        }
                    });
        } else
        {
            ApiUtils.getInstance()
                    .okgoPostUpdateStoreGoodsInfo(this, myApp.mToken, myApp.mCurrentStoreOwnerBean.getStore_id(), mGoodsBean.getProduct()
                            .getId(), myGoodsBean, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            GoodsBean goodsBean = mGson.fromJson(response.body(), GoodsBean.class);
                            if (goodsBean != null && mGoodsBean != null)
                            {
                                mGoodsBean.setProduct(goodsBean.getProduct());
                                Intent intent = getIntent();
                                intent.putExtra(MyConstant.STR_BEAN, mGoodsBean);
                                setResult(1, intent);
                                finish();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("修改失败");
                        }
                    });
        }
    }

    @SuppressLint("DefaultLocale")
    private void addStoreGoods()
    {
        String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
        if (!TextUtils.isEmpty(store_id))
        {
            String productId = null;
            if (mProductOwnerDataBean != null)
            {
                productId = mProductOwnerDataBean.getProduct_id();
                mProdctCategoryId = mProductOwnerDataBean.getCat_id();
            } else
            {
                if (TextUtils.isEmpty(mProdctCategoryId))
                {
                    MyToastUtils.error("请选择商品类目");
                    return;
                }
            }

            String name = getPruductName();
            if (TextUtils.isEmpty(name))
            {
                MyToastUtils.error("请输入商品名称");
                return;
            }
            String summary = mEtSummary.getText()
                    .toString()
                    .trim();

            //            if (TextUtils.isEmpty(mMarketCategoryId))
            //            {
            //                MyToastUtils.error("请选择店内分类");
            //                return;
            //            }

            String attrsJson = null;
            if (mAttributeBeanList != null)
            {
                attrsJson = mGson.toJson(mAttributeBeanList, new TypeToken<List<AttributeBean>>()
                {
                }.getType());
            }

            String saleTimeJson = null;
            if (mLimitDateBean != null)
            {
                saleTimeJson = mGson.toJson(mLimitDateBean, LimitDateBean.class);
            }

            String place = mEtPlace.getText()
                    .toString()
                    .trim();

            String min_purchase = mEtMinPurchase.getText()
                    .toString()
                    .trim();

            String priceYuan = mEtPrice.getText()
                    .toString()
                    .trim();

            if (TextUtils.isEmpty(priceYuan))
            {
                MyToastUtils.error("请输入商品价格");
                return;
            }

            @SuppressLint("DefaultLocale") String price = String.format("%.0f", Double.parseDouble(priceYuan) * 100f);

            String weight = mEtWeight.getText()
                    .toString()
                    .trim();

            if (TextUtils.isEmpty(weight))
            {
                MyToastUtils.error("请输入商品的重量");
                return;
            }

            if (weight.contains("kg"))
            {
                weight = null;
            }

            String stock = mEtStock.getText()
                    .toString()
                    .trim();

            String unit = mTvUnit.getText()
                    .toString()
                    .trim()
                    .replace(" ", "");
            String weight_unit = mTvWeightUnit.getText()
                    .toString()
                    .trim()
                    .replace(" ", "");

            String pack_fee = null;
            String packFeeYuan = mEtPackFee.getText()
                    .toString()
                    .trim();
            if (!TextUtils.isEmpty(packFeeYuan))
            {
                pack_fee = String.format("%.0f", Float.parseFloat(packFeeYuan) * 100);
            }

            String imagesJson = null;
            if (mImageList != null && mImageList.size() > 0)
            {
                List<String> tempImage = new ArrayList<>(mImageList);

                tempImage.removeAll(Collections.singleton(""));
                tempImage.removeAll(Collections.singleton(null));
                if (tempImage.size() > 0)
                {
                    imagesJson = mGson.toJson(tempImage, new TypeToken<List<String>>()
                    {
                    }.getType());
                }
            }

            String title = mEtFataherName.getText()
                    .toString()
                    .trim();
            if (mLlFatherName.getVisibility() == View.VISIBLE && TextUtils.isEmpty(title))
            {
                MyToastUtils.error("请输入标品名称");
                return;
            }

            if (mLlQr.getVisibility() == View.VISIBLE && mLlUpcCode.getVisibility() == View.GONE)
            {
                name = title;
            }

            String brandId = null;
            if (mLlBrand.getVisibility() == View.VISIBLE)
            {
                if (mBrandBean == null)
                {
                    MyToastUtils.error("请选择商品品牌");
                    return;
                }
                brandId = mBrandBean.getId();
            }

            String extraSummary = mEtExtraSummary.getText()
                    .toString()
                    .trim();

            String upcCode = mTvUpcCode.getText()
                    .toString()
                    .trim();

            if (mLlQr.getVisibility() == View.VISIBLE)
            {
                final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
                rxDialogSureCancel.setTitle("自主创建商品");
                rxDialogSureCancel.setContent("自主创建的商品，需要平台的审核，是否继续创建？");
                rxDialogSureCancel.getTitleView()
                        .setTextSize(17);
                rxDialogSureCancel.getContentView()
                        .setTextSize(14);
                TextView sureView = rxDialogSureCancel.getSureView();
                TextView cancelView = rxDialogSureCancel.getCancelView();
                sureView.setText("取消创建");
                cancelView.setText("继续创建");
                String finalProductId = productId;
                String finalAttrsJson = attrsJson;
                String finalSaleTimeJson = saleTimeJson;
                String finalWeight = weight;
                String finalPack_fee = pack_fee;
                String finalImagesJson = imagesJson;
                String finalBrandId = brandId;
                String finalName = name;
                cancelView.setOnClickListener(v -> {
                    httpPostAddGoods(store_id, finalProductId, finalName, summary, finalAttrsJson, finalSaleTimeJson, place, min_purchase, price, finalWeight, stock, unit, weight_unit, finalPack_fee, finalImagesJson, title, finalBrandId, extraSummary, upcCode);
                    rxDialogSureCancel.cancel();
                });
                sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
                rxDialogSureCancel.show();
            }
            else
            {
                httpPostAddGoods(store_id, productId, name, summary, attrsJson, saleTimeJson, place, min_purchase, price, weight, stock, unit, weight_unit, pack_fee, imagesJson, title, brandId, extraSummary, upcCode);
            }
        }
    }

    private void httpPostAddGoods(String store_id, String productId, String name, String summary, String attrsJson, String saleTimeJson, String place, String min_purchase, String price, String weight, String stock, String unit, String weight_unit, String pack_fee, String imagesJson, String title, String brandId, String extraSummary, String upcCode)
    {
        ApiUtils.getInstance()
                .okgoPostAddStoreGoods(this, myApp.mToken, store_id, productId, name, upcCode, title, brandId, mProdctCategoryId, summary, mMarketCategoryId, attrsJson, saleTimeJson, place, min_purchase, price, weight, stock, unit, weight_unit, pack_fee, imagesJson, extraSummary, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        MyToastUtils.success("添加商品成功");
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        MyToastUtils.error("添加失败");
                    }
                });
    }


    private void activityResultBrand(Intent data)
    {
        mBrandBean = data.getParcelableExtra(MyConstant.STR_BEAN);
        if (mBrandBean != null)
        {
            ViewSetDataUtils.textViewSetText(mTvBrand, mBrandBean.getName());
        }
    }

    private void activityResultProductCategory(Intent data)
    {
        if (data != null)
        {
            String categoryName = data.getStringExtra(MyConstant.STR_NAME);
            mProdctCategoryId = data.getStringExtra(MyConstant.STR_CATID);
            if (!TextUtils.isEmpty(categoryName) && !TextUtils.isEmpty(mProdctCategoryId))
            {
                ViewSetDataUtils.textViewSetText(mTvCategoryProduct, categoryName);
            }
        }
    }

    private void activityResultMarketCategory(Intent data)
    {
        if (data != null)
        {
            String categoryName = data.getStringExtra(MyConstant.STR_NAME);
            mMarketCategoryId = data.getStringExtra(MyConstant.STR_CATID);
            if (!TextUtils.isEmpty(categoryName) && !TextUtils.isEmpty(mMarketCategoryId))
            {
                ViewSetDataUtils.textViewSetText(mTvCategoryMarket, categoryName);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void activityResultTimes()
    {
        if (mLimitDateBean != null)
        {
            mLlSellingTimes.removeAllViews();
            TextView tvTitle = new TextView(this);
            tvTitle.setTextSize(15);
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
            LinearLayout.LayoutParams lpTTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpTTv.setMargins(0, 0, 0, WindowUtils.dip2px(this, 5));
            tvTitle.setLayoutParams(lpTTv);
            mLlSellingTimes.addView(tvTitle);
            if (mLimitDateBean.isFull_time())
            {
                tvTitle.setText("全时段售卖");
            } else
            {
                tvTitle.setText(mLimitDateBean.getStart_date() + " 至 " + mLimitDateBean.getEnd_date());
                TextView tvSub = new TextView(this);
                List<String> limit_hour = mLimitDateBean.getLimit_hour();
                if (mLimitDateBean.isFull_hour() || limit_hour == null || limit_hour.size() == 0)
                {
                    tvSub.setHint("全天售卖");
                } else
                {
                    for (int i = 0; i < limit_hour.size(); i++)
                    {
                        tvSub.append(limit_hour.get(i));
                        if (i != limit_hour.size() - 1)
                        {
                            if (i % 2 == 1)
                            {
                                tvSub.append("\n");
                            } else
                            {
                                tvSub.append("\t");
                            }
                        }
                    }
                }
                LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpSub.setMargins(0, 0, 0, WindowUtils.dip2px(this, 10));
                tvSub.setLayoutParams(lpSub);
                mLlSellingTimes.addView(tvSub);
            }

            View view = new View(this);
            view.setBackgroundResource(R.color.colorFg);
            LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowUtils.dip2px(this, 1));
            view.setLayoutParams(lpView);
            mLlSellingTimes.addView(view);

            TextView tvTip = new TextView(this);
            tvTip.setText("管理售卖时间");
            tvTip.setGravity(Gravity.CENTER);
            tvTip.setTextColor(ContextCompat.getColor(this, R.color.colorMain));
            LinearLayout.LayoutParams lpTip = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpTip.setMargins(0, WindowUtils.dip2px(this, 10), 0, 0);
            tvTip.setLayoutParams(lpTip);
            mLlSellingTimes.addView(tvTip);
        }
    }

    private void clickLlSellingTimes()
    {
        Bundle bundle = new Bundle();
        if (mLimitDateBean != null)
        {
            bundle.putParcelable(MyConstant.STR_BEAN, mLimitDateBean);
        }
        MyActivityUtils.skipActivityForResult(this, SellingTimeActivity.class, bundle, REQUEST_CODE_SELLING_TIME);
    }

    private void activityResultCamera(int requestCode, Intent data)
    {
        switch (requestCode)
        {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片选择结果回调
                mSelectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                int i = 0;
                for (LocalMedia media : mSelectList)
                {
                    //                    Log.i("图片-----》", media.getPath());
                    String path = media.getPath();
                    if (media.isCompressed())
                    {
                        path = media.getCompressPath();
                    }
                    httpPostImage(path, i);
                    i++;
                }
                if (mSelectList != null && mSelectList.size() != 0)
                {
                    mRecyclerViewImgsDefault.setVisibility(View.GONE);
                }
                mGridImageAdapter.setList(mSelectList);
                mGridImageAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void httpPostImage(String path, int position)
    {
        ApiUtils.getInstance()
                .okgoPostFile(this, myApp.mToken, path, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        FileBean fileBean = mGson.fromJson(response.body(), FileBean.class);
                        if (fileBean != null)
                        {
                            String file_key = fileBean.getFile_key();
                            if (!TextUtils.isEmpty(file_key))
                            {
                                //                                mImageList.add(file_key);
                                if (mImageList.size() > position)
                                {
                                    mImageList.set(position, file_key);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }

    private void activityResultAttrs()
    {
        if (mAttributeBeanList != null && mAttributeBeanList.size() > 0)
        {
            mLlAttrs.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < mAttributeBeanList.size(); i++)
            {
                AttributeBean attributeBean = mAttributeBeanList.get(i);
                if (attributeBean != null)
                {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setLayoutParams(lp);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    TextView tvName = new TextView(this);
                    ViewSetDataUtils.textViewSetText(tvName, attributeBean.getAtt_name());
                    tvName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    //                    LinearLayout.LayoutParams lpName = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvName.setLayoutParams(lp);
                    tvName.setTextSize(15);
                    tvName.setPadding(0, WindowUtils.dip2px(this, 5), 0, 0);
                    linearLayout.addView(tvName);

                    List<String> att_label = attributeBean.getAtt_label();
                    if (att_label != null)
                    {
                        StringBuilder builder = new StringBuilder(1024);
                        for (int j = 0; j < att_label.size(); j++)
                        {
                            String label = att_label.get(j);
                            if (!TextUtils.isEmpty(label))
                            {
                                if (builder.length() != 0)
                                {
                                    builder.append(",");
                                }
                                builder.append(label);
                            }
                        }
                        TextView tvLabel = new TextView(this);
                        tvLabel.setPadding(0, 0, 0, WindowUtils.dip2px(this, 10));
                        tvLabel.setHint(builder.toString());
                        builder.reverse();
                        //                        LinearLayout.LayoutParams lpLabel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tvLabel.setLayoutParams(lp);
                        linearLayout.addView(tvLabel);
                    }
                    mLlAttrs.addView(linearLayout);
                    View view = new View(this);
                    LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowUtils.dip2px(this, 1));
                    view.setBackgroundResource(R.color.colorFg);
                    view.setLayoutParams(lpView);
                    mLlAttrs.addView(view);
                }
            }
            TextView textView = new TextView(this);
            textView.setLayoutParams(lp);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorMain));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0, WindowUtils.dip2px(this, 10), 0, 0);
            textView.setText("管理商品属性");
            mLlAttrs.addView(textView);
        }
    }

    private void clickLlAttrs()
    {
        Bundle bundle = new Bundle();
        if (mAttributeBeanList != null)
        {
            bundle.putParcelableArrayList(MyConstant.STR_BEAN_LIST, mAttributeBeanList);
        }
        MyActivityUtils.skipActivityForResult(this, AddAttrActivity.class, bundle, REQUEST_CODE_ADD_ATTRS);
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener()
    {
        @Override
        public void onAddPicClick()
        {
            if (mode)
            {
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(mContext)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(mThemeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(3)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(3)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(true)// 是否裁剪
                        .compress(true)// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(0, 0)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                        .isGif(true)// 是否显示gif图片
                        .withAspectRatio(1, 1)
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(mSelectList)// 是否传入已选图片
                        //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                        //                        .videoMaxSecond(15)
                        //                        .videoMinSecond(10)
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        .rotateEnabled(false) // 裁剪是否可旋转图片
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片
                        //.videoQuality()// 视频录制质量 0 or 1
                        //.videoSecond()//显示多少秒以内的视频or音频也可适用
                        //.recordVideoSecond()//录制视频秒数 默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            } else
            {
                // 单独拍照
                PictureSelector.create(mContext)
                        .openCamera(PictureMimeType.ofAll())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                        .theme(mThemeId)// 主题样式设置 具体参考 values/styles
                        .maxSelectNum(3)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .enableCrop(false)// 是否裁剪
                        .compress(false)// 是否压缩
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(0, 0)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                        .isGif(false)// 是否显示gif图片
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(mSelectList)// 是否传入已选图片
                        .previewEggs(false)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 裁剪压缩质量 默认为100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.rotateEnabled() // 裁剪是否可旋转图片
                        //.scaleEnabled()// 裁剪是否可放大缩小图片
                        //.videoQuality()// 视频录制质量 0 or 1
                        //.videoSecond()////显示多少秒以内的视频or音频也可适用
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        }
    };

    private void initView()
    {
        initEtChangeListener();
        initData();
        initRecyclerViewImgs();
        initRecyclerImgsDefault();

        //直接自主创建标品
        Intent intent = getIntent();
        String upc = intent.getStringExtra(MyConstant.STR_UPC_CODE);
        if (!TextUtils.isEmpty(upc))
        {
            httpgetProdcutByUpcCode(upc);
        }
    }

    private void initRecyclerImgsDefault()
    {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerViewImgsDefault.setLayoutManager(manager);
        List<String> list = imgsGetData();
        if (list != null && list.size() > 0)
        {
            list.add("");
            mRecyclerViewImgsDefault.setAdapter(new ImageDefaultAdapter(list, onAddPicClickListener));
            mRecyclerViewImgsDefault.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initEtChangeListener()
    {
        mEtWeight.addTextChangedListener(nameHouTextWatcher);
        mTvWeightUnit.addTextChangedListener(nameHouTextWatcher);
        mTvUnit.addTextChangedListener(nameHouTextWatcher);
        mEtPlace.addTextChangedListener(nameHouTextWatcher);
        mEtExtraSummary.addTextChangedListener(nameHouTextWatcher);
        mEtFataherName.addTextChangedListener(nameHouTextWatcher);
    }

    private TextWatcher nameHouTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (mProductOwnerDataBean != null || mGoodsBean != null)
            {
                showPingName();
            }else if (mLlQr.getVisibility() == View.VISIBLE && mLlUpcCode.getVisibility() == View.GONE)
            {
                showFeiPingName();
            }
        }
    };

    private void initData()
    {
        myApp = (MyApp) getApplication();
        Intent intent = getIntent();
        mIsUpdate = intent.getBooleanExtra(MyConstant.STR_IS, false);
        mEtName.setEnabled(false);
        if (mIsUpdate)
        {
            mType = intent.getStringExtra(MyConstant.STR_TYPE);
            mBtnConfirm.setText("修改商品");
            mGoodsBean = intent.getParcelableExtra(MyConstant.STR_BEAN);
            if (mGoodsBean != null)
            {
                initUpdateInfoData();
            }
        } else
        {
            mProductOwnerDataBean = intent.getParcelableExtra(MyConstant.STR_BEAN);
            if (mProductOwnerDataBean != null)
            {
                mLlQr.setVisibility(View.GONE);
                mEtName.setEnabled(false);
            }
            viewSetByProductOwnerDataBean();
        }

        if (mLlQr.getVisibility() == View.VISIBLE && mLlUpcCode.getVisibility() == View.VISIBLE)
        {
            mEtName.setEnabled(true);
        }
        else if (mLlQr.getVisibility() == View.VISIBLE && mLlUpcCode.getVisibility() == View.GONE){
            mEtName.setEnabled(false);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initUpdateInfoData()
    {
        mLlQr.setVisibility(View.GONE);
        if (MyConstant.STR_FAILED.equals(mType))
        {
            new MyToolBar(this, "修改审核失败的商品信息", null);
        } else
        {
            new MyToolBar(this, "修改商品信息", null);
        }
        mLlBrand.setEnabled(false);
        mLlCategoryProduct.setEnabled(false);
        GoodsBean.ProductBean product = mGoodsBean.getProduct();
        if (product != null)
        {
            List<GoodsBean.ProductBean.AttributeBean> attributeBeanList = product.getAttribute();
            if (attributeBeanList != null)
            {
                mAttributeBeanList = new ArrayList<>();
                for (int i = 0; i < attributeBeanList.size(); i++)
                {
                    GoodsBean.ProductBean.AttributeBean productAttributeBean = attributeBeanList.get(i);
                    AttributeBean attributeBean = new AttributeBean();
                    attributeBean.setAtt_name(productAttributeBean.getAtt_name());
                    List<String> att_labelList = productAttributeBean.getAtt_label();
                    int chaSize = 3 - att_labelList.size();
                    for (int j = 0; j < chaSize; j++)
                    {
                        att_labelList.add("");
                    }
                    attributeBean.setAtt_label(productAttributeBean.getAtt_label());
                    mAttributeBeanList.add(attributeBean);
                }
                activityResultAttrs();
            }
            GoodsBean.ProductBean.SaleTimeBean saleTimeBean = product.getSale_time();
            if (saleTimeBean != null)
            {
                mLimitDateBean = new LimitDateBean();
                mLimitDateBean.setLimit_hour(saleTimeBean.getLimit_hour());
                mLimitDateBean.setFull_hour(saleTimeBean.isFull_hour());
                mLimitDateBean.setFull_time(saleTimeBean.isFull_time());
                mLimitDateBean.setEnd_date(saleTimeBean.getEnd_date());
                mLimitDateBean.setStart_date(saleTimeBean.getStart_date());
                activityResultTimes();
            }
            GoodsBean.ProductBean.ExtraBean extra = product.getExtra();
            if (extra != null)
            {
                ViewSetDataUtils.textViewSetText(mEtName, product.getName());
                String upcCode = extra.getUpcCode();
                String cat_id = extra.getCat_id();
                if (!TextUtils.isEmpty(cat_id))
                {
                    httpGetMarket2CategoryName(cat_id);
                }
                String categoryId = extra.getCategory();
                if (!TextUtils.isEmpty(categoryId))
                {
                    httpGetProductCategoryName(categoryId);
                }
                long[] priceMinMax = ZebraUtils.getInstance()
                        .goodPlatformPriceMinMax(mGoodsBean);
                if (priceMinMax[0] != -1)
                {
                    mEtPrice.setText(String.format("%.2f", (double) priceMinMax[0] * 0.01));
                }
                ViewSetDataUtils.textViewSetText(mEtWeight, extra.getWeight());
                ViewSetDataUtils.textViewSetText(mTvUnit, extra.getUnit());
                ViewSetDataUtils.textViewSetText(mTvWeightUnit, extra.getWeight_unit());
                showPingName();
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product.getPriceAndStock();
                if (priceAndStockBeanList != null)
                {
                    long stock = 0;
                    for (int i = 0; i < priceAndStockBeanList.size(); i++)
                    {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList.get(i);
                        if (priceAndStockBean.getBinded() == 1)
                        {
                            stock += priceAndStockBean.getStock();
                        }
                    }
                    mEtStock.setText(stock + "");
                }

                mEtPackFee.setText(String.format("%.2f", (double) product.getPack_fee() * 0.01));
                mEtMinPurchase.setText(product.getMin_purchase() + "");
                ViewSetDataUtils.textViewSetText(mEtSummary, product.getSummary());
                if (!TextUtils.isEmpty(upcCode))
                {
                    //标品修改
                    mLlPlace.setVisibility(View.GONE);
                    mLlFatherName.setVisibility(View.GONE);
                    mLlEaxtraSummary.setVisibility(View.GONE);
                    mEtWeight.setFocusable(false);
                    mTvUpcCode.setText(upcCode);
                    String brand = extra.getBrand();
                    if (!TextUtils.isEmpty(brand))
                    {
                        httpGetBrandName(brand);
                    }
                } else
                {
                    //非标品修改
                    mLlUpcCode.setVisibility(View.GONE);
                    mLlBrand.setVisibility(View.GONE);
                    ViewSetDataUtils.textViewSetText(mEtPlace, extra.getPlace());
                    ViewSetDataUtils.textViewSetText(mEtFataherName, extra.getTitle());
                    mEtFataherName.setFocusable(false);
                    ViewSetDataUtils.textViewSetText(mEtExtraSummary, extra.getExtra_summary());
                }
            }
        }
    }

    //自主非标品名字
    private void showFeiPingName()
    {
        StringBuilder builder = new StringBuilder(1024);
        String place = mEtPlace.getText()
                .toString()
                .trim();
        builder.append(place);
        builder.append(" ");
        String name = mEtFataherName.getText()
                .toString()
                .trim();
        builder.append(name);
        builder.append(" ");
        String extraSummary = mEtExtraSummary.getText()
                .toString()
                .trim();
        builder.append(extraSummary);
        builder.append(" ");
        String weight = mEtWeight.getText()
                .toString()
                .trim();
        String weightUnit = mTvWeightUnit.getText()
                .toString()
                .trim()
                .replace(" ", "");
        String unit = mTvUnit.getText()
                .toString()
                .trim()
                .replace(" ", "");
        if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(weightUnit) && !TextUtils.isEmpty(unit))
        {
            builder.append(String.format("约%s%s/%s", weight, weightUnit, unit));
        }
        mEtName.setText(builder.toString());
    }

    @SuppressLint("SetTextI18n")
    private void showPingName()
    {
        String pruductName = getPruductName();
        if (TextUtils.isEmpty(pruductName))
        {
            return;
        }
        if (hasUpcCode())
        {
            mEtName.setText(pruductName);
        } else
        {
            StringBuilder builder = new StringBuilder(1024);
            builder.append(mEtPlace.getText()
                    .toString()
                    .trim());
            builder.append(" ");
            builder.append(pruductName);
            builder.append(" ");
            builder.append(mEtExtraSummary.getText()
                    .toString()
                    .trim());
            builder.append(" ");
            String weight = mEtWeight.getText()
                    .toString()
                    .trim();
            if (!TextUtils.isEmpty(weight))
            {
                builder.append("约");
                builder.append(weight);
                builder.append(mTvWeightUnit.getText()
                        .toString()
                        .trim()
                        .replace(" ", ""));
                builder.append("/");
                builder.append(mTvUnit.getText()
                        .toString()
                        .trim()
                        .replace(" ", ""));
            }
            mEtName.setText(builder.toString());
            builder.reverse();
        }
    }

    @SuppressLint("SetTextI18n")
    private void viewSetByProductOwnerDataBean()
    {
        if (mProductOwnerDataBean != null)
        {
            ProductOwnerDataBean.ProductDataBean product = mProductOwnerDataBean.getProduct();
            if (product != null)
            {
                mEtWeight.setText(400 + "");
                showPingName();
                ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
                    String categoryId = extra.getCategory();
                    if (!TextUtils.isEmpty(categoryId))
                    {
                        httpGetProductCategoryName(categoryId);
                    }
                    String upcCode = extra.getUpcCode();
                    //标品
                    if (!TextUtils.isEmpty(upcCode))
                    {
                        mLlPlace.setVisibility(View.GONE);
                        mLlFatherName.setVisibility(View.GONE);
                        mLlEaxtraSummary.setVisibility(View.GONE);
                        mEtWeight.setFocusable(false);
                        mLlBrand.setEnabled(false);
                        ViewSetDataUtils.textViewSetText(mTvUpcCode, upcCode);
                        ViewSetDataUtils.textViewSetText(mEtWeight, String.valueOf(extra.getWeight()));
                        String brandId = extra.getBrand();
                        if (!TextUtils.isEmpty(brandId))
                        {
                            httpGetBrandName(brandId);
                        }
                        new MyToolBar(this, "添加标品商品", null);
                    } else
                    {
                        //非标品
                        mLlUpcCode.setVisibility(View.GONE);
                        mLlBrand.setVisibility(View.GONE);
                        mEtWeight.setFocusable(true);
                        ViewSetDataUtils.textViewSetText(mEtFataherName, product.getTitle());
                        mEtFataherName.setFocusable(false);
                        new MyToolBar(this, "添加非标品商品", null);
                    }
                } else
                {
                    //非标品
                    mLlUpcCode.setVisibility(View.GONE);
                    mLlBrand.setVisibility(View.GONE);
                    mEtWeight.setFocusable(true);
                    ViewSetDataUtils.textViewSetText(mEtFataherName, product.getTitle());
                    mEtFataherName.setFocusable(false);
                    new MyToolBar(this, "添加非标品商品", null);
                }
            }
        } else
        {
            mLlUpcCode.setVisibility(View.GONE);
            mLlBrand.setVisibility(View.GONE);
            mEtWeight.setFocusable(true);
            mTvCategoryProduct.setText("");
            new MyToolBar(this, "自主创建商品", null);
        }
    }

    private void httpGetMarket2CategoryName(String cat_id)
    {
        ApiUtils.getInstance()
                .okgoGet2StoreCategoryName(this, myApp.mToken, cat_id, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has("category_name"))
                            {
                                ViewSetDataUtils.textViewSetText(mTvCategoryMarket, jsonObject.getString("category_name"));
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }

    private void httpGetProductCategoryName(String categoryId)
    {
        ApiUtils.getInstance()
                .okgoGet3ProductCategoryName(this, myApp.mToken, categoryId, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has(MyConstant.STR_CATEGORY))
                            {
                                ViewSetDataUtils.textViewSetText(mTvCategoryProduct, jsonObject.getString(MyConstant.STR_CATEGORY));
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }

    private void httpGetBrandName(String brandId)
    {
        ApiUtils.getInstance()
                .okgoGetBrandName(this, myApp.mToken, brandId, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has(MyConstant.STR_BRAND))
                            {
                                mBrandBean = mGson.fromJson(jsonObject.getString(MyConstant.STR_BRAND), BrandBean.class);
                                if (mBrandBean != null)
                                {
                                    ViewSetDataUtils.textViewSetText(mTvBrand, mBrandBean.getName());
                                }
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                    }
                });
    }

    private void initRecyclerViewImgs()
    {
        //设置样式
        mImageList = new ArrayList<>();
        mImageList.add("");
        mImageList.add("");
        mImageList.add("");
        mThemeId = R.style.picture_default_style;
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mGridImageAdapter = new GridImageAdapter(this, onAddPicClickListener);
        mGridImageAdapter.setList(mSelectList);
        //选择最多图片
        mGridImageAdapter.setSelectMax(3);
        mRecyclerView.setAdapter(mGridImageAdapter);
        mGridImageAdapter.setOnItemClickListener((position, v) -> {
            if (mSelectList.size() > 0)
            {
                LocalMedia media = mSelectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType)
                {
                    case 1:
                        // 预览图片 可自定长按保存路径
                        //PictureSelector.create(MainActivity.this).themeStyle(mThemeId).externalPicturePreview(position, "/custom_file", mSelectList);
                        PictureSelector.create(mContext)
                                .themeStyle(mThemeId)
                                .openExternalPreview(position, mSelectList);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector.create(mContext)
                                .externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
                        PictureSelector.create(mContext)
                                .externalPictureAudio(media.getPath());
                        break;
                }
            }
        });

        mGridImageAdapter.setOnDeleteClickListener((position, v) -> {
            if (position < mImageList.size())
            {
                mImageList.set(position, "");
            }
        });
    }

    private String getPruductName()
    {
        String name = null;
        if (mGoodsBean != null)
        {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null)
            {
                name = product.getName();
            }
        } else if (mProductOwnerDataBean != null)
        {
            ProductOwnerDataBean.ProductDataBean product = mProductOwnerDataBean.getProduct();
            if (product != null)
            {
                name = product.getTitle();
            }
        }
        else
        {
            name = mEtName.getText().toString().trim();
        }
        return name;
    }

    //判断添加商品库里的商品
    private boolean hasUpcCode()
    {
        String upcCode = null;
        if (mGoodsBean != null)
        {
            upcCode = mGoodsBean.getProduct()
                    .getExtra()
                    .getUpcCode();
        } else if (mProductOwnerDataBean != null)
        {
            upcCode = mProductOwnerDataBean.getProduct()
                    .getExtra()
                    .getUpcCode();
        }
        return !TextUtils.isEmpty(upcCode);
    }

    private List<String> imgsGetData()
    {
        List<String> images = null;
        if (mGoodsBean != null)
        {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null)
            {
                images = product.getImages();
            }
        }
        else if (mProductOwnerDataBean != null)
        {
            ProductOwnerDataBean.ProductDataBean product = mProductOwnerDataBean.getProduct();
            if (product != null)
            {
                ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
                    images = extra.getImages();
                }
            }
        }
        return images;
    }
}