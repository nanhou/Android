package cn.jinxiit.zebra.activities.me.storeupdate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class StoreUpdateNameActivity extends BaseActivity
{

    @BindView(R.id.et_text)
    EditText mEtText;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_update_name);
        ButterKnife.bind(this);
        new MyToolBar(this, "修改门店名称", null);
        myApp = (MyApp) getApplication();

        initView();
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked()
    {
        final String name = mEtText.getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(name) || myApp.mCurrentStoreOwnerBean == null)
        {
            return;
        }

        final StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
        if (store != null)
        {
            String id = store.getId();
            if (!TextUtils.isEmpty(id))
            {
                ApiUtils.getInstance()
                        .okgoPostUpdateStore(this, myApp.mToken, id, name, null, null, new ApiResultListener()
                        {
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                store.setName(name);
                                MyToastUtils.success("修改成功");
                                finish();
                            }

                            @Override
                            public void onError(Response<String> response)
                            {

                            }
                        });
            }
        }
    }

    private void initView()
    {
        initData();
        initListener();
    }

    private void initData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                String name = store.getName();
                if (!TextUtils.isEmpty(name))
                {
                    mEtText.setText(name);
                    mEtText.setSelection(name.length());
                }
            }
        }
    }

    private void initListener()
    {
        final String oldName = mEtText.getText()
                .toString()
                .trim();
        mEtText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String trim = s.toString()
                        .trim();
                if (TextUtils.isEmpty(trim) || oldName.equals(trim))
                {
                    mBtnConfirm.setEnabled(false);
                } else
                {
                    mBtnConfirm.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }
}
