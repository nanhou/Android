package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;

public class TextCenterAdapter extends RecyclerView.Adapter<TextCenterAdapter.ViewHolder> implements View.OnClickListener
{
    private List<String> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public TextCenterAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void addDataList (List<String> list)
    {
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDataList (List<String> list)
    {
        this.mDataList = list;
        notifyDataSetChanged();
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
                .inflate(R.layout.recycler_item_textcenter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);
        holder.tvName.setBackgroundResource(R.color.white);
        holder.tvName.setText(mDataList.get(position));
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
            mOnRecyclerViewItemClickListener.onClick(v, (Integer) v.getTag());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);}
    }
}
