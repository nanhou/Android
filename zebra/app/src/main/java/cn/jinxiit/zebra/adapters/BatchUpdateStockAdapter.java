package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class BatchUpdateStockAdapter extends RecyclerView.Adapter<BatchUpdateStockAdapter.ViewHolder>
{
    private List<GoodsBean> mDataList;
    private Context mContext;

    public BatchUpdateStockAdapter(List<GoodsBean> mDataList)
    {
        this.mDataList = mDataList;
    }

    public List<GoodsBean> getDataList()
    {
        return mDataList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_batch_update_stock, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.jdMyTextWatcher = new MyTextWatcher(MyConstant.STR_EN_JDDJ);
        holder.etStockJd.addTextChangedListener(holder.jdMyTextWatcher);
        holder.mtMyTextWatcher = new MyTextWatcher(MyConstant.STR_EN_MT);
        holder.etStockMt.addTextChangedListener(holder.mtMyTextWatcher);
        holder.elemeMyTextWatcher = new MyTextWatcher(MyConstant.STR_EN_ELEME);
        holder.etStockEleme.addTextChangedListener(holder.elemeMyTextWatcher);
        holder.ebaiMyTextWatcher = new MyTextWatcher(MyConstant.STR_EN_EBAI);
        holder.etStockEbai.addTextChangedListener(holder.ebaiMyTextWatcher);
        holder.tvJdMin.setOnClickListener(v -> holder.etStockJd.setText("0"));
        holder.tvJdMax.setOnClickListener(v -> holder.etStockJd.setText("999"));
        holder.tvMtMin.setOnClickListener(v -> holder.etStockMt.setText("0"));
        holder.tvMtMax.setOnClickListener(v -> holder.etStockMt.setText("999"));
        holder.tvElemeMin.setOnClickListener(v -> holder.etStockEleme.setText("0"));
        holder.tvElemeMax.setOnClickListener(v -> holder.etStockEleme.setText("999"));
        holder.tvEbaiMin.setOnClickListener(v -> holder.etStockEbai.setText("0"));
        holder.tvEbaiMax.setOnClickListener(v -> holder.etStockEbai.setText("999"));
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.updatePosition(position);

        holder.ivImg.setImageResource(R.drawable.img_default);
        holder.tvName.setText("未知名称");
        holder.tvProductNo.setHint("商品编码未知");
        holder.tvStock.setText("库存未知");
        holder.llJd.setVisibility(View.GONE);
        holder.llMt.setVisibility(View.GONE);
        holder.llEleme.setVisibility(View.GONE);
        holder.llEbai.setVisibility(View.GONE);

        GoodsBean goodsBean = mDataList.get(position);
        if (goodsBean != null)
        {
            GoodsBean.ProductBean product = goodsBean.getProduct();
            if (product != null)
            {
                List<String> images = product.getImages();
                if (images != null && images.size() > 0)
                {
                    String fileKey = images.get(0);
                    if (!TextUtils.isEmpty(fileKey))
                    {
                        String imgUrl = MyConstant.URL_GET_FILE + fileKey + ".jpg";
                        int width = WindowUtils.dip2px(mContext, 80);
                        MyPicassoUtils.downSizeImageForUrl(width, width, imgUrl, holder.ivImg);
                    }
                }

                ViewSetDataUtils.textViewSetText(holder.tvName, product.getName());
                String product_code = product.getProduct_code();
                if (!TextUtils.isEmpty(product_code))
                {
                    holder.tvProductNo.setHint("商品编码: " + product_code);
                }
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product.getPriceAndStock();
                if (priceAndStockBeanList != null)
                {
                    long totalStock = 0;
                    for (int i = 0; i < priceAndStockBeanList.size(); i++)
                    {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList.get(i);
                        if (priceAndStockBean != null)
                        {
                            long stock = priceAndStockBean.getStock();
                            int binded = priceAndStockBean.getBinded();

                            String third_type = priceAndStockBean.getThird_type();
                            if (!TextUtils.isEmpty(third_type))
                            {
                                switch (third_type)
                                {
                                    case MyConstant.STR_EN_JDDJ:
                                        if (binded == 1)
                                        {
                                            holder.etStockJd.setText(stock + "");
                                            totalStock += stock;
                                            holder.llJd.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case MyConstant.STR_EN_MT:
                                        if (binded == 1)
                                        {
                                            holder.etStockMt.setText(stock + "");
                                            totalStock += stock;
                                            holder.llMt.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case MyConstant.STR_EN_ELEME:
                                        if (binded == 1)
                                        {
                                            holder.etStockEleme.setText(stock + "");
                                            totalStock += stock;
                                            holder.llEleme.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case MyConstant.STR_EN_EBAI:
                                        if (binded == 1)
                                        {
                                            holder.etStockEbai.setText(stock + "");
                                            totalStock += stock;
                                            holder.llEbai.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    holder.tvStock.setText("当前库存: " + totalStock);
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
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_product_no)
        TextView tvProductNo;
        @BindView(R.id.tv_stock)
        TextView tvStock;
        @BindView(R.id.et_stock_jd)
        EditText etStockJd;
        @BindView(R.id.et_stock_mt)
        EditText etStockMt;
        @BindView(R.id.et_stock_eleme)
        EditText etStockEleme;
        @BindView(R.id.et_stock_ebai)
        EditText etStockEbai;
        @BindView(R.id.ll_jd)
        LinearLayout llJd;
        @BindView(R.id.ll_mt)
        LinearLayout llMt;
        @BindView(R.id.ll_eleme)
        LinearLayout llEleme;
        @BindView(R.id.ll_ebai)
        LinearLayout llEbai;
        @BindView(R.id.tv_jd_min)
        TextView tvJdMin;
        @BindView(R.id.tv_jd_max)
        TextView tvJdMax;
        @BindView(R.id.tv_mt_min)
        TextView tvMtMin;
        @BindView(R.id.tv_mt_max)
        TextView tvMtMax;
        @BindView(R.id.tv_eleme_min)
        TextView tvElemeMin;
        @BindView(R.id.tv_eleme_max)
        TextView tvElemeMax;
        @BindView(R.id.tv_ebai_min)
        TextView tvEbaiMin;
        @BindView(R.id.tv_ebai_max)
        TextView tvEbaiMax;

        MyTextWatcher jdMyTextWatcher;
        MyTextWatcher mtMyTextWatcher;
        MyTextWatcher elemeMyTextWatcher;
        MyTextWatcher ebaiMyTextWatcher;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }

        void updatePosition(int position)
        {
            if (jdMyTextWatcher != null && mtMyTextWatcher != null && elemeMyTextWatcher != null)
            {
                jdMyTextWatcher.updatePosition(position);
                mtMyTextWatcher.updatePosition(position);
                elemeMyTextWatcher.updatePosition(position);
            }
        }
    }

    class MyTextWatcher implements TextWatcher
    {
        //由于TextWatcher的afterTextChanged中拿不到对应的position值，所以自己创建一个子类
        private int position;
        private String type;

        MyTextWatcher(String type)
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
            String strNum = s.toString()
                    .trim();
            if (TextUtils.isEmpty(strNum))
            {
                return;
            }

            GoodsBean goodsBean = mDataList.get(position);
            if (goodsBean != null)
            {
                GoodsBean.ProductBean product = goodsBean.getProduct();
                if (product != null)
                {
                    List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockList = product.getPriceAndStock();
                    if (priceAndStockList != null)
                    {
                        for (int i = 0; i < priceAndStockList.size(); i++)
                        {
                            GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockList.get(i);
                            if (priceAndStockBean != null)
                            {
                                if (type.equals(priceAndStockBean.getThird_type()))
                                {
                                    priceAndStockBean.setStock(Long.parseLong(strNum));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
