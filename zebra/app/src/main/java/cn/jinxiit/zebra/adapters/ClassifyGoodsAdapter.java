package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;

public class ClassifyGoodsAdapter extends RecyclerView.Adapter<ClassifyGoodsAdapter.ViewHolder>
{
    private List<CategoryBean> mDataList;
    private Context mContext;
    private boolean mIsOpen = false;
    private int mClickGroupPosition = -1;//被点击的父布局 只有父布局
    private int mClickItemPosition;//被选中的子布局 包含父子布局
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_ITEM = 1;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private OnRecyclerViewItemClickListener onRecyclerViewGroupItemClickListener;

    public ClassifyGoodsAdapter()
    {
        mDataList = new ArrayList<>();
    }

    public void addDataList(List<CategoryBean> list)
    {
        mDataList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void setDataList(List<CategoryBean> list)
    {
        mDataList = list;
        this.notifyDataSetChanged();
    }

    public CategoryBean getGroupBean(int groupPosition)
    {
        if (mDataList.size() > groupPosition)
        {
            return mDataList.get(groupPosition);
        }
        return null;
    }

    public void setMyOnItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener)
    {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setOnRecyclerViewGroupItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewGroupItemClickListener)
    {
        this.onRecyclerViewGroupItemClickListener = onRecyclerViewGroupItemClickListener;
    }

    public void init()
    {
        mIsOpen = false;
        mClickGroupPosition = -1;
        mClickItemPosition = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mIsOpen)
        {
            if (mClickGroupPosition < mDataList.size())
            {
                CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
                int size = goodsTypeBean.getChildren()
                        .size();
                if (position > mClickGroupPosition && position <= mClickGroupPosition + size)
                {
                    return TYPE_ITEM;
                }
            }
        }
        return TYPE_GROUP;
    }

    @NonNull
    @Override
    public ClassifyGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.classify_goods_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassifyGoodsAdapter.ViewHolder holder, final int position)
    {
        int type = getItemViewType(position);
        if (type == TYPE_GROUP)
        {
            groupItemData(holder, position);
        } else
        {
            childItemData(holder, position);
        }
    }

    private void childItemData(@NonNull final ViewHolder holder, final int position)
    {
        if (mClickItemPosition == position)
        {
            holder.tvClassify.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
        } else
        {
            holder.tvClassify.setTextColor(Color.BLACK);
        }
        holder.ivImg.setVisibility(View.VISIBLE);
        holder.itemView.setBackgroundResource(R.color.colorFg);

        CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
        if (goodsTypeBean.getChildren()
                .size() > 0)
        {
            List<CategoryBean> sub_types = goodsTypeBean.getChildren();
            CategoryBean categoryBean = sub_types.get(position - mClickGroupPosition - 1);
            String type = categoryBean
                    .getName();
            holder.tvClassify.setText(type);

            holder.itemView.setOnClickListener(v -> {
                mClickItemPosition = position;
                notifyDataSetChanged();
                holder.itemView.setTag(categoryBean);
                if (onRecyclerViewItemClickListener != null)
                {
                    onRecyclerViewItemClickListener.onClick(v, position);
                }
            });
        }
    }

    private void groupItemData(@NonNull final ViewHolder holder, final int position)
    {
        if (mClickGroupPosition == position)
        {
            holder.itemView.setBackgroundResource(R.drawable.shape_white_leftcolormain);
            holder.tvClassify.setTextColor(ContextCompat.getColor(mContext, R.color.colorMain));
        } else
        {
            holder.itemView.setBackgroundResource(R.color.white);
            holder.tvClassify.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        holder.ivImg.setVisibility(View.GONE);

        String type;
        if (!mIsOpen || position <= mClickGroupPosition)
        {
            type = mDataList.get(position)
                    .getName();
        } else
        {
            int size = mDataList.get(mClickGroupPosition)
                    .getChildren()
                    .size();
            type = mDataList.get(position - size)
                    .getName();
        }
        holder.tvClassify.setText(type);

        holder.itemView.setOnClickListener(v -> {
            int oldGroupPosition = mClickGroupPosition;
            boolean oldIsOpen = mIsOpen;
            if (mIsOpen)
            {
                if (mClickGroupPosition == position)
                {
                    //                        mClickGroupPosition = 0;
                    mIsOpen = false;
                    notifyDataSetChanged();
                } else
                {
                    if (mClickGroupPosition < position)
                    {
                        mClickGroupPosition = position - mDataList.get(mClickGroupPosition)
                                .getChildren()
                                .size();
                    } else
                    {
                        mClickGroupPosition = position;
                    }
                }
            } else
            {
                mClickGroupPosition = position;
                mIsOpen = true;
            }
            //控制子项选择
            if (mIsOpen)
            {
                onRecyclerViewGroupItemClickListener.onClick(v, mClickGroupPosition);
                if (mClickGroupPosition < mDataList.size())
                {
                    CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
                    List<CategoryBean> sub_types = goodsTypeBean.getChildren();
                    if (sub_types.size() > 0)
                    {
                        mClickItemPosition = position + 1;
                        if (oldGroupPosition < mClickGroupPosition && oldIsOpen)
                        {
                            mClickItemPosition -= mDataList.get(oldGroupPosition)
                                    .getChildren()
                                    .size();
                        }
                        holder.itemView.setTag(goodsTypeBean.getChildren()
                                .get(0)
                        );
                        if (onRecyclerViewItemClickListener != null)
                        {
                            onRecyclerViewItemClickListener.onClick(v, position);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int listSize = mDataList.size();
        if (mIsOpen)
        {
            if (mClickGroupPosition < mDataList.size())
            {
                CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
                listSize += goodsTypeBean.getChildren()
                        .size();
            }
        }
        return listSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.tv_classify)
        TextView tvClassify;
        @BindView(R.id.iv_img)
        ImageView ivImg;

        public ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
