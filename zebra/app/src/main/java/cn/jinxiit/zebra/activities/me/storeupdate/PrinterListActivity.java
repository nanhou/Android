package cn.jinxiit.zebra.activities.me.storeupdate;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.PrinterListAdapter;
import cn.jinxiit.zebra.beans.PrinterBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class PrinterListActivity extends BaseActivity
{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private PrinterListAdapter mPrinterListAdapter;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_list);
        ButterKnife.bind(this);

        intView();
    }

    private void intView()
    {
        initData();
        initRecyclerView();
        initListener();

        httpGetPrintList();
    }

    private void httpGetPrintList()
    {
        ApiUtils.getInstance()
                .okgoGetPrinterList(this, myApp.mToken, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has(MyConstant.STR_PRINTERS))
                            {
                                List<PrinterBean> printerBeanList = new Gson().fromJson(jsonObject.getString(MyConstant.STR_PRINTERS), new TypeToken<List<PrinterBean>>()
                                {
                                }.getType());
                                if (printerBeanList != null)
                                {
                                    mPrinterListAdapter.addDataList(printerBeanList);
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
                    }
                });
    }

    private void initListener()
    {
        mPrinterListAdapter.setOnRecyclerViewItemClickListener((view, position) -> {

            PrinterBean printerBean = mPrinterListAdapter.getItemData(position);
            if (printerBean != null && myApp.mCurrentStoreOwnerBean != null)
            {
                ApiUtils.getInstance()
                        .okgoPostStoreBindPrinter(mContext, myApp.mToken, printerBean.getSerial(), myApp.mCurrentStoreOwnerBean.getStore_id(), new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                myApp.mCurrentStoreOwnerBean.getStore().getExtra().setPrint_name(printerBean.getName());
                                MyToastUtils.success("绑定成功");
                                finish();
                            }

                            @Override
                            public void onError(Response<String> response)
                            {
                                MyToastUtils.error("绑定失败");
                            }
                        });
            }
        });
    }

    private void initRecyclerView()
    {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = ZebraUtils.getInstance()
                .getRecyclerViewGetDivider(this);
        mRecyclerView.addItemDecoration(divider);
        mPrinterListAdapter = new PrinterListAdapter();
        mRecyclerView.setAdapter(mPrinterListAdapter);
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        new MyToolBar(this, "连接打印设备", null);
    }
}
