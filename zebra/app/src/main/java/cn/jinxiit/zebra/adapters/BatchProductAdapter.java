package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class BatchProductAdapter extends RecyclerView.Adapter<BatchProductAdapter.ViewHolder> implements View.OnClickListener {
    private List<GoodsBean> mDataList;
    private Context mContext;
    private ArrayList<GoodsBean> mSelectList;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public BatchProductAdapter() {
        mDataList = new ArrayList<>();
        mSelectList = new ArrayList<>();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener) {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void addDataList(List<GoodsBean> list) {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearSelectedList() {
        mSelectList.clear();
        notifyDataSetChanged();
    }

    public void clearDataList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<GoodsBean> getmSelectList() {
        return mSelectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_batch_product, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        holder.ivSelect.setImageResource(R.drawable.batch_unsele);
        holder.ivImg.setImageResource(R.drawable.img_default);
        holder.tvName.setText("未知名");
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
        holder.tvPrice.setText("价格未知");
        holder.tvInventory.setText("库存月销未知");
        holder.tvStatus.setVisibility(View.GONE);
        GoodsBean currentGoodsBean = mDataList.get(position);
        if (checkSelected(currentGoodsBean, mSelectList) != -1) {
            holder.ivSelect.setImageResource(R.drawable.batch_sele);
        }

        if (currentGoodsBean != null) {
            GoodsBean.ProductBean product = currentGoodsBean.getProduct();
            if (product != null) {
                List<String> images = product.getImages();
                if (images != null && images.size() > 0) {
                    String fileKey = images.get(0);
                    if (!TextUtils.isEmpty(fileKey)) {
                        String imgUrl = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                        int width = WindowUtils.dip2px(mContext, 80);
                        MyPicassoUtils.downSizeImageForUrl(width, width, imgUrl, holder.ivImg);
                    }
                }
                ViewSetDataUtils.textViewSetText(holder.tvName,
                        ZebraUtils.getInstance().showGoodName(currentGoodsBean));
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
                            if (priceAndStockBean.getBinded() == 1) {
                                //绑定平台信息
                                bindPlatformData(holder, priceAndStockBean);
                                long price = priceAndStockBean.getPrice();
                                if (minPrice == -1 && maxPrice == -1) {
                                    minPrice = maxPrice = price;
                                }

                                if (minPrice > price) {
                                    minPrice = price;
                                }

                                if (maxPrice < price) {
                                    maxPrice = price;
                                }
                                totalStock += priceAndStockBean.getStock();
                            }
                        }
                    }

                    String strPrice = String.format("¥%.2f~%.2f", (double) minPrice * 0.01,
                            (double) maxPrice * 0.01);
                    if (minPrice == -1) {
                        minPrice = 0;
                        maxPrice = 0;
                    }
                    if (minPrice == maxPrice || strPrice.length() > 12) {
                        holder.tvPrice.setText(String.format("¥ %.2f", (double) minPrice * 0.01));
                    } else {
                        holder.tvPrice.setText(strPrice);
                    }
                    holder.tvInventory.setText("库存: " + totalStock);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            if (tag instanceof Integer) {
                int position = (Integer) tag;
                GoodsBean currentGoodsBean = mDataList.get(position);
                int selectPosition = checkSelected(currentGoodsBean, mSelectList);
                if (selectPosition == -1) {
                    mSelectList.add(currentGoodsBean);
                } else {
                    mSelectList.remove(selectPosition);
                }
                notifyDataSetChanged();
                if (mOnRecyclerViewItemClickListener != null) {
                    mOnRecyclerViewItemClickListener.onClick(v, position);
                }
            }
        }
    }

    private void bindPlatformData(@NonNull ViewHolder holder, GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean) {
        long stock = priceAndStockBean.getStock();
        String product_status = priceAndStockBean.getProduct_status();
        String third_type = priceAndStockBean.getThird_type();
        switch (third_type) {
            case MyConstant.STR_EN_JDDJ:
                holder.tvPlatform0.setEnabled(true);
                if (stock == 0 || MyConstant.STR_FALSE.equals(product_status)) {
                    holder.tvPlatform0.setBackgroundResource(R.drawable.shape_stroke3_colorred);
                    holder.tvPlatform0
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                } else {
                    holder.tvPlatform0.setBackgroundResource(R.drawable.shape_stroke3_colormain);
                    holder.tvPlatform0
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
                }
                break;
            case MyConstant.STR_EN_MT:
                holder.tvPlatform1.setEnabled(true);
                if (stock == 0 || MyConstant.STR_FALSE.equals(product_status)) {
                    holder.tvPlatform1.setBackgroundResource(R.drawable.shape_stroke3_colorred);
                    holder.tvPlatform1
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                } else {
                    holder.tvPlatform1.setBackgroundResource(R.drawable.shape_stroke3_colormain);
                    holder.tvPlatform1
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
                }
                break;
            case MyConstant.STR_EN_ELEME:
                holder.tvPlatform2.setEnabled(true);
                if (stock == 0 || MyConstant.STR_FALSE.equals(product_status)) {
                    holder.tvPlatform2.setBackgroundResource(R.drawable.shape_stroke3_colorred);
                    holder.tvPlatform2
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                } else {
                    holder.tvPlatform2.setBackgroundResource(R.drawable.shape_stroke3_colormain);
                    holder.tvPlatform2
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
                }
                break;
            case MyConstant.STR_EN_EBAI:
                holder.tvPlatform3.setEnabled(true);
                if (stock == 0 || MyConstant.STR_FALSE.equals(product_status)) {
                    holder.tvPlatform3.setBackgroundResource(R.drawable.shape_stroke3_colorred);
                    holder.tvPlatform3
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                } else {
                    holder.tvPlatform3.setBackgroundResource(R.drawable.shape_stroke3_colormain);
                    holder.tvPlatform3
                            .setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
                }
                break;
            default:
                break;
        }
    }

    //-1未被选中
    private int checkSelected(GoodsBean currentGoodsBean, List<GoodsBean> list) {
        int selectPosition = -1;
        if (currentGoodsBean != null && list != null && list.size() > 0) {
            String currentId = currentGoodsBean.getProduct().getId();
            if (!TextUtils.isEmpty(currentId)) {
                for (int i = 0; i < list.size(); i++) {
                    GoodsBean goodsBean = list.get(i);
                    if (goodsBean != null) {
                        if (currentId.equals(goodsBean.getProduct().getId())) {
                            selectPosition = i;
                            break;
                        }
                    }
                }
            }
        }
        return selectPosition;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_select)
        ImageView ivSelect;
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
        @BindView(R.id.tv_pay_status)
        TextView tvStatus;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
