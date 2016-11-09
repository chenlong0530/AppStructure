package com.app.library.net;

import com.app.library.net.BaseRequest;
import com.app.library.net.annotation.RequestParameter;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by chenlong on 16/10/18.
 */

public class BaseOkHttpRequest<T> extends BaseRequest<T> {


    private Call call;

    public BaseOkHttpRequest(Class<T> clazz, IRequestCallBack<T> callBack) {
        super(clazz, callBack);
    }

    @Override
    protected void doExec() {
        Request request;
        if (mMethod.equalsIgnoreCase(GET)){
            request = new Request.Builder()
                    .headers(Headers.of(mHeader))
                    .url("")
                    .build();
        }else if (mMethod.equalsIgnoreCase(POST)){

        }
    }

    @Override
    protected void doCancel() {

    }
}
