package cn.jinxiit.zebra.activities.me.accounts;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;

public class UpdatePasswordActivity extends BaseActivity
{

    @BindView(R.id.btn_get_code)
    Button mBtnGetCode;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.et_new_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirm_password)
    EditText mEtNewPasswd;

    private MyApp myApp;
    private Handler mHandler = new Handler();
    private int mTime = 0;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTime--;
            if (mTime <= 0) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "修改密码", null);
        myApp = (MyApp) getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
        {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }

    @OnClick({R.id.btn_get_code, R.id.btn_confirm})
    public void onViewClicked(View view)
    {
        switch (view.getId()) {
            case R.id.btn_get_code:
                httpSendSMSCode();
                break;
            case R.id.btn_confirm:
                clickConfrim();
                break;
        }
    }

    private void clickConfrim() {
        if (myApp.mUser == null)
        {
            return;
        }
        UserBean.ExtraBean extra = myApp.mUser.getExtra();
        if (extra == null)
        {
            return;
        }
        String phone = extra.getPhone();

        if (!NumberUtils.isPhoneNumberValid(phone))
        {
            MyToastUtils.error("该账号手机号不存在");
            return;
        }
        String code = mEtCode.getText().toString().trim();
        String passwd = mEtPassword.getText().toString().trim();
        String newpasswd = mEtNewPasswd.getText().toString().trim();

        if (TextUtils.isEmpty(code))
        {
            MyToastUtils.error("验证码不能为空");
            return;
        }

        if (passwd.length() < 6) {
            MyToastUtils.error("请输入6-16位密码");
            return;
        }

        if (!passwd.equals(newpasswd))
        {
            MyToastUtils.error("两次密码不一致，请重新输入");
            mEtPassword.setText("");
            mEtNewPasswd.setText("");
            return;
        }
        httpUpdatePasswd(phone, passwd, code);
    }

    private void httpUpdatePasswd(String phone, String passwd, String code) {
        ApiUtils.getInstance().okgoPostReSetPassword(this, phone, passwd, code, new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                if (response.body().contains(MyConstant.STR_OK))
                {
                    MyToastUtils.success("密码修改成功");
                    mContext.finish();
                }
            }

            @Override
            public void onError(Response<String> response) {

            }
        });
    }

    private void httpSendSMSCode() {

        if (myApp.mUser == null)
        {
            return;
        }
        UserBean.ExtraBean extra = myApp.mUser.getExtra();
        if (extra == null)
        {
            return;
        }
        String phone = extra.getPhone();

        if (!NumberUtils.isPhoneNumberValid(phone))
        {
            MyToastUtils.error("该账号手机号不存在");
            return;
        }
        ApiUtils.getInstance().okgoPostSendSMSCode(this, phone, new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.has(MyConstant.STR_RESULT))
                    {
                        String result = jsonObject.getString(MyConstant.STR_RESULT);
                        if (MyConstant.STR_OK.equals(result))
                        {
                            MyToastUtils.success("验证码发送成功,请及时验收");
                            mTime = 60;
                            mHandler.post(mRunnable);
                        }
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
}
