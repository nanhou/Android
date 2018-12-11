package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import cn.jinxiit.zebra.beans.AttributeBean;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class AddAttrAdapter extends RecyclerView.Adapter<AddAttrAdapter.ViewHolder>
{
    private ArrayList<AttributeBean> mDataList;
    private Context mContext;

    public AddAttrAdapter()
    {
        mDataList = new ArrayList<>();
    }

    public void addData(AttributeBean data)
    {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    public void addDataList(List<AttributeBean> list)
    {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<AttributeBean> getDataList()
    {
        return mDataList;
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
                .inflate(R.layout.recycler_item_add_attr, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.nameTextWatcher = new MyTextWatcher(MyTextWatcher.TYPE_NAME);
        holder.etName.addTextChangedListener(holder.nameTextWatcher);
        holder.tip0TextWatcher = new MyTextWatcher(MyTextWatcher.TYPE_TIP0);
        holder.etTip0.addTextChangedListener(holder.tip0TextWatcher);
        holder.tip1TextWatcher = new MyTextWatcher(MyTextWatcher.TYPE_TIP1);
        holder.etTip1.addTextChangedListener(holder.tip1TextWatcher);
        holder.tip2TextWatcher = new MyTextWatcher(MyTextWatcher.TYPE_TIP2);
        holder.etTip2.addTextChangedListener(holder.tip2TextWatcher);
        holder.btnDelete.setOnClickListener(v -> {
            int position = (int) v.getTag();
            if (mDataList.size() == 1)
            {
                MyToastUtils.error("至少保留一条属性信息");
                return;
            }
            mDataList.remove(position);
            notifyDataSetChanged();
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.updatePosition(position);
        holder.btnDelete.setTag(position);

        AttributeBean attributeBean = mDataList.get(position);
        ViewSetDataUtils.textViewSetText(holder.etName, attributeBean.getAtt_name());

        List<String> att_label = attributeBean.getAtt_label();
        if (att_label != null && att_label.size() > 0)
        {
            for (int i = 0; i < att_label.size(); i++)
            {
                if (i == 0)
                {
                    ViewSetDataUtils.textViewSetText(holder.etTip0, att_label.get(0));
                } else if (i == 1)
                {
                    ViewSetDataUtils.textViewSetText(holder.etTip1, att_label.get(1));
                } else if (i == 2)
                {
                    ViewSetDataUtils.textViewSetText(holder.etTip2, att_label.get(2));
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
        @BindView(R.id.et_name)
        EditText etName;
        @BindView(R.id.et_tip0)
        EditText etTip0;
        @BindView(R.id.et_tip1)
        EditText etTip1;
        @BindView(R.id.et_tip2)
        EditText etTip2;
        @BindView(R.id.btn_delete)
        Button btnDelete;

        MyTextWatcher nameTextWatcher;
        MyTextWatcher tip0TextWatcher;
        MyTextWatcher tip1TextWatcher;
        MyTextWatcher tip2TextWatcher;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }

        void updatePosition(int position)
        {
            nameTextWatcher.updatePosition(position);
            tip0TextWatcher.updatePosition(position);
            tip1TextWatcher.updatePosition(position);
            tip2TextWatcher.updatePosition(position);
        }
    }

    class MyTextWatcher implements TextWatcher
    {
        static final int TYPE_NAME = 0;
        static final int TYPE_TIP0 = 1;
        static final int TYPE_TIP1 = 2;
        static final int TYPE_TIP2 = 3;
        //由于TextWatcher的afterTextChanged中拿不到对应的position值，所以自己创建一个子类
        private int position;
        private int type;

        MyTextWatcher(int type)
        {
            this.type = type;
        }

        public void updatePosition(int position)
        {
            this.position = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            AttributeBean attributeBean = mDataList.get(position);
            if (attributeBean != null)
            {
                List<String> att_label = attributeBean.getAtt_label();
                switch (type)
                {
                    case TYPE_NAME:
                        attributeBean.setAtt_name(s.toString()
                                .trim());
                        break;
                    case TYPE_TIP0:
                        if (att_label != null && att_label.size() > 0)
                        {
                            att_label.set(0, s.toString()
                                    .trim());
                        }
                        break;
                    case TYPE_TIP1:
                        if (att_label != null && att_label.size() > 1)
                        {
                            att_label.set(1, s.toString()
                                    .trim());
                        }
                        break;
                    case TYPE_TIP2:
                        if (att_label != null && att_label.size() > 2)
                        {
                            att_label.set(2, s.toString()
                                    .trim());
                        }
                        break;
                }
            }
        }
    }
}
