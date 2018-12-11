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

public class BatchCreateLeftAdapter extends RecyclerView.Adapter<BatchCreateLeftAdapter.ViewHolder> implements View.OnClickListener
{
    private List<CategoryBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private int mCurrentClickPosition = 0;

    public BatchCreateLeftAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void addDataList(List<CategoryBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public CategoryBean getCurrentData()
    {
        return mDataList.get(mCurrentClickPosition);
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

        if (position == mCurrentClickPosition)
        {
            holder.tvName.setBackgroundResource(R.drawable.shape_colorfg_leftcolormain);
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
        } else
        {
            holder.tvName.setBackgroundResource(R.color.white);
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        CategoryBean categoryBean = mDataList.get(position);
        if (categoryBean != null)
        {
            ViewSetDataUtils.textViewSetText(holder.tvName, categoryBean.getName());
        }
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
                int position = (int) tag;
                if (mCurrentClickPosition != position)
                {
                    mCurrentClickPosition = position;
                    mOnRecyclerViewItemClickListener.onClick(v, position);
                    notifyDataSetChanged();
                }
            }
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
