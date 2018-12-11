package cn.jinxiit.zebra.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.product.UpdateInventoryPriceAllPlatformActivity;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class UpdatePriceAndInventoryComponent {
    @BindView(R.id.iv_img)
    ImageView mIvImg;
    @BindView(R.id.iv_tag)
    ImageView mIvTag;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_product_no)
    TextView mTvProductNo;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_stock)
    TextView mTvStock;
    @BindView(R.id.et_price)
    EditText mEtPrice;
    @BindView(R.id.et_stock)
    EditText mEtStock;
    @BindView(R.id.tv_cannot_update)
    TextView mTvCannotUpdate;
    @BindView(R.id.tv_pay_status)
    TextView mTvStatus;
    @BindView(R.id.btn_update_status)
    Button mBtnUpdateStatus;
    @BindView(R.id.tv_chengben)
    TextView mTvChengBen;
    @BindView(R.id.ll_update_jinjia)
    View mViewUpdateJinJia;
    @BindView(R.id.et_jinjia)
    EditText mEtJinjia;

    private Activity mContext;
    private GoodsBean mGoodsBean;
    private String mPlatform;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public UpdatePriceAndInventoryComponent(View root, GoodsBean goodsBean, String platform) {
        mContext = (Activity) root.getContext();
        this.mGoodsBean = goodsBean;
        this.mPlatform = platform;
        ButterKnife.bind(this, root);
        initData();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    @OnClick({R.id.btn_jian_jinjia, R.id.btn_jia_jinjia, R.id.btn_jian_price, R.id.btn_jia_price, R.id.btn_clear_stock, R.id.btn_max_stock, R.id.btn_update_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_jian_jinjia:
                clickJian(mEtJinjia);
                break;
            case R.id.btn_jia_jinjia:
                clickJia(mEtJinjia);
                break;
            case R.id.btn_jian_price:
                clickJian(mEtPrice);
                break;
            case R.id.btn_jia_price:
                clickJia(mEtPrice);
                break;
            case R.id.btn_clear_stock:
                mEtStock.setText("0");
                break;
            case R.id.btn_max_stock:
                mEtStock.setText("999");
                break;
            case R.id.btn_update_status:
                clickBtnShangJia();
                break;
        }
    }

    public void setmGoodsBean(GoodsBean goodsBean) {
        this.mGoodsBean = goodsBean;
    }

    public void initShangJiaStatus() {
        if (mGoodsBean != null && !TextUtils.isEmpty(mPlatform)) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                if (MyConstant.STR_All.equals(mPlatform)) {
                    String product_status = product.getProduct_status();
                    if (MyConstant.STR_FALSE.equals(product_status)) {
                        mTvStatus.setVisibility(View.VISIBLE);
                        mBtnUpdateStatus.setText("设置上架");
                    } else if (MyConstant.STR_TRUE.equals(product_status)) {
                        mTvStatus.setVisibility(View.GONE);
                        mBtnUpdateStatus.setText("设置下架");
                    }
                } else {
                    List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product
                            .getPriceAndStock();
                    if (priceAndStockBeanList != null) {
                        for (int i = 0; i < priceAndStockBeanList.size(); i++) {
                            GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList
                                    .get(i);
                            if (priceAndStockBean != null) {
                                String third_type = priceAndStockBean.getThird_type();
                                if (mPlatform.equals(third_type)) {
                                    String product_status = priceAndStockBean.getProduct_status();
                                    if (MyConstant.STR_FALSE.equals(product_status)) {
                                        mTvStatus.setVisibility(View.VISIBLE);
                                        mBtnUpdateStatus.setText("设置上架");
                                    } else if (MyConstant.STR_TRUE.equals(product_status)) {
                                        mTvStatus.setVisibility(View.GONE);
                                        mBtnUpdateStatus.setText("设置下架");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void clickBtnShangJia() {
        if (mGoodsBean != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                String product_status = product.getProduct_status();
                if (mPlatform.equals(MyConstant.STR_All)) {
                    if (MyConstant.STR_TRUE.equals(product_status)) {
                        setXiaJiaStatus();
                    } else if (MyConstant.STR_FALSE.equals(product_status)) {
                        setShangJiaStatus();
                    }
                } else {
                    List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStock = product
                            .getPriceAndStock();
                    for (int i = 0; i < priceAndStock.size(); i++) {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStock
                                .get(i);
                        String third_type = priceAndStockBean.getThird_type();

                        //判断点击了那个平台
                        if (mPlatform.equals(third_type)) {
                            String platform_status = priceAndStockBean.getProduct_status();
                            //状态为上架时 ，设置为下架状态
                            if (MyConstant.STR_TRUE.equals(platform_status)) {
                                setXiaJiaStatus();
                            } else if (MyConstant.STR_FALSE
                                    .equals(platform_status)) {  //状态为下架时，设置为上架状态
                                setShangJiaStatus();
                            }
                        }
                    }
                }
            }
        }
    }

    private void setShangJiaStatus() {
        MyGoodsBean myGoodsBean = new MyGoodsBean();
        //判断点击的平台设置myGoodsBean状态
        setPlatformStatus(myGoodsBean, MyConstant.STR_TRUE);
        httpUpdateGoodsInfo(myGoodsBean, MyConstant.STR_TRUE);
    }

    private void setXiaJiaStatus() {
        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_set_theshelves, null);
        dialog.setContentView(dialogContent);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources().getColor(R.color.colorTm));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        dialogContent.measure(0, 0);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        lp.width = view.getMeasuredWidth();
        lp.width = WindowUtils.dip2px(mContext, 280);
        lp.alpha = 5f; // 透明度
        window.setAttributes(lp);
        RadioGroup radioGroup = dialogContent.findViewById(R.id.rg_dialog_title);
        final TextView tvAutomatic = dialogContent.findViewById(R.id.tv_automatic_shelves);
        final TextView tvDateTime = dialogContent.findViewById(R.id.tv_datetime);
        Button btnNo = dialogContent.findViewById(R.id.btn_no);
        Button btnYes = dialogContent.findViewById(R.id.btn_yes);
        tvDateTime.setVisibility(View.GONE);
        tvAutomatic.setVisibility(View.VISIBLE);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int radioGroupPosition = group.indexOfChild(group.findViewById(checkedId));
            if (radioGroupPosition == 0) {
                tvDateTime.setVisibility(View.INVISIBLE);
                tvAutomatic.setVisibility(View.VISIBLE);
            } else {
                tvDateTime.setVisibility(View.VISIBLE);
                tvAutomatic.setVisibility(View.INVISIBLE);
            }
        });
        tvDateTime.setOnClickListener(v -> showTimePickView((TextView) v));
        btnYes.setOnClickListener(v -> {
            int radioPosition = radioGroup
                    .indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
            MyGoodsBean myGoodsBean = new MyGoodsBean();
            if (radioPosition != 0) {
                String strTime = tvDateTime.getText().toString().trim();
                if (TextUtils.isEmpty(strTime)) {
                    MyToastUtils.error("请选择自动上架时刻");
                    return;
                }

                long time = 0;
                try {
                    time = dateFormat.parse(strTime).getTime() / 1000;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                myGoodsBean.setStatus_time(time + "");
            }
            dialog.dismiss();
            setPlatformStatus(myGoodsBean, MyConstant.STR_FALSE);
            myGoodsBean.setAuto_status(MyConstant.STR_TRUE);
            httpUpdateGoodsInfo(myGoodsBean, MyConstant.STR_FALSE);
        });
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            MyGoodsBean myGoodsBean = new MyGoodsBean();
            setPlatformStatus(myGoodsBean, MyConstant.STR_FALSE);
            httpUpdateGoodsInfo(myGoodsBean, MyConstant.STR_FALSE);
        });
        dialog.show();
    }

    private void setPlatformStatus(MyGoodsBean myGoodsBean, String status) {
        switch (mPlatform) {
            case MyConstant.STR_All:
                myGoodsBean.setProduct_status(status);
                break;
            case MyConstant.STR_EN_JDDJ:
                myGoodsBean.setJd_status(status);
                break;
            case MyConstant.STR_EN_MT:
                myGoodsBean.setMeituan_status(status);
                break;
            case MyConstant.STR_EN_ELEME:
                myGoodsBean.setEleme_status(status);
                break;
            case MyConstant.STR_EN_EBAI:
                myGoodsBean.setEbai_status(status);
                break;
        }
    }

    private void httpUpdateGoodsInfo(MyGoodsBean myGoodsBean, String status) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp.mCurrentStoreOwnerBean != null && mGoodsBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            String product_id = mGoodsBean.getProduct().getId();
            if (!TextUtils.isEmpty(store_id) && !TextUtils.isEmpty(product_id)) {
                ApiUtils.getInstance()
                        .okgoPostUpdateStoreGoodsInfo(mContext, myApp.mToken, store_id, product_id,
                                myGoodsBean, new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        GoodsBean goodsBean = new Gson()
                                                .fromJson(response.body(), GoodsBean.class);
                                        if (goodsBean != null) {
                                            mGoodsBean = goodsBean;
                                            if (mContext instanceof UpdateInventoryPriceAllPlatformActivity) {
                                                ((UpdateInventoryPriceAllPlatformActivity) mContext)
                                                        .refreshShangjiaStatus(mGoodsBean);
                                            } else {
                                                setOnePlatformStatus(status);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        MyToastUtils.error("修改失败");
                                    }
                                });
            }
        }
    }

    private void setOnePlatformStatus(String status) {
        if (MyConstant.STR_FALSE.equals(status)) {
            mTvStatus.setVisibility(View.VISIBLE);
            mBtnUpdateStatus.setText("设置上架");
        } else if (MyConstant.STR_TRUE.equals(status)) {
            mTvStatus.setVisibility(View.GONE);
            mBtnUpdateStatus.setText("设置下架");
        }
    }

    private void showTimePickView(TextView textView) {
        String strDate = textView.getText().toString().trim();
        Date currentDate = null;
        if (!TextUtils.isEmpty(strDate)) {
            try {
                currentDate = dateFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Calendar limitStartCalendar = Calendar.getInstance();
        Calendar limitEndCalendar = Calendar.getInstance();
        limitEndCalendar.add(Calendar.YEAR, 1);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(mContext, (date, v) -> {//选中事件回调
            String strTime = dateFormat.format(date);
            if (!TextUtils.isEmpty(strTime)) {
                textView.setText(strTime);
            }
        }).setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                //                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择上架时刻")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                //                .setTitleColor(Color.BLACK)//标题文字颜色
                //                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                //                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                //                .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                .setRangDate(limitStartCalendar, limitEndCalendar)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", null)//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true);//是否显示为对话框样式

        if (currentDate != null) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);
            timePickerBuilder.setDate(currentCalendar);
        }

        timePickerBuilder.build().show();
    }

    @SuppressLint("DefaultLocale")
    private void clickJian(TextView tv) {
        String strPrice = tv.getText().toString().trim();
        if (TextUtils.isEmpty(strPrice)) {
            strPrice = "0.00";
        }

        double aDouble = Double.parseDouble(strPrice) - 0.5;

        if (aDouble < 0) {
            aDouble = 0;
        }
        tv.setText(String.format("%.2f", aDouble));
    }

    @SuppressLint("DefaultLocale")
    private void clickJia(TextView tv) {
        String strPrice = tv.getText().toString().trim();
        if (TextUtils.isEmpty(strPrice)) {
            strPrice = "0.00";
        }

        double aDouble = Double.parseDouble(strPrice) + 0.5;

        tv.setText(String.format("%.2f", aDouble));
    }

    private void initListener() {
        mEtPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mEtPrice.setText("");
            }
        });
        mEtStock.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mEtStock.setText("");
            }
        });
        mEtJinjia.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mEtJinjia.setText("");
            }
        });
        mEtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() > 0) {
                    if (".".equals(str.substring(0, 1))) {
                        str = "0" + str;
                        mEtPrice.setText(str);
                        mEtPrice.setSelection(str.length());
                    }

                    if (str.length() >= 2) {
                        String onChart = str.substring(0, 1);
                        String twoChart = str.substring(1, 2);
                        if ("0".equals(onChart) && !twoChart.equals(".")) {
                            str = str.substring(1);
                            mEtPrice.setText(str);
                            mEtPrice.setSelection(str.length());
                        }
                    }
                }
            }
        });

        mEtStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() >= 2) {
                    String onChart = str.substring(0, 1);
                    String twoChart = str.substring(1, 2);
                    if ("0".equals(onChart) && !twoChart.equals(".")) {
                        str = str.substring(1);
                        mEtStock.setText(str);
                        mEtStock.setSelection(str.length());
                    }
                }
            }
        });

        mEtJinjia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() > 0) {
                    if (".".equals(str.substring(0, 1))) {
                        str = "0" + str;
                        mEtJinjia.setText(str);
                        mEtJinjia.setSelection(str.length());
                    }

                    if (str.length() >= 2) {
                        String onChart = str.substring(0, 1);
                        String twoChart = str.substring(1, 2);
                        if ("0".equals(onChart) && !twoChart.equals(".")) {
                            str = str.substring(1);
                            mEtJinjia.setText(str);
                            mEtJinjia.setSelection(str.length());
                        }
                    }
                }

                String strJinjia = mEtJinjia.getText().toString().trim();
                if (!TextUtils.isEmpty(strJinjia)) {
                    double chenBen = ZebraUtils.getInstance()
                            .jsChenBen(Double.valueOf(strJinjia), mGoodsBean);
                    mTvChengBen.setHint(String.format(" /成本: ¥%.2f", chenBen));
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initData() {
        if (mGoodsBean != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                double jinjia = product.getPurchase_price() * 0.01;
                mEtJinjia.setText(String.format("%.2f", jinjia));
                mTvChengBen.setHint(String.format(" /成本: ¥%.2f",
                        ZebraUtils.getInstance().jsChenBen(jinjia, mGoodsBean)));
                List<String> images = product.getImages();
                if (images != null && images.size() > 0) {
                    String fileKey = images.get(0);
                    if (!TextUtils.isEmpty(fileKey)) {
                        String imgUrl = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                        int width = WindowUtils.dip2px(mContext, 80);
                        MyPicassoUtils.downSizeImageForUrl(width, width, imgUrl, mIvImg);
                    }
                }
                ViewSetDataUtils.textViewSetText(mTvName,
                        ZebraUtils.getInstance().showGoodName(mGoodsBean));
                String product_code = product.getProduct_code();
                if (!TextUtils.isEmpty(product_code)) {
                    mTvProductNo.setHint("商品编码：" + product_code);
                }
            }
        }

        switch (mPlatform) {
            case MyConstant.STR_All:
                initAll();
                break;
            case MyConstant.STR_EN_JDDJ:
                initPlatformData(MyConstant.STR_EN_JDDJ);
                break;
            case MyConstant.STR_EN_MT:
                initPlatformData(MyConstant.STR_EN_MT);
                break;
            case MyConstant.STR_EN_ELEME:
                initPlatformData(MyConstant.STR_EN_ELEME);
                break;
            case MyConstant.STR_EN_EBAI:
                initPlatformData(MyConstant.STR_EN_EBAI);
                break;
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initPlatformData(String platform) {
        if (mGoodsBean != null) {
            switch (platform) {
                case MyConstant.STR_EN_JDDJ:
                    mIvTag.setImageResource(R.drawable.tag_jd);
                    break;
                case MyConstant.STR_EN_MT:
                    mIvTag.setImageResource(R.drawable.tag_mt);
                    break;
                case MyConstant.STR_EN_ELEME:
                    mIvTag.setImageResource(R.drawable.tag_eleme);
                    break;
                case MyConstant.STR_EN_EBAI:
                    mIvTag.setImageResource(R.drawable.tag_ebai);
                    break;
            }
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product
                        .getPriceAndStock();
                if (priceAndStockBeanList != null) {
                    for (int i = 0; i < priceAndStockBeanList.size(); i++) {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList
                                .get(i);
                        if (priceAndStockBean != null) {
                            String third_type = priceAndStockBean.getThird_type();
                            if (platform.equals(third_type)) {
                                String product_status = priceAndStockBean.getProduct_status();
                                if (MyConstant.STR_FALSE.equals(product_status)) {
                                    mTvStatus.setVisibility(View.VISIBLE);
                                    mBtnUpdateStatus.setText("设置上架");
                                } else if (MyConstant.STR_TRUE.equals(product_status)) {
                                    mTvStatus.setVisibility(View.GONE);
                                    mBtnUpdateStatus.setText("设置下架");
                                }
                                int binded = priceAndStockBean.getBinded();
                                if (binded == 0) {
                                    mTvCannotUpdate.setVisibility(View.VISIBLE);
                                } else if (binded == 1) {
                                    String strPrice = String.format("%.2f",
                                            (double) priceAndStockBean.getPrice() * 0.01);
                                    mTvPrice.setText("售价：¥" + strPrice);
                                    mEtPrice.setText(strPrice);
                                    long stock = priceAndStockBean.getStock();
                                    mTvStock.setText("库存: " + stock);
                                    mEtStock.setText(stock + "");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initAll() {
        mTvChengBen.setVisibility(View.VISIBLE);
        mViewUpdateJinJia.setVisibility(View.VISIBLE);
        if (mGoodsBean != null) {
            mIvTag.setVisibility(View.GONE);
            mEtPrice.setText("0.00");
            mEtStock.setText("0");
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                String product_status = product.getProduct_status();
                if ("false".equals(product_status)) {
                    mTvStatus.setVisibility(View.VISIBLE);
                    mBtnUpdateStatus.setText("设置上架");
                } else if ("true".equals(product_status)) {
                    mTvStatus.setVisibility(View.GONE);
                    mBtnUpdateStatus.setText("设置下架");
                }
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product
                        .getPriceAndStock();
                if (priceAndStockBeanList != null) {
                    long minPrice = -1;
                    long maxPrice = -1;
                    long totalStock = 0;

                    for (int i = 0; i < priceAndStockBeanList.size(); i++) {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList
                                .get(i);
                        if (priceAndStockBean != null && priceAndStockBean.getBinded() == 1) {
                            long price = priceAndStockBean.getPrice();
                            if (minPrice == -1 || minPrice > price) {
                                minPrice = price;
                            }

                            if (maxPrice == -1 || maxPrice < price) {
                                maxPrice = price;
                            }
                            totalStock += priceAndStockBean.getStock();
                        }
                    }

                    if (minPrice == -1) {
                        minPrice = 0;
                        maxPrice = 0;
                    }
                    mTvStock.setHint("库存: " + totalStock);
                    mEtPrice.setText(String.format("%.2f", (double) minPrice * 0.01));
                    mEtStock.setText(totalStock + "");
                    @SuppressLint("DefaultLocale") String strPrice = String
                            .format("售价:¥%.2f~%.2f", (double) minPrice * 0.01,
                                    (double) maxPrice * 0.01);
                    if (maxPrice == minPrice || strPrice.length() > 17) {
                        mTvPrice.setText(String.format("售价: ¥%.2f", (double) minPrice * 0.01));
                    } else {
                        mTvPrice.setText(strPrice);
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public List<String> updatePriceAndStock() {
        List<String> list = new ArrayList<>();
        String strPrice = mEtPrice.getText().toString().trim();
        if (TextUtils.isEmpty(strPrice)) {
            strPrice = "0";
        }
        list.add(strPrice);
        String strStock = mEtStock.getText().toString().trim();
        if (TextUtils.isEmpty(strStock)) {
            strStock = "0";
        }
        list.add(strStock);
        if (MyConstant.STR_All.equals(mPlatform)) {
            String strJinjia = mEtJinjia.getText().toString().trim();
            if (TextUtils.isEmpty(strJinjia)) {
                strJinjia = "0";
            }
            list.add(strJinjia);
            String strChenBen = "0";
            if (!TextUtils.isEmpty(strJinjia)) {
                strChenBen = String.format("%.2f",
                        ZebraUtils.getInstance().jsChenBen(Double.valueOf(strJinjia), mGoodsBean));
            }
            list.add(strChenBen);
        }
        return list;
    }
}
