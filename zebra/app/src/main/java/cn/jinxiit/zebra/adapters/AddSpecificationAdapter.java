package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;

public class AddSpecificationAdapter extends RecyclerView.Adapter<AddSpecificationAdapter.ViewHolder> implements View.OnClickListener
{
    private List mDataList;
    private Context mContext;

    public AddSpecificationAdapter()
    {
        mDataList = new ArrayList();
        mDataList.add(1);
    }

    public void addData(Object data)
    {
        mDataList.add(data);
        this.notifyDataSetChanged();
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
                .inflate(R.layout.recycler_item_add_specification, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.btnDelete.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.btnDelete.setTag(position);
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_delete:
                clickDelete(v);
                break;
        }
    }

    private void clickDelete(View v)
    {
        int position = (int) v.getTag();
        mDataList.remove(position);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.et_name)
        EditText etName;
        @BindView(R.id.et_price)
        EditText etPrice;
        @BindView(R.id.et_weight)
        EditText etWeight;
        @BindView(R.id.btn_delete)
        Button btnDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);}
    }
}
