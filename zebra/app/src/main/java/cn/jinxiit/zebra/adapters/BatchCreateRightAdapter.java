package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.product.batchadds.CategoryOfProductListActivity;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class BatchCreateRightAdapter extends RecyclerView.Adapter<BatchCreateRightAdapter.ViewHolder>
{
    private List<CategoryBean> mDataList;
    private Context mContext;

    public BatchCreateRightAdapter()
    {
        this.mDataList = new ArrayList<>();
    }

    public void addDataList(List<CategoryBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clearDataList()
    {
        mDataList.clear();
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
                .inflate(R.layout.recycler_item_batchcreate_right, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        CategoryBean categoryBean = mDataList.get(position);
        if (categoryBean != null)
        {
            ViewSetDataUtils.textViewSetText(holder.tv2category, categoryBean.getName());

            holder.ll3category.removeAllViews();
            List<CategoryBean> childList = categoryBean.getChildren();
            if (childList != null)
            {
                LinearLayout linearLayout = null;
                int textViewWidth = (int) (WindowUtils.getScreenSize(mContext)[0] / 4.3 * 3.3 - WindowUtils.dip2px(mContext, 50)) / 3;
                for (int i = 0; i < childList.size(); i++)
                {
                    if (linearLayout == null)
                    {
                        linearLayout = new LinearLayout(mContext);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                    }

                    CategoryBean childCategoryBean = childList.get(i);
                    if (childCategoryBean != null)
                    {
                        TextView textView = new TextView(mContext);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(textViewWidth, WindowUtils.dip2px(mContext, 50));
                        if (linearLayout.getChildCount() != 0)
                        {
//                            lp.setMargins(WindowUtils.dip2px(mContext, 5), 0, 0, 0);
                        }
                        textView.setGravity(Gravity.CENTER);
                        textView.setMaxLines(2);
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                        textView.setPadding(WindowUtils.dip2px(mContext, 5), WindowUtils.dip2px(mContext, 5), WindowUtils.dip2px(mContext, 5), WindowUtils.dip2px(mContext, 5));
                        textView.setLayoutParams(lp);
                        textView.setElevation(5);
                        textView.setBackgroundResource(R.drawable.shape_stroke6_black);
                        ViewSetDataUtils.textViewSetText(textView, childCategoryBean.getName());
                        textView.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(MyConstant.STR_CATEGORY, childCategoryBean);
                            MyActivityUtils.skipActivity(mContext, CategoryOfProductListActivity.class, bundle);
                        });
                        linearLayout.addView(textView);
                    }

                    if (linearLayout.getChildCount() == 3 || i == childList.size() - 1)
                    {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.setMargins(0, WindowUtils.dip2px(mContext, 3), 0, 0);
                        linearLayout.setLayoutParams(layoutParams);
                        holder.ll3category.addView(linearLayout);
                        linearLayout = null;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_2category)
        TextView tv2category;
        @BindView(R.id.ll_3category)
        LinearLayout ll3category;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
