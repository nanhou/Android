package cn.jinxiit.zebra.activities.product;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.product.batchupdate.BatchUpdateStockActivity;
import cn.jinxiit.zebra.activities.product.creategoods.SellingTimeActivity;
import cn.jinxiit.zebra.adapters.AnalysisiGoodsAdapter;
import cn.jinxiit.zebra.adapters.BatchProductAdapter;
import cn.jinxiit.zebra.adapters.TextCenterAdapter;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.LimitDateBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class BatchProductManageActivity extends BaseActivity
{
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_classification)
    ConstraintLayout mLayoutClassification;
    @BindView(R.id.recyclerview0)
    RecyclerView mRecyclerview0;
    @BindView(R.id.recyclerview1)
    RecyclerView mRecyclerview1;
    @BindView(R.id.cb_goods_all)
    CheckBox mCbGoodsAll;
    @BindView(R.id.cb_goods_other)
    CheckBox mCbGoodsOther;
    @BindView(R.id.recycler_view_product)
    RecyclerView mRecyclerViewProduct;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.btn_shangjia)
    Button mBtnShangjia;
    @BindView(R.id.btn_xiajia)
    Button mBtnXiajia;
    @BindView(R.id.btn_update_stock)
    Button mBtnUpdateStock;
    @BindView(R.id.btn_more)
    Button mBtnMore;

    private @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private AnalysisiGoodsAdapter mAnalysisiGoodsAdapter;
    private TextCenterAdapter mTextCenterAdapter;
    private BatchProductAdapter mBatchProductAdapter;
    private MyApp myApp;
    private String mCurrentCategoryId;
    private int mFrom;
    private Gson mGson = new Gson();

    private static final int REQUEST_CODE_DIY_STOCK = 0;
    private static final int REQUEST_CODE_UPDATE_SALE_TIME = 1;

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_product_manage);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return !disClassification(ev) && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy()
    {
        setResult(0);
        super.onDestroy();

        mAnalysisiGoodsAdapter = null;
        mTextCenterAdapter = null;
        mBatchProductAdapter = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_DIY_STOCK && resultCode == 1)
        {
            if (data != null)
            {
                String goodsJson = data.getStringExtra(MyConstant.STR_JSON);
                if (!TextUtils.isEmpty(goodsJson))
                {
                    httpPostBatchUpdateGoods(goodsJson);
                }
            }
        }

        if (requestCode == REQUEST_CODE_UPDATE_SALE_TIME && resultCode == 1)
        {
            saleTimeResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.view_gone_bottom, R.id.tv_clear_selected, R.id.btn_more, R.id.btn_update_stock, R.id.btn_shangjia, R.id.btn_xiajia, R.id.iv_back, R.id.tv_title})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_clear_selected:
                mBatchProductAdapter.clearSelectedList();
                selectedListLayout();
                break;
            case R.id.btn_more:
                clickMore();
                break;
            case R.id.btn_update_stock:
                clickUpdateStock();
                break;
            case R.id.btn_shangjia:
                clickShangJia();
                break;
            case R.id.btn_xiajia:
                clickXiaJia();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.view_gone_bottom:
            case R.id.tv_title:
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                break;
        }
    }

    private void saleTimeResult(Intent data)
    {
        if (data != null)
        {
            LimitDateBean limitDateBean = data.getParcelableExtra(MyConstant.STR_BEAN);
            if (limitDateBean != null)
            {
                String json = mGson.toJson(limitDateBean, limitDateBean.getClass());
                if (!TextUtils.isEmpty(json))
                {
                    JSONArray jsonArray = new JSONArray();
                    ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
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
                        httpPostBatchUpdateGoods(jsonArray.toString());
                    }
                }
            }
        }
    }

    private void clickShangJia()
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList != null)
        {
            int size = goodsBeanArrayList.size();
            if (size == 0)
            {
                MyToastUtils.error("还未选中任何商品");
                return;
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < size; i++)
            {
                GoodsBean goodsBean = goodsBeanArrayList.get(i);
                if (goodsBean != null)
                {
                    String product_id = goodsBean.getProduct()
                            .getId();
                    if (!TextUtils.isEmpty(product_id))
                    {
                        JSONObject jsonObject = new JSONObject();
                        try
                        {
                            jsonObject.put(MyConstant.STR_ID, product_id);
                            jsonObject.put(MyConstant.STR_PRODUCT_STATUS, MyConstant.STR_TRUE);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (jsonArray.length() > 0)
            {
                httpPostBatchUpdateGoods(jsonArray.toString());
            }
        }
    }

    private void clickMore()
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            return;
        }
        @SuppressLint("InflateParams") LinearLayout root = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_batch_manage_action_more, null);
        Dialog dialog = ZebraUtils.getInstance()
                .createButtonDialog(mContext, root);
        root.findViewById(R.id.btn_update_category)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    updateCategory();
                });
        root.findViewById(R.id.btn_update_sale_time)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    MyActivityUtils.skipActivityForResult(mContext, SellingTimeActivity.class, REQUEST_CODE_UPDATE_SALE_TIME);
                });
        root.findViewById(R.id.btn_update_pack_fee)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    updatePackFeeOrMinPurchase(true);
                });
        root.findViewById(R.id.btn_update_min_purchase)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    updatePackFeeOrMinPurchase(false);
                });
        root.findViewById(R.id.btn_cancel)
                .setOnClickListener(view -> dialog.cancel());
        dialog.show();
    }

    private void updatePackFeeOrMinPurchase(boolean isPackFee)
    {
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(this);
        EditText editText = rxDialogEditSureCancel.getEditText();
        if (isPackFee)
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setHint("单位:元");
            rxDialogEditSureCancel.setTitle("修改包装费");
        } else
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("请输入...");
            rxDialogEditSureCancel.setTitle("修改最小购买量");
        }
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");

        cancelView.setOnClickListener(v -> {
            String num = editText.getText()
                    .toString()
                    .trim();
            if (TextUtils.isEmpty(num) || ".".equals(num))
            {
                MyToastUtils.error("请输入");
                return;
            }
            rxDialogEditSureCancel.cancel();
            int numInt;
            if (isPackFee)
            {
                numInt = (int) (Double.parseDouble(num) * 100);
            } else
            {
                numInt = Integer.parseInt(num);
            }

            ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < goodsBeanArrayList.size(); i++)
            {
                GoodsBean goodsBean = goodsBeanArrayList.get(i);
                JSONObject jsonObject = new JSONObject();
                try
                {
                    jsonObject.put(MyConstant.STR_ID, goodsBean.getProduct()
                            .getId());
                    if (isPackFee)
                    {
                        jsonObject.put(MyConstant.STR_PACK_FEE, numInt);
                    } else
                    {
                        jsonObject.put(MyConstant.STR_MIN_PURCHASE, numInt);
                    }
                    jsonArray.put(jsonObject);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            httpPostBatchUpdateGoods(jsonArray.toString());
        });
        sureView.setOnClickListener(v -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    private void updateCategory()
    {
        if (myApp.mMarketCategoryList == null)
        {
            return;
        }

        List<String> options1items = new ArrayList<>();
        List<List<String>> options2items = new ArrayList<>();

        for (int i = 0; i < myApp.mMarketCategoryList.size(); i++)
        {
            CategoryBean categoryBean = myApp.mMarketCategoryList.get(i);
            if (categoryBean != null)
            {
                List<CategoryBean> children = categoryBean.getChildren();
                if (children != null && children.size() > 0)
                {
                    List<String> childList = new ArrayList<>();
                    for (int j = 0; j < children.size(); j++)
                    {
                        childList.add(children.get(j)
                                .getName());
                    }
                    options1items.add(categoryBean.getName());
                    options2items.add(childList);
                }
            }
        }

        if (options1items.size() == 0 || options2items.size() == 0)
        {
            MyToastUtils.error("分类不足，请先创建分类");
            return;
        }

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String category2Name = options2items.get(options1)
                    .get(option2);
            for (int i = 0; i < myApp.mMarketCategoryList.size(); i++)
            {
                CategoryBean categoryBean = myApp.mMarketCategoryList.get(i);
                List<CategoryBean> children = categoryBean.getChildren();
                for (int j = 0; j < children.size(); j++)
                {
                    CategoryBean childCategoryBean = children.get(j);
                    if (category2Name.equals(childCategoryBean.getName()))
                    {
                        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
                        JSONArray jsonArray = new JSONArray();
                        for (int q = 0; q < goodsBeanArrayList.size(); q++)
                        {
                            GoodsBean goodsBean = goodsBeanArrayList.get(q);
                            JSONObject jsonObject = new JSONObject();
                            try
                            {
                                jsonObject.put(MyConstant.STR_ID, goodsBean.getProduct()
                                        .getId());
                                jsonObject.put(MyConstant.STR_CATID, childCategoryBean.getId());
                                jsonArray.put(jsonObject);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        httpPostBatchUpdateGoods(jsonArray.toString());
                        break;
                    }
                }
            }
        }).setTitleText("移动分类")
                .isDialog(true)
                .build();

        pvOptions.setPicker(options1items, options2items);
        pvOptions.show();
    }

    private void clickUpdateStock()
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            return;
        }

        @SuppressLint("InflateParams") LinearLayout root = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_batch_manage_action_stock, null);
        Dialog dialog = ZebraUtils.getInstance()
                .createButtonDialog(mContext, root);

        root.findViewById(R.id.btn_min)
                .setOnClickListener(v -> batchUpdateStock(dialog, 0));

        root.findViewById(R.id.btn_max)
                .setOnClickListener(v -> batchUpdateStock(dialog, 999));

        root.findViewById(R.id.btn_cancel)
                .setOnClickListener(view -> dialog.cancel());

        root.findViewById(R.id.btn_batch)
                .setOnClickListener(v -> {
                    clickBatchUpdateStock();
                    dialog.cancel();
                });

        root.findViewById(R.id.btn_update_list)
                .setOnClickListener(v -> {
                    dialog.cancel();
                    ArrayList<GoodsBean> goodsBeanList = mBatchProductAdapter.getmSelectList();
                    if (goodsBeanList.size() > 0)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(MyConstant.STR_BEAN, goodsBeanList);
                        MyActivityUtils.skipActivityForResult(mContext, BatchUpdateStockActivity.class, bundle, REQUEST_CODE_DIY_STOCK);
                    } else
                    {
                        MyToastUtils.error("还未选中任何商品");
                    }
                });
        dialog.show();
    }

    private void batchUpdateStock(Dialog dialog, long num)
    {
        dialog.dismiss();
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < goodsBeanArrayList.size(); i++)
        {
            GoodsBean goodsBean = goodsBeanArrayList.get(i);
            if (goodsBean != null)
            {
                String product_id = goodsBean.getProduct()
                        .getId();
                if (!TextUtils.isEmpty(product_id))
                {
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put(MyConstant.STR_ID, product_id);
                        jsonObject.put(MyConstant.STR_STOCK, num);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (jsonArray.length() > 0)
        {
            httpPostBatchUpdateGoods(jsonArray.toString());
        }
    }

    private void clickBatchUpdateStock()
    {
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(mContext);
        rxDialogEditSureCancel.setTitle("修改库存");
        rxDialogEditSureCancel.getEditText()
                .setInputType(InputType.TYPE_CLASS_NUMBER);
        rxDialogEditSureCancel.setCanceledOnTouchOutside(true);
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {

            String num = rxDialogEditSureCancel.getEditText()
                    .getText()
                    .toString()
                    .trim();
            if (TextUtils.isEmpty(num))
            {
                MyToastUtils.error("请设置库存量");
                return;
            }
            batchUpdateStock(rxDialogEditSureCancel, Long.parseLong(num));
        });
        sureView.setOnClickListener(v -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    private void clickXiaJia()
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            return;
        }

        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_set_theshelves, null);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        if (window == null)
        {
            return;
        }
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources()
                        .getColor(R.color.colorTm));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        view.measure(0, 0);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        lp.width = view.getMeasuredWidth() - WindowUtils.dip2px(mContext, 80);
        lp.width = WindowUtils.dip2px(mContext, 280);
        lp.alpha = 1f; // 透明度
        window.setAttributes(lp);
        RadioGroup radioGroup = view.findViewById(R.id.rg_dialog_title);
        final TextView tvAutomatic = view.findViewById(R.id.tv_automatic_shelves);
        final TextView tvDateTime = view.findViewById(R.id.tv_datetime);
        Button btnYes = view.findViewById(R.id.btn_yes);
        Button btnNo = view.findViewById(R.id.btn_no);
        tvAutomatic.setVisibility(View.VISIBLE);
        tvDateTime.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (position == 0)
            {
                tvAutomatic.setVisibility(View.VISIBLE);
                tvDateTime.setVisibility(View.INVISIBLE);
            } else
            {
                tvAutomatic.setVisibility(View.INVISIBLE);
                tvDateTime.setVisibility(View.VISIBLE);
            }
        });
        tvDateTime.setOnClickListener(v -> showTimePickView((TextView) v));
        btnNo.setOnClickListener(v -> clickNo(dialog));
        btnYes.setOnClickListener(v -> clickYes(dialog, radioGroup, tvDateTime));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void clickNo(Dialog dialog)
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            dialog.dismiss();
            return;
        }

        dialog.dismiss();
        JSONArray jsonArray = new JSONArray();
        int size = goodsBeanArrayList.size();
        for (int i = 0; i < size; i++)
        {
            GoodsBean goodsBean = goodsBeanArrayList.get(i);
            if (goodsBean != null)
            {
                String product_id = goodsBean.getProduct()
                        .getId();
                if (!TextUtils.isEmpty(product_id))
                {
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put(MyConstant.STR_ID, product_id);
                        jsonObject.put(MyConstant.STR_PRODUCT_STATUS, MyConstant.STR_FALSE);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (jsonArray.length() > 0)
        {
            httpPostBatchUpdateGoods(jsonArray.toString());
        }
    }

    private void showTimePickView(TextView textView)
    {
        String strDate = textView.getText()
                .toString()
                .trim();
        Date currentDate = null;
        if (!TextUtils.isEmpty(strDate))
        {
            try
            {
                currentDate = dateFormat.parse(strDate);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        Calendar limitStartCalendar = Calendar.getInstance();
        Calendar limitEndCalendar = Calendar.getInstance();
        limitEndCalendar.add(Calendar.YEAR, 1);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(mContext, (date, v) -> {//选中事件回调
            String strTime = dateFormat.format(date);
            if (!TextUtils.isEmpty(strTime))
            {
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

        if (currentDate != null)
        {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);
            timePickerBuilder.setDate(currentCalendar);
        }

        timePickerBuilder.build()
                .show();
    }

    private void clickYes(Dialog dialog, RadioGroup radioGroup, TextView tvDateTime)
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList == null || goodsBeanArrayList.size() == 0)
        {
            MyToastUtils.error("还未选中任何商品");
            dialog.dismiss();
            return;
        }
        int position = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < goodsBeanArrayList.size(); i++)
        {
            GoodsBean goodsBean = goodsBeanArrayList.get(i);
            if (goodsBean != null)
            {
                String product_id = goodsBean.getProduct()
                        .getId();
                if (!TextUtils.isEmpty(product_id))
                {
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put(MyConstant.STR_ID, product_id);
                        jsonObject.put(MyConstant.STR_PRODUCT_STATUS, MyConstant.STR_FALSE);
                        jsonObject.put(MyConstant.STR_AUTO_STATUS, MyConstant.STR_TRUE);
                        if (position != 0)
                        {
                            String strTime = tvDateTime.getText()
                                    .toString()
                                    .trim();
                            if (TextUtils.isEmpty(strTime))
                            {
                                MyToastUtils.error("请选择自动上架的时刻");
                                return;
                            }
                            long time = 0;
                            try
                            {
                                time = dateFormat.parse(strTime)
                                        .getTime() / 1000;
                            } catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                            jsonObject.put(MyConstant.STR_STATUS_TIME, time);
                        }
                        jsonArray.put(jsonObject);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        dialog.dismiss();
        if (jsonArray.length() > 0)
        {
            httpPostBatchUpdateGoods(jsonArray.toString());
        }
    }

    private void httpPostBatchUpdateGoods(String goodsJson)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoPostBatchUpdateGoodsInfo(this, myApp.mToken, store_id, goodsJson, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                setResult(1);
                                MyToastUtils.success("修改成功");
                                if (mSmartRefreshLayout != null)
                                {
                                    mSmartRefreshLayout.autoRefresh();
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
    }

    private boolean disClassification(MotionEvent ev)
    {
        if (mLayoutClassification.getVisibility() == View.VISIBLE)
        {
            Rect viewRect = new Rect();
            mLayoutClassification.getGlobalVisibleRect(viewRect);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
            {
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                return true;
            }
        }
        return false;
    }

    private void initView()
    {
        initData();
        initRecyclerView();
        initListener();
        httpGetGoodsByMarketCategoryId(true);
    }

    private void httpGetGoodsByMarketCategoryId(boolean isShowDialog)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetGoodsByMarketCategoryId(mContext, myApp.mToken, store_id, mCurrentCategoryId, mQuery, String.valueOf(mFrom), isShowDialog, new ApiResultListener()
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

    @SuppressLint("DefaultLocale")
    private void onSuccessGoodsData(String body)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(body);

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
                            mBatchProductAdapter.clearDataList();
                        }
                        mFrom += size;
                        mBatchProductAdapter.addDataList(goodsBeanList);
                    }
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initData()
    {
        Intent intent = getIntent();
        mQuery = intent.getStringExtra(MyConstant.STR_QUERY);
        myApp = (MyApp) getApplication();
    }

    private void initListener()
    {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener()
        {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout)
            {
                httpGetGoodsByMarketCategoryId(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                mFrom = 0;
                mBatchProductAdapter.clearDataList();
                httpGetGoodsByMarketCategoryId(false);
                refreshLayout.finishRefresh(5000);
            }
        });

        mCbGoodsAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                mAnalysisiGoodsAdapter.clearSelected();
                mCbGoodsOther.setChecked(false);
                mTextCenterAdapter.clearDataList();
                mTvTitle.setText("全部分类");
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                if (mCurrentCategoryId != null)
                {
                    mBatchProductAdapter.clearDataList();
                    mFrom = 0;
                    mCurrentCategoryId = null;
                    httpGetGoodsByMarketCategoryId(true);
                }
            }
        });

        mCbGoodsAll.setOnClickListener(v -> mCbGoodsAll.setChecked(true));

        mCbGoodsOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                mAnalysisiGoodsAdapter.clearSelected();
                mCbGoodsAll.setChecked(false);
                mTextCenterAdapter.clearDataList();
                mTvTitle.setText("未分类");
                classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
                if (!"0".equals(mCurrentCategoryId))
                {
                    mBatchProductAdapter.clearDataList();
                    mFrom = 0;
                    mCurrentCategoryId = "0";
                    httpGetGoodsByMarketCategoryId(true);
                }
            }
        });

        mCbGoodsOther.setOnClickListener(v -> mCbGoodsOther.setChecked(true));

        mAnalysisiGoodsAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            mCbGoodsAll.setChecked(false);
            mCbGoodsOther.setChecked(false);
            CategoryBean data = mAnalysisiGoodsAdapter.getItemData(position);
            if (data != null)
            {
                List<CategoryBean> sub_types = data.getChildren();
                if (sub_types != null)
                {
                    List<String> datalist = new ArrayList<>();
                    datalist.add(data.getName());
                    for (CategoryBean subTypesBean : sub_types)
                    {
                        datalist.add(subTypesBean.getName());
                    }
                    mTextCenterAdapter.setDataList(datalist);
                }
            }
        });

        mTextCenterAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            CategoryBean currentData = mAnalysisiGoodsAdapter.getItemData(mAnalysisiGoodsAdapter.getmCurrentPosition());
            if (currentData != null)
            {
                String fatherType = currentData.getName();
                StringBuilder buffer = new StringBuilder(1024);
                buffer.append(fatherType);
                if (position > 0)
                {
                    List<CategoryBean> children = currentData.getChildren();
                    if (children != null && children.size() > position - 1)
                    {
                        CategoryBean categoryBean = children.get(position - 1);
                        if (categoryBean != null)
                        {
                            buffer.append(" l ");
                            buffer.append(categoryBean.getName());
                            String categoryId = categoryBean.getId();
                            if (!TextUtils.isEmpty(categoryId))
                            {
                                if (!categoryId.equals(mCurrentCategoryId))
                                {
                                    mFrom = 0;
                                    mBatchProductAdapter.clearDataList();
                                    mCurrentCategoryId = categoryId;
                                    httpGetGoodsByMarketCategoryId(true);
                                }
                            }
                        }
                    }
                } else
                {
                    MyToastUtils.error("暂不支持一级分类查询");
                    return;
                }
                ViewSetDataUtils.textViewSetText(mTvTitle, buffer.toString());
                buffer.reverse();
            }
            classificationLayoutVisibleOrGone(mLayoutClassification.getVisibility());
        });

        mBatchProductAdapter.setOnRecyclerViewItemClickListener((view, position) -> selectedListLayout());
    }

    private void selectedListLayout()
    {
        ArrayList<GoodsBean> goodsBeanArrayList = mBatchProductAdapter.getmSelectList();
        if (goodsBeanArrayList.size() == 0)
        {
            mBtnShangjia.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
            mBtnXiajia.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
            mBtnUpdateStock.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
            mBtnMore.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
        } else
        {
            mBtnShangjia.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
            mBtnXiajia.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
            mBtnUpdateStock.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
            mBtnMore.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
        }
    }

    private void initRecyclerView()
    {
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));
        initCategoryRecyclerView(divider);
        initProductsRecyclerView(divider);
    }

    private void initProductsRecyclerView(DividerItemDecoration divider)
    {
        mRecyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewProduct.addItemDecoration(divider);
        mBatchProductAdapter = new BatchProductAdapter();
        mRecyclerViewProduct.setAdapter(mBatchProductAdapter);
    }

    private void initCategoryRecyclerView(DividerItemDecoration divider)
    {
        mRecyclerview0.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerview0.addItemDecoration(divider);

        //        mRecyclerview0.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAnalysisiGoodsAdapter = new AnalysisiGoodsAdapter();
        mRecyclerview0.setAdapter(mAnalysisiGoodsAdapter);
        if (myApp.mMarketCategoryList != null)
        {
            mAnalysisiGoodsAdapter.addDataList(myApp.mMarketCategoryList);
        }

        mRecyclerview1.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview1.addItemDecoration(divider);
        //        mRecyclerview1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mTextCenterAdapter = new TextCenterAdapter();
        mRecyclerview1.setAdapter(mTextCenterAdapter);
    }


    private void classificationLayoutVisibleOrGone(int visible)
    {
        if (visible == View.VISIBLE)
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
            translateAnimation.setDuration(200);
            mLayoutClassification.setAnimation(translateAnimation);
            mLayoutClassification.setVisibility(View.GONE);
        } else
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
            translateAnimation.setDuration(200);
            mLayoutClassification.setAnimation(translateAnimation);
            mLayoutClassification.setVisibility(View.VISIBLE);
        }
    }
}
