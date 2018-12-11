package cn.jinxiit.zebra.activities.me;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.dialog.RxDialogAcfunVideoLoading;

import net.posprinter.posprinterface.UiExecute;
import net.posprinter.utils.DataForSendToPrinterPos58;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.adapters.PrinterListToSetAdapter;
import cn.jinxiit.zebra.beans.PrinterBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.StringUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

import static cn.jinxiit.zebra.MyApp.mBlueBinder;
import static cn.jinxiit.zebra.MyApp.mIsBlueLink;

public class SettingPrinterActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private MyApp myApp;

    private PrinterListToSetAdapter mPrinterListToSetAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mBluedeviceList = new ArrayList<>();//已绑定过的list
    private ArrayAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_printer);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initData();
        initRecyclerView();
    }

    private void initData() {
        myApp = (MyApp) getApplication();
        new MyToolBar(mContext, "打印设置", null);
    }

    private void initRecyclerView() {
        DividerItemDecoration divider = ZebraUtils.getInstance().getRecyclerViewGetDivider(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(divider);
        mPrinterListToSetAdapter = new PrinterListToSetAdapter();
        mRecyclerView.setAdapter(mPrinterListToSetAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initResume();
    }

    private void initResume() {
        OkGo.getInstance().cancelAll();
        ApiUtils.getInstance().okgoGetPrinterList(this, myApp.mToken, new ApiResultListener() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.has(MyConstant.STR_PRINTERS)) {
                        List<PrinterBean> printerBeanList = new Gson()
                                .fromJson(jsonObject.getString(MyConstant.STR_PRINTERS),
                                        new TypeToken<List<PrinterBean>>() {
                                        }.getType());
                        if (printerBeanList != null) {
                            mPrinterListToSetAdapter.clearDataList();
                            mPrinterListToSetAdapter.addDataList(printerBeanList);
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

    @OnClick({R.id.ll_add_printer, R.id.tv_help, R.id.tv_link_blue_printer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_add_printer:
                MyActivityUtils.skipActivity(this, AddPrinterActivity.class);
                break;
            case R.id.tv_help:

                break;
            case R.id.tv_link_blue_printer:
                setBluetooth();
                break;
        }
    }

    /*
    选择蓝牙设备
     */
    public void setBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断时候打开蓝牙设备
        if (!mBluetoothAdapter.isEnabled()) {
            //请求用户开启
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        } else {
            showblueboothlist();
        }
    }

    private void showblueboothlist() {
        if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View dialogView = inflater
                .inflate(R.layout.blue_printer_list, null);
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                mBluedeviceList);
        ListView lv1 = dialogView.findViewById(R.id.listView1);
        lv1.setAdapter(mArrayAdapter);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("BLE").setView(dialogView)
                .create();
        dialog.show();
        findAvalibleDevice();
        lv1.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                String msg = mBluedeviceList.get(position);
                String[] split = msg.split("\n");
                if (split.length == 2) {
                    String mac = split[1];
                    connetBle(mac);
                }
                dialog.cancel();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    /*
    找可连接的蓝牙设备
     */
    private void findAvalibleDevice() {
        // TODO Auto-generated method stub
        //获取可配对蓝牙设备
        Set<BluetoothDevice> device = mBluetoothAdapter.getBondedDevices();

        mBluedeviceList.clear();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mArrayAdapter.notifyDataSetChanged();
        }
        if (device.size() > 0) {
            //存在已经配对过的蓝牙设备
            for (BluetoothDevice btd : device) {
                mBluedeviceList.add(btd.getName() + '\n' + btd.getAddress());
                mArrayAdapter.notifyDataSetChanged();
            }
        } else {  //不存在已经配对过的蓝牙设备
            mBluedeviceList.add("不存在已经配对过的蓝牙设备");
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    /*
     *  蓝牙连接
     */
    private void connetBle(String bleAdrress) {
        if (TextUtils.isEmpty(bleAdrress)) {
            MyToastUtils.error("设备地址错误");
        } else if (mBlueBinder != null) {
            RxDialogAcfunVideoLoading rxDialogAcfunVideoLoading = new RxDialogAcfunVideoLoading(mContext);
            rxDialogAcfunVideoLoading.show();
            mBlueBinder.connectBtPort(bleAdrress, new UiExecute() {
                @Override
                public void onsucess() {
                    mIsBlueLink = true;
                    MyToastUtils.success("设备连接成功");
                    rxDialogAcfunVideoLoading.cancel();
                    //此处也可以开启读取打印机的数据
                    //参数同样是一个实现的UiExecute接口对象
                    //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                    //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                    //直到连接断开或异常才结束，并执行onfailed
                    mBlueBinder.writeDataByYouself(new UiExecute() {
                        @Override
                        public void onsucess() {

                        }

                        @Override
                        public void onfailed() {

                        }
                    }, () -> {

                        List<byte[]> list = new ArrayList<>();
                        //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中

                        //初始化打印机，清除缓存
                        list.add(DataForSendToPrinterPos58.initializePrinter());
                        list.add(StringUtils.strTobytes("连接成功"));
                        //追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(3));
                        return list;
                    });
                }

                @Override
                public void onfailed() {
                    //连接失败后在UI线程中的执行
                    MyToastUtils.error("设备连接失败");
                    rxDialogAcfunVideoLoading.cancel();
                }
            });
        }
    }
}
