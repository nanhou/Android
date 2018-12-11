package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vondear.rxtools.view.roundprogressbar.RxTextRoundProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.ProductStatusComparedBean;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class GoodsTop5Adapter extends BaseAdapter
{
    private Context mContext;
    private List<ProductStatusComparedBean.ProductStatusBean> mDataList;
    private boolean mIsPrice = false;

    public GoodsTop5Adapter(Context context)
    {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    public void setmDataList(List<ProductStatusComparedBean.ProductStatusBean> mDataList, boolean isPrice)
    {
        this.mDataList = mDataList;
        this.mIsPrice = isPrice;
        notifyDataSetChanged();
    }

    public void clearDataList()
    {
        if (mDataList != null)
        {
            mDataList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount()
    {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_goodstop5, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mDataList.size() > 0)
        {
            ProductStatusComparedBean.ProductStatusBean productStatusBean = mDataList.get(position);
            ProductStatusComparedBean.ProductStatusBean productStatusBean0 = mDataList.get(0);
            ViewSetDataUtils.textViewSetText(holder.tvGoodsName, productStatusBean.getSkuName());
            if (mIsPrice)
            {
                int price0 = productStatusBean0.getPrice();
                holder.rxRoundPd15.setMax(price0);
                holder.rxRoundPd15.setSecondaryProgress(price0);
                int price = productStatusBean.getPrice();
                int compared = price0 / 3;
                if (price < compared)
                {
                    holder.rxRoundPd15.setProgress(compared);
                } else
                {
                    holder.rxRoundPd15.setProgress(price);
                }
                holder.rxRoundPd15.setProgressText(String.format("%.2f元", price * 0.01));
            } else
            {
                int count0 = productStatusBean0.getCount();
                holder.rxRoundPd15.setMax(count0);
                holder.rxRoundPd15.setSecondaryProgress(count0);
                int count = productStatusBean.getCount();
                int compared = count0 / 3;
                if (count < compared)
                {
                    holder.rxRoundPd15.setProgress(compared);
                } else
                {
                    holder.rxRoundPd15.setProgress(count);
                }
                holder.rxRoundPd15.setProgressText(count + "份");
            }
        }

        return convertView;
    }

    static class ViewHolder
    {
        @BindView(R.id.tv_goods_name)
        TextView tvGoodsName;
        @BindView(R.id.rx_round_pd15)
        RxTextRoundProgressBar rxRoundPd15;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
