package cn.jinxiit.zebra.interfaces;

import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public interface ApiResultListener
{
    void onSuccess(Response<String> response);
    void onError(Response<String> response);
}
