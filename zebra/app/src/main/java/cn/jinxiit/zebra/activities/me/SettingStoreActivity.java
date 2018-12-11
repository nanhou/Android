package cn.jinxiit.zebra.activities.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcssloop.widget.RCImageView;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.me.storeupdate.PrinterListActivity;
import cn.jinxiit.zebra.activities.me.storeupdate.StoreUpdateNameActivity;
import cn.jinxiit.zebra.activities.me.storeupdate.UpdateStoreTimeActivity;
import cn.jinxiit.zebra.beans.FileBean;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.AvatarUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class SettingStoreActivity extends BaseActivity implements View.OnClickListener
{

    @BindView(R.id.tv_store_name)
    TextView mTvStoreName;
    @BindView(R.id.ll_store_name)
    LinearLayout mLlStoreName;
    @BindView(R.id.rciv_store_logo)
    RCImageView mIvStoreLogo;
    @BindView(R.id.ll_store_image)
    LinearLayout mLlStoreImage;
    @BindView(R.id.tv_store_type)
    TextView mTvStoreType;
    @BindView(R.id.ll_store_type)
    LinearLayout mLlStoreType;
    @BindView(R.id.tv_store_address)
    TextView mTvStoreAddress;
    @BindView(R.id.ll_store_address)
    LinearLayout mLlStoreAddress;
    @BindView(R.id.tv_store_annunciate)
    TextView mTvStoreAnnunciate;
    @BindView(R.id.ll_store_annunciate)
    LinearLayout mLlStoreAnnunciate;
    @BindView(R.id.tv_business_time)
    TextView mTvBusinessTime;
    @BindView(R.id.ll_store_time)
    LinearLayout mLlStoreTime;
    @BindView(R.id.tv_store_phone)
    TextView mTvStorePhone;
    @BindView(R.id.ll_store_phone)
    LinearLayout mLlStorePhone;
    @BindView(R.id.tv_printer)
    TextView mTvPrinter;
    @BindView(R.id.sb_auto_print)
    SwitchButton mSbAutoPrint;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_store);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initResumeData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        AvatarUtils.onRequestPermissionsResult(requestCode, grantResults, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String imgUrl = AvatarUtils.onActivityResult(requestCode, data, this, true, false);
        if (!TextUtils.isEmpty(imgUrl))
        {
            httpPostFile(imgUrl);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v)
    {
        AvatarUtils.imgOnClick(v, this);
    }

    @OnClick({R.id.ll_store_annunciate, R.id.ll_store_time, R.id.ll_printer, R.id.ll_store_name, R.id.ll_store_image})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_store_annunciate:
                clickAnnunciate();
                break;
            case R.id.ll_store_time:
                MyActivityUtils.skipActivity(this, UpdateStoreTimeActivity.class);
                break;
            case R.id.ll_printer:
                MyActivityUtils.skipActivity(this, PrinterListActivity.class);
                break;
            case R.id.ll_store_name:
                MyActivityUtils.skipActivity(this, StoreUpdateNameActivity.class);
                break;
            case R.id.ll_store_image:
                AvatarUtils.creatmenuWindowDialog(this, mIvStoreLogo);
                break;
        }
    }

    private void clickAnnunciate()
    {
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(this);
        rxDialogEditSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogEditSureCancel.setTitle("修改公告");
        EditText editText = rxDialogEditSureCancel.getEditText();
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");

        cancelView.setOnClickListener(v -> {
            String str = editText.getText()
                    .toString();
            if (TextUtils.isEmpty(str) || str.length() > 15)
            {
                MyToastUtils.error("输入15字内的公告信息");
                return;
            }
            rxDialogEditSureCancel.cancel();
            httpPostUpdateStoreInfo(null, str, null);
        });
        sureView.setOnClickListener(v -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    private void initView()
    {
        initData();
        initListener();
    }

    private void initListener()
    {
        mSbAutoPrint.setOnCheckedChangeListener((buttonView, isChecked) -> {

            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put(MyConstant.STR_AUTO_PRINT, isChecked);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            httpPostUpdateStoreInfo(null, null, jsonObject);
        });
    }

    private void initData()
    {
        new MyToolBar(mContext, "门店设置", null);
        myApp = (MyApp) getApplication();
    }

    private void httpPostFile(String imgUrl)
    {
        ApiUtils.getInstance()
                .okgoPostFile(this, myApp.mToken, imgUrl, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        FileBean fileBean = new Gson().fromJson(response.body(), FileBean.class);
                        if (fileBean != null)
                        {
                            String file_key = fileBean.getFile_key();
                            if (!TextUtils.isEmpty(file_key))
                            {
                                httpPostUpdateStoreLogo(file_key);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                    }
                });
    }

    private void httpPostUpdateStoreLogo(String file_key)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(MyConstant.STR_LOGO, file_key);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        httpPostUpdateStoreInfo(null, null, jsonObject);
    }

    private void httpPostUpdateStoreInfo(String name, String summary, JSONObject jsonObject)
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            final StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                String id = store.getId();
                if (!TextUtils.isEmpty(id))
                {
                    String strJson = null;
                    if (jsonObject != null)
                    {
                        strJson = jsonObject.toString();
                    }
                    ApiUtils.getInstance()
                            .okgoPostUpdateStore(this, myApp.mToken, id, name, summary, strJson, new ApiResultListener()
                            {
                                @Override
                                public void onSuccess(Response<String> response)
                                {
                                    StoreOwnerBean.StoreBean storeBean = new Gson().fromJson(response.body(), StoreOwnerBean.StoreBean.class);
                                    myApp.mCurrentStoreOwnerBean.setStore(storeBean);
                                    initResumeData();
                                }

                                @Override
                                public void onError(Response<String> response)
                                {
                                    MyToastUtils.error("修改失败");
                                }
                            });
                }
            }
        }
    }

    private void initResumeData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                ViewSetDataUtils.textViewSetHint(mTvStoreName, store.getName());
                ViewSetDataUtils.textViewSetHint(mTvStoreAnnunciate, store.getSummary());
                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                if (extra != null)
                {
                    String logo = extra.getLogo();
                    if (!TextUtils.isEmpty(logo))
                    {
                        String url = MyConstant.URL_GET_FILE + logo + ".jpg";
                        int width = WindowUtils.dip2px(this, 80);
                        MyPicassoUtils.downSizeImageForUrl(width, width, url, mIvStoreLogo);
                    }

                    String city = extra.getCity();
                    if (!TextUtils.isEmpty(city))
                    {
                        StringBuilder buffer = new StringBuilder(1024);
                        buffer.append(city);
                        String address = extra.getAddress();
                        if (!TextUtils.isEmpty(address))
                        {
                            buffer.append("(")
                                    .append(address)
                                    .append(")");
                        }
                        mTvStoreAddress.setHint(buffer.toString());
                        buffer.reverse();
                    }
                    List<String> business_time = extra.getBusiness_time();
                    if (business_time != null && business_time.size() > 0)
                    {
                        String strBusinessTime = business_time.toString();
                        mTvBusinessTime.setHint(strBusinessTime.substring(1, strBusinessTime.length() - 1));
                    }

                    ViewSetDataUtils.textViewSetHint(mTvStorePhone, extra.getTelephone());
                    ViewSetDataUtils.textViewSetText(mTvPrinter, extra.getPrint_name());
                    mSbAutoPrint.setChecked(extra.isAuto_print());
                }
            }
        }
    }
}