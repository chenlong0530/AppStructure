package com.app.library.net;

/**
 * Created by chenlong on 16/10/18.
 */

public interface IRequestCallBack<T> {
    void onResponse(T response);
}
