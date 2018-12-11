package cn.jinxiit.zebra.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.DeleteRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.jinxiit.zebra.beans.MyGoodsBean;
import cn.jinxiit.zebra.callbacks.StringDialogCallback;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;

public class ApiUtils {
    private static ApiUtils apiUtils = null;

    private ApiUtils() {}

    //静态工厂方法
    public static ApiUtils getInstance() {
        if (apiUtils == null) {
            apiUtils = new ApiUtils();
        }
        return apiUtils;
    }

    //#get#
    public void okgoGetOrderCount(Activity activity, String token, String storeId, String dayTime, String platform, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/third_party/order/store/" + storeId + "/count";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(dayTime, MyConstant.STR_DAY_TIME, getRequest);
        checkGetParams(platform, MyConstant.STR_THIRD_TYPE, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    private boolean checkUnConnectAndToken(Activity activity, String token) {
        if (isUnNetWorkConnected(activity)) {
            return true;
        }

        if (TextUtils.isEmpty(token)) {
            MyToastUtils.error("您还未登录");
            return true;
        }
        return false;
    }

    //
    private void checkGetParams(String value, String key, GetRequest<String> getRequest) {
        if (!TextUtils.isEmpty(value)) {
            getRequest.params(key, value);
            Log.e(key, value);
        }
    }

    private void getRequestExecute(final Activity activity, GetRequest<String> getRequest, boolean isShowDialog, final ApiResultListener apiResultListener) {
        getRequest.execute(new StringDialogCallback(activity, isShowDialog) {
            @Override
            public void onSuccess(Response<String> response) {
                super.onSuccess(response);
                ApiUtils.this.onSuccess(response, apiResultListener);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                apiResultListener.onError(response);
            }
        });
    }

    private boolean isUnNetWorkConnected(Context context) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            MyToastUtils.warning("网络不可用");
            return true;
        }
        return false;
    }

