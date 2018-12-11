package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
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
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class SearchCreateAdapter extends RecyclerView.Adapter<SearchCreateAdapter.ViewHolder> implements View.OnClickListener
{
    private List<ProductOwnerDataBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public SearchCreateAdapter()
    {
        mDataList = new ArrayList<>();
    }

    public void addDataList(List<ProductOwnerDataBean> list)
    {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public ProductOwnerDataBean getItemData(int position)
    {
        if (mDataList.size() > position)
        {
            return mDataList.get(position);
        }
        return null;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_search_create, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);

        holder.ivStandard.setImageResource(R.drawable.icon_nonstandard);
        ProductOwnerDataBean productOwnerDataBean = mDataList.get(position);
        if (productOwnerDataBean != null)
        {
            ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
            if (product != null)
            {
                ViewSetDataUtils.textViewSetText(holder.tvTitle, product.getTitle());
                String id = product.getId();
                StringBuilder builder = new StringBuilder(1024);
                int idLength = id.length();
                if (idLength < 10)
                {
                    for (int i = 0; i < 10 - idLength; i++)
                    {
                        builder.append(0);
                    }
                }
                builder.append(id);
                holder.tvSummary.setText("商品编号: " + builder.toString());
                builder.reverse();

                ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
                    String upcCode = extra.getUpcCode();
                    if (!TextUtils.isEmpty(upcCode))
                    {
                        holder.ivStandard.setImageResource(R.drawable.icon_standard);
                    }

                    List<String> images = extra.getImages();
                    if (images != null)
                    {
                        int imagesSize = images.size();
                        if (imagesSize > 0)
                        {
                            String imageUrl = images.get(0);
                            if (!TextUtils.isEmpty(imageUrl))
                            {
                                String url = MyConstant.URL_GET_FILE + imageUrl + ".jpg";
                                int width = WindowUtils.dip2px(mContext, 80);
                                MyPicassoUtils.downSizeImageForUrl(width, width, url, holder.ivImg);
                                return;
                            }
                        }
                    }
                }
            }
        }
        holder.ivImg.setImageResource(R.drawable.img_default);
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    @Override
    public void onClick(View v)
    {
        if (mOnRecyclerViewItemClickListener != null)
        {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer)
            {
                mOnRecyclerViewItemClickListener.onClick(v, (Integer) v.getTag());
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_summary)
        TextView tvSummary;
        @BindView(R.id.iv_standard)
        ImageView ivStandard;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
