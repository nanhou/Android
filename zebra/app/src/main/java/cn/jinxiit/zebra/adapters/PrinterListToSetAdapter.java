package cn.jinxiit.zebra.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.PrinterBean;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;

public class PrinterListToSetAdapter extends RecyclerView.Adapter<PrinterListToSetAdapter.ViewHolder>
{
    private List<PrinterBean> mDataList;
    private Activity mContext;

    public PrinterListToSetAdapter()
    {
        mDataList = new ArrayList<>();
    }

    public void clearDataList()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void addDataList(List<PrinterBean> list)
    {
        mDataList.addAll(list);
        notifyDataSetChanged();
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
                .inflate(R.layout.recycler_item_printer_set, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvTestPrint.setOnClickListener(v -> clickTestPrint(v));
        holder.tvDeletePrinter.setOnClickListener(v -> clickDeletePrinter(v));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.tvTestPrint.setTag(position);
        holder.tvDeletePrinter.setTag(position);

        PrinterBean printerBean = mDataList.get(position);
        ViewSetDataUtils.textViewSetText(holder.tvName, printerBean.getName());
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    private void clickDeletePrinter(View v)
    {
        int position = (int) v.getTag();
        PrinterBean printerBean = mDataList.get(position);
        MyApp myApp = (MyApp) mContext.getApplication();
        ApiUtils.getInstance().okgoDeletePrinter(mContext, myApp.mToken, printerBean.getSerial(), new ApiResultListener()
        {
            @Override
            public void onSuccess(Response<String> response)
            {
                String name = printerBean.getName();
                if (myApp.mCurrentStoreOwnerBean != null)
                {
                    StoreOwnerBean.StoreBean.ExtraBean extra = myApp.mCurrentStoreOwnerBean.getStore()
                            .getExtra();
                    if (name.equals(extra.getPrint_name()))
                    {
                        extra.setPrint_name("");
                    }
                }
                mDataList.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onError(Response<String> response)
            {
                MyToastUtils.error("删除失败");
            }
        });
    }

    private void clickTestPrint(View v)
    {
        int position = (int) v.getTag();
        PrinterBean printerBean = mDataList.get(position);
        MyApp myApp = (MyApp) mContext.getApplication();
        ApiUtils.getInstance()
                .okgoPostTestPrint(mContext, myApp.mToken, printerBean.getSerial(), new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        MyToastUtils.success("请等待打印机反应");
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        MyToastUtils.error("测试失败");
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_test_print)
        TextView tvTestPrint;
        @BindView(R.id.tv_delete_printer)
        TextView tvDeletePrinter;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
