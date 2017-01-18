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

    void setTitle(String title);

    boolean isShowTitle();

    void setProgress(int newProgress);

    void showCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void hideCustomView();

}
