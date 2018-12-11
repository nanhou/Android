package cn.jinxiit.zebra.fragments.homes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcssloop.widget.RCImageView;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.logins.LoginActivity;
import cn.jinxiit.zebra.activities.me.BusinessStateActivity;
import cn.jinxiit.zebra.activities.me.HelpAndFeedbackActivity;
import cn.jinxiit.zebra.activities.me.ImageQualityActivity;
import cn.jinxiit.zebra.activities.me.NotificationActivity;
import cn.jinxiit.zebra.activities.me.SettingOrderActivity;
import cn.jinxiit.zebra.activities.me.SettingPrinterActivity;
import cn.jinxiit.zebra.activities.me.SettingRemidActivity;
import cn.jinxiit.zebra.activities.me.SettingStoreActivity;
import cn.jinxiit.zebra.activities.me.SettingUserActivity;
import cn.jinxiit.zebra.activities.me.StoresActivity;
import cn.jinxiit.zebra.beans.CountBean;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.APKVersionCodeUtils;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.views.ObservableScrollView;

public class MeFragment extends Fragment
{
    @BindView(R.id.scrollview)
    ObservableScrollView mObservableScrollView;
    @BindView(R.id.ll_top0)
    LinearLayout mllTop;
    @BindView(R.id.rl_top)
    RelativeLayout mrlTop;
    @BindView(R.id.rciv_store_logo)
    RCImageView mIvStoreLogo;
    @BindView(R.id.tv_store_name)
    TextView mTvStoreName;
    @BindView(R.id.tv_store_id)
    TextView mTvStoreId;
    @BindView(R.id.rciv_store_logo_m)
    RCImageView mIvStoreLogoM;
    @BindView(R.id.tv_store_name_m)
    TextView mTvStoreNameM;
    @BindView(R.id.tv_count_notification)
    TextView mTvCountNotification;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    Unbinder unbinder;

    private MyApp myApp;
    private Gson mGson = new Gson();

    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mTvCountNotification != null)
            {
                httpGetNotificationCount();
                mTvCountNotification.postDelayed(mRunnable, 5000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initResumeData();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_setting_store, R.id.ll_business_state, R.id.ll_current_user, R.id.ibtn_stores, R.id.btn_setting_store, R.id.btn_setting_order, R.id.btn_setting_printer, R.id.btn_setting_remind, R.id.btn_logout, R.id.ll_notification, R.id.tv_help_feedback, R.id.ll_image_quality,})
    public void onViewClicked(View view)
    {
        FragmentActivity activity = Objects.requireNonNull(getActivity());
        switch (view.getId())
        {
            case R.id.ll_image_quality:
                MyActivityUtils.skipActivity(activity, ImageQualityActivity.class);
                break;
            case R.id.tv_help_feedback:
                MyActivityUtils.skipActivity(activity, HelpAndFeedbackActivity.class);
                break;
            case R.id.ll_setting_store:
                //                MyActivityUtils.skipActivity(activity, StoreInfoActivity.class);
                break;
            case R.id.ll_business_state:
                MyActivityUtils.skipActivity(activity, BusinessStateActivity.class);
                break;
            case R.id.btn_setting_store:
                MyActivityUtils.skipActivity(activity, SettingStoreActivity.class);
                break;
            case R.id.btn_setting_order:
                MyActivityUtils.skipActivity(activity, SettingOrderActivity.class);
                break;
            case R.id.btn_setting_printer:
                MyActivityUtils.skipActivity(activity, SettingPrinterActivity.class);
                break;
            case R.id.btn_setting_remind:
                MyActivityUtils.skipActivity(activity, SettingRemidActivity.class);
                break;
            case R.id.ll_current_user:
                MyActivityUtils.skipActivity(activity, SettingUserActivity.class);
                break;
            case R.id.ibtn_stores:
                MyActivityUtils.skipActivityForResult(activity, StoresActivity.class, 0);
                break;
            case R.id.btn_logout:
                logout(activity);
                break;
            case R.id.ll_notification:
                MyActivityUtils.skipActivity(activity, NotificationActivity.class);
                break;
        }
    }

    private void initResumeData()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean.getStore();
            if (store != null)
            {
                String name = store.getName();
                ViewSetDataUtils.textViewSetText(mTvStoreName, name);
                ViewSetDataUtils.textViewSetText(mTvStoreNameM, name);
                String id = "门店编号：" + store.getId();
                ViewSetDataUtils.textViewSetText(mTvStoreId, id);
                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                if (extra != null)
                {
                    String logo = extra.getLogo();
                    if (!TextUtils.isEmpty(logo))
                    {
                        String url = MyConstant.URL_GET_FILE + logo + ".jpg";
                        int width = WindowUtils.dip2px(getActivity(), 80);
                        int widthm = width / 2;
                        MyPicassoUtils.downSizeImageForUrl(width, width, url, mIvStoreLogo);
                        MyPicassoUtils.downSizeImageForUrl(widthm, widthm, url, mIvStoreLogoM);
                    } else
                    {
                        mIvStoreLogo.setImageResource(R.drawable.img_default);
                        mIvStoreLogoM.setImageResource(R.drawable.img_default);
                    }
                }
            }
        }
    }

    private void httpGetNotificationCount()
    {
        if (myApp.mCurrentStoreOwnerBean != null)
        {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id))
            {
                ApiUtils.getInstance()
                        .okgoGetCountTip(getActivity(), myApp.mToken, store_id, MyConstant.STR_MESSAGE, false, new ApiResultListener()
                        {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Response<String> response)
                            {
                                CountBean countBean = mGson.fromJson(response.body(), CountBean.class);
                                if (countBean != null && mTvCountNotification != null)
                                {
                                    mTvCountNotification.setText(countBean.getAll() + "");
                                }
                            }

                            @Override
                            public void onError(Response<String> response)
                            {
                            }
                        });
            }
        }
    }

    private void logout(final FragmentActivity activity)
    {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(activity);
        rxDialogSureCancel.setTitle("退出登录");
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            rxDialogSureCancel.cancel();
            myApp.clearData();
            MyActivityUtils.skipActivityAndFinishAll(activity, LoginActivity.class);
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    private void initView()
    {
        initData();
        initListener();

        mTvCountNotification.post(mRunnable);
    }

    private void initData()
    {
        Activity activity = Objects.requireNonNull(getActivity());
        myApp = (MyApp) activity.getApplication();
        if (myApp.mUser != null)
        {
            ViewSetDataUtils.textViewSetHint(mTvUserName, myApp.mUser.getName());
        }

        ViewSetDataUtils.textViewSetHint(mTvVersion, APKVersionCodeUtils.getVerName(activity));
    }

    private void initListener()
    {
        mObservableScrollView.setScrollViewListener((scrollView, x, y, oldx, oldy) -> {
            float height = WindowUtils.dip2px(myApp, 74);
            float scale = (float) y / height;
            if (scale >= 1)
            {
                scale = 1;
                mllTop.setVisibility(View.VISIBLE);
            } else
            {
                mllTop.setVisibility(View.GONE);
            }
            mrlTop.setBackgroundColor(Color.argb((int) (scale * 255), 19, 127, 255));
        });
    }
}
