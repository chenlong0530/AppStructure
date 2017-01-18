package com.app.library.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.library.lg.Lg;

import java.io.File;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by chenlong on 16/5/11.
 */
public class CommonWebView extends WebView {
    private IJs jsInterface;
    private IWebView viewListener;
    private WebView webView;
    private String jsPromptUrl;
    public ValueCallback<Uri> mUploadMessage;
    public String mCameraFilePath;
    public Activity activity;
    public static final int FILECHOOSER_RESULTCODE = 2003;

    public CommonWebView(Activity activity) {
        super(activity);
        this.activity = activity;
        initWebView();
    }

    protected void initWebView() {
        webView = this;
        jsInterface = new JsImp();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        try {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
            initWebSettings(getSettings());
            if (Lg.isDebug()) {
                if (Build.VERSION.SDK_INT >= 19) {
                    setWebContentsDebuggingEnabled(true);
                }
            }
            setDownloadListener(new MyDownloadListener());
            setWebViewClient(new MyWebViewClient());
            setWebChromeClient(new MyWebChromeClient());
            requestFocus();
        } catch (Exception e) {
        }
    }

    @SuppressLint("NewApi")
    protected void initWebSettings(WebSettings settings) {
        settings.setUserAgentString("");
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        if (getActivity() != null) {
            File file = getActivity().getFilesDir();
            if (file != null) {
                settings.setGeolocationDatabasePath(file.getPath());
            }
        }

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    public Activity getActivity() {
        return activity;
    }


    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Lg.i("onDownloadStart url:" + url + ",contentDisposition:" + contentDisposition
                    + ",mimetype:" + mimetype + ",contentLength:" + contentLength);
            try {
                long r = DownloadHelper.download(activity, url, userAgent, mimetype, contentLength);
                Lg.i("download r:" + r);
                if (r == DownloadHelper.ERROR_NO_DOWNLOAD_APP) {
                    Lg.d("系统下载器被卸载或停用");
                }
            } catch (Exception e) {
                Lg.w(e);
                Lg.d("下载失败");
            }
        }
    }


    class MyWebViewClient extends WebViewClient {

        private String mFailingUrl;

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mFailingUrl = failingUrl;
            view.stopLoading();
            viewListener.showFail();
        }

        /**
         * 根据Url区分右边按钮
         *
         * @param url
         */
        private void analysisUrl(String url) {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Lg.i("onPageStarted---" + url);
            viewListener.setTitle(view.getTitle());
            if (url != null && url.equals(mFailingUrl)) {
                return;
            }

            viewListener.showLoading();
            analysisUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            viewListener.setTitle(view.getTitle());
            if (url != null && (url.equals(mFailingUrl) || url.contains("chromewebdata"))) {
                return;
            }
            viewListener.showSuccess();
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url != null && url.equals(mFailingUrl)) {
                mFailingUrl = null;
            }
            return super.shouldInterceptRequest(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mFailingUrl = null;
            // 处理tel scheme
            if (url.startsWith("tel:")) {
                Lg.d("##### handle tel scheme");
                Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                activity.startActivity(it);
                return true;
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 不要直接调用父类的方法，否则不会加载；解决证书过期类的portal页面自动停止加载的问题。
            // super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

    }

    class MyWebChromeClient extends WebChromeClient {

        //H5传给native数据协议后带有CALLID字段
        //H5传给native数据协议后带有CALLID字段
        private static final String PATTERN = "PAWIFI\\|METHOD=(.+?)\\|PARAM=(.*)" + "\\|CALLID=(.*)";

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            viewListener.showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            viewListener.hideCustomView();
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            // 发现在小米3的手机上，当通过webview.goBack()回退的时候，
            // 并没有触发onReceiveTitle()，这样会导致标题仍然是之前子页面的标题，没有切换回来.
            // 解决方案：在onPageFinished重新设置title
            viewListener.setTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            viewListener.setProgress(newProgress);
        }

        @Override
        public boolean onJsPrompt(WebView view, final String url, String message, String defaultValue, JsPromptResult result) {
            Lg.i(String.format("onJsPrompt %s %s", url, message));
            if (message != null) {
                // message内带有CALLID表示是新协议，匹配新规则，没有则表示旧协议，匹配旧规则
                Pattern p = null;
                if (message.contains("CALLID")) {
                    p = Pattern.compile(PATTERN);
                }
                Matcher m = p.matcher(message);
                boolean r = m.find();
                if (r) {
                    int c = m.groupCount();
                    if (c == 2 || c == 3) {
                        final String method = m.group(1);
                        final String param = m.group(2);
                        // c == 2表示message参数没有带callId，这里设置为""，下面会走旧协议，
                        // 否则message有callId参数，下面会走新协议
                        final String callId = m.group(3);
                        result.confirm();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (activity == null) {
                                    return;
                                }
                                jsPromptUrl = url;
                                Class c = jsInterface.getClass();
                                Method m;
                                if (!TextUtils.isEmpty(callId)) {
                                    // 如果callid不为空，走新协议调用方法
                                    Lg.d("new pattern matched");
                                    try {
                                        // 如果匹配成功，则表示是新协议调用新接口的方法，会走到return，不再匹配下面的方法
                                        // 否则失败，会走到catch后继续走下去，继续匹配新协议调用老接口的方法
                                        Lg.d("onJsPrompt PAWIFI新协议调用Native新接口的方法，带参数CALLID");
                                        m = c.getMethod(method, String.class, String.class);
                                        m.invoke(jsInterface, param, callId);
                                        return;
                                    } catch (Exception e) {
                                        Lg.d("js调用异常1 " + e.getCause());
                                        Lg.e("js调用异常1 ", e);
                                    }

                                } else {
                                    // 如果callid为空，走原来的旧协议调用方法
                                    Lg.d("callid为空");
                                }
                            }
                        });
                        return true;
                    }
                }
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }


        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String msg = "CONSOLE MESSAGE: line " + consoleMessage.lineNumber() + ": " + consoleMessage.message();
            Lg.i(msg);
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
            Lg.i("onGeolocationPermissionsHidePrompt");
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                       final GeolocationPermissions.Callback callback) {
            if (activity == null) {
                Lg.d("onGeolocationPermissionsShowPrompt activity is null");
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
//                    SpecialDialog d = new SpecialDialog(activity);
//                    d.setTitle("获取地理位置信息");
//                    d.setMessage(origin + "需要获取您的地理位置");
//                    d.setPositiveButton("允许");
//                    d.setNegativeButton("拒绝");
//                    d.setCloseButtonInvisible();
//                    d.setOnClickListener(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (DialogInterface.BUTTON_POSITIVE == which) {
//                                callback.invoke(origin, true, true);
//                            } else if (DialogInterface.BUTTON_NEGATIVE == which) {
//                                callback.invoke(origin, false, false);
//                            }
//                        }
//                    });
//                    d.show();
                }
            });
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            Lg.i("onGeolocationPermissionsShowPrompt");
        }

    }


}
