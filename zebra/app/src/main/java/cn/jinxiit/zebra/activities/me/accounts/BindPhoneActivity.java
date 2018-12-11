package cn.jinxiit.zebra.activities.me.accounts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;

public class BindPhoneActivity extends BaseActivity
{

    @BindView(R.id.ll_layout0)
    LinearLayout mLlLayout0;
    @BindView(R.id.ll_layout1)
    LinearLayout mLlLayout1;
    @BindView(R.id.et_new_phone)
    EditText mEtNewPhone;
    @BindView(R.id.btn_get_code0)
    Button mBtnGetCode0;
    @BindView(R.id.btn_get_code1)
    Button mBtnGetCode1;

    private Handler mHandler = new Handler();

    private int mTime;

    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mIsBindPhone)
            {
                updateBtnGetCode(mBtnGetCode1);
            } else
            {
                updateBtnGetCode(mBtnGetCode0);
            }
        }
    };

    private boolean mIsBindPhone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "绑定手机号", null);
    }

    @OnClick({R.id.btn_next_step, R.id.btn_get_code0, R.id.btn_get_code1})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_next_step:
                mLlLayout0.setVisibility(View.GONE);
                mLlLayout1.setVisibility(View.VISIBLE);
                mEtNewPhone.setFocusable(true);
                mEtNewPhone.setFocusableInTouchMode(true);
                mEtNewPhone.requestFocus();
                mHandler.removeCallbacks(mRunnable);
                mIsBindPhone = true;
                break;
            case R.id.btn_get_code0:
                String phone = "18850061041";
                httpSendSMSCode(phone);
                break;
            case R.id.btn_get_code1:
                String newPhone = mEtNewPhone.getText()
                        .toString()
                        .trim();
                httpSendSMSCode(newPhone);
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateBtnGetCode(Button btnGetCode)
    {
        mTime--;
        if (mTime <= 0)
        {
            btnGetCode.setText("获取验证码");
            btnGetCode.setEnabled(true);
        } else
        {
            btnGetCode.setText(String.format("%ds重新获取", mTime));
            btnGetCode.setEnabled(false);
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    private void httpSendSMSCode(String phone)
    {
        if (!NumberUtils.isPhoneNumberValid(phone))
        {
            MyToastUtils.error("请输入正确的手机号");
            return;
        }

        ApiUtils.getInstance()
                .okgoPostSendSMSCode(this, phone, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.has(MyConstant.STR_RESULT))
                            {
                                String result = jsonObject.getString(MyConstant.STR_RESULT);
                                if (MyConstant.STR_OK.equals(result))
                                {
                                    MyToastUtils.success("验证码发送成功，请及时查验");
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
}
