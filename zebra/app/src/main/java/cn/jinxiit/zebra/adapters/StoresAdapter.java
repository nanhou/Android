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
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> implements View.OnClickListener
{
    private List<StoreOwnerBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public StoresAdapter()
    {
        this.mDataList = new ArrayList();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void addDataList(List<StoreOwnerBean> list)
    {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public StoreOwnerBean getItemData(int position)
    {
        StoreOwnerBean storeOwnerBean = null;
        if (position < mDataList.size())
        {
            storeOwnerBean = mDataList.get(position);
        }
        return storeOwnerBean;
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
                .inflate(R.layout.item_stores, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);

        StoreOwnerBean.StoreBean store = mDataList.get(position)
                .getStore();

        if (store != null)
        {
            holder.tvStoreName.setText(store.getName());
            StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
            if (extra != null)
            {
                holder.tvState.setEnabled(extra.isBusiness_status());
            }
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
            mOnRecyclerViewItemClickListener.onClick(v, (Integer) v.getTag());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_store_name)
        TextView tvStoreName;
        @BindView(R.id.tv_state)
        TextView tvState;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
