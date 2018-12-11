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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.NotificationBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> implements View.OnClickListener
{
    private List<NotificationBean> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private SimpleDateFormat dateFormat;

    @SuppressLint("SimpleDateFormat")
    public NotificationAdapter()
    {
        this.mDataList = new ArrayList();
        dateFormat = new SimpleDateFormat("MM-dd");
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void clearDataList()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void addDataList(List<NotificationBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public NotificationBean getItemData(int position)
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
                .inflate(R.layout.recycler_item_notification, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);
        NotificationBean notificationBean = mDataList.get(position);
        if (notificationBean != null)
        {
            ViewSetDataUtils.textViewSetText(holder.tvTitle, notificationBean.getTitle());
            String message_type = notificationBean.getMessage_type();
            if (!TextUtils.isEmpty(message_type))
            {
                switch (message_type)
                {
                    case MyConstant.STR_PRODUCT:
                        holder.tvType.setText("商品");
                        break;
                    case MyConstant.STR_NOTICE:
                        holder.tvType.setText("公告");
                        break;
                    case MyConstant.STR_OTHER:
                        holder.tvType.setText("其他");
                        break;
                }
            }
            ViewSetDataUtils.textViewSetText(holder.tvTime, dateFormat.format(new Date(notificationBean.getCreated_at() * 1000)));
            int is_read = notificationBean.getIs_read();
            if (is_read == 1)
            {
                holder.ivCircle.setVisibility(View.INVISIBLE);
            } else
            {
                holder.ivCircle.setVisibility(View.VISIBLE);
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
        int position = (int) v.getTag();
        mDataList.get(position)
                .setIs_read(1);
        notifyItemChanged(position);
        if (mOnRecyclerViewItemClickListener != null)
        {
            mOnRecyclerViewItemClickListener.onClick(v, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_circle)
        ImageView ivCircle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_type)
        TextView tvType;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
