package cn.jinxiit.zebra.activities;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.RefreshHttpService;
import cn.jinxiit.zebra.activities.me.StoresActivity;
import cn.jinxiit.zebra.fragments.homes.AllOrderFragment;
import cn.jinxiit.zebra.fragments.homes.MeFragment;
import cn.jinxiit.zebra.fragments.homes.ProductFragment;
import cn.jinxiit.zebra.fragments.homes.StaticFragment;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyActivityUtils;

import static cn.jinxiit.zebra.MyApp.mBlueBinder;
import static cn.jinxiit.zebra.MyApp.mIsBlueLink;

public class MainActivity extends BaseActivity {
    @BindView(R.id.rg)
    RadioGroup mRgNavigator;
    //    blue printer
    //bindService的参数connection
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //绑定成功
            mBlueBinder = (IMyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private List<Fragment> mFragmentList;
    private boolean mIsFirstTip = true;
    private MyApp myApp;
    //service
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RefreshHttpService refreshHttpService = ((RefreshHttpService.MyBinder) service)
                    .getService();
            if (refreshHttpService != null) {
                refreshHttpService.startForeground(98, getNotification());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();
        createBindService();
    }

    @Override
    protected void protectApp() {
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }

    private void initView() {
        initData();
        initFragment();
    }

    private void initListener() {
        mRgNavigator.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));

            Fragment fragment = mFragmentList.get(position);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (int i = 0; i < mFragmentList.size(); i++) {
                Fragment getFragment = mFragmentList.get(i);
                if (getFragment.isAdded()) {
                    transaction.hide(getFragment);
                }
            }

            if (!fragment.isAdded()) {
                transaction.add(R.id.rl_fragment, fragment);
            }
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        });
    }

    private void createBindService() {
        startService(new Intent(MainActivity.this, RefreshHttpService.class));
        bindService(new Intent(MainActivity.this, RefreshHttpService.class), mConnection,
                Context.BIND_AUTO_CREATE);

        //绑定service，获取ImyBinder对象
        Intent intent = new Intent(this, PosprinterService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initData() {
        myApp = (MyApp) getApplication();
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new AllOrderFragment());
        mFragmentList.add(new ProductFragment());
        mFragmentList.add(new StaticFragment());
        mFragmentList.add(new MeFragment());

        int position = getIntent().getIntExtra(MyConstant.STR_POSITION, 0);
        mRgNavigator.check(mRgNavigator.getChildAt(position).getId());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment startFragment = mFragmentList.get(position);
        transaction.add(R.id.rl_fragment, startFragment);
        transaction.show(startFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 99) {//返回订单
            ((RadioButton) mRgNavigator.getChildAt(0)).setChecked(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelAll();
        unbindService(mConnection);
        if (MyApp.mBlueBinder != null && MyApp.mIsBlueLink) {
            mBlueBinder.disconnectCurrentPort(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            });
        }
        unbindService(conn);
        mIsBlueLink = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsFirstTip && myApp.mCurrentStoreOwnerBean == null) {
            mIsFirstTip = false;
            showCreateStoreTip();
        }
    }

    private void showCreateStoreTip() {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);//提示弹窗
        rxDialogSureCancel.setTitle("未选择门店");
        rxDialogSureCancel.getTitleView()
                .setTextColor(ContextCompat.getColor(mContext, R.color.black));
        rxDialogSureCancel.setContent("您还没有门店，部分功能将无法用，\n快去选择门店吧?");
        TextView titleView = rxDialogSureCancel.getTitleView();
        titleView.setTextSize(17);
        TextView contentView = rxDialogSureCancel.getContentView();
        contentView.setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("选择门店");
        cancelView.setText("取消");
        cancelView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        sureView.setOnClickListener(v -> {
            rxDialogSureCancel.cancel();
            MyActivityUtils.skipActivityForResult(mContext, StoresActivity.class, 0);
        });
        rxDialogSureCancel.show();
    }

    private Notification getNotification() {
        Notification.Builder mBuilder = new Notification.Builder(MainActivity.this);
        mBuilder.setShowWhen(false);
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(R.drawable.logo_zebra);
        mBuilder.setLargeIcon(
                ((BitmapDrawable) Objects.requireNonNull(getDrawable(R.drawable.logo_zebra)))
                        .getBitmap());
        mBuilder.setContentTitle("斑马鲜生订单状态通知服务");
        return mBuilder.build();
    }
}