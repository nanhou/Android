package cn.jinxiit.zebra.adapters;

import android.app.Activity;
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
import cn.jinxiit.zebra.beans.BrandBean;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class SearchBrandAdapter extends RecyclerView.Adapter<SearchBrandAdapter.ViewHolder>
{
    private List<BrandBean> mDataList;
    private Activity mActivity;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public SearchBrandAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void clearDataList()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void addDataList(List<BrandBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public BrandBean getItemData(int position)
    {
        if (position < mDataList.size())
        {
            return mDataList.get(position);
        }
        return null;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mActivity == null)
        {
            mActivity = (Activity) parent.getContext();
        }
        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.recycler_item_search_brand, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvName.setOnClickListener(v -> clickItem(v));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.tvName.setTag(position);

        BrandBean brandBean = mDataList.get(position);
        if (brandBean != null)
        {
            ViewSetDataUtils.textViewSetText(holder.tvName, brandBean.getName());
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    private void clickItem(View v)
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

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
