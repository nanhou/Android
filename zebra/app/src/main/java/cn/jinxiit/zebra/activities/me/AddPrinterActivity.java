package cn.jinxiit.zebra.activities.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.PrinterBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class AddPrinterActivity extends BaseActivity
{
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_sn)
    EditText mEtSn;
    @BindView(R.id.et_key)
    EditText mEtKey;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printer);
        ButterKnife.bind(this);

        initView();
    }

    private void initView()
    {
        initData();
    }

    private void initData()
    {
        myApp = (MyApp) getApplication();
        new MyToolBar(this, "添加打印机", "完成").setOnTopMenuClickListener(v -> clickSave());
    }

    private void clickSave()
    {
        String name = mEtName.getText()
                .toString()
                .trim();
        String sn = mEtSn.getText()
                .toString()
                .trim();
        String key = mEtKey.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sn) || TextUtils.isEmpty(key))
        {
            MyToastUtils.error("请补全打印机信息");
            return;
        }

        ApiUtils.getInstance()
                .okgoPostAddPrinter(this, myApp.mToken, sn, name, key, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has(MyConstant.STR_PRINTER))
                            {
                                PrinterBean printerBean = new Gson().fromJson(jsonObject.getString(MyConstant.STR_PRINTER), PrinterBean.class);
                                if (printerBean != null)
                                {
                                    MyToastUtils.success("添加成功");
                                    finish();
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
                        MyToastUtils.error("绑定失败,该打印已可能已被绑定过");
                    }
                });
    }
}
