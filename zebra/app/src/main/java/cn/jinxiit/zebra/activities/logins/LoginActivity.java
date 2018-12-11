package cn.jinxiit.zebra.activities.logins;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.MainActivity;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_sign_in)
    Button mBtnSignIn;

    private MyApp myApp;
    private Gson mGson = new Gson();
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            initState();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        myApp = (MyApp) getApplication();
        initView();
    }

    @OnClick({R.id.btn_sign_in, R.id.btn_sign_up, R.id.btn_forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                clickSignIn();
                break;
            case R.id.btn_sign_up:
                MyActivityUtils.skipActivity(mContext, SignupORResetpasswordActivity.class);
                break;
            case R.id.btn_forgot_password:
                Bundle bundle = new Bundle();
                bundle.putBoolean(MyConstant.STR_ISSIGNUP, false);
                MyActivityUtils.skipActivity(mContext, SignupORResetpasswordActivity.class, bundle);
                break;
        }
    }

    private void initView() {
        initState();
        initListener();
        spTokenIsExist();
    }

    private void spTokenIsExist() {
        myApp.mToken = ZebraUtils.getInstance()
                .sharedPreferencesGetString(mContext, MyConstant.STR_TOKEN);
        if (!TextUtils.isEmpty(myApp.mToken)) {
            tokenAfterAction();
        }
    }

    private void initListener() {
        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
    }

    private void initState() {
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();

        if (username.length() >= 6 && password.length() >= 6) {
            mBtnSignIn.setEnabled(true);
        } else {
            mBtnSignIn.setEnabled(false);
        }
    }

    private void clickSignIn() {
        String username = mEtUsername.getText().toString().trim();

        String password = mEtPassword.getText().toString().trim();

        ApiUtils.getInstance().okgoPostSignIn(this, username, password, new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.has(MyConstant.STR_TOKEN)) {
                        myApp.mToken = jsonObject.getString(MyConstant.STR_TOKEN);
                        ZebraUtils.getInstance()
                                .sharedPreferencesSaveString(mContext, MyConstant.STR_TOKEN,
                                        myApp.mToken);
                        tokenAfterAction();
                        //                                MyActivityUtils.skipActivityAndFinishAll(mContext, MainActivity.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {

            }
        });
    }

    private void tokenAfterAction() {
        refreshUserInfo(myApp.mToken);
        httpGetUserStores(myApp.mToken);
    }

    private void httpGetUserStores(String token) {
        ApiUtils.getInstance().okgoGetUserStores(this, token, "0", "10", new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.has(MyConstant.STR_STORES)) {
                        myApp.mStoreOwnerBeanList = mGson
                                .fromJson(jsonObject.getString(MyConstant.STR_STORES),
                                        new TypeToken<List<StoreOwnerBean>>() {
                                        }.getType());
                        if (myApp.mStoreOwnerBeanList != null && myApp.mStoreOwnerBeanList
                                .size() > 0) {
                            String storeId = ZebraUtils.getInstance()
                                    .sharedPreferencesGetString(mContext, myApp.mUser.getId());
                            if (!TextUtils.isEmpty(storeId)) {
                               for (int i = 0; i < myApp.mStoreOwnerBeanList.size(); i ++) {
                                   StoreOwnerBean storeOwnerBean = myApp.mStoreOwnerBeanList.get(i);
                                   if (storeId.equals(storeOwnerBean.getStore_id())){
                                       myApp.mCurrentStoreOwnerBean = storeOwnerBean;
                                       break;
                                   }
                               }
                            }
                        }
                        MyActivityUtils.skipActivityAndFinishAll(mContext, MainActivity.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {}
        });
    }

    private void refreshUserInfo(String token) {
        ApiUtils.getInstance().okgoGetUserInfo(this, token, new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                String result = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has(MyConstant.STR_USER)) {
                        myApp.mUser = mGson.fromJson(jsonObject.getString(MyConstant.STR_USER),
                                UserBean.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {}
        });
    }
}