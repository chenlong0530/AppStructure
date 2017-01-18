package com.app.library.webview;

/**
 * h5和native的接口
 */
public interface JsInterface {

    void finish(String param, String callId);

    void getUserData(String param, String callId);

    void openBrowser(String param, String callId);

}