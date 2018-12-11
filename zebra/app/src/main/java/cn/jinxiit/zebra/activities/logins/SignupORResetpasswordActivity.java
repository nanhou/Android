package cn.jinxiit.zebra.activities.logins;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.MainActivity;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;

public class SignupORResetpasswordActivity extends BaseActivity
{

    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.btn_sign_up)
    Button mBtnConfirm;
    @BindView(R.id.btn_get_code)
    Button mBtnGetCode;
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    private MyApp myApp;
    private Handler mHandler = new Handler();

    private int mTime = 0;
    private Runnable mRunnable = new Runnable()
    {
        @SuppressLint("DefaultLocale")
        @Override
        public void run()
        {
            mTime--;
            if (mTime <= 0)
            {
                mBtnGetCode.setText("获取验证码");
                mBtnGetCode.setEnabled(true);
            } else
            {
                mBtnGetCode.setText(String.format("%ds重新获取", mTime));
                mBtnGetCode.setEnabled(false);
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    private boolean mIsSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_resetpassword);
        ButterKnife.bind(this);
        myApp = (MyApp) getApplication();
        mIsSignUp = getIntent().getBooleanExtra(MyConstant.STR_ISSIGNUP, true);
        initView();
    }

    @OnClick({R.id.btn_sign_up, R.id.btn_get_code, R.id.ibtn_back})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_sign_up:
                clickConfirm();
                break;
            case R.id.ibtn_back:
                onBackPressed();
                break;
            case R.id.btn_get_code:
                clickGetCode();
                break;
        }
    }

    private void clickGetCode()
    {
        String phone = mEtUsername.getText()
                .toString()
                .trim();
        if (!NumberUtils.isPhoneNumberValid(phone))
        {
            MyToastUtils.error("请输入正确的手机号");
            return;
        }

        ApiUtils instance = ApiUtils.getInstance();

        instance.okgoPostSendSMSCode(mContext, phone, new ApiResultListener()
        {
            @Override
            public void onSuccess(Response<String> response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.has(MyConstant.STR_RESULT))
                    {
                        String reslut = jsonObject.getString(MyConstant.STR_RESULT);
                        if (MyConstant.STR_OK.equals(reslut))
                        {
                            MyToastUtils.success("验证码发送成功,请及时查收");
                            mTime = 60;
                            mHandler.post(mRunnable);
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

    private void initView()
    {
        initData();
        initListener();
    }

    private void initData()
    {
        if (!mIsSignUp)
        {
            mTvTitle.setText("忘记密码");
            mBtnConfirm.setText("重置密码");
        }
    }

    private void initListener()
    {
        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
        mEtCode.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            String phone = mEtUsername.getText()
                    .toString()
                    .trim();
            String password = mEtPassword.getText()
                    .toString()
                    .trim();
            String code = mEtCode.getText()
                    .toString()
                    .trim();
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(code))
            {
                mBtnConfirm.setEnabled(false);
            } else
            {
                mBtnConfirm.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void clickConfirm()
    {
        String phone = mEtUsername.getText()
                .toString()
                .trim();

        if (!NumberUtils.isPhoneNumberValid(phone))
        {
            MyToastUtils.error("请输入正确的手机号");
            return;
        }

        String password = mEtPassword.getText()
                .toString()
                .trim();

        if (password.length() < 6)
        {
            MyToastUtils.error("请输入6-16位密码");
            return;
        }

        String code = mEtCode.getText()
                .toString()
                .trim();

        if (mIsSignUp)
        {
            httpPostSignUp(phone, password, code);
        }
        else
        {
            httpPostResetPassword(phone, password, code);
        }
    }

    private void httpPostResetPassword(String phone, String password, String code)
    {
        ApiUtils.getInstance().okgoPostReSetPassword(this, phone, password, code, new ApiResultListener()
        {
            @Override
            public void onSuccess(Response<String> response)
            {
                if (response.body().contains(MyConstant.STR_OK))
                {
                    MyToastUtils.success("密码重置成功");
                    mContext.finish();
                }
            }

            @Override
            public void onError(Response<String> response)
            {
            }
        });
    }

    private void httpPostSignUp(String phone, String password, String code)
    {
        ApiUtils.getInstance()
                .okgoPostSignUp(this, phone, password, code, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        String body = response.body();
                        if (body.contains(MyConstant.STR_TOKEN))
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(body);
                                if (jsonObject.has(MyConstant.STR_TOKEN))
                                {
                                    myApp.mToken = jsonObject.getString(MyConstant.STR_TOKEN);
                                    MyToastUtils.success("注册成功");
                                    MyActivityUtils.skipActivityAndFinishAll(mContext, MainActivity.class);
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {

                    }
                });
    }
}
