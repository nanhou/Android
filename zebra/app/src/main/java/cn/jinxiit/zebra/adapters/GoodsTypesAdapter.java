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
import cn.jinxiit.zebra.beans.CategoryBean;

public class GoodsTypesAdapter extends RecyclerView.Adapter<GoodsTypesAdapter.ViewHolder>
{
    private List<CategoryBean> mDataList;
    private Context mContext;
    private boolean mIsOpen = false;
    private int mClickGroupPosition;

    private static final int TYPE_FATHER = 0;
    private static final int TYPE_CHILD = 1;

    public GoodsTypesAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public List<CategoryBean> getmDataList()
    {
        return mDataList;
    }

    public void setmClickGroupPosition(int mClickGroupPosition)
    {
        this.mClickGroupPosition = mClickGroupPosition;
    }

    public void addDataList(List<CategoryBean> list)
    {
        mDataList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addData(CategoryBean goodsTypeBean)
    {
        mDataList.add(goodsTypeBean);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mIsOpen)
        {
            CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
            int size = goodsTypeBean.getChildren()
                    .size();
            if (position > mClickGroupPosition && position <= mClickGroupPosition + size)
            {
                return TYPE_CHILD;
            }
        }
        return TYPE_FATHER;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_type, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        int type = getItemViewType(position);
        if (type == TYPE_FATHER)
        {
            dataToGround(holder, position);
        } else
        {
            dataToChild(holder, position);
        }
    }

    private void dataToChild(@NonNull ViewHolder holder, int positon)
    {
        holder.ivImg.setVisibility(View.GONE);
        holder.itemView.setBackgroundResource(R.color.colorFg);

        List<CategoryBean> sub_types = mDataList.get(mClickGroupPosition)
                .getChildren();
        CategoryBean subTypesBean = sub_types.get(positon - (mClickGroupPosition + 1));
        holder.tvName.setText(subTypesBean.getName());
    }

    private void dataToGround(@NonNull ViewHolder holder, int position)
    {
        if (mIsOpen && mClickGroupPosition == position)
        {
            holder.ivImg.setImageResource(R.drawable.down);
        } else
        {
            holder.ivImg.setImageResource(R.drawable.up);
        }

        holder.ivImg.setVisibility(View.VISIBLE);

        holder.itemView.setBackgroundResource(R.color.white);
        int groupPosition = position;
        if (mIsOpen && position > mClickGroupPosition)
        {
            groupPosition = position - mDataList.get(mClickGroupPosition)
                    .getChildren()
                    .size();
        }
        CategoryBean goodsTypeBean = mDataList.get(groupPosition);
        holder.tvName.setText(goodsTypeBean.getName());

        holderFatherCickListener(holder, position);
    }

    private void holderFatherCickListener(@NonNull ViewHolder holder, final int position)
    {
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mIsOpen)
                {
                    if (mClickGroupPosition == position)
                    {
                        mClickGroupPosition = 0;
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
                        notifyDataSetChanged();
                    }
                } else
                {
                    mClickGroupPosition = position;
                    mIsOpen = true;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int listSize = mDataList.size();
        if (mIsOpen)
        {
            CategoryBean goodsTypeBean = mDataList.get(mClickGroupPosition);
            listSize += goodsTypeBean.getChildren()
                    .size();
        }

        return listSize;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_edit)
        TextView tvEdit;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
