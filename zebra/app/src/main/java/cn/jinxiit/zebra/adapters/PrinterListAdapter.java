package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import cn.jinxiit.zebra.beans.PrinterBean;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class PrinterListAdapter extends RecyclerView.Adapter<PrinterListAdapter.ViewHolder> implements View.OnClickListener
{
    private List<PrinterBean> mDataList;
    private Context mContext;
    private int mCurrentPosition = -1;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public PrinterListAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public PrinterBean getItemData(int position)
    {
        if (mDataList.size() > position)
        {
            return mDataList.get(position);
        }
        return null;
    }

    public void addDataList(List<PrinterBean> list)
    {
        mDataList.addAll(list);
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
                .inflate(R.layout.recycler_item_printer, parent, false);
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
            holder.ivStatus.setVisibility(View.VISIBLE);
        } else
        {
            holder.ivStatus.setVisibility(View.GONE);
        }
        PrinterBean printerBean = mDataList.get(position);
        ViewSetDataUtils.textViewSetText(holder.tvName, printerBean.getName());
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    @Override
    public void onClick(View v)
    {
        Object tag = v.getTag();
        if (tag instanceof Integer)
        {
            mCurrentPosition = (int) tag;
            notifyDataSetChanged();
            if (mOnRecyclerViewItemClickListener != null)
            {
                mOnRecyclerViewItemClickListener.onClick(v, mCurrentPosition);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_status)
        ImageView ivStatus;
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
