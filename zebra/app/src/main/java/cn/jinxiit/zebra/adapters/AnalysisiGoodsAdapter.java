package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class AnalysisiGoodsAdapter extends RecyclerView.Adapter<AnalysisiGoodsAdapter.ViewHolder> implements View.OnClickListener
{
    private List<CategoryBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    private int mCurrentPosition = -1;

    public AnalysisiGoodsAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void addDataList(List<CategoryBean> list)
    {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDataList(List<CategoryBean> list)
    {
        mDataList = list;
        notifyDataSetChanged();
    }

    public void clearDataList()
    {
        mDataList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public CategoryBean getItemData(int position)
    {
        if (position >= 0 && position < mDataList.size())
        {
            return mDataList.get(position);
        }
        return null;
    }

    public int getmCurrentPosition()
    {
        return mCurrentPosition;
    }

    public void setmCurrentPosition(int mCurrentPosition)
    {
        this.mCurrentPosition = mCurrentPosition;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void clearSelected()
    {
        mCurrentPosition = -1;
        notifyDataSetChanged();
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
                .inflate(R.layout.recycler_item_textcenter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);

        if (position == mCurrentPosition)
        {
            holder.tvName.setBackgroundResource(R.drawable.shape_white_leftcolormain);
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
        } else
        {
            holder.tvName.setBackgroundResource(R.color.colorFg);
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        CategoryBean goodsTypeBean = mDataList.get(position);
        ViewSetDataUtils.textViewSetText(holder.tvName, goodsTypeBean.getName());
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
            mCurrentPosition = (int) v.getTag();
            mOnRecyclerViewItemClickListener.onClick(v, mCurrentPosition);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
