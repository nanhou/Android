package cn.jinxiit.zebra.fragments.homes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.codescanner.ScannerCodeActivity;
import cn.jinxiit.zebra.activities.me.NotificationActivity;
import cn.jinxiit.zebra.activities.product.AuditOfProductActivity;
import cn.jinxiit.zebra.activities.product.BatchProductManageActivity;
import cn.jinxiit.zebra.activities.product.GoodsInfoActivity;
import cn.jinxiit.zebra.activities.product.GoodsTypesActivity;
import cn.jinxiit.zebra.activities.product.ProductStatusActivity;
import cn.jinxiit.zebra.activities.product.SearchActivity;
import cn.jinxiit.zebra.activities.product.UpdateInventoryPriceAllPlatformActivity;
import cn.jinxiit.zebra.activities.product.UpdateInventoryPriceOnePlatformActivity;
import cn.jinxiit.zebra.activities.product.UpdateOrAddGoodsActivity;
import cn.jinxiit.zebra.activities.product.batchadds.BatchCreateProductCategoryActivity;
import cn.jinxiit.zebra.activities.product.creategoods.SellingTimeActivity;
import cn.jinxiit.zebra.adapters.ClassifyGoodsAdapter;
import cn.jinxiit.zebra.adapters.SearchGoodsAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.LimitDateBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.beans.ScreeningGoodsBean;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class ProductFragment extends Fragment
{
    @BindView(R.id.rv_goodstypes)
    RecyclerView mRvGoodsTypes;
    @BindView(R.id.rv_goods)
    RecyclerView mRvGoods;
    @BindView(R.id.tv_goods_type)
    TextView mTvGoodsType;
    Unbinder unbinder;
    @BindView(R.id.ll_menu)
    LinearLayout mLlMenu;
    @BindView(R.id.iv_add)
    ImageView mIvAdd;
    @BindView(R.id.btn_all_category)
    Button mBtnAllCategory;
    @BindView(R.id.btn_other_category)
    Button mBtnOtherCategory;
    @BindView(R.id.tv_goods_count)
    TextView mTvGoodsCount;
    @BindView(R.id.sl_goods)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.iv_notification)
    ImageView mIvNotification;
    @BindView(R.id.tv_screening)
    TextView mTvScreening;
    @BindView(R.id.tv_nodata)
    TextView mTvNoData;

    private Activity mContext;
    private ClassifyGoodsAdapter mMarketCategoryAdapter;
    private SearchGoodsAdapter mSearchGoodsAdapter;

    private int mFrom = 0;
    private String mCurrentCategoryId = null;

    private MyApp myApp;
    private Gson mGson = new Gson();

    private static final int REQUEST_CODE_UPDA_GOODS_INFO = 0;
    private static final int REQUEST_CODE_BATCH_MANAGE = 1;
    private static final int REQUEST_CODE_UPDATE_SALE_TIME = 2;//修改售卖时间

    private boolean mIsSearchGood = false;
    private ScreeningGoodsBean mScreeningGoodsBean;
    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mIvNotification != null)
            {
                httpGetNotificationCount();
                mIvNotification.postDelayed(mRunnable, 5000);
            }
        }
    };

    private int mUpdatePosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();
        myApp = (MyApp) (mContext != null ? mContext.getApplication() : null);
        initView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initResumeData();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MyConstant.REQUEST_CAMERA_PERMISSION)
        {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_UPDA_GOODS_INFO && resultCode == 1)
        {
            if (data != null)
            {
                GoodsBean goodsBean = data.getParcelableExtra(MyConstant.STR_BEAN);
                if (goodsBean != null)
                {
                    mSearchGoodsAdapter.updateItemData(goodsBean);
                }
            }
        }

        if (requestCode == REQUEST_CODE_BATCH_MANAGE && resultCode == 1)
        {
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.autoRefresh();
            }
        }

        if (requestCode == MyConstant.REQUEST_CODE_QR && resultCode == 1)
        {
            activityQR(data);
        }

        if (requestCode == REQUEST_CODE_UPDATE_SALE_TIME && resultCode == 1)
        {
            saleTimeResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.ibtn_notification, R.id.ll_menu, R.id.btn_all_category, R.id.btn_other_category, R.id.tv_message, R.id.tv_batch_manage, R.id.tv_qr_create, R.id.tv_batch_create, R.id.tv_search, R.id.tv_screening, R.id.tv_classify, R.id.iv_add, R.id.btn_search, R.id.ibtn_qrcode, R.id.btn_audit})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.ibtn_notification:
                MyActivityUtils.skipActivity(mContext, NotificationActivity.class);
                break;
            case R.id.ll_menu:
                clickAdd();
                break;
            case R.id.btn_all_category:
                clickAllCategory();
                break;
            case R.id.btn_other_category:
                clickOtherCategory();
                break;
            case R.id.tv_message:
                CountBean countBeanMessage = (CountBean) mTvMessage.getTag();
                if (countBeanMessage != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MyConstant.STR_BEAN, countBeanMessage);
                    MyActivityUtils.skipActivity(mContext, ProductStatusActivity.class, bundle);
                }
                break;
            case R.id.tv_batch_manage:
                Intent intent = new Intent(mContext, BatchProductManageActivity.class);
                String query = createQuery();
                intent.putExtra(MyConstant.STR_QUERY, query);
                startActivityForResult(intent, REQUEST_CODE_BATCH_MANAGE);
                break;
            case R.id.tv_qr_create:
                mIsSearchGood = false;
                saoyisao();
                break;
            case R.id.tv_batch_create:
                MyActivityUtils.skipActivity(mContext, BatchCreateProductCategoryActivity.class);
                break;
            case R.id.tv_search:
                Bundle bundle = new Bundle();
                bundle.putString(MyConstant.STR_TYPE, MyConstant.STR_SEARCH_CREATE);
                MyActivityUtils.skipActivity(mContext, SearchActivity.class, bundle);
                break;
            case R.id.tv_screening:
                ClickScreening();
                break;
            case R.id.btn_audit:
                MyActivityUtils.skipActivity(mContext, AuditOfProductActivity.class);
                break;
            case R.id.tv_classify:
                MyActivityUtils.skipActivity(mContext, GoodsTypesActivity.class);
                break;
            case R.id.btn_search:
                MyActivityUtils.skipActivity(mContext, SearchActivity.class);
                break;
            case R.id.iv_add:
                clickAdd();
                //                MyActivityUtils.skipActivity(mContext, SearchActivity.class);
                break;
            case R.id.ibtn_qrcode:
                mIsSearchGood = true;
                clickQrCode();
                break;
        }
    }

    private void saleTimeResult(Intent data)
    {
        if (data != null && mUpdatePosition != -1)
        {
            GoodsBean itemData = mSearchGoodsAdapter.getItemData(mUpdatePosition);
            if (itemData == null)
            {
                return;
            }
            LimitDateBean limitDateBean = data.getParcelableExtra(MyConstant.STR_BEAN);
            if (limitDateBean != null)
            {
                String json = mGson.toJson(limitDateBean, limitDateBean.getClass());
                if (!TextUtils.isEmpty(json))
                {
                    JSONArray jsonArray = new JSONArray();
                    ArrayList<GoodsBean> goodsBeanArrayList = new ArrayList<>();
                    goodsBeanArrayList.add(itemData);
                    for (int i = 0; i < goodsBeanArrayList.size(); i++)
                    {
                        JSONObject jsonObject = new JSONObject();
                        GoodsBean goodsBean = goodsBeanArrayList.get(i);
                        try
                        {
                            jsonObject.put(MyConstant.STR_ID, goodsBean.getProduct()
                                    .getId());
                            jsonObject.put(MyConstant.STR_SALE_TIME, json);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (jsonArray.length() > 0)
                    {
                        mSearchGoodsAdapter.httpPostBatchUpdateGoods(jsonArray.toString());
                    }
                }
            }
        }
    }

    private void activityQR(Intent data)
    {
        //处理扫描结果（在界面上显示）
        if (null != data)
        {
            String result = data.getStringExtra(MyConstant.STR_RESULT);
            if (!TextUtils.isEmpty(result))
            {
                if (NumberUtils.isInteger(result))
                {
                    if (mIsSearchGood)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString(MyConstant.STR_UPC_CODE, result);
                        MyActivityUtils.skipActivity(mContext, SearchActivity.class, bundle);

//                        httpGetSearchGoodsOfStoreByUpcCode(result);
                    } else
                    {
                        httpgetProdcutByUpcCode(result);
                    }
                } else
                {
                    MyToastUtils.error("条形码错误:" + result);
                }
            }
        }
    }

//    private void httpGetSearchGoodsOfStoreByUpcCode(String upcCode)
//    {
//        if (myApp.mCurrentStoreOwnerBean != null)
//        {
//            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
//            if (!TextUtils.isEmpty(store_id))
//            {
//                ApiUtils.getInstance()
//                        .okgoGetSearchGoodsOfStore(mContext, myApp.mToken, store_id, null, upcCode, "0", new ApiResultListener()
//                        {
//                            @Override
//                            public void onSuccess(Response<String> response)
//                            {
//                                try
//                                {
//                                    JSONObject jsonObject = new JSONObject(response.body());
//                                    if (jsonObject.has(MyConstant.STR_PRODUCTS))
//                                    {
//                                        List<GoodsBean> goodsBeanList = mGson.fromJson(jsonObject.getString(MyConstant.STR_PRODUCTS), new TypeToken<List<GoodsBean>>()
//                                        {
//                                        }.getType());
//                                        if (goodsBeanList != null && goodsBeanList.size() > 0)
//                                        {
//                                            GoodsBean goodsBean = goodsBeanList.get(0);
//                                            if (goodsBean != null)
//                                            {
//                                                Intent intent = new Intent(getActivity(), GoodsInfoActivity.class);
//                                                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
//                                                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
//                                                return;
//                                            }
//                                        }
//                                    }
//                                    MyToastUtils.error("未找到该商品");
//                                } catch (JSONException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onError(Response<String> response)
//                            {
//                                MyToastUtils.error("未找到该商品");
//                            }
//                        });
//            }
//        }
//    }

    private void httpgetProdcutByUpcCode(String upcCode)
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
                                if (productOwnerDataBeanList != null && productOwnerDataBeanList.size() == 1)
                                {
                                    ProductOwnerDataBean productOwnerDataBean = productOwnerDataBeanList.get(0);

                                    if (productOwnerDataBean != null)
                                    {
                                        httpCheckProductExist(productOwnerDataBean);
                                        return;
                                    }
                                }
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(MyConstant.STR_UPC_CODE, upcCode);
                            MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
                            MyToastUtils.error("商品库中未找到该条形码的产品");
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString(MyConstant.STR_UPC_CODE, upcCode);
                        MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
                        MyToastUtils.error("商品库中未找到该条形码的产品");
                    }
                });
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
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(MyConstant.STR_BEAN, productOwnerDataBean);
                                        MyActivityUtils.skipActivity(mContext, UpdateOrAddGoodsActivity.class, bundle);
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

    private void clickAllCategory()
    {
        if (mCurrentCategoryId != null)
        {
            mTvGoodsType.setText("全部商品");
            mCurrentCategoryId = null;
            mMarketCategoryAdapter.init();

            mFrom = 0;
            mSearchGoodsAdapter.clearDataList();
            httpGetGoodsByMarketCategoryId(true);
            mBtnAllCategory.setBackgroundResource(R.drawable.shape_white_leftcolormain);
            mBtnOtherCategory.setBackgroundResource(R.color.white);
        }
    }

    private void clickOtherCategory()
    {
        if (!"0".equals(mCurrentCategoryId))
        {
            mTvGoodsType.setText("未分类商品");
            mCurrentCategoryId = "0";
            mMarketCategoryAdapter.init();

            mFrom = 0;
            mSearchGoodsAdapter.clearDataList();
            httpGetGoodsByMarketCategoryId(true);
            mBtnAllCategory.setBackgroundResource(R.color.white);
            mBtnOtherCategory.setBackgroundResource(R.drawable.shape_white_leftcolormain);
        }
    }

    private void initResumeData()
    {
        if (myApp.mMarketCategoryList != null)
        {
            mMarketCategoryAdapter.setDataList(myApp.mMarketCategoryList);
        }
        httpGetStatusOfGoodsTip();
    }

    private void httpGetStatusOfGoodsTip()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetCountTip(mContext, myApp.mToken, store_id, MyConstant.STR_STATUS, false, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                CountBean countBean = mGson.fromJson(response.body(), CountBean.class);
                                if (countBean != null)
                                {
                                    mTvMessage.setTag(countBean);
                                    long outStock = countBean.getOutStock();
                                    long lowerFrame = countBean.getLowerFrame();
                                    long lessStock = countBean.getLessStock();
                                    StringBuilder builder = new StringBuilder(1024);
                                    builder.append("您有商品");
                                    mTvMessage.setVisibility(View.GONE);
                                    if (outStock != 0)
                                    {
                                        mTvMessage.setVisibility(View.VISIBLE);
                                        builder.append(outStock);
                                        builder.append("个已售空,");
                                    }

                                    if (lessStock != 0)
                                    {
                                        mTvMessage.setVisibility(View.VISIBLE);
                                        builder.append(lessStock);
                                        builder.append("个库存紧张,");
                                    }

                                    if (lowerFrame != 0)
                                    {
                                        mTvMessage.setVisibility(View.VISIBLE);
                                        builder.append(lowerFrame);
                                        builder.append("个下架,");
                                    }

                                    if (mTvMessage.getVisibility() == View.VISIBLE)
                                    {
                                        builder.deleteCharAt(builder.length() - 1);
                                        mTvMessage.setText(builder.toString());
                                    }
                                    builder.reverse();
                                }
                            }

                            @Override
                            public void onError(Response<String> response)
                            {

                            }
                        });
            }
        }
    }

    private void httpGetNotificationCount()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetCountTip(mContext, myApp.mToken, store_id, MyConstant.STR_MESSAGE, false, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                CountBean countBean = mGson.fromJson(response.body(), CountBean.class);
                                if (countBean != null)
                                {
                                    if (mIvNotification != null)
                                    {
                                        if (countBean.getAll() == 0)
                                        {
                                            mIvNotification.setVisibility(View.GONE);
                                        } else
                                        {
                                            mIvNotification.setVisibility(View.VISIBLE);
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
        }
    }

    private void clickAdd()
    {
        if (mLlMenu.getVisibility() == View.GONE)
        {
            mIvAdd.setImageResource(R.drawable.create_cancel);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(300);
            mLlMenu.setVisibility(View.VISIBLE);
            mLlMenu.startAnimation(alphaAnimation);
        } else
        {
            mIvAdd.setImageResource(R.drawable.create_new);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(100);
            mLlMenu.startAnimation(alphaAnimation);
            mLlMenu.setVisibility(View.GONE);
        }
    }

    private void saoyisao()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                MyToastUtils.info("使用扫一扫功能需相机权限!!");
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MyConstant.REQUEST_CAMERA_PERMISSION);
                return;
            }
        }

        Intent data = new Intent(getActivity(), ScannerCodeActivity.class);
        startActivityForResult(data, MyConstant.REQUEST_CODE_QR);
    }

    private void clickQrCode()
    {
        saoyisao();
    }

    @SuppressLint("RtlHardcoded")
    private void ClickScreening()
    {
        Dialog screeningDialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View root = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_screening_menu, null);
        RadioGroup radioGroup0 = root.findViewById(R.id.radiogroup0);
        RadioGroup radioGroup1 = root.findViewById(R.id.radiogroup1);
        CheckBox cbJd = root.findViewById(R.id.cb_jd);
        CheckBox cbMt = root.findViewById(R.id.cb_mt);
        CheckBox cbElm = root.findViewById(R.id.cb_elm);
        CheckBox cbEbai = root.findViewById(R.id.cb_ebai);
        initScreeningDialog(radioGroup0, radioGroup1, cbJd, cbMt, cbElm, cbEbai);
        root.findViewById(R.id.btn_confirm)
                .setOnClickListener(v -> {
                    if (mScreeningGoodsBean == null)
                    {
                        mScreeningGoodsBean = new ScreeningGoodsBean();
                    }
                    int position0 = radioGroup0.indexOfChild(radioGroup0.findViewById(radioGroup0.getCheckedRadioButtonId()));
                    switch (position0)
                    {
                        case -1:
                            mScreeningGoodsBean.setStock(null);
                            break;
                        case 0:
                            mScreeningGoodsBean.setStock(MyConstant.STR_IN_STOCK);
                            break;
                        case 1:
                            mScreeningGoodsBean.setStock(MyConstant.STR_OUT_STOCK);
                            break;
                        case 2:
                            mScreeningGoodsBean.setStock(MyConstant.STR_LESS_STOCK);
                            break;
                    }
                    int position1 = radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()));
                    switch (position1)
                    {
                        case -1:
                            mScreeningGoodsBean.setStatus(null);
                            break;
                        case 0:
                            mScreeningGoodsBean.setStatus(MyConstant.STR_ON_FRAME);
                            break;
                        case 1:
                            mScreeningGoodsBean.setStatus(MyConstant.STR_LOWER_FRAME);
                            break;
                    }
                    List<String> platformList = new ArrayList<>();
                    if (cbJd.isChecked())
                    {
                        platformList.add(MyConstant.STR_EN_JDDJ);
                    }
                    if (cbMt.isChecked())
                    {
                        platformList.add(MyConstant.STR_EN_MT);
                    }
                    if (cbElm.isChecked())
                    {
                        platformList.add(MyConstant.STR_EN_ELEME);
                    }
                    if (cbEbai.isChecked())
                    {
                        platformList.add(MyConstant.STR_EN_EBAI);
                    }
                    mScreeningGoodsBean.setPlatform(platformList);
                    if (mSmartRefreshLayout != null)
                    {
                        mSmartRefreshLayout.autoRefresh();
                    }
                    screeningDialog.cancel();
                });

        root.findViewById(R.id.btn_reset)
                .setOnClickListener(v -> {
                    radioGroup0.clearCheck();
                    radioGroup1.clearCheck();
                    cbJd.setChecked(false);
                    cbMt.setChecked(false);
                    cbElm.setChecked(false);
                    cbEbai.setChecked(false);
                });

        screeningDialog.setContentView(root);
        Window dialogWindow = screeningDialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.RIGHT);
        dialogWindow.setWindowAnimations(R.style.dialogAnimations1); // 添加动画

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                dialogWindow.setStatusBarColor(getResources().getColor(R.color.colorMain));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowUtils.dip2px(mContext, 250); // 宽度
        lp.height = WindowUtils.getScreenSize(mContext)[1]; // 高度
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        screeningDialog.setCanceledOnTouchOutside(true);
        screeningDialog.show();
    }

    private void initScreeningDialog(RadioGroup radioGroup0, RadioGroup radioGroup1, CheckBox cbJd, CheckBox cbMt, CheckBox cbElm, CheckBox cbEbai)
    {
        if (mScreeningGoodsBean != null)
        {
            String stock = mScreeningGoodsBean.getStock();
            if (!TextUtils.isEmpty(stock))
            {
                int stockPosition;
                switch (stock)
                {
                    case MyConstant.STR_OUT_STOCK:
                        stockPosition = 1;
                        break;
                    case MyConstant.STR_LESS_STOCK:
                        stockPosition = 2;
                        break;
                    case MyConstant.STR_IN_STOCK:
                    default:
                        stockPosition = 0;
                        break;
                }
                radioGroup0.check(radioGroup0.getChildAt(stockPosition)
                        .getId());
            }

            String status = mScreeningGoodsBean.getStatus();
            if (!TextUtils.isEmpty(status))
            {
                int statusPosition;
                switch (status)
                {
                    case MyConstant.STR_LOWER_FRAME:
                        statusPosition = 1;
                        break;
                    case MyConstant.STR_ON_FRAME:
                    default:
                        statusPosition = 0;
                        break;
                }
                radioGroup1.check(radioGroup1.getChildAt(statusPosition)
                        .getId());
            }

            List<String> platformList = mScreeningGoodsBean.getPlatform();
            if (platformList != null)
            {
                for (int i = 0; i < platformList.size(); i++)
                {
                    String platform = platformList.get(i);
                    if (!TextUtils.isEmpty(platform))
                    {
                        switch (platform)
                        {
                            case MyConstant.STR_EN_JDDJ:
                                cbJd.setChecked(true);
                                break;
                            case MyConstant.STR_EN_MT:
                                cbMt.setChecked(true);
                                break;
                            case MyConstant.STR_EN_ELEME:
                                cbElm.setChecked(true);
                                break;
                            case MyConstant.STR_EN_EBAI:
                                cbEbai.setChecked(true);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void initView()
    {
        initData();
        initleftRecyclerView();
        initRightRecyclerView();
        initListener();

        httpGetMarketCategory();

        mFrom = 0;
        mSearchGoodsAdapter.clearDataList();
        httpGetGoodsByMarketCategoryId(true);

        mIvNotification.post(mRunnable);
    }

    private void httpGetMarketCategory()
    {
        if (myApp != null)
        {
            if (myApp.mUser != null && myApp.mMarketCategoryList == null)
            {
                UserBean.ExtraBean extra = myApp.mUser.getExtra();
                if (extra != null)
                {
                    String market_id = extra.getMarket_id();
                    if (!TextUtils.isEmpty(market_id))
                    {
                        ApiUtils.getInstance()
                                .okgoGetMarketCategory(mContext, myApp.mToken, market_id, new ApiResultListener()
                                {
                                    @Override
                                    public void onSuccess(Response<String> response)
                                    {
                                        myApp.mMarketCategoryList = new Gson().fromJson(response.body(), new TypeToken<List<CategoryBean>>()
                                        {
                                        }.getType());
                                        if (myApp.mMarketCategoryList != null && myApp.mMarketCategoryList.size() > 0)
                                        {
                                            mMarketCategoryAdapter.setDataList(myApp.mMarketCategoryList);
                                        }
                                    }

                                    @Override
                                    public void onError(Response<String> response)
                                    {

                                    }
                                });
                    }
                }
            }
        }
    }

    private void initListener()
    {
        mMarketCategoryAdapter.setOnRecyclerViewGroupItemClickListener((view, position) -> {
            CategoryBean groupCategoryBean = mMarketCategoryAdapter.getGroupBean(position);
            if (groupCategoryBean != null)
            {
                List<CategoryBean> children = groupCategoryBean.getChildren();
                if (children == null || children.size() == 0)
                {
                    mTvGoodsType.setText(groupCategoryBean.getName());
                    mCurrentCategoryId = groupCategoryBean.getId();

                    mFrom = 0;
                    mSearchGoodsAdapter.clearDataList();
                    httpGetGoodsByMarketCategoryId(true);
                }
            }
        });

        mMarketCategoryAdapter.setMyOnItemClickListener((view, position) -> {
            CategoryBean categoryBean = (CategoryBean) view.getTag();
            mBtnAllCategory.setBackgroundResource(R.color.white);
            mBtnOtherCategory.setBackgroundResource(R.color.white);
            if (categoryBean != null)
            {
                String id = categoryBean.getId();
                if (!TextUtils.isEmpty(id) && !id.equals(mCurrentCategoryId))
                {
                    mTvGoodsType.setText(categoryBean.getName());
                    mCurrentCategoryId = id;

                    mFrom = 0;
                    mSearchGoodsAdapter.clearDataList();
                    httpGetGoodsByMarketCategoryId(true);
                }
            }
        });

        mSearchGoodsAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            Intent intent = new Intent(getActivity(), GoodsInfoActivity.class);
            intent.putExtra(MyConstant.STR_BEAN, goodsBean);
            startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
        });

        mSearchGoodsAdapter.setTopClickListener((view, position) -> {
            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            if (goodsBean != null)
            {
                GoodsBean.ProductBean product = goodsBean.getProduct();
                if (product != null)
                {
                    int top;
                    if (product.getTop() == 0)
                    {
                        top = 1;
                    } else
                    {
                        top = 0;
                    }
                    httpPostActionTop(top, product.getId());
                }
            }
        });

        mSearchGoodsAdapter.setUpdatePriceAndStockClickListener((view, position) -> {

            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            if (goodsBean != null)
            {
                Intent intent = new Intent(getActivity(), UpdateInventoryPriceAllPlatformActivity.class);
                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
            }
        });

        mSearchGoodsAdapter.setmOnRecyclerViewPlatformClickListener((view, position, platform) -> {

            GoodsBean goodsBean = mSearchGoodsAdapter.getItemData(position);
            if (goodsBean != null)
            {
                Intent intent = new Intent(getActivity(), UpdateInventoryPriceOnePlatformActivity.class);
                intent.putExtra(MyConstant.STR_BEAN, goodsBean);
                intent.putExtra(MyConstant.STR_PLATFORM, platform);
                startActivityForResult(intent, REQUEST_CODE_UPDA_GOODS_INFO);
            }
        });

        mSearchGoodsAdapter.setRefreshListener((view, position) -> {
            if (mSmartRefreshLayout != null)
            {
                mSmartRefreshLayout.autoRefresh();
            }
        });

        mSearchGoodsAdapter.setUpdateSaleTimeListener((view, position) -> {
            mUpdatePosition = position;
            Intent intent = new Intent(mContext, SellingTimeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_SALE_TIME);
        });

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                mSearchGoodsAdapter.clearDataList();
                httpGetGoodsByMarketCategoryId(false);
                refreshLayout.finishRefresh(5000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                httpGetGoodsByMarketCategoryId(false);
                refreshLayout.finishLoadMore(5000);
            }
        });
    }

    private void httpPostActionTop(int top, String goodsId)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id) && !TextUtils.isEmpty(goodsId))
            {
                MyGoodsBean myGoodsBean = new MyGoodsBean();
                myGoodsBean.setTop(String.valueOf(top));
                ApiUtils.getInstance()
                        .okgoPostUpdateStoreGoodsInfo(mContext, myApp.mToken, store_id, goodsId, myGoodsBean, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                mFrom = 0;
                                mSearchGoodsAdapter.clearDataList();
                                httpGetGoodsByMarketCategoryId(true);
                            }

                            @Override
                            public void onError(Response<String> response)
                            {

                            }
                        });
            }
        }
    }

    private void httpGetGoodsByMarketCategoryId(boolean isShowDialog)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                String query = createQuery();
                ApiUtils.getInstance()
                        .okgoGetGoodsByMarketCategoryId(mContext, myApp.mToken, store_id, mCurrentCategoryId, query, String.valueOf(mFrom), isShowDialog, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                if (mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.finishRefresh();
                                    mSmartRefreshLayout.finishLoadMore();
                                }
                                onSuccessGoodsData(response.body());
                            }

                            @Override
                            public void onError(Response<String> response)
                            {
                                if (mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.finishRefresh();
                                    mSmartRefreshLayout.finishLoadMore();
                                }
                            }
                        });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    private String createQuery()
    {
        String query = null;
        int coutQuery = 0;
        if (mScreeningGoodsBean != null)
        {
            try
            {
                JSONObject jsonObject = new JSONObject();
                String stock = mScreeningGoodsBean.getStock();
                if (!TextUtils.isEmpty(stock))
                {
                    jsonObject.put(MyConstant.STR_STOCK, stock);
                    coutQuery++;
                }
                String status = mScreeningGoodsBean.getStatus();
                if (!TextUtils.isEmpty(status))
                {
                    jsonObject.put(MyConstant.STR_STATUS, status);
                    coutQuery++;
                }
                List<String> platformList = mScreeningGoodsBean.getPlatform();
                if (platformList != null && platformList.size() > 0)
                {
                    coutQuery += platformList.size();
                    String strPlatform = mGson.toJson(platformList, new TypeToken<List<String>>()
                    {
                    }.getType());
                    jsonObject.put(MyConstant.STR_PLATFORM, strPlatform);
                }
                query = jsonObject.toString();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        if (coutQuery == 0)
        {
            mTvScreening.setText("筛选");
        } else
        {
            mTvScreening.setText("筛选(" + coutQuery + ")");
        }
        return query;
    }

    @SuppressLint("DefaultLocale")
    private void onSuccessGoodsData(String body)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has(MyConstant.STR_TOTAL))
            {
                int total = jsonObject.getInt(MyConstant.STR_TOTAL);
                mTvGoodsCount.setHint(String.format("共有%d件商品", total));
                if (total == 0 && mFrom == 0)
                {
                    mTvNoData.setVisibility(View.VISIBLE);
                } else
                {
                    mTvNoData.setVisibility(View.GONE);
                }
            }

            if (jsonObject.has(MyConstant.STR_PRODUCTS) || jsonObject.has("articles"))
            {
                String strGoods;
                if (jsonObject.has(MyConstant.STR_PRODUCTS))
                {
                    strGoods = jsonObject.getString(MyConstant.STR_PRODUCTS);
                } else
                {
                    strGoods = jsonObject.getString("articles");
                }

                List<GoodsBean> goodsBeanList = mGson.fromJson(strGoods, new TypeToken<List<GoodsBean>>()
                {
                }.getType());
                if (goodsBeanList != null)
                {
                    int size = goodsBeanList.size();
                    if (size > 0)
                    {
                        if (mFrom == 0)
                        {
                            mSearchGoodsAdapter.clearDataList();
                        }
                        if (mFrom == 10 && size > 7) {
                            GoodsBean goodsBean = goodsBeanList.get(7);
                            Log.e("bean", mGson.toJson(goodsBean));
                        }
                        mFrom += size;
                        mSearchGoodsAdapter.addDataList(goodsBeanList);
                    }
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initRightRecyclerView()
    {
        mRvGoods.setLayoutManager(new LinearLayoutManager(mContext));
        mRvGoods.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        mSearchGoodsAdapter = new SearchGoodsAdapter(R.layout.item_right_goods, true);
        mRvGoods.setAdapter(mSearchGoodsAdapter);
    }

    private void initleftRecyclerView()
    {
        mRvGoodsTypes.setLayoutManager(new LinearLayoutManager(mContext));
        mMarketCategoryAdapter = new ClassifyGoodsAdapter();
        mRvGoodsTypes.setAdapter(mMarketCategoryAdapter);
    }

    private void initData()
    {

    }
}
