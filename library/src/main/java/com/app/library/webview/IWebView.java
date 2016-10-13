package com.app.library.webview;

import android.view.View;
import android.webkit.WebChromeClient;

/**
 * Created by chenlong on 16/10/12.
 */

public interface IWebView {
    void showLoading();

    void showSuccess();

    void showFail();

    void loadUrl(String url);

    void postRunable(Runnable r);

    void setTitle(String title);

    boolean isShowTitle();

    void setProgress(int newProgress);

    void showCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void hideCustomView();


    void displayShareComp(String title, String shareUrl);

    void goToRegisterPage(String param);

    void wxShare(String param, String callId);

    void alertLocationSetting();

    void appShare(String param, String callId);

    void loading(String param);

    void hideLoading(String param);

    void showDfpService(String param);

    void setHeaderRightResource(int type);

    void setVpnType(String vpnType);

    void loginSuccess();

    void loginFail(String reason);

    void showToast(String message);

    void fullScreen(String param, String callId);

    void channelAppShare(String param, String callId);
}
