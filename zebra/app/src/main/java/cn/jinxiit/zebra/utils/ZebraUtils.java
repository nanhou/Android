package cn.jinxiit.zebra.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.beans.ProductStatusComparedBean;
import cn.jinxiit.zebra.beans.StatisticBean;
import cn.jinxiit.zebra.components.ChartComponent;
import cn.jinxiit.zebra.interfaces.MyConstant;

public class ZebraUtils {
    private static ZebraUtils zebraUtils = null;

    private ZebraUtils() {}

    //静态工厂方法
    public static ZebraUtils getInstance() {
        if (zebraUtils == null) {
            zebraUtils = new ZebraUtils();
        }
        return zebraUtils;
    }

    public void sharedPreferencesSaveString(Context context, String key, String value) {
        SharedPreferences sp = context
                .getSharedPreferences(MyConstant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public String sharedPreferencesGetString(Context context, String key) {
        SharedPreferences sp = context
                .getSharedPreferences(MyConstant.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    //获取商品平台的最大最小值
    public long[] goodPlatformPriceMinMax(GoodsBean goodsBean) {
        long[] result = {-1, -1};
        if (goodsBean != null) {
            GoodsBean.ProductBean product = goodsBean.getProduct();
            if (product != null) {
                List<GoodsBean.ProductBean.PriceAndStockBean> priceAndStockBeanList = product
                        .getPriceAndStock();
                if (priceAndStockBeanList != null) {
                    for (int i = 0; i < priceAndStockBeanList.size(); i++) {
                        GoodsBean.ProductBean.PriceAndStockBean priceAndStockBean = priceAndStockBeanList
                                .get(i);
                        if (priceAndStockBean != null) {
                            if (priceAndStockBean.getBinded() == 1) {
                                long price = priceAndStockBean.getPrice();
                                if (result[0] == -1) {
                                    result[0] = result[1] = price;
                                }

                                if (result[0] > price) {
                                    result[0] = price;
                                }

                                if (result[1] < price) {
                                    result[1] = price;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    //计算升降持平 比例
    @SuppressLint("DefaultLocale")
    public void jsjComparedProportion(float onedata, float beforeData, TextView textView) {
        Context context = textView.getContext();
        float compared;
        int drawableLeftId = R.drawable.data_up;
        if (beforeData == 0) {
            if (onedata == 0) {
                compared = 0;
            } else {
                compared = 1f;
                drawableLeftId = R.drawable.data_up;
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            }
        } else {
            if (onedata > beforeData) {
                compared = ((onedata - beforeData) * 0.01f) / (beforeData * 0.01f);
                drawableLeftId = R.drawable.data_up;
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                compared = ((beforeData - onedata) * 0.01f) / (beforeData * 0.01f);
                drawableLeftId = R.drawable.data_down;
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            }
        }

        if (compared == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            textView.setText("持平");
        } else {
            Drawable left = context.getResources().getDrawable(drawableLeftId);
            textView.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            textView.setText(String.format("%.2f%s", compared * 100f, "%"));
        }
    }

    //计算升降持平 差值
    @SuppressLint("DefaultLocale")
    public void jsjComparedDifference(float onedata, float beforeData, TextView textView) {
        Context context = textView.getContext();
        float compared;
        int drawableLeftId;
        if (onedata > beforeData) {
            compared = onedata - beforeData;
            drawableLeftId = R.drawable.data_up;
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        } else {
            compared = beforeData - onedata;
            drawableLeftId = R.drawable.data_down;
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        }

        if (compared == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            textView.setText("持平");
        } else {
            Drawable left = context.getResources().getDrawable(drawableLeftId);
            textView.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            textView.setText(String.format("%.2f%s", compared * 100f, "%"));
        }
    }

    //statistic 图表数据
    public void statisticdataSetChart(ChartComponent mChartComponent, String mPlatform, String[] mLineCharTitles0, String[] mLineCharTitlesUnit0, StatisticBean statisticBean) {
        if (statisticBean == null) {
            return;
        }
        List<String> dayArr = statisticBean.getDayArr();
        if (dayArr == null || dayArr.size() < 1) {
            return;
        }
        List<StatisticBean.StatisticBean0> statisticBeanList = statisticBean.getStatistic();
        if (statisticBeanList == null) {
            return;
        }

        List<StatisticBean.StatisticBean0> elemeStatisticBeanList = null;
        List<StatisticBean.StatisticBean0> meituanStatisticBeanList = null;
        if (mPlatform.equals(MyConstant.STR_ALLSTATIS)) {
            elemeStatisticBeanList = statisticBean.getElemeStatistic();
            meituanStatisticBeanList = statisticBean.getMeituanStatistic();
        }
        if (mChartComponent != null) {
            Map<String, Map<String, List<Float>>> dataMap = new HashMap<>();
            Map<String, String> itemMap = new HashMap<>();
            Map<String, String> unitMap = new HashMap<>();
            String startDate = dayArr.get(dayArr.size() - 1);
            for (int i = 0; i < mLineCharTitles0.length; i++) {
                unitMap.put(mLineCharTitles0[i], mLineCharTitlesUnit0[i]);
                itemMap.put(mLineCharTitles0[i], mLineCharTitles0[i]);
                Map<String, List<Float>> map = new HashMap<>();
                List<Float> list = new ArrayList<>();
                List<Float> elemeList = new ArrayList<>();
                List<Float> meituanList = new ArrayList<>();
                List<Float> allList = new ArrayList<>();

                for (int j = 0; j < dayArr.size(); j++) {
                    String currentDay = dayArr.get(j);
                    if (TextUtils.isEmpty(currentDay)) {
                        continue;
                    }

                    StatisticBean.StatisticBean0 statisticBean0 = null;
                    for (int q = 0; q < statisticBeanList.size(); q++) {
                        StatisticBean.StatisticBean0 tempStatisticBean0 = statisticBeanList.get(q);
                        if (tempStatisticBean0 != null) {
                            if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                statisticBean0 = tempStatisticBean0;
                                break;
                            }
                        }
                    }

                    StatisticBean.StatisticBean0 elemeStatisticBean0 = null;
                    if (elemeStatisticBeanList != null) {
                        for (int q = 0; q < elemeStatisticBeanList.size(); q++) {
                            StatisticBean.StatisticBean0 tempStatisticBean0 = elemeStatisticBeanList
                                    .get(q);
                            if (tempStatisticBean0 != null) {
                                if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                    elemeStatisticBean0 = tempStatisticBean0;
                                    break;
                                }
                            }
                        }
                    }

                    StatisticBean.StatisticBean0 meituanStatisticBean0 = null;
                    if (meituanStatisticBeanList != null) {
                        for (int q = 0; q < meituanStatisticBeanList.size(); q++) {
                            StatisticBean.StatisticBean0 tempStatisticBean0 = meituanStatisticBeanList
                                    .get(q);
                            if (tempStatisticBean0 != null) {
                                if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                    meituanStatisticBean0 = tempStatisticBean0;
                                    break;
                                }
                            }
                        }
                    }
                    //
                    float totalIncoming = 0;
                    float totalCost = 0;
                    float totalOrder = 0;
                    float failOrder = 0;
                    if (statisticBean0 != null) {
                        totalIncoming = statisticBean0.getTotalIncoming() * 0.01f;
                        totalCost = statisticBean0.getTotalCost() * 0.01f;
                        totalOrder = statisticBean0.getTotalOrder();
                        failOrder = statisticBean0.getFailOrder();
                    }
                    float finishOrder = totalOrder - failOrder;
                    //饿了么
                    float elemeTotalIncoming = 0;
                    float elemeTotalCost = 0;
                    float elemeTotalOrder = 0;
                    float elemeFailOrder = 0;
                    if (elemeStatisticBean0 != null) {
                        elemeTotalIncoming = elemeStatisticBean0.getTotalIncoming() * 0.01f;
                        elemeTotalCost = elemeStatisticBean0.getTotalCost() * 0.01f;
                        elemeTotalOrder = elemeStatisticBean0.getTotalOrder();
                        elemeFailOrder = elemeStatisticBean0.getFailOrder();
                    }
                    float elemeFinishOrder = elemeTotalOrder - elemeFailOrder;

                    //美团外卖
                    float meituanTotalIncoming = 0;
                    float meituanTotalCost = 0;
                    float meituanTotalOrder = 0;
                    float meituanFailOrder = 0;
                    if (elemeStatisticBean0 != null) {
                        meituanTotalIncoming = meituanStatisticBean0.getTotalIncoming() * 0.01f;
                        meituanTotalCost = meituanStatisticBean0.getTotalCost() * 0.01f;
                        meituanTotalOrder = meituanStatisticBean0.getTotalOrder();
                        meituanFailOrder = meituanStatisticBean0.getFailOrder();
                    }
                    float meituanFinishOrder = meituanTotalOrder - meituanFailOrder;

                    switch (mLineCharTitles0[i]) {
                        case "营业额":
                            list.add(totalIncoming);
                            elemeList.add(elemeTotalIncoming);
                            meituanList.add(meituanTotalIncoming);
                            allList.add(totalIncoming + elemeTotalIncoming + meituanTotalIncoming);
                            break;
                        case "预计收入":
                            float yComing = totalIncoming - totalCost;
                            list.add(yComing);
                            float yElemeComing = elemeTotalIncoming - elemeTotalCost;
                            elemeList.add(yElemeComing);
                            float yMeituanComing = meituanTotalIncoming - meituanTotalCost;
                            meituanList.add(yMeituanComing);
                            allList.add(yComing + yElemeComing + yMeituanComing);
                            break;
                        case "有效订单":
                            list.add(finishOrder);
                            elemeList.add(elemeFinishOrder);
                            meituanList.add(meituanFinishOrder);
                            allList.add(finishOrder + elemeFinishOrder + meituanFinishOrder);
                            break;
                        case "无效订单":
                            list.add(failOrder);
                            elemeList.add(elemeFailOrder);
                            meituanList.add(meituanFailOrder);
                            allList.add(failOrder + elemeFailOrder);
                            break;
                        case "客单价":
                            list.add(jsjCompared(finishOrder, totalIncoming));
                            elemeList.add(jsjCompared(elemeFinishOrder, elemeTotalIncoming));
                            meituanList.add(jsjCompared(meituanFinishOrder, meituanTotalIncoming));
                            allList.add(
                                    jsjCompared(finishOrder + elemeFinishOrder + meituanFinishOrder,
                                            totalIncoming + elemeTotalIncoming + meituanTotalIncoming));
                            break;
                        case "预计毛利":
                            list.add(0f);
                            elemeList.add(0f);
                            meituanList.add(0f);
                            allList.add(0f);
                            break;
                    }
                }
                dataChartPutMap(mPlatform, mLineCharTitles0[i], dataMap, map, list, elemeList,
                        meituanList, allList);
            }
            mChartComponent.setData(dataMap, startDate, itemMap, unitMap);
        }
    }

    //顾客图表数据
    public void customerdataSetChart(ChartComponent mChartComponent1, String mPlatform, String[] mLineCharTitles1, String[] mLineCharTitlesUnit1, StatisticBean statisticBean) {
        if (statisticBean == null) {
            return;
        }
        List<String> dayArr = statisticBean.getDayArr();
        if (dayArr == null || dayArr.size() < 1) {
            return;
        }

        List<StatisticBean.UserStatBean> userStatList = statisticBean.getUserStats();
        if (userStatList == null) {
            return;
        }
        List<StatisticBean.UserStatBean> elemeUserStatList = null;
        List<StatisticBean.UserStatBean> meituanUserStatList = null;
        if (MyConstant.STR_ALLSTATIS.equals(mPlatform)) {
            elemeUserStatList = statisticBean.getElemeUserStats();
            meituanUserStatList = statisticBean.getMeituanUserStats();
        }

        if (mChartComponent1 != null) {
            Map<String, Map<String, List<Float>>> dataMap = new HashMap<>();
            Map<String, String> itemMap = new HashMap<>();
            Map<String, String> unitMap = new HashMap<>();
            String startDate = dayArr.get(dayArr.size() - 1);
            for (int i = 0; i < mLineCharTitles1.length; i++) {
                unitMap.put(mLineCharTitles1[i], mLineCharTitlesUnit1[i]);
                itemMap.put(mLineCharTitles1[i], mLineCharTitles1[i]);
                Map<String, List<Float>> map = new HashMap<>();
                List<Float> list = new ArrayList<>();
                List<Float> elemeList = new ArrayList<>();
                List<Float> meituanList = new ArrayList<>();
                List<Float> allList = new ArrayList<>();
                for (int j = 0; j < dayArr.size(); j++) {
                    String currentDay = dayArr.get(j);

                    if (TextUtils.isEmpty(currentDay)) {
                        continue;
                    }

                    StatisticBean.UserStatBean userStatBean = null;
                    for (int q = 0; q < userStatList.size(); q++) {
                        StatisticBean.UserStatBean tempUserStatBean = userStatList.get(q);
                        if (tempUserStatBean != null) {
                            if (currentDay.equals(tempUserStatBean.getDayTime())) {
                                userStatBean = tempUserStatBean;
                                break;
                            }
                        }
                    }

                    StatisticBean.UserStatBean elemeUserStatBean = null;
                    if (elemeUserStatList != null) {
                        for (int q = 0; q < elemeUserStatList.size(); q++) {
                            StatisticBean.UserStatBean tempElemeUserStatBean = elemeUserStatList
                                    .get(q);
                            if (tempElemeUserStatBean != null) {
                                if (currentDay.equals(tempElemeUserStatBean.getDayTime())) {
                                    elemeUserStatBean = tempElemeUserStatBean;
                                }
                            }
                        }
                    }

                    StatisticBean.UserStatBean meituanUserStatBean = null;
                    if (meituanUserStatList != null) {
                        for (int q = 0; q < meituanUserStatList.size(); q++) {
                            StatisticBean.UserStatBean tempMeituanUserStatBean = meituanUserStatList
                                    .get(q);
                            if (tempMeituanUserStatBean != null) {
                                if (currentDay.equals(tempMeituanUserStatBean.getDayTime())) {
                                    meituanUserStatBean = tempMeituanUserStatBean;
                                }
                            }
                        }
                    }

                    float newX = 0;
                    float old = 0;
                    if (userStatBean != null) {
                        newX = userStatBean.getNewX();
                        old = userStatBean.getOld();
                    }

                    float elemeNewX = 0;
                    float elemeOld = 0;
                    if (elemeUserStatBean != null) {
                        elemeNewX = elemeUserStatBean.getNewX();
                        elemeOld = elemeUserStatBean.getOld();
                    }

                    float meituanNewX = 0;
                    float meituanOld = 0;
                    if (meituanUserStatBean != null) {
                        meituanNewX = meituanUserStatBean.getNewX();
                        meituanOld = meituanUserStatBean.getOld();
                    }

                    switch (mLineCharTitles1[i]) {
                        case "全部顾客":
                            float allusers = newX + old;
                            float elemeAllusers = elemeNewX + elemeOld;
                            float meituanAllusers = meituanNewX + meituanOld;
                            list.add(allusers);
                            elemeList.add(elemeAllusers);
                            meituanList.add(meituanAllusers);
                            allList.add(allusers + elemeAllusers + meituanAllusers);
                            break;
                        case "新顾客":
                            list.add(newX);
                            elemeList.add(elemeNewX);
                            meituanList.add(meituanNewX);
                            allList.add(newX + elemeNewX + meituanNewX);
                            break;
                        case "老顾客":
                            list.add(old);
                            elemeList.add(elemeOld);
                            meituanList.add(meituanOld);
                            allList.add(old + elemeOld + meituanOld);
                            break;
                    }
                }
                dataChartPutMap(mPlatform, mLineCharTitles1[i], dataMap, map, list, elemeList,
                        meituanList, allList);
            }
            mChartComponent1.setData(dataMap, startDate, itemMap, unitMap);
        }
    }

    //商品数据 - 曲线图展示数据
    public void productdataSetChart(ChartComponent mChartComponent1, String mPlatform, String[] mLineCharTitles1, String[] mLineCharTitlesUnit1, ProductStatusComparedBean productStatusComparedBean) {

        if (productStatusComparedBean == null) {
            return;
        }

        List<String> dayArr = productStatusComparedBean.getDayArr();
        if (dayArr == null || dayArr.size() < 1) {
            return;
        }
        String startDate = dayArr.get(dayArr.size() - 1);
        List<ProductStatusComparedBean.ProductStatusBean> statusBeanList = productStatusComparedBean
                .getStats();
        if (statusBeanList == null) {
            return;
        }
        List<ProductStatusComparedBean.ProductStatusBean> elemeStatusBeanList = null;
        List<ProductStatusComparedBean.ProductStatusBean> meituanStatusBeanList = null;
        if (MyConstant.STR_ALLSTATIS.equals(mPlatform)) {
            elemeStatusBeanList = productStatusComparedBean.getElemeStats();
            meituanStatusBeanList = productStatusComparedBean.getMeituanStats();
        }

        if (mChartComponent1 != null) {
            Map<String, Map<String, List<Float>>> dataMap = new HashMap<>();
            Map<String, String> itemMap = new HashMap<>();
            Map<String, String> unitMap = new HashMap<>();
            for (int i = 0; i < mLineCharTitles1.length; i++) {
                unitMap.put(mLineCharTitles1[i], mLineCharTitlesUnit1[i]);
                itemMap.put(mLineCharTitles1[i], mLineCharTitles1[i]);
                Map<String, List<Float>> map = new HashMap<>();
                List<Float> list = new ArrayList<>();
                List<Float> elemeList = new ArrayList<>();
                List<Float> meituanList = new ArrayList<>();
                List<Float> allList = new ArrayList<>();
                for (int j = 0; j < dayArr.size(); j++) {
                    ProductStatusComparedBean.ProductStatusBean productStatusBean = null;
                    String date = dayArr.get(j);
                    for (int q = 0; q < statusBeanList.size(); q++) {
                        ProductStatusComparedBean.ProductStatusBean tempProductStatusBean = statusBeanList
                                .get(q);
                        if (tempProductStatusBean != null) {
                            String dayTime = tempProductStatusBean.getDayTime();
                            if (date.equals(dayTime)) {
                                productStatusBean = tempProductStatusBean;
                                break;
                            }
                        }
                    }

                    ProductStatusComparedBean.ProductStatusBean elemeProductStatusBean = null;
                    if (elemeStatusBeanList != null) {
                        for (int q = 0; q < elemeStatusBeanList.size(); q++) {
                            ProductStatusComparedBean.ProductStatusBean tempProductStatusBean = elemeStatusBeanList
                                    .get(q);
                            if (tempProductStatusBean != null) {
                                String dayTime = tempProductStatusBean.getDayTime();
                                if (date.equals(dayTime)) {
                                    elemeProductStatusBean = tempProductStatusBean;
                                    break;
                                }
                            }
                        }
                    }

                    ProductStatusComparedBean.ProductStatusBean meituanProductStatusBean = null;
                    if (meituanStatusBeanList != null) {
                        for (int q = 0; q < meituanStatusBeanList.size(); q++) {
                            ProductStatusComparedBean.ProductStatusBean tempProductStatusBean = meituanStatusBeanList
                                    .get(q);
                            if (tempProductStatusBean != null) {
                                String dayTime = tempProductStatusBean.getDayTime();
                                if (date.equals(dayTime)) {
                                    meituanProductStatusBean = tempProductStatusBean;
                                    break;
                                }
                            }
                        }
                    }

                    float price = 0;
                    float count = 0;
                    if (productStatusBean != null) {
                        price = productStatusBean.getPrice() * 0.01f;
                        count = productStatusBean.getCount();
                    }

                    float elemePrice = 0;
                    float elemeCount = 0;
                    if (elemeProductStatusBean != null) {
                        elemePrice = elemeProductStatusBean.getPrice() * 0.01f;
                        elemeCount = elemeProductStatusBean.getCount();
                    }

                    float meituanPrice = 0;
                    float meituanCount = 0;
                    if (meituanProductStatusBean != null) {
                        meituanPrice = meituanProductStatusBean.getPrice() * 0.01f;
                        meituanCount = meituanProductStatusBean.getCount();
                    }

                    switch (mLineCharTitles1[i]) {
                        case "销售数量":
                            list.add(count);
                            elemeList.add(elemeCount);
                            meituanList.add(meituanCount);
                            allList.add(count + elemeCount + meituanCount);
                            break;
                        case "销售额":
                            list.add(price);
                            elemeList.add(elemePrice);
                            meituanList.add(meituanPrice);
                            allList.add(price + elemePrice + meituanPrice);
                            break;
                    }
                }
                dataChartPutMap(mPlatform, mLineCharTitles1[i], dataMap, map, list, elemeList,
                        meituanList, allList);
            }
            mChartComponent1.setData(dataMap, startDate, itemMap, unitMap);
        }
    }

    public void trafficdataSetChart(ChartComponent mChartComponent, String mPlatform, String[] mLineCharTitles0, String[] mLineCharTitlesUnit0, StatisticBean statisticBean) {
        if (statisticBean == null) {
            return;
        }
        List<String> dayArr = statisticBean.getDayArr();
        if (dayArr == null || dayArr.size() < 1) {
            return;
        }
        List<StatisticBean.StatisticBean0> statisticBeanList = statisticBean.getStatistic();
        List<StatisticBean.VisiterResultBean> visiterResultList = statisticBean.getVisiterResult();
        if (statisticBeanList == null || visiterResultList == null) {
            return;
        }

        List<StatisticBean.StatisticBean0> elemeStatisticBeanList = null;
        List<StatisticBean.VisiterResultBean> elemeVisiterResultList = null;
        List<StatisticBean.StatisticBean0> meituanStatisticBeanList = null;
        List<StatisticBean.VisiterResultBean> meituanVisiterResultList = null;
        if (mPlatform.equals(MyConstant.STR_ALLSTATIS)) {
            elemeStatisticBeanList = statisticBean.getElemeStatistic();
            elemeVisiterResultList = statisticBean.getElemeVisiterResult();
            meituanStatisticBeanList = statisticBean.getMeituanStatistic();
            meituanVisiterResultList = statisticBean.getMeituanVisiterResult();
        }

        if (mChartComponent != null) {
            Map<String, Map<String, List<Float>>> dataMap = new HashMap<>();
            Map<String, String> itemMap = new HashMap<>();
            Map<String, String> unitMap = new HashMap<>();
            String startDate = dayArr.get(dayArr.size() - 1);
            for (int i = 0; i < mLineCharTitles0.length; i++) {
                unitMap.put(mLineCharTitles0[i], mLineCharTitlesUnit0[i]);
                itemMap.put(mLineCharTitles0[i], mLineCharTitles0[i]);
                Map<String, List<Float>> map = new HashMap<>();
                List<Float> list = new ArrayList<>();
                List<Float> elemeList = new ArrayList<>();
                List<Float> meituanList = new ArrayList<>();
                List<Float> allList = new ArrayList<>();

                for (int j = 0; j < dayArr.size(); j++) {
                    StatisticBean.StatisticBean0 statisticBean0 = null;
                    StatisticBean.VisiterResultBean visiterResultBean = null;
                    String currentDay = dayArr.get(j);
                    if (TextUtils.isEmpty(currentDay)) {
                        continue;
                    }
                    for (int q = 0; q < statisticBeanList.size(); q++) {
                        StatisticBean.StatisticBean0 tempStatisticBean0 = statisticBeanList.get(q);
                        if (tempStatisticBean0 != null) {
                            if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                statisticBean0 = tempStatisticBean0;
                                break;
                            }
                        }
                    }

                    for (int q = 0; q < visiterResultList.size(); q++) {
                        StatisticBean.VisiterResultBean tempVisiterResultBean = visiterResultList
                                .get(q);
                        if (tempVisiterResultBean != null) {
                            if (currentDay.equals(tempVisiterResultBean.getDayTime())) {
                                visiterResultBean = tempVisiterResultBean;
                                break;
                            }
                        }
                    }

                    StatisticBean.StatisticBean0 elemeStatisticBean0 = null;
                    StatisticBean.VisiterResultBean elemeVisiterResultBean = null;

                    if (elemeStatisticBeanList != null && elemeVisiterResultList != null) {
                        for (int q = 0; q < elemeStatisticBeanList.size(); q++) {
                            StatisticBean.StatisticBean0 tempStatisticBean0 = elemeStatisticBeanList
                                    .get(q);
                            if (tempStatisticBean0 != null) {
                                if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                    elemeStatisticBean0 = tempStatisticBean0;
                                    break;
                                }
                            }
                        }

                        for (int q = 0; q < elemeVisiterResultList.size(); q++) {
                            StatisticBean.VisiterResultBean tempVisiterResultBean = elemeVisiterResultList
                                    .get(q);
                            if (tempVisiterResultBean != null) {
                                if (currentDay.equals(tempVisiterResultBean.getDayTime())) {
                                    elemeVisiterResultBean = tempVisiterResultBean;
                                    break;
                                }
                            }
                        }
                    }

                    StatisticBean.StatisticBean0 meituanStatisticBean0 = null;
                    StatisticBean.VisiterResultBean meituanVisiterResultBean = null;

                    if (meituanStatisticBeanList != null && meituanVisiterResultList != null) {
                        for (int q = 0; q < meituanStatisticBeanList.size(); q++) {
                            StatisticBean.StatisticBean0 tempStatisticBean0 = meituanStatisticBeanList
                                    .get(q);
                            if (tempStatisticBean0 != null) {
                                if (currentDay.equals(tempStatisticBean0.getDayTime())) {
                                    meituanStatisticBean0 = tempStatisticBean0;
                                    break;
                                }
                            }
                        }

                        for (int q = 0; q < meituanVisiterResultList.size(); q++) {
                            StatisticBean.VisiterResultBean tempVisiterResultBean = meituanVisiterResultList
                                    .get(q);
                            if (tempVisiterResultBean != null) {
                                if (currentDay.equals(tempVisiterResultBean.getDayTime())) {
                                    meituanVisiterResultBean = tempVisiterResultBean;
                                    break;
                                }
                            }
                        }
                    }

                    float exposure = 0;
                    float visiter = 0;
                    float totalOrder = 0;
                    if (visiterResultBean != null && statisticBean0 != null) {
                        exposure = visiterResultBean.getExposure();
                        visiter = visiterResultBean.getVisiter();
                        totalOrder = statisticBean0.getTotalOrder();
                    }

                    float elemeExposure = 0;
                    float elemeVisiter = 0;
                    float elemeTotalOrder = 0;
                    if (elemeVisiterResultBean != null && elemeStatisticBean0 != null) {
                        elemeExposure = elemeVisiterResultBean.getExposure();
                        elemeVisiter = elemeVisiterResultBean.getVisiter();
                        elemeTotalOrder = elemeStatisticBean0.getTotalOrder();
                    }

                    float meituanExposure = 0;
                    float meituanVisiter = 0;
                    float meituanTotalOrder = 0;
                    if (meituanVisiterResultBean != null && meituanStatisticBean0 != null) {
                        meituanExposure = meituanVisiterResultBean.getExposure();
                        meituanVisiter = meituanVisiterResultBean.getVisiter();
                        meituanTotalOrder = meituanStatisticBean0.getTotalOrder();
                    }

                    switch (mLineCharTitles0[i]) {
                        // "曝光人数", "访问人数", "下单人数", "访问转化", "下单转化"
                        case "曝光人数":
                            list.add(exposure);
                            elemeList.add(elemeExposure);
                            meituanList.add(meituanExposure);
                            allList.add(exposure + elemeExposure + meituanExposure);
                            break;
                        case "访问人数":
                            list.add(visiter);
                            elemeList.add(elemeVisiter);
                            meituanList.add(meituanVisiter);
                            allList.add(visiter + elemeVisiter + meituanVisiter);
                            break;
                        case "下单人数":
                            list.add(totalOrder);
                            elemeList.add(elemeTotalOrder);
                            meituanList.add(meituanTotalOrder);
                            allList.add(totalOrder + elemeTotalOrder + meituanTotalOrder);
                            break;
                        case "访问转化":
                            float compared0 = jsjCompared(exposure, visiter);
                            list.add(compared0 * 100f);

                            float elemeCompared0 = jsjCompared(elemeExposure, elemeVisiter);
                            elemeList.add(elemeCompared0 * 100f);

                            float meituanCompared0 = jsjCompared(meituanExposure, meituanVisiter);
                            meituanList.add(meituanCompared0 * 100f);

                            float allExposure0 = exposure + elemeExposure + meituanExposure;
                            float allVisiter0 = visiter + elemeVisiter + meituanVisiter;

                            float allCompared0 = jsjCompared(allExposure0, allVisiter0);
                            allList.add(allCompared0 * 100f);
                            break;
                        case "下单转化":
                            float compared1 = jsjCompared(visiter, totalOrder);
                            list.add(compared1 * 100f);

                            float elemeCompared1 = jsjCompared(elemeVisiter, elemeTotalOrder);
                            elemeList.add(elemeCompared1 * 100f);

                            float meituanCompared1 = jsjCompared(meituanVisiter, meituanTotalOrder);
                            meituanList.add(meituanCompared1 * 100f);

                            float allVisiter1 = visiter + elemeVisiter + meituanVisiter;
                            float allTotalOrder1 = totalOrder + elemeTotalOrder + meituanTotalOrder;

                            float allCompared1 = jsjCompared(allVisiter1, allTotalOrder1);
                            allList.add(allCompared1 * 100f);
                            break;
                    }
                }

                dataChartPutMap(mPlatform, mLineCharTitles0[i], dataMap, map, list, elemeList,
                        meituanList, allList);
            }
            mChartComponent.setData(dataMap, startDate, itemMap, unitMap);
        }
    }

    //计算两数比值
    private float jsjCompared(float fenMu, float fenZi) {
        return fenMu == 0 ? fenZi == 0 ? 0 : 1 : fenZi / fenMu;
    }

    private void dataChartPutMap(String mPlatform, String key, Map<String, Map<String, List<Float>>> dataMap, Map<String, List<Float>> map, List<Float> jdList, List<Float> elemeList, List<Float> mtlist, List<Float> allList) {
        if (mPlatform.equals(MyConstant.STR_ALLSTATIS)) {
            Collections.reverse(jdList);//京东数据
            map.put(MyConstant.STR_JDDJ, jdList);

            Collections.reverse(elemeList);//饿了么
            map.put(MyConstant.STR_ELEME, elemeList);

            Collections.reverse(mtlist);//美团
            map.put(MyConstant.STR_MT, mtlist);

            Collections.reverse(allList);//总的数据
            map.put(MyConstant.STR_ALLSTATIS, allList);
        } else {
            Collections.reverse(jdList);//
            map.put(mPlatform, jdList);
        }
        dataMap.put(key, map);
    }

    public DividerItemDecoration getRecyclerViewGetDivider(Context context) {
        DividerItemDecoration divider = new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(context, R.drawable.custom_divider)));
        return divider;
    }

    //从底部弹出的对话框
    public Dialog createButtonDialog(Context context, View root) {
        Dialog dialog = new Dialog(context, R.style.my_dialog);
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.dialogAnimations0); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = -20; // 新位置Y坐标//隐藏头部导航栏
            lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(true);
        }
        return dialog;
    }

    //进价500g 计算成本价  元
    public double jsChenBen(double jinjia, GoodsBean goodsBean) {
        try {
            if (goodsBean != null) {
                GoodsBean.ProductBean product = goodsBean.getProduct();
                if (product != null) {
                    GoodsBean.ProductBean.ExtraBean extra = product.getExtra();
                    if (extra != null) {
                        String upcCode = extra.getUpcCode();
                        if (!TextUtils.isEmpty(upcCode)) {
                            return jinjia;
                        }
                    }

                    String strWeight = product.getWeight();
                    String strWeight_unit = product.getWeight_unit();
                    if (!TextUtils.isEmpty(strWeight) && !TextUtils.isEmpty(strWeight_unit)) {
                        double weight = Double.valueOf(strWeight);
                        if (strWeight_unit.equalsIgnoreCase("kg")) {
                            weight *= 1000;
                        }
                        return weight / 500d * jinjia;
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    //名字 place name extraSummary 3234g/份
    public String showGoodName(GoodsBean mGoodsBean) {
        if (mGoodsBean != null) {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null) {
                String name = product.getName();
                if (!TextUtils.isEmpty(name)) {
                    StringBuilder builder = new StringBuilder(1024);
                    GoodsBean.ProductBean.ExtraBean extra = product.getExtra();
                    String extra_summary = null;
                    if (extra != null) {
                        String upcCode = extra.getUpcCode();
                        if (!TextUtils.isEmpty(upcCode)) {
                            return name;
                        }

                        String place = extra.getPlace();
                        if (!TextUtils.isEmpty(place)) {
                            builder.append(place);
                            builder.append(" ");
                        }
                        extra_summary = extra.getExtra_summary();
                    }
                    builder.append(name);
                    if (!TextUtils.isEmpty(extra_summary)) {
                        builder.append(" ");
                        builder.append(extra_summary);
                    }

                    String weight = product.getWeight();
                    String unit = product.getUnit();
                    String weight_unit = product.getWeight_unit();
                    if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(unit) && !TextUtils
                            .isEmpty(weight_unit)) {
                        builder.append(String.format(" 约%s%s/%s", weight, weight_unit, unit));
                    }
                    return builder.toString();
                }
            }
        }
        return "";
    }
}
