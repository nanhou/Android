package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lzy.okgo.model.Response;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewPlatformClickListener;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class SearchGoodsAdapter extends RecyclerView.Adapter<SearchGoodsAdapter.ViewHolder> {
    private List<GoodsBean> mDataList;
    private Activity mContext;
    private int mLayoutId;
    private boolean mIsTop;
    private @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private OnRecyclerViewItemClickListener mTopClickListener;
    private OnRecyclerViewItemClickListener mUpdatePriceAndStockClickListener;
    private OnRecyclerViewPlatformClickListener mOnRecyclerViewPlatformClickListener;
    private OnRecyclerViewItemClickListener mRefreshListener;
    private OnRecyclerViewItemClickListener mUpdateSaleTimeListener;

    public SearchGoodsAdapter(int layoutId, boolean isTop) {
        mLayoutId = layoutId;
        mDataList = new ArrayList<>();
        this.mIsTop = isTop;
    }

    public void setUpdateSaleTimeListener(OnRecyclerViewItemClickListener mUpdateSaleTimeListener) {
        this.mUpdateSaleTimeListener = mUpdateSaleTimeListener;
    }

    public void setRefreshListener(OnRecyclerViewItemClickListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public void setmOnRecyclerViewPlatformClickListener(OnRecyclerViewPlatformClickListener mOnRecyclerViewPlatformClickListener) {
        this.mOnRecyclerViewPlatformClickListener = mOnRecyclerViewPlatformClickListener;
    }

    public void setUpdatePriceAndStockClickListener(OnRecyclerViewItemClickListener mUpdatePriceAndStockClickListener) {
        this.mUpdatePriceAndStockClickListener = mUpdatePriceAndStockClickListener;
    }

    public void setTopClickListener(OnRecyclerViewItemClickListener mTopClickListener) {
        this.mTopClickListener = mTopClickListener;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener) {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void clearDataList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void updateItemData(GoodsBean goodsBean) {
        if (goodsBean != null) {
            GoodsBean.ProductBean product = goodsBean.getProduct();
            if (product != null) {
                String product_id = product.getId();
                if (!TextUtils.isEmpty(product_id)) {
                    for (int i = 0; i < mDataList.size(); i++) {
                        GoodsBean goodsBeanI = mDataList.get(i);
                        if (goodsBeanI != null) {
                            if (product_id.equals(goodsBeanI.getProduct().getId())) {
                                mDataList.set(i, goodsBean);
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void addDataList(List<GoodsBean> list) {
        if (list != null && list.size() > 0) {
            mDataList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    public GoodsBean getItemData(int position) {
        if (position < mDataList.size()) {
            return mDataList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = (Activity) parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.ivImg.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.btnUpdatePriceInventory.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.btnSetShangJia.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.tvPlatform0.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.tvPlatform1.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.tvPlatform2.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.tvPlatform3.setOnLongClickListener(v -> mIsTop && itemLongClick(v));
        holder.ivActionTop.setOnLongClickListener(v -> mIsTop && itemLongClick(v));

        holder.ivImg.setOnClickListener(this::clickImg);
        holder.btnUpdatePriceInventory.setOnClickListener(this::clickBtnUpdatePriceAndStock);
        holder.btnSetShangJia.setOnClickListener(this::clickBtnShangJia);
        holder.tvPlatform0.setOnClickListener(v -> clickPlatform(v, MyConstant.STR_EN_JDDJ));
        holder.tvPlatform1.setOnClickListener(v -> clickPlatform(v, MyConstant.STR_EN_MT));
        holder.tvPlatform2.setOnClickListener(v -> clickPlatform(v, MyConstant.STR_EN_ELEME));
        holder.tvPlatform3.setOnClickListener(v -> clickPlatform(v, MyConstant.STR_EN_EBAI));
        holder.ivActionTop.setOnClickListener(this::clickActionTop);
        return holder;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.ivImg.setTag(position);
        holder.tvPlatform0.setTag(position);
        holder.tvPlatform1.setTag(position);
        holder.tvPlatform2.setTag(position);
        holder.tvPlatform3.setTag(position);
        holder.ivActionTop.setTag(position);
        holder.btnUpdatePriceInventory.setTag(position);
        holder.btnSetShangJia.setTag(position);
        if (!mIsTop) {
            holder.ivTop.setVisibility(View.INVISIBLE);
            holder.ivActionTop.setVisibility(View.INVISIBLE);
        }

        holder.ivImg.setImageResource(R.drawable.img_default);
        holder.tvUpc.setText("");
        holder.tvPrice.setText("价格未知");
        holder.ivTop.setVisibility(View.GONE);
        holder.ivActionTop.setImageResource(R.drawable.action_top_up);
        holder.tvPlatform0.setEnabled(false);
        holder.tvPlatform1.setEnabled(false);
        holder.tvPlatform2.setEnabled(false);
        holder.tvPlatform3.setEnabled(false);
        holder.tvPlatform0.setBackgroundResource(R.drawable.shape_stroke3_color700);
        holder.tvPlatform1.setBackgroundResource(R.drawable.shape_stroke3_color700);
        holder.tvPlatform2.setBackgroundResource(R.drawable.shape_stroke3_color700);
        holder.tvPlatform3.setBackgroundResource(R.drawable.shape_stroke3_color700);
        holder.tvPlatform0.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
        holder.tvPlatform1.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
        holder.tvPlatform2.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
        holder.tvPlatform3.setTextColor(ContextCompat.getColor(mContext, R.color.color_700));
        holder.tvStatus.setVisibility(View.GONE);
        holder.btnSetShangJia.setText("设置下架");

        bindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(@NonNull ViewHolder holder, int position) {
        GoodsBean goodsBean = mDataList.get(position);
        if (goodsBean != null) {
            GoodsBean.ProductBean product = goodsBean.getProduct();
            if (product != null) {
                if (product.getTop() != 0 && mIsTop) {
                    holder.ivTop.setVisibility(View.VISIBLE);
                    holder.ivActionTop.setImageResource(R.drawable.action_top_down);
                }
                ViewSetDataUtils.textViewSetText(holder.tvName,
                        ZebraUtils.getInstance().showGoodName(goodsBean));
                List<String> images = product.getImages();
                if (images != null && images.size() > 0) {
                    String fileKey = images.get(0);
                    String url = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                    int width = WindowUtils.dip2px(mContext, 80);
                    MyPicassoUtils.downSizeImageForUrl(width, width, url, holder.ivImg);
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
                        if (priceAndStockBean != null) {
                            long price = priceAndStockBean.getPrice();
                            long stock = priceAndStockBean.getStock();
                            if (priceAndStockBean.getBinded() == 1) {
                                String third_type = priceAndStockBean.getThird_type();
                                String product_status = priceAndStockBean.getProduct_status();
                                if (!TextUtils.isEmpty(third_type)) {
                                    switch (third_type) {
                                        case MyConstant.STR_EN_JDDJ:
                                            holder.tvPlatform0.setEnabled(true);
                                            if (stock == 0 || MyConstant.STR_FALSE
                                                    .equals(product_status)) {
                                                holder.tvPlatform0.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colorred);
                                                holder.tvPlatform0.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorRed));
                                            } else if (stock > 0 || MyConstant.STR_TRUE
                                                    .equals(product_status)) {
                                                holder.tvPlatform0.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colormain);
                                                holder.tvPlatform0.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorMain));
                                            }
                                            break;
                                        case MyConstant.STR_EN_MT:
                                            holder.tvPlatform1.setEnabled(true);
                                            if (stock == 0 || MyConstant.STR_FALSE
                                                    .equals(product_status)) {
                                                holder.tvPlatform1.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colorred);
                                                holder.tvPlatform1.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorRed));
                                            } else if (stock > 0 || MyConstant.STR_TRUE
                                                    .equals(product_status)) {
                                                holder.tvPlatform1.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colormain);
                                                holder.tvPlatform1.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorMain));
                                            }
                                            break;
                                        case MyConstant.STR_EN_ELEME:
                                            holder.tvPlatform2.setEnabled(true);
                                            if (stock == 0 || MyConstant.STR_FALSE
                                                    .equals(product_status)) {
                                                holder.tvPlatform2.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colorred);
                                                holder.tvPlatform2.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorRed));
                                            } else if (stock > 0 || MyConstant.STR_TRUE
                                                    .equals(product_status)) {
                                                holder.tvPlatform2.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colormain);
                                                holder.tvPlatform2.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorMain));
                                            }
                                            break;
                                        case MyConstant.STR_EN_EBAI:
                                            holder.tvPlatform3.setEnabled(true);
                                            if (stock == 0 || MyConstant.STR_FALSE
                                                    .equals(product_status)) {
                                                holder.tvPlatform3.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colorred);
                                                holder.tvPlatform3.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorRed));
                                            } else if (stock > 0 || MyConstant.STR_TRUE
                                                    .equals(product_status)) {
                                                holder.tvPlatform3.setBackgroundResource(
                                                        R.drawable.shape_stroke3_colormain);
                                                holder.tvPlatform3.setTextColor(ContextCompat
                                                        .getColor(mContext, R.color.colorMain));
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                if (minPrice == -1 || minPrice > price) {
                                    minPrice = price;
                                }

                                if (maxPrice == -1 || maxPrice < price) {
                                    maxPrice = price;
                                }
                                totalStock += stock;
                            }
                        }
                    }

                    String product_status = product.getProduct_status();
                    if ("false".equals(product_status)) {
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.btnSetShangJia.setText("设置上架");
                    }

                    holder.tvInventory.setText("库存: " + totalStock);

                    if (minPrice == -1) {
                        minPrice = 0;
                        maxPrice = 0;
                    }
                    @SuppressLint("DefaultLocale") String price = String
                            .format("¥%.2f~%.2f", (double) minPrice * 0.01,
                                    (double) maxPrice * 0.01);

                    if (maxPrice == minPrice || price.length() > 12) {
                        holder.tvPrice.setText(String.format("¥%.2f", (double) minPrice * 0.01));
                    } else {
                        holder.tvPrice.setText(price);
                    }
                }
                GoodsBean.ProductBean.ExtraBean extra = product.getExtra();
                if (extra != null) {
                    String upcCode = extra.getUpcCode();
                    if (!TextUtils.isEmpty(upcCode)) {
                        holder.tvUpc.setText("UPC: " + upcCode);
                    }
                }
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_platfrom0)
        TextView tvPlatform0;
        @BindView(R.id.tv_platfrom1)
        TextView tvPlatform1;
        @BindView(R.id.tv_platfrom2)
        TextView tvPlatform2;
        @BindView(R.id.tv_platfrom3)
        TextView tvPlatform3;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_inventory)
        TextView tvInventory;
        @BindView(R.id.btn_update_price_inventory)
        Button btnUpdatePriceInventory;
        @BindView(R.id.btn_set_the_shelves)
        Button btnSetShangJia;
        @BindView(R.id.tv_upc_code)
        TextView tvUpc;
        @BindView(R.id.iv_top)
        ImageView ivTop;
        @BindView(R.id.iv_action_top)
        ImageView ivActionTop;
        @BindView(R.id.tv_pay_status)
        TextView tvStatus;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private boolean itemLongClick(View view) {
        int position = (int) view.getTag();
        GoodsBean goodsBean = mDataList.get(position);
        GoodsBean.ProductBean product = goodsBean.getProduct();

        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_goods_update, null);

        TextView tvTop = dialogContent.findViewById(R.id.tv_top);
        if (product != null) {
            if (product.getTop() == 1) {
                tvTop.setText("取消置顶");
            } else {
                tvTop.setText("置顶");
            }
        }

        tvTop.setOnClickListener(v -> {
            dialog.cancel();
            clickActionTop(view);
        });
        dialogContent.findViewById(R.id.tv_move_category).setOnClickListener(v -> {
            dialog.cancel();
            updateCategory(position);
        });
        dialogContent.findViewById(R.id.tv_update_packing).setOnClickListener(v -> {
            dialog.cancel();
            updatePackFeeOrMinPurchase(true, position);
        });
        dialogContent.findViewById(R.id.tv_update_sale_time).setOnClickListener(v -> {
            dialog.cancel();
            if (mUpdateSaleTimeListener != null) {
                mUpdateSaleTimeListener.onClick(view, position);
            }
        });
        dialogContent.findViewById(R.id.tv_update_min).setOnClickListener(v -> {
            dialog.cancel();
            updatePackFeeOrMinPurchase(false, position);
        });
        dialogContent.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.cancel());
        dialog.setContentView(dialogContent);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window == null) {
            return false;
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
        dialog.show();
        return true;
    }

    private void updatePackFeeOrMinPurchase(boolean isPackFee, int position) {
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(mContext);
        EditText editText = rxDialogEditSureCancel.getEditText();
        if (isPackFee) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setHint("单位:元");
            rxDialogEditSureCancel.setTitle("修改包装费");
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("请输入...");
            rxDialogEditSureCancel.setTitle("修改最小购买量");
        }
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");

        cancelView.setOnClickListener(v -> {
            String num = editText.getText().toString().trim();
            if (TextUtils.isEmpty(num) || ".".equals(num)) {
                MyToastUtils.error("请输入");
                return;
            }
            rxDialogEditSureCancel.cancel();
            int numInt;
            if (isPackFee) {
                numInt = (int) (Double.parseDouble(num) * 100);
            } else {
                numInt = Integer.parseInt(num);
            }

            ArrayList<GoodsBean> goodsBeanArrayList = new ArrayList<>();
            goodsBeanArrayList.add(mDataList.get(position));
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < goodsBeanArrayList.size(); i++) {
                GoodsBean goodsBean = goodsBeanArrayList.get(i);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MyConstant.STR_ID, goodsBean.getProduct().getId());
                    if (isPackFee) {
                        jsonObject.put(MyConstant.STR_PACK_FEE, numInt);
                    } else {
                        jsonObject.put(MyConstant.STR_MIN_PURCHASE, numInt);
                    }
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            httpPostBatchUpdateGoods(jsonArray.toString());
        });
        sureView.setOnClickListener(v -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    private void updateCategory(int position) {
        MyApp myApp = (MyApp) mContext.getApplication();

        if (myApp.mMarketCategoryList == null) {
            return;
        }

        List<String> options1items = new ArrayList<>();
        List<List<String>> options2items = new ArrayList<>();

        for (int i = 0; i < myApp.mMarketCategoryList.size(); i++) {
            CategoryBean categoryBean = myApp.mMarketCategoryList.get(i);
            if (categoryBean != null) {
                List<CategoryBean> children = categoryBean.getChildren();
                if (children != null && children.size() > 0) {
                    List<String> childList = new ArrayList<>();
                    for (int j = 0; j < children.size(); j++) {
                        childList.add(children.get(j).getName());
                    }
                    options1items.add(categoryBean.getName());
                    options2items.add(childList);
                }
            }
        }

        if (options1items.size() == 0 || options2items.size() == 0) {
            MyToastUtils.error("分类不足，请先创建分类");
            return;
        }

        OptionsPickerView pvOptions = new OptionsPickerBuilder(mContext,
                (options1, option2, options3, v) -> {
                    //返回的分别是三个级别的选中位置
                    String category2Name = options2items.get(options1).get(option2);
                    for (int i = 0; i < myApp.mMarketCategoryList.size(); i++) {
                        CategoryBean categoryBean = myApp.mMarketCategoryList.get(i);
                        List<CategoryBean> children = categoryBean.getChildren();
                        for (int j = 0; j < children.size(); j++) {
                            CategoryBean childCategoryBean = children.get(j);
                            if (category2Name.equals(childCategoryBean.getName())) {
                                ArrayList<GoodsBean> goodsBeanArrayList = new ArrayList<>();
                                goodsBeanArrayList.add(mDataList.get(position));
                                JSONArray jsonArray = new JSONArray();
                                for (int q = 0; q < goodsBeanArrayList.size(); q++) {
                                    GoodsBean goodsBean = goodsBeanArrayList.get(q);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put(MyConstant.STR_ID,
                                                goodsBean.getProduct().getId());
                                        jsonObject.put(MyConstant.STR_CATID,
                                                childCategoryBean.getId());
                                        jsonArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                httpPostBatchUpdateGoods(jsonArray.toString());
                                break;
                            }
                        }
                    }
                }).setTitleText("移动分类").isDialog(true).build();
        pvOptions.setPicker(options1items, options2items);
        pvOptions.show();
    }

    public void httpPostBatchUpdateGoods(String goodsJson) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp.mCurrentStoreOwnerBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id)) {
                ApiUtils.getInstance()
                        .okgoPostBatchUpdateGoodsInfo(mContext, myApp.mToken, store_id, goodsJson,
                                new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        MyToastUtils.success("修改成功");
                                        if (mRefreshListener != null) {
                                            mRefreshListener.onClick(new View(mContext), 0);
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

    private void clickActionTop(View v) {
        if (mTopClickListener != null) {
            mTopClickListener.onClick(v, (Integer) v.getTag());
        }
    }

    private void clickPlatform(View v, String platform) {
        int position = (int) v.getTag();

        if (mOnRecyclerViewPlatformClickListener != null) {
            mOnRecyclerViewPlatformClickListener.onClick(v, position, platform);
        }
    }

    private void clickBtnShangJia(View view) {
        int position = (int) view.getTag();
        GoodsBean goodsBean = mDataList.get(position);
        if (goodsBean != null) {
            GoodsBean.ProductBean product = goodsBean.getProduct();
            if (product != null) {
                String product_status = product.getProduct_status();
                if (MyConstant.STR_TRUE.equals(product_status)) {
                    final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
                    @SuppressLint("InflateParams") View dialogContent = LayoutInflater
                            .from(mContext).inflate(R.layout.dialog_set_theshelves, null);
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
                            window.addFlags(
                                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(
                                    mContext.getResources().getColor(R.color.colorTm));
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
                    final TextView tvAutomatic = dialogContent
                            .findViewById(R.id.tv_automatic_shelves);
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
                        int radioPosition = radioGroup.indexOfChild(
                                radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
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
                        myGoodsBean.setProduct_status(MyConstant.STR_FALSE);
                        myGoodsBean.setAuto_status(MyConstant.STR_TRUE);
                        httpUpdateGoodsInfo(goodsBean, myGoodsBean);
                    });
                    btnNo.setOnClickListener(v -> {
                        dialog.dismiss();
                        MyGoodsBean myGoodsBean = new MyGoodsBean();
                        myGoodsBean.setProduct_status(MyConstant.STR_FALSE);
                        httpUpdateGoodsInfo(goodsBean, myGoodsBean);
                    });
                    dialog.show();
                } else if (MyConstant.STR_FALSE.equals(product_status)) {
                    MyGoodsBean myGoodsBean = new MyGoodsBean();
                    myGoodsBean.setProduct_status(MyConstant.STR_TRUE);
                    httpUpdateGoodsInfo(goodsBean, myGoodsBean);
                }
            }
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

    private void httpUpdateGoodsInfo(GoodsBean goodsBean, MyGoodsBean myGoodsBean) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp.mCurrentStoreOwnerBean != null && goodsBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            String product_id = goodsBean.getProduct().getId();
            if (!TextUtils.isEmpty(store_id) && !TextUtils.isEmpty(product_id)) {
                ApiUtils.getInstance()
                        .okgoPostUpdateStoreGoodsInfo(mContext, myApp.mToken, store_id, product_id,
                                myGoodsBean, new ApiResultListener() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        goodsBean.getProduct()
                                                .setProduct_status(myGoodsBean.getProduct_status());
                                        List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStock = goodsBean
                                                .getProduct().getPriceAndStock();
                                        for (int i = 0; i < priceAndStock.size(); i++) {
                                            goodsBean.getProduct().getPriceAndStock().get(i)
                                                    .setProduct_status(
                                                            myGoodsBean.getProduct_status());
                                        }
                                        updateItemData(goodsBean);
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        MyToastUtils.error("修改失败");
                                    }
                                });
            }
        }
    }

    private void clickBtnUpdatePriceAndStock(View v) {
        int position = (int) v.getTag();
        if (mUpdatePriceAndStockClickListener != null) {
            mUpdatePriceAndStockClickListener.onClick(v, position);
        }
    }

    private void clickImg(View v) {
        if (mOnRecyclerViewItemClickListener != null) {
            mOnRecyclerViewItemClickListener.onClick(v, (Integer) v.getTag());
        }
    }
}
