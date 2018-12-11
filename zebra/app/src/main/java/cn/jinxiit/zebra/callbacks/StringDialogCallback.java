package cn.jinxiit.zebra.callbacks;

import android.app.Activity;
import android.util.Log;
import android.view.Window;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.vondear.rxtools.view.dialog.RxDialogLoading;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyToastUtils;

public class StringDialogCallback extends StringCallback {

    private RxDialogLoading mRxDialogLoading;

    protected StringDialogCallback(Activity activity, boolean isShowDialog) {
        if (isShowDialog) {
            mRxDialogLoading = new RxDialogLoading(activity);
            Window window = mRxDialogLoading.getWindow();
            if (window != null) {
                window.setDimAmount(0);
            }
        }
    }

    @Override
    public void onSuccess(Response<String> response) {
        String result = response.body();
        Log.e("onSuccess", result);
        if (result.contains(MyConstant.STR_ERR)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(MyConstant.STR_ERR)) {
                    MyToastUtils.error(jsonObject.getString(MyConstant.STR_ERR));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (mRxDialogLoading != null && !mRxDialogLoading.isShowing()) {
            mRxDialogLoading.show();
        }
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);

        int code = response.code();
        String err = "请求错误：" + code;
        switch (code) {
            case 403:
                err = "未登录或无权访问";
                break;
            case 400:
                err = "请求参数错误";
                break;
            case 500:
                err = "数据错误，请认真检查";
                break;
        }
        MyToastUtils.error(err);
    }

    @Override
    public void onFinish() {
        if (mRxDialogLoading != null && mRxDialogLoading.isShowing()) {
            mRxDialogLoading.dismiss();
        }
    }
}