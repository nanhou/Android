package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class BatchCreateProductListAdapter extends RecyclerView.Adapter<BatchCreateProductListAdapter.ViewHolder> implements View.OnClickListener
{
    private List<ProductOwnerDataBean> mDataList;
    private Context mContext;

    public BatchCreateProductListAdapter(List<ProductOwnerDataBean> list)
    {
        this.mDataList = list;
    }

    public List<ProductOwnerDataBean> getDataList()
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
                .inflate(R.layout.recycler_item_batch_create_product, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvUpdate.setOnClickListener(this);
        return holder;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.tvUpdate.setTag(position);
        holder.tvPrice.setText("价格: ¥ 0.00");
        holder.ivBq.setImageResource(R.drawable.icon_nonstandard);
        holder.ivImg.setImageResource(R.drawable.img_default);
        ProductOwnerDataBean productOwnerDataBean = mDataList.get(position);
        if (productOwnerDataBean != null)
        {
            ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
            if (product != null)
            {
                ViewSetDataUtils.textViewSetText(holder.tvName, product.getTitle());
                ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
                    holder.tvPrice.setText(String.format("价格: ¥ %.2f", (double) extra.getPrice() * 0.01));
                    String upcCode = extra.getUpcCode();
                    if (!TextUtils.isEmpty(upcCode))
                    {
                        holder.ivBq.setImageResource(R.drawable.icon_standard);
                    }
                    List<String> images = extra.getImages();
                    if (images != null && images.size() > 0)
                    {
                        String fileKey = images.get(0);
                        if (!TextUtils.isEmpty(fileKey))
                        {
                            int width = WindowUtils.dip2px(mContext, 80);
                            String url = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                            MyPicassoUtils.downSizeImageForUrl(width, width, url, holder.ivImg);
                        }
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_update:
                clickUpdatePrice(v);
                break;
        }
    }

    private void clickUpdatePrice(View v)
    {
        int position = (int) v.getTag();
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(mContext);//提示弹窗
        rxDialogEditSureCancel.setTitle("调整价格");
        EditText editText = rxDialogEditSureCancel.getEditText();
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v1 -> {
            String strNum = rxDialogEditSureCancel.getEditText()
                    .getText()
                    .toString()
                    .trim();
            if (TextUtils.isEmpty(strNum))
            {
                MyToastUtils.error("请输入调整的价格");
                return;
            }
            rxDialogEditSureCancel.cancel();
            ProductOwnerDataBean productOwnerDataBean = mDataList.get(position);
            if (productOwnerDataBean != null)
            {
                ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
                if (product != null)
                {
                    ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                    if (extra != null)
                    {
                        long price = (long) (Double.parseDouble(strNum) * 100);
                        extra.setPrice(price);
                        notifyDataSetChanged();
                    }
                }
            }
        });
        sureView.setOnClickListener(v12 -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.iv_bq)
        ImageView ivBq;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_update)
        TextView tvUpdate;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
