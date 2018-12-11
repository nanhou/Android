package cn.jinxiit.zebra.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class CategoryOfProductAdapter extends RecyclerView.Adapter<CategoryOfProductAdapter.ViewHolder> implements View.OnClickListener
{
    private List<ProductOwnerDataBean> mDataList;
    private Activity mContext;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private MyApp myApp;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener)
    {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public CategoryOfProductAdapter(MyApp myApp)
    {
        this.myApp = myApp;
        this.mDataList = new ArrayList<>();
    }

    public void clearDataList()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void addDataList(List<ProductOwnerDataBean> list)
    {
        if (list != null && list.size() > 0)
        {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = (Activity) parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_category_product, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.itemView.setTag(position);
        holder.ivBq.setImageResource(R.drawable.icon_nonstandard);
        holder.ivImg.setImageResource(R.drawable.img_default);
        ProductOwnerDataBean productOwnerDataBean = mDataList.get(position);
        if (productOwnerDataBean != null)
        {
            boolean isSelected = false;

            for (int i = 0; i < myApp.mBatchProductOwnerDataBeanList.size(); i++)
            {
                if (myApp.mBatchProductOwnerDataBeanList.get(i)
                        .getProduct_id()
                        .equals(productOwnerDataBean.getProduct_id()))
                {
                    isSelected = true;
                    break;
                }
            }

            if (isSelected)
            {
                holder.tvAdd.setText("已添加");
                holder.tvAdd.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                holder.tvAdd.setBackgroundResource(R.drawable.shape_corners20_colorfg);
            } else
            {
                holder.tvAdd.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.tvAdd.setText("添加");
                holder.tvAdd.setBackgroundResource(R.drawable.shape_corners20_colormain);
            }

            ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
            if (product != null)
            {
                ViewSetDataUtils.textViewSetText(holder.tvName, product.getTitle());
                String id = product.getId();
                if (!TextUtils.isEmpty(id))
                {
                    StringBuilder builder = new StringBuilder(1024);
                    builder.append("商品编号:");
                    int length = id.length();
                    if (length < 10)
                    {
                        for (int i = 0; i < 10 - length; i++)
                        {
                            builder.append(0);
                        }
                    }
                    builder.append(id);
                    holder.tvNo.setText(builder.toString());
                    builder.reverse();
                }

                ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
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
        int position = (int) v.getTag();
        ProductOwnerDataBean productOwnerDataBean = mDataList.get(position);

        boolean isSelected = false;
        int i = 0;
        for (; i < myApp.mBatchProductOwnerDataBeanList.size(); i++)
        {
            if (myApp.mBatchProductOwnerDataBeanList.get(i)
                    .getProduct_id()
                    .equals(productOwnerDataBean.getProduct_id()))
            {
                isSelected = true;
                break;
            }
        }

        if (isSelected)
        {
            myApp.mBatchProductOwnerDataBeanList.remove(i);
            notifyDataSetChanged();
        } else
        {
            httpCheckProductExist(productOwnerDataBean, v, position);
        }

        if (mOnRecyclerViewItemClickListener != null)
        {
            mOnRecyclerViewItemClickListener.onClick(v, position);
        }
    }

    private void httpCheckProductExist(ProductOwnerDataBean productOwnerDataBean, View view, int position)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();

            ApiUtils.getInstance()
                    .okgoPostCheckProductExisit(mContext, myApp.mToken, store_id, productOwnerDataBean.getProduct_id(), new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_RESULT))
                                {
                                    boolean isUnExisit = jsonObject.getBoolean(MyConstant.STR_RESULT);
                                    if (isUnExisit)
                                    {
                                        myApp.mBatchProductOwnerDataBeanList.add(productOwnerDataBean);
                                        notifyDataSetChanged();
                                        if (mOnRecyclerViewItemClickListener != null)
                                        {
                                            mOnRecyclerViewItemClickListener.onClick(view, position);
                                        }
                                    } else
                                    {
                                        productExisitDo(productOwnerDataBean, view, position);
                                    }
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {
                            MyToastUtils.error("检测失败");
                        }
                    });
        }
    }

    private void productExisitDo(ProductOwnerDataBean productOwnerDataBean, View view, int position)
    {
        ProductOwnerDataBean.ProductDataBean product = productOwnerDataBean.getProduct();
        if (product != null)
        {
            ProductOwnerDataBean.ProductDataBean.ExtraBean extra = product.getExtra();
            if (extra != null)
            {
                String upcCode = extra.getUpcCode();
                if (!TextUtils.isEmpty(upcCode))
                {
                    RxDialogSure rxDialogSure = new RxDialogSure(mContext);
                    rxDialogSure.setTitle("提示");
                    rxDialogSure.setContent("该店铺已存在该标品，不可重复创建");
                    rxDialogSure.getSureView()
                            .setOnClickListener(v -> rxDialogSure.cancel());
                    rxDialogSure.show();
                    return;
                }
            }
        }

        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);
        rxDialogSureCancel.setTitle("提示");
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        rxDialogSureCancel.setContent("该店铺已有类似的商品，是否继续创建？");
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            myApp.mBatchProductOwnerDataBeanList.add(productOwnerDataBean);
            notifyDataSetChanged();
            rxDialogSureCancel.cancel();
            if (mOnRecyclerViewItemClickListener != null)
            {
                mOnRecyclerViewItemClickListener.onClick(view, position);
            }
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.iv_into)
        ImageView ivInto;
        @BindView(R.id.iv_bq)
        ImageView ivBq;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_no)
        TextView tvNo;
        @BindView(R.id.tv_add)
        TextView tvAdd;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
