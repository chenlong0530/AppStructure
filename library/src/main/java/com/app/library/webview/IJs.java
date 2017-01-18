package com.app.library.webview;

/**
 * h5和native交互接口
 */
public interface IJs {

    /**
     * 关闭页面
     *
     * @param param
     * @param callId
     */
    void finish(String param, String callId);

    /**
     * H5获取APP信息
     *
     * @param param
     * @param callId
     */
    void getData(String param, String callId);

    /**
     * 打开浏览器
     *
     * @param param
     * @param callId
     */
    void openBrowser(String param, String callId);

}