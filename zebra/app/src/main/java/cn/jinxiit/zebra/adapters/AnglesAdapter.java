package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;

public class AnglesAdapter extends RecyclerView.Adapter<AnglesAdapter.ViewHolder>{

    private Context mContext;

    private List<String> mDataList;


    public AnglesAdapter() {
        mDataList = new ArrayList<>();
    }

    public void addData(List<String> list)
    {
        mDataList.addAll(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_angles, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.tvItemAngles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean selected = !holder.tvItemAngles.isSelected();
                holder.tvItemAngles.setSelected(selected);

                if (selected)
                {
                    holder.tvItemAngles.setBackgroundResource(R.drawable.shape_stroke_gray_5dp);
                }
                else
                {
                    holder.tvItemAngles.setBackgroundResource(R.color.colorTm);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String angles = mDataList.get(position);
        holder.tvItemAngles.setText(angles);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_item_angles)
        TextView tvItemAngles;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
