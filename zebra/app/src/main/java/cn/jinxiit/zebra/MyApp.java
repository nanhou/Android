package cn.jinxiit.zebra;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.vondear.rxtools.RxTool;

import net.posprinter.posprinterface.IMyBinder;

import java.util.ArrayList;
import java.util.List;

import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.ProductOwnerDataBean;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class MyApp extends Application
{
    //static 代码段可以防止内存泄露
    static
    {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorMain, android.R.color.white);//全局设置主题颜色
            return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    public String mToken = null;
    public UserBean mUser = null;
    public List<CategoryBean> mMarketCategoryList;
    public List<StoreOwnerBean> mStoreOwnerBeanList = null;
    public StoreOwnerBean mCurrentStoreOwnerBean = null;//
    //批量创建缓存
    public List<ProductOwnerDataBean> mBatchProductOwnerDataBeanList = new ArrayList<>();
//    public static int mFlag = -1;//控制崩溃

    //IMyBinder接口，所有可供调用的连接和发送数据的方法都封装在这个接口内
    public static IMyBinder mBlueBinder;
    public static boolean mIsBlueLink = false;

    @Override
    public void onCreate()
    {
        super.onCreate();
        RxTool.init(this);
        OkGo.getInstance()
                .init(this);
    }

    public void clearData()
    {
        mToken = null;
        mUser = null;
        mMarketCategoryList = null;
        mStoreOwnerBeanList = null;
        mCurrentStoreOwnerBean = null;
        ZebraUtils.getInstance().sharedPreferencesSaveString(this, MyConstant.STR_TOKEN, null);
    }
}
