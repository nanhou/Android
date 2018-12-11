package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class AuditOfProductAdapter extends RecyclerView.Adapter<AuditOfProductAdapter.ViewHolder>
{
    private List<GoodsBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public AuditOfProductAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void clearDataList()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void addDataList(List<GoodsBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public GoodsBean getItemData(int position)
    {
        if (position < mDataList.size())
        {
            return mDataList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_audit_product, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            if (mOnRecyclerViewItemClickListener != null)
            {
                mOnRecyclerViewItemClickListener.onClick(v, (Integer) v.getTag());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);

        holder.ivImg.setImageResource(R.drawable.img_default);
        holder.tvName.setText("商品名称");
        holder.tvNo.setText("商品编号");
        holder.ivBq.setImageResource(R.drawable.icon_shz);
        holder.tvWhy.setVisibility(View.GONE);

        GoodsBean goodsBean = mDataList.get(position);
        GoodsBean.ProductBean product = goodsBean.getProduct();
        if (product != null)
        {
            List<String> images = product.getImages();
            if (images != null && images.size() > 0)
            {
                String fileKey = images.get(0);
                if (!TextUtils.isEmpty(fileKey))
                {
                    int width = WindowUtils.dip2px(mContext, 80);
                    String url = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                    MyPicassoUtils.downSizeImageForUrl(width, width, url, holder.ivImg);
                }
            }
            ViewSetDataUtils.textViewSetText(holder.tvName, product.getName());
            ViewSetDataUtils.textViewSetText(holder.tvNo, product.getProduct_code());
        }
        String status = goodsBean.getStatus();
        if (!TextUtils.isEmpty(status))
        {
            switch (status)
            {
                case MyConstant.STR_SUCCESS:
                    holder.ivBq.setImageResource(R.drawable.icon_tg);
                    break;
                case MyConstant.STR_FAILED:
                    holder.ivBq.setImageResource(R.drawable.icon_rejected);
                    holder.tvWhy.setVisibility(View.VISIBLE);
                    ViewSetDataUtils.textViewSetText(holder.tvWhy, goodsBean.getMessage());
                    break;
                case MyConstant.STR_APPLYING:
                default:
                    holder.ivBq.setImageResource(R.drawable.icon_shz);
                    break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.iv_bq)
        ImageView ivBq;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_no)
        TextView tvNo;
        @BindView(R.id.tv_why)
        TextView tvWhy;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