    private void onSuccess(Response<String> response, ApiResultListener apiResultListener) {
        String body = response.body();
        if (TextUtils.isEmpty(body)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has(MyConstant.STR_ERR)) {
                return;
            }
            apiResultListener.onSuccess(response);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                new JSONArray(body);
                apiResultListener.onSuccess(response);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void okgoGetNotification(Activity activity, String token, String storeId, String type, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(storeId)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/get_message/" + storeId + "/list";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(type, MyConstant.STR_TYPE, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetAbNormalGoods(Activity activity, String token, String storeId, String status, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_status_market_goods/" + storeId + "/goods";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(status, MyConstant.STR_STATUS, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetCountTip(Activity activity, String token, String storeId, String remind, boolean isShowDialog, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        if (TextUtils.isEmpty(remind)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_count/" + storeId + "/remind/" + remind;

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, isShowDialog, apiResultListener);
    }

    public void okgoGetExamineGoods(Activity activity, String token, String storeId, String status, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(status)) {
            MyToastUtils.error("当前门店不存在");
            return;
        }

        String url = MyConstant.SERVER + "/api/get_examine_goods/" + storeId + "/status";

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(status, MyConstant.STR_STATUS, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetSearchBrand(Activity activity, String token, String q, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/search_brand/";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(q, MyConstant.STR_Q, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        checkGetParams("20", MyConstant.STR_SIZE, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetPrinterList(Activity activity, String token, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/third_party/printers/";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    public void okgoGetSearchGoodsOfStore(Activity activity, String token, String storeId, String q, String upcCode, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(storeId)) {
            MyToastUtils.error("店铺不存在");
            return;
        }
        String url = MyConstant.SERVER + "/api/search_market_product/" + storeId;
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(q, MyConstant.STR_Q, getRequest);
        checkGetParams(upcCode, MyConstant.STR_UPC_CODE, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetStatusOrderList(Activity activity, String token, String storeId, String status, String dayTime, String thirdType, String orderId, String from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/third_party/order/store/" + storeId + "/";
        OkGo.getInstance().cancelTag(1);
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .tag(1)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(status, "status", getRequest);
        checkGetParams(dayTime, MyConstant.STR_DAY_TIME, getRequest);
        checkGetParams(thirdType, MyConstant.STR_THIRD_TYPE, getRequest);
        checkGetParams(orderId, MyConstant.STR_ORDER_ID, getRequest);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    //获取店内分类
    public void okgoGet2StoreCategoryName(Activity activity, String token, String catId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(catId)) {
            MyToastUtils.error("不存在该商户分类");
            return;
        }

        if ("0".equals(catId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_market_category/name/" + catId;
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    public void okgoGet3ProductCategoryName(Activity activity, String token, String categoryId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_category/" + categoryId;

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetBrandName(Activity activity, String token, String brandId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_brand/" + brandId;

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);

        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    public void okgoGetGoodsByMarketCategoryId(Activity activity, String token, String storeId, String categoryId, String query, String from, boolean isShowDialog, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_category_product/" + storeId + "/";

        if (!TextUtils.isEmpty(categoryId)) {
            url += "cat_id/" + categoryId;
        }

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkGetParams(from, MyConstant.STR_FROM, getRequest);
        checkGetParams(query, MyConstant.STR_QUERY, getRequest);
        checkGetParams("top,-created_at", "sort", getRequest);
        getRequestExecute(activity, getRequest, isShowDialog, apiResultListener);
    }

    public void okgoGetMarketCategory(Activity activity, String token, String marketId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_market_category/" + marketId;

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);

        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    public void okgoGetProductCategoryList(Activity activity, String token, String fatherCategoryId, boolean is1ji, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url;
        if (is1ji) {
            //       一级分类 fatherCategoryId = 0
            url = MyConstant.SERVER + "/api/get_category_list/cat_id/" + fatherCategoryId;
        } else {
            url = MyConstant.SERVER + "/api/get_category_list/child/" + fatherCategoryId;
        }

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    public void okgoGetProductList(Activity activity, String token, String q, String upcCode, String categoryId, int from, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/search_product_bank";

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);

        checkGetParams(q, MyConstant.STR_Q, getRequest);
        checkGetParams(upcCode, MyConstant.STR_UPC_CODE, getRequest);
        checkGetParams(categoryId, MyConstant.STR_CATEGORY, getRequest);

        checkGetParams(String.valueOf(from), MyConstant.STR_FROM, getRequest);
        getRequestExecute(activity, getRequest, false, apiResultListener);
    }

    //获取门店信息
    //京东 请求完请求下一个
    public void okgoGetStoreStatistic(Activity activity, String token, String platform, String storeId, String type, boolean isShowDialog, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = null;
        switch (platform) {
            case MyConstant.STR_JDDJ:
                url = MyConstant.SERVER + "/api/third_party/jd/stats/" + storeId;
                break;
            case MyConstant.STR_ELEME:
                url = MyConstant.SERVER + "/api/third_party/eleme/stats/" + storeId;
                break;
            case MyConstant.STR_MT:
                url = MyConstant.SERVER + "/api/third_party/meituan/stats/" + storeId;
                break;
            case MyConstant.STR_ALLSTATIS:
                url = MyConstant.SERVER + "/api/third_party/stats/" + storeId;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            MyToastUtils.info("该平台暂无数据");
            return;
        }

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_TYPE, type);

        getRequestExecute(activity, getRequest, isShowDialog, apiResultListener);
    }

    public void okgoGetStoreProductStatus(Activity activity, String token, String platform, String storeId, String type, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = null;
        switch (platform) {
            case MyConstant.STR_JDDJ:
                url = MyConstant.SERVER + "/api/third_party/jd/product_stats/" + storeId;
                break;
            case MyConstant.STR_ELEME:
                url = MyConstant.SERVER + "/api/third_party/eleme/product_stats/" + storeId;
                break;
            case MyConstant.STR_MT:
                url = MyConstant.SERVER + "/api/third_party/meituan/product_stats/" + storeId;
                break;
            case MyConstant.STR_ALLSTATIS:
                url = MyConstant.SERVER + "/api/third_party/product_stats/" + storeId;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            MyToastUtils.info("该平台暂无数据");
            return;
        }

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_TYPE, type);

        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    public void okgoGetUserStores(Activity activity, String token, String from, String size, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/get_user_stores/";

        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_FROM, from)
                .params(MyConstant.STR_SIZE, size);

        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    //获取自己的信息
    public void okgoGetUserInfo(Activity activity, String token, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/users/me";
        GetRequest<String> getRequest = OkGo.<String>get(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        getRequestExecute(activity, getRequest, true, apiResultListener);
    }

    //#post#
    //
    //配送异常
    public void okgoPostOrderAbnormalDeliveryAction(Activity activity, String token, String platform, String orderId, String action, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(action) || TextUtils
                .isEmpty(platform)) {
            return;
        }
        String url = MyConstant.SERVER + String
                .format("/api/%s/order/%s/%s", platform, action, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //
    private void postRequestExecute(final Activity activity, PostRequest<String> postRequest, boolean isShowDialog, final ApiResultListener apiResultListener) {
        postRequest.execute(new StringDialogCallback(activity, isShowDialog) {
            @Override
            public void onSuccess(Response<String> response) {
                super.onSuccess(response);
                ApiUtils.this.onSuccess(response, apiResultListener);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                apiResultListener.onError(response);
            }
        });
    }

    //    京东商家确认收到拒收退回（或取消）的商品
    public void okgoPostJdConfirmReBackGoods(Activity activity, String token, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/jd/order/getReback/" + orderId;
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //京东 --- 是否同意
    //action
    // 1.取消单//拒绝 reject  //同意 agree
    // 2.配送取货失败  receiveAgree 同意  receiveFalse拒绝
    public void okgoPostJdIsCanCancel(Activity activity, String token, String platform, String action, String orderId, String reason, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(action) || TextUtils
                .isEmpty(platform)) {
            return;
        }
        String url = MyConstant.SERVER + String
                .format("/api/%s/order/%s/%s", platform, action, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(reason, "reason", postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //post检测添加参数
    private void checkPostParams(String value, String key, PostRequest<String> postRequest) {
        if (!TextUtils.isEmpty(value)) {
            postRequest.params(key, value);
            Log.e(key, value);
        }
    }

    //美团退款
    //拒绝 reject
    //同意 agree
    public void okgoPostMeituanTuikuan(Activity activity, String token, String action, String orderId, String reason, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(action)) {
            return;
        }
        String url = MyConstant.SERVER + String.format("/api/meituan/order/%s/%s", action, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(reason, "reason", postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //美团自配送 /api/meituan/order/delivering/:order_id
    public void okgoPostMeituanDeliveringOrder(Activity activity, String token, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/meituan/order/delivering/" + orderId;
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //增加小费
    public void okgoPostAddTip(Activity activity, String token, String platform, String orderId, String tips, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(platform) || TextUtils.isEmpty(tips)) {
            return;
        }
        String url = MyConstant.SERVER + String
                .format("/api/%s/order/addTip/%s", platform, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(tips, "tips", postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //再次下发配送 京东
    public void okgoPostDispatchPushOrder(Activity activity, String token, String platform, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(platform)) {
            return;
        }
        String url = MyConstant.SERVER + String.format("/api/%s/order/dispatch/%s", platform, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //下发配送
    public void okgoPostPushOrder(Activity activity, String token, String platform, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(platform)) {
            return;
        }
        String url = MyConstant.SERVER + String.format("/api/%s/order/push/%s", platform, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //拒单
    public void okgoPostRefuseOrder(Activity activity, String token, String platform, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(platform)) {
            return;
        }
        String url = MyConstant.SERVER + String
                .format("/api/%s/order/cancel/%s", platform, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //接单
    public void okgoPostReceiveOrder(Activity activity, String token, String plaform, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(plaform)) {
            return;
        }
        String url = MyConstant.SERVER + String
                .format("/api/%s/order/receive/%s", plaform, orderId);
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //通知条数
    public void okgoPostReadNotification(Activity activity, String token, String storeId, String notificationId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        if (TextUtils.isEmpty(notificationId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/read_message/" + storeId + "/message/" + notificationId;

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostBatchAddGoods(Activity activity, String token, String storeId, String productsJson, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/batch_create_market_goods/" + storeId + "/goods";
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(productsJson, MyConstant.STR_PRODUCTS, postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostUpdateFailedGoods(Activity activity, String token, String storeId, String goodsId, MyGoodsBean myGoodsBean, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        if (TextUtils.isEmpty(goodsId)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/update_examine_goods/" + storeId + "/goods/" + goodsId;

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(myGoodsBean.getTop(), MyConstant.STR_TOP, postRequest);
        checkPostParams(myGoodsBean.getPrice(), "price", postRequest);
        checkPostParams(myGoodsBean.getStock(), "stock", postRequest);
        checkPostParams(myGoodsBean.getJd_price(), "jd_price", postRequest);
        checkPostParams(myGoodsBean.getJd_stock(), "jd_stock", postRequest);
        checkPostParams(myGoodsBean.getMeituan_price(), "meituan_price", postRequest);
        checkPostParams(myGoodsBean.getMeituan_stock(), "meituan_stock", postRequest);
        checkPostParams(myGoodsBean.getEleme_price(), "eleme_price", postRequest);
        checkPostParams(myGoodsBean.getEleme_stock(), "eleme_stock", postRequest);
        checkPostParams(myGoodsBean.getProduct_status(), "product_status", postRequest);
        checkPostParams(myGoodsBean.getAuto_status(), "auto_status", postRequest);
        checkPostParams(myGoodsBean.getStatus_time(), "status_time", postRequest);

        checkPostParams(myGoodsBean.getName(), MyConstant.STR_NAME, postRequest);
        checkPostParams(myGoodsBean.getPlace(), "place", postRequest);
        checkPostParams(myGoodsBean.getExtra_summary(), "extra_summary", postRequest);
        checkPostParams(myGoodsBean.getCat_id(), MyConstant.STR_CATID, postRequest);
        checkPostParams(myGoodsBean.getWeight(), "weight", postRequest);
        checkPostParams(myGoodsBean.getUnit(), "unit", postRequest);
        checkPostParams(myGoodsBean.getWeight_unit(), "weight_unit", postRequest);

        checkPostParams(myGoodsBean.getPack_fee(), MyConstant.STR_PACK_FEE, postRequest);
        checkPostParams(myGoodsBean.getMin_purchase(), MyConstant.STR_MIN_PURCHASE, postRequest);
        checkPostParams(myGoodsBean.getAttribute(), "attribute", postRequest);
        checkPostParams(myGoodsBean.getSale_time(), MyConstant.STR_SALE_TIME, postRequest);
        checkPostParams(myGoodsBean.getSummary(), MyConstant.STR_SUMMARY, postRequest);
        checkPostParams(myGoodsBean.getImages(), MyConstant.STR_IMAGES, postRequest);

        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //检测商品是否存在
    public void okgoPostCheckProductExisit(Activity activity, String token, String storeId, String productId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            MyToastUtils.error("当前店铺不存在");
            return;
        }

        if (TextUtils.isEmpty(productId)) {
            MyToastUtils.error("不存在该商品");
            return;
        }

        String url = MyConstant.SERVER + "/api/check_product/" + storeId + "/goods/" + productId;

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    ///api/create_examine_goods/:store_id/goods
    //    参数： title: 标品名称（标品与商品名称一致） brand： 品牌编号， upcCode: upc编码， category: 分类编码 name: 商品名称, summary: 描述, cat_id: 商户分类id
    //    attribute: 属性 [{ att_name:属性名称，att_label:属性标签}], sale_time: { full_time: 全时段售卖 boolean (), start_date: 开始售卖日期, end_date: 结束售卖日期, full_hour: 全天售卖, limit_hour: []售卖限制时间段} 售卖时间, place: 产地, min_purchase: 最小售卖量 price: 价格, weight: 重量, stock: 库存, unit: 单位， weight_unit: 重量单位 pack_fee: 包装费, images: 图片 ["", ""]  extra_summary 补充标题

    public void okgoPostStoreBindPrinter(Activity activity, String token, String serial, String storeId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/third_party/printers/" + serial + "/bind/" + storeId + "/";
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostTestPrint(Activity activity, String token, String serial, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/third_party/printers/" + serial + "/test/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostAddPrinter(Activity activity, String token, String serial, String name, String secret, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/third_party/printers/";
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(serial, MyConstant.STR_SERIAL, postRequest);
        checkPostParams(name, MyConstant.STR_NAME, postRequest);
        checkPostParams(secret, MyConstant.STR_SECRET, postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostBatchUpdateGoodsInfo(Activity activity, String token, String storeId, String goodsJson, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/batch_update_market_goods/" + storeId + "/goods";
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(goodsJson, MyConstant.STR_PRODUCTS, postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostUpdateStoreGoodsInfo(Activity activity, String token, String storeId, String goodsId, MyGoodsBean myGoodsBean, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/update_market_goods/" + storeId + "/goods/" + goodsId;
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        checkPostParams(myGoodsBean.getTop(), MyConstant.STR_TOP, postRequest);
        checkPostParams(myGoodsBean.getPrice(), "price", postRequest);
        checkPostParams(myGoodsBean.getStock(), "stock", postRequest);

        checkPostParams(myGoodsBean.getCost_price(), "cost_price", postRequest);
        checkPostParams(myGoodsBean.getPurchase_price(), "purchase_price", postRequest);

        checkPostParams(myGoodsBean.getJd_price(), "jd_price", postRequest);
        checkPostParams(myGoodsBean.getJd_stock(), "jd_stock", postRequest);
        checkPostParams(myGoodsBean.getMeituan_price(), "meituan_price", postRequest);
        checkPostParams(myGoodsBean.getMeituan_stock(), "meituan_stock", postRequest);
        checkPostParams(myGoodsBean.getEleme_price(), "eleme_price", postRequest);
        checkPostParams(myGoodsBean.getEleme_stock(), "eleme_stock", postRequest);
        checkPostParams(myGoodsBean.getEbai_price(), "ebai_price", postRequest);
        checkPostParams(myGoodsBean.getEbai_stock(), "ebai_stock", postRequest);

        checkPostParams(myGoodsBean.getProduct_status(), "product_status", postRequest);
        checkPostParams(myGoodsBean.getAuto_status(), "auto_status", postRequest);
        checkPostParams(myGoodsBean.getStatus_time(), "status_time", postRequest);

        checkPostParams(myGoodsBean.getName(), MyConstant.STR_NAME, postRequest);
        checkPostParams(myGoodsBean.getPlace(), "place", postRequest);
        checkPostParams(myGoodsBean.getExtra_summary(), "extra_summary", postRequest);
        checkPostParams(myGoodsBean.getCat_id(), MyConstant.STR_CATID, postRequest);
        checkPostParams(myGoodsBean.getWeight(), "weight", postRequest);
        checkPostParams(myGoodsBean.getUnit(), "unit", postRequest);
        checkPostParams(myGoodsBean.getWeight_unit(), "weight_unit", postRequest);
        checkPostParams(myGoodsBean.getPack_fee(), MyConstant.STR_PACK_FEE, postRequest);
        checkPostParams(myGoodsBean.getMin_purchase(), MyConstant.STR_MIN_PURCHASE, postRequest);
        checkPostParams(myGoodsBean.getAttribute(), "attribute", postRequest);
        checkPostParams(myGoodsBean.getSale_time(), MyConstant.STR_SALE_TIME, postRequest);
        checkPostParams(myGoodsBean.getSummary(), MyConstant.STR_SUMMARY, postRequest);
        checkPostParams(myGoodsBean.getImages(), MyConstant.STR_IMAGES, postRequest);

        checkPostParams(myGoodsBean.getJd_status(), "jd_status", postRequest);
        checkPostParams(myGoodsBean.getEleme_status(), "eleme_status", postRequest);
        checkPostParams(myGoodsBean.getMeituan_status(), "meituan_status", postRequest);
        checkPostParams(myGoodsBean.getEbai_status(), "ebai_status", postRequest);

        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //    /api/create_market_goods/:market_id/goods/:id
    //    参数: id: 商品id  market_id:商户id, name: 商品名称, summary: 描述, cat_id: 商户分类id
    //    attribute: 属性 [{ att_name:属性名称，att_label:属性标签}], sale_time: { full_time: 全时段售卖 boolean (), start_date: 开始售卖日期, end_date: 结束售卖日期, full_hour: 全天售卖, limit_hour: []售卖限制时间段} 售卖时间, place: 产地, min_purchase: 最小售卖量 price: 价格, weight: 重量, stock: 库存, unit: 单位， weight_unit: 重量单位 pack_fee: 包装费, images: 图片 ["", ""]
    //    方法: POST
    public void okgoPostAddStoreGoods(Activity activity, String token, String storeId, String productId, String name, String upcCode, String title, String brandId, String productCategory, String summary, String categoryId, String attributeJson, String saleTimeJson, String place, String min_purchase, String price, String weight, String stock, String unit, String weight_unit, String pack_fee, String imagesJson, String extra_summary, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            MyToastUtils.error("当前店铺不存在");
            return;
        }

        String url;
        if (TextUtils.isEmpty(productId)) {
            if (!TextUtils.isEmpty(upcCode)) {
                title = name;
            }
            url = MyConstant.SERVER + "/api/create_examine_goods/" + storeId + "/goods";
        } else {
            url = MyConstant.SERVER + "/api/create_market_goods/" + storeId + "/goods/" + productId;
        }

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);

        checkPostParams(name, MyConstant.STR_NAME, postRequest);
        checkPostParams(brandId, MyConstant.STR_BRAND, postRequest);
        checkPostParams(title, MyConstant.STR_TITLE, postRequest);
        checkPostParams(productCategory, MyConstant.STR_CATEGORY, postRequest);
        checkPostParams(summary, MyConstant.STR_SUMMARY, postRequest);
        checkPostParams(categoryId, MyConstant.STR_CATID, postRequest);
        checkPostParams(upcCode, MyConstant.STR_UPC_CODE, postRequest);
        checkPostParams(extra_summary, "extra_summary", postRequest);
        checkPostParams(attributeJson, "attribute", postRequest);
        checkPostParams(saleTimeJson, "sale_time", postRequest);
        checkPostParams(place, "place", postRequest);
        checkPostParams(min_purchase, "min_purchase", postRequest);
        checkPostParams(price, "price", postRequest);
        checkPostParams(weight, "weight", postRequest);
        checkPostParams(stock, "stock", postRequest);
        checkPostParams(unit, "unit", postRequest);
        checkPostParams(weight_unit, "weight_unit", postRequest);
        checkPostParams(pack_fee, "pack_fee", postRequest);
        checkPostParams(imagesJson, "images", postRequest);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostUpdateMarketCategory(Activity activity, String token, String marketId, String categoryId, String name, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/update_market_category/" + marketId + "/cat/" + categoryId;

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_NAME, name);
        postRequestExecute(activity, postRequest, true, apiResultListener);

    }

    public void okgoPostSortMarketCategory(Activity activity, String token, String marketId, String seqJson, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/update_market_category/" + marketId + "/seq/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_SEQ, seqJson);
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //创建店内分类
    public void okgoPostCreateMarketCategory(Activity activity, String token, String marketId, String name, String fatherId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/create_market_category/" + marketId;

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_NAME, name);
        if (!TextUtils.isEmpty(fatherId)) {
            postRequest.params(MyConstant.STR_CATID, fatherId);
        }
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //更新门店信息
    public void okgoPostUpdateStore(Activity activity, String token, String storeId, String name, String summary, String extraJson, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/update_store/" + storeId;
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);

        checkPostParams(name, MyConstant.STR_NAME, postRequest);
        checkPostParams(summary, MyConstant.STR_SUMMARY, postRequest);
        checkPostParams(extraJson, MyConstant.STR_EXTRA, postRequest);

        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    public void okgoPostSendSMSCode(Activity activity, String phone, ApiResultListener apiResultListener) {
        if (isUnNetWorkConnected(activity)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/send_sms_code/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params(MyConstant.STR_PHONE, phone);

        postRequestExecute(activity, postRequest, true, apiResultListener);

    }

    //创建门店
    public void okgoPostCreateStore(Activity activity, String token, String name, String extrajSON, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/create_store/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token).params(MyConstant.STR_NAME, name)
                .params(MyConstant.STR_EXTRA, extrajSON);

        postRequestExecute(activity, postRequest, true, apiResultListener);

    }

    //上传文件
    public void okgoPostFile(Activity activity, String token, String path, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/upload/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token)
                .params(MyConstant.STR_FILE, new File(path));
        postRequestExecute(activity, postRequest, true, apiResultListener);
    }

    //登录
    public void okgoPostSignIn(Activity activity, String username, String password, ApiResultListener apiResultListener) {
        if (isUnNetWorkConnected(activity)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/signin/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params(MyConstant.STR_USERNAME, username)
                .params(MyConstant.STR_PASSWORD, password);

        postRequestExecute(activity, postRequest, true, apiResultListener);

    }

    //重置密码
    public void okgoPostReSetPassword(Activity context, String phone, String password, String code, ApiResultListener apiResultListener) {
        if (isUnNetWorkConnected(context)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/reset_passwd/";

        PostRequest<String> postRequest = OkGo.<String>post(url).params(MyConstant.STR_PHONE, phone)
                .params(MyConstant.STR_PASSWORD, password).params(MyConstant.STR_CODE, code);

        postRequestExecute(context, postRequest, true, apiResultListener);

    }

    //注册
    public void okgoPostSignUp(Activity context, String username, String password, String code, ApiResultListener apiResultListener) {
        if (isUnNetWorkConnected(context)) {
            return;
        }

        String url = MyConstant.SERVER + "/api/signup/";

        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params(MyConstant.STR_USERNAME, username).params(MyConstant.STR_PASSWORD, password)
                .params(MyConstant.STR_CODE, code);
        postRequestExecute(context, postRequest, true, apiResultListener);

    }

    //#delete#
    public void okgoDeleteMarketCategory(Activity activity, String token, String marketId, String categoryId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(marketId)) {
            MyToastUtils.error("未找到该商户");
            return;
        }

        if (TextUtils.isEmpty(categoryId)) {
            MyToastUtils.error("未找到该分类");
            return;
        }

        String url = MyConstant.SERVER + "/api/remove_market_category/" + marketId + "/cat/" + categoryId;
        DeleteRequest<String> deleteRequest = OkGo.<String>delete(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        deleteRequestExecute(activity, deleteRequest, true, apiResultListener);
    }

    private void deleteRequestExecute(final Activity activity, DeleteRequest<String> deleteRequest, boolean isShowDialog, final ApiResultListener apiResultListener) {
        deleteRequest.execute(new StringDialogCallback(activity, isShowDialog) {
            @Override
            public void onSuccess(Response<String> response) {
                super.onSuccess(response);
                ApiUtils.this.onSuccess(response, apiResultListener);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                apiResultListener.onError(response);
            }
        });
    }

    public void okgoDeletePrinter(Activity activity, String token, String serial, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/third_party/printers/" + serial + "/";
        DeleteRequest<String> deleteRequest = OkGo.<String>delete(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        deleteRequestExecute(activity, deleteRequest, true, apiResultListener);
    }

    public void okgoPostPrinterOrder(Activity activity, String token, String storeId, String orderId, ApiResultListener apiResultListener) {
        if (checkUnConnectAndToken(activity, token)) {
            return;
        }

        if (TextUtils.isEmpty(storeId)) {
            return;
        }

        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        String url = MyConstant.SERVER + "/api/third_party/printers/" + storeId + "/order/" + orderId + "/";
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .headers(MyConstant.STR_REQUEST_TOKEN, token);
        postRequestExecute(activity, postRequest, false, apiResultListener);
    }
}
