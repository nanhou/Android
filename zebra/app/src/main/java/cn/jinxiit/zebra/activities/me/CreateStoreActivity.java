package cn.jinxiit.zebra.activities.me;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.CityJsonBean;
import cn.jinxiit.zebra.beans.CropSquareTransformation;
import cn.jinxiit.zebra.beans.FileBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.AvatarUtils;
import cn.jinxiit.zebra.utils.GetJsonDataUtil;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class CreateStoreActivity extends BaseActivity implements View.OnClickListener
{
    @BindView(R.id.fl_parent)
    View mParent;
    @BindView(R.id.bg)
    View mBg;
    @BindView(R.id.img)
    PhotoView mPhotoView;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.pv_logo)
    PhotoView mPvLogo;
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_standby_phone)
    EditText mEtStandbyPhone;
    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.pv_store)
    PhotoView mPvStore;

    private PhotoView mCurrentClickView;
    private Info mInfo;

    private AlphaAnimation in = new AlphaAnimation(0, 1);
    private AlphaAnimation out = new AlphaAnimation(1, 0);
    private MyApp myApp;

    private Gson mGson = new Gson();

    //城市数据解析
    private ArrayList<CityJsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private OptionsPickerView mPvOptions;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;

    private OptionsPickerView<String> mStartTimePickerView;
    private OptionsPickerView<String> mEndTimePickerView;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_LOAD_DATA:
                    if (thread == null)
                    {//如果已创建就不再重新创建子线程了

                        thread = new Thread(() -> {
                            // 子线程中解析省市区数据
                            initJsonData();
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    MyToastUtils.error("城市数据解析失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);
        ButterKnife.bind(this);
        new MyToolBar(mContext, "创建门店", null);
        myApp = (MyApp) getApplication();
        initView();
    }

    @OnClick({R.id.btn_replace, R.id.pv_logo, R.id.pv_store, R.id.ll_select_city, R.id.tv_start_time, R.id.tv_end_time, R.id.btn_sign_up})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_replace:
                clickReplace();
                break;
            case R.id.pv_logo:
            case R.id.pv_store:
                clickPhotoView(view);
                break;
            case R.id.ll_select_city:
                if (isLoaded)
                {
                    showCityPickerView(view);
                } else
                {
                    MyToastUtils.error("获取城市数据失败");
                }
                break;
            case R.id.tv_start_time:
                showTimePickerView(view, true);
                break;
            case R.id.tv_end_time:
                showTimePickerView(view, false);
                break;
            case R.id.btn_sign_up:
                clickConfirm();
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        AvatarUtils.imgOnClick(v, this);
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
        if (resultCode == RESULT_OK)
        {
            final String imgUrl = AvatarUtils.onActivityResult(requestCode, data, this, true, true);
            if (imgUrl != null)
            {
                //                mCurrentClickView.setTag(imgUrl);
                //                mCurrentClickView.setImageResource(R.drawable.default_img);
                httpPostFile(imgUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        if (mParent.getVisibility() == View.VISIBLE)
        {
            mBg.startAnimation(out);
            mPhotoView.animaTo(mInfo, new Runnable()
            {
                @Override
                public void run()
                {
                    mParent.setVisibility(View.GONE);
                }
            });
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mHandler != null)
        {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void clickReplace()
    {
        mBg.setVisibility(View.GONE);
        mPhotoView.animaTo(mInfo, new Runnable()
        {
            @Override
            public void run()
            {
                mParent.setVisibility(View.GONE);
            }
        });
        mCurrentClickView.setImageResource(R.drawable.img_default);
        mCurrentClickView.setTag(null);
        clickPhotoView(mCurrentClickView);
    }

    private void clickConfirm()
    {
        String name = mEtName.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(name))
        {
            MyToastUtils.error("请输入门店名称");
            return;
        }
        Bundle bundle0 = (Bundle) mPvLogo.getTag();
        if (bundle0 == null)
        {
            MyToastUtils.error("请上传LOGO");
            return;
        }
        String logo = (String) bundle0.get(MyConstant.STR_FILEKEY);
        if (TextUtils.isEmpty(logo))
        {
            MyToastUtils.error("请上传LOGO");
            return;
        }

        String telephone = mEtPhone.getText()
                .toString()
                .trim();
        if (!NumberUtils.isPhoneNumberValid(telephone))
        {
            MyToastUtils.error("请输入正确的门店电话");
            return;
        }

        String standby_phone = mEtStandbyPhone.getText()
                .toString()
                .trim();
        if (!TextUtils.isEmpty(standby_phone) && !NumberUtils.isPhoneNumberValid(standby_phone))
        {
            MyToastUtils.error("请输入正确的备用电话");
            return;
        }

        String start_time = mTvStartTime.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(start_time))
        {
            MyToastUtils.error("请选择门店营业的起始时间");
            return;
        }

        String end_time = mTvEndTime.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(end_time))
        {
            MyToastUtils.error("请选择门店营业的结束时间");
            return;
        }

        String city = mTvCity.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(city))
        {
            MyToastUtils.error("请选门店所在城市");
            return;
        }

        String address = mEtAddress.getText()
                .toString()
                .trim();

        Bundle bundle1 = (Bundle) mPvStore.getTag();
        if (bundle1 == null)
        {
            MyToastUtils.error("请上传门店照片");
            return;
        }
        String image = (String) bundle0.get(MyConstant.STR_FILEKEY);
        if (TextUtils.isEmpty(image))
        {
            MyToastUtils.error("请上传门店照片");
            return;
        }

        JSONObject extraJSON = new JSONObject();
        try
        {
            extraJSON.put(MyConstant.STR_LOGO, logo);
            extraJSON.put(MyConstant.STR_TELEPHONE, telephone);
            if (!TextUtils.isEmpty(standby_phone))
            {
                extraJSON.put(MyConstant.STR_STANDBY_PHONE, standby_phone);
            }
            extraJSON.put(MyConstant.STR_START_TIME, start_time);
            extraJSON.put(MyConstant.STR_END_TIME, end_time);
            extraJSON.put(MyConstant.STR_CITY, city);
            if (!TextUtils.isEmpty(address))
            {
                extraJSON.put(MyConstant.STR_ADDRESS, address);
            }
            extraJSON.put(MyConstant.STR_IMAGE, image);
            extraJSON.put(MyConstant.STR_BUSINESS_STATUS, false);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        ApiUtils.getInstance()
                .okgoPostCreateStore(mContext, myApp.mToken, name, extraJSON.toString(), new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                    }
                });
    }

    private void showTimePickerView(View view, final boolean isStartTime)
    {
        WindowUtils.hideInput(mContext, view);

        if ((isStartTime && mStartTimePickerView == null) || (!isStartTime && mEndTimePickerView == null))
        {
            String title;
            final List<String> list0 = new ArrayList<>();
            List<String> list1 = new ArrayList<>();
            final List<String> list2 = new ArrayList<>();
            for (int i = 0; i < 60; i++)
            {
                if (i < 10)
                {
                    list0.add("0" + i);
                    list2.add("0" + i);
                } else
                {
                    if (i < 24)
                    {
                        list0.add(i + "");
                    }
                    list2.add(i + "");
                }
            }
            list1.add(":");

            if (isStartTime)
            {
                title = "起始时间";
            } else
            {
                title = "结束时间";
            }

            OptionsPickerView<String> optionsPickerView = new OptionsPickerBuilder(mContext, (options1, options2, options3, v) -> {
                StringBuffer buffer = new StringBuffer(1024);
                String one = list0.get(options1);
                if (one.length() < 2)
                {
                    buffer.append(0);
                }
                buffer.append(one);
                buffer.append(":");
                String two = list2.get(options3);
                if (two.length() < 2)
                {
                    buffer.append(0);
                }
                buffer.append(two);
                if (isStartTime)
                {
                    mTvStartTime.setText(buffer.toString());
                } else
                {
                    mTvEndTime.setText(buffer.toString());
                }
                buffer.reverse();
            }).setTitleText(title)
                    .build();
            optionsPickerView.setNPicker(list0, list1, list2);
            if (isStartTime)
            {
                mStartTimePickerView = optionsPickerView;
            } else
            {
                mEndTimePickerView = optionsPickerView;
            }

        }

        if (isStartTime)
        {
            mStartTimePickerView.show();
        } else
        {
            mEndTimePickerView.show();
        }
    }

    private void showCityPickerView(View view)
    {// 弹出选择器

        WindowUtils.hideInput(mContext, view);
        if (mPvOptions == null)
        {
            mPvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener()
            {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v)
                {
                    //返回的分别是三个级别的选中位置
                    String tx = options1Items.get(options1)
                            .getPickerViewText() + options2Items.get(options1)
                            .get(options2) + options3Items.get(options1)
                            .get(options2)
                            .get(options3);
                    mTvCity.setText(tx);
                }
            })

                    .setTitleText("城市选择")
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(20)
                    .build();

        /*mPvOptions.setPicker(options1Items);//一级选择器
        mPvOptions.setPicker(options1Items, options2Items);//二级选择器*/
            mPvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        }

        mPvOptions.show();
    }

    private void initJsonData()
    {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<CityJsonBean> cityJsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = cityJsonBean;

        for (int i = 0; i < cityJsonBean.size(); i++)
        {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < cityJsonBean.get(i)
                    .getCityList()
                    .size(); c++)
            {//遍历该省份的所有城市
                String CityName = cityJsonBean.get(i)
                        .getCityList()
                        .get(c)
                        .getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (cityJsonBean.get(i)
                        .getCityList()
                        .get(c)
                        .getArea() == null || cityJsonBean.get(i)
                        .getCityList()
                        .get(c)
                        .getArea()
                        .size() == 0)
                {
                    City_AreaList.add("");
                } else
                {
                    City_AreaList.addAll(cityJsonBean.get(i)
                            .getCityList()
                            .get(c)
                            .getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    public ArrayList<CityJsonBean> parseData(String result)
    {//Gson 解析
        ArrayList<CityJsonBean> detail = new ArrayList<>();
        try
        {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++)
            {
                CityJsonBean entity = gson.fromJson(data.optJSONObject(i)
                        .toString(), CityJsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private void initView()
    {
        jsonGetCityData();
        initListener();
    }

    private void jsonGetCityData()
    {
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
    }

    private void initListener()
    {
        mPhotoView.enable();
        mPhotoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                mBg.startAnimation(out);
                mBg.setVisibility(View.GONE);
                mPhotoView.animaTo(mInfo, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mParent.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void httpPostFile(final String imgUrl)
    {
        ApiUtils.getInstance()
                .okgoPostFile(this, myApp.mToken, imgUrl, new ApiResultListener()
                {
                    @Override
                    public void onSuccess(Response<String> response)
                    {
                        FileBean fileBean = mGson.fromJson(response.body(), FileBean.class);
                        if (fileBean != null)
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString(MyConstant.STR_PATH, imgUrl);
                            bundle.putString(MyConstant.STR_FILEKEY, fileBean.getFile_key());
                            mCurrentClickView.setTag(bundle);
                        }
                    }

                    @Override
                    public void onError(Response<String> response)
                    {
                        mCurrentClickView.setImageResource(R.drawable.img_default);
                    }
                });
    }

    private void clickPhotoView(View view)
    {
        Object tag = view.getTag();
        mCurrentClickView = (PhotoView) view;
        if (tag == null)
        {
            AvatarUtils.creatmenuWindowDialog(this, (PhotoView) view);
        } else
        {
            Bundle bundle = (Bundle) tag;
            PhotoView p = (PhotoView) view;
            mInfo = p.getInfo();
            int[] screenSize = WindowUtils.getScreenSize(this);
            Picasso.get()
                    .load(new File(bundle.getString(MyConstant.STR_PATH)))
                    .transform(new CropSquareTransformation(screenSize[1], screenSize[0]))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .noFade()
                    .into(mPhotoView);
            mBg.startAnimation(in);
            mBg.setVisibility(View.VISIBLE);
            mParent.setVisibility(View.VISIBLE);
            mPhotoView.animaFrom(mInfo);
        }
    }
}
