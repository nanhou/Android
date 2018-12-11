package cn.jinxiit.zebra;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jinxiit.zebra.interfaces.MyConstant;

/**
 * 通知服务
 */
public class RefreshHttpService extends Service {

    //mqtt
    private final static String HOST = "tcp://www.bmxs.top:11883";
    private final static String[] mTopicFilters = new String[]{"/order"};
    private final IBinder myBinder = new MyBinder();
    private MqttClient client;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String obj = (String) msg.obj;
                // 数据处理
                Log.e("msg", obj);
                playVoices(obj);
                //                msgIntoView(obj);
            } else if (msg.what == 2) {
                Log.e("mqtt", "mqtt连接成功");
                try {
                    if (client != null) {
                        client.subscribe(mTopicFilters, new int[]{1});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == 3) {
                Log.e("mqtt", "连接失败，系统正在重连");
            }
        }
    };
    private MqttConnectOptions options;
    private Gson mGson = new Gson();
    //    private CountBean mCountBean = new CountBean();
    private MyApp myApp;
    private int newPlayCount = 0;
    private int abnormalPlayCount = 0;
    private int deliveryPlayCount = 0;
    private Runnable mPlayVoiceNewRunnable = new Runnable() {
        @Override
        public void run() {
            if (newPlayCount < 3) {
                newPlayCount++;
                playVoice("new_order.mp3");
                mHandler.postDelayed(mPlayVoiceNewRunnable, 3000);
            }
        }
    };
    private Runnable mPlayVoiceAbnormalRunnable = new Runnable() {
        @Override
        public void run() {
            if (abnormalPlayCount < 3) {
                abnormalPlayCount++;
                playVoice("abnormal_order.mp3");
                mHandler.postDelayed(mPlayVoiceAbnormalRunnable, 3000);
            }
        }
    };
    private Runnable mPlayVoiceDeliveryRunnable = new Runnable() {
        @Override
        public void run() {
            if (deliveryPlayCount < 3) {
                deliveryPlayCount++;
                playVoice("delivery_order.mp3");
                mHandler.postDelayed(mPlayVoiceDeliveryRunnable, 3000);
            }
        }
    };

    private void playVoices(String obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj);
            if (jsonObject.has(MyConstant.STR_STATUS)) {
                String status = jsonObject.getString(MyConstant.STR_STATUS);
                if (!TextUtils.isEmpty(status)) {
                    switch (status) {
                        case MyConstant.STR_NEW_ORDER:
                            newPlayCount = 0;
                            mHandler.post(mPlayVoiceNewRunnable);
                            break;
                        case MyConstant.STR_ABNORMAL:
                            abnormalPlayCount = 0;
                            mHandler.post(mPlayVoiceAbnormalRunnable);
                            break;
                        case "deliveryFail":
                        case MyConstant.STR_DELIVERY:
                        case "pick":
                        case "self_pick":
                        case "deliveryCancel":
                            deliveryPlayCount = 0;
                            mHandler.post(mPlayVoiceDeliveryRunnable);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    private Runnable mRunnable = new Runnable() {
    //        @Override
    //        public void run() {
    //            httpGetOrderNum();
    //            mHandler.postDelayed(mRunnable, 3000);
    //        }
    //    };

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplication();
        //        mHandler.post(mRunnable);

        initMqttClient();
        startReconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private void initMqttClient() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, myApp.mToken, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName("ecaa5e5e53c463674222");
            //设置连接的密码
            String passWord = myApp.mUser.getExtra().getMqttToken();
            if (passWord != null) {
                options.setPassword(passWord.toCharArray());
            }
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    Log.e("mqtt", "connectionLost----------");
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    //subscribe后得到的消息会执行到这里面
                    Log.e("mqtt", "messageArrived----------");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = topicName + "---" + message.toString();
                    mHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    Log.e("mqtt", "deliveryComplete---------" + token.isComplete());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startReconnect() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (!client.isConnected()) {
                connect();
            }
        }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void connect() {
        new Thread(() -> {
            try {
                client.connect(options);
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 3;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    //    private void httpGetOrderNum() {
    //        if (myApp.mCurrentStoreOwnerBean != null) {
    //            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
    //            if (!TextUtils.isEmpty(store_id)) {
    //                httpGetOrderCount();
    //            }
    //        }
    //    }

    //    private void httpGetOrderCount() {
    //        if (!NetWorkUtils.isNetworkConnected(this) || TextUtils.isEmpty(myApp.mToken)) {
    //            return;
    //        }
    //        if (myApp.mCurrentStoreOwnerBean == null) {
    //            return;
    //        }
    //        String storeId = myApp.mCurrentStoreOwnerBean.getStore_id();
    //        if (TextUtils.isEmpty(storeId)) {
    //            return;
    //        }
    //        String url = MyConstant.SERVER + "/api/third_party/order/store/" + storeId + "/count";
    //        OkGo.<String>get(url).headers(MyConstant.STR_REQUEST_TOKEN, myApp.mToken)
    //                .execute(new StringCallback() {
    //                    @Override
    //                    public void onSuccess(Response<String> response) {
    //                        Log.e("serviceOnSuccess", response.body());
    //                        CountBean countBean = mGson.fromJson(response.body(), CountBean.class);
    //                        notifyVoice(countBean);
    //                    }
    //                });
    //
    //    }

    //    private void notifyVoice(CountBean countBean) {
    //        long old_new_order_num = mCountBean.getNew_order();
    //        long new_order_num = countBean.getNew_order();
    //
    //        if (old_new_order_num < new_order_num) {
    //            newPlayCount = 0;
    //            mHandler.post(mPlayVoiceNewRunnable);
    //        }
    //        mCountBean = countBean;
    //    }

    //播放 asset 声音
    private void playVoice(String assetFileName) {
        try {
            AssetManager assetManager = Objects.requireNonNull(this).getAssets();
            AssetFileDescriptor afd = assetManager.openFd(assetFileName);
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(false);//循环播放
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyBinder extends Binder {
        public RefreshHttpService getService() {
            return RefreshHttpService.this;
        }
    }
}
