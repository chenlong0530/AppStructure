package com.app.library.webview;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;

import java.io.File;

import library.app.com.applibrary.lg.Lg;


/**
 * 下载助手
 * 
 * @author EX-YANGZHIHONG001 E-mail:EX-YANGZHIHONG001@pingan.com.cn
 * @version 1.1.0
 */
public class DownloadHelper {

    public static final int ERROR_NO_DOWNLOAD_APP = -1;

    public static long download(Context context, String url, String userAgent, String mimetype, long contentLength)
            throws Exception {
        WebAddress webAddress = new WebAddress(url);
        webAddress.setPath(encodePath(webAddress.getPath()));

        File destFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), webAddress.getPath());
        Lg.d("destination file is: " + destFile.getAbsolutePath());
        destFile.getParentFile().mkdirs();

        String addressString = webAddress.toString();
        Uri uri = Uri.parse(addressString);
        Request request = new Request(uri);
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setMimeType(mimetype);
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDestinationUri(Uri.fromFile(destFile));
        request.allowScanningByMediaScanner();
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            return dm.enqueue(request);
        } catch (Exception e) {
            Lg.w(e);
        }
        return ERROR_NO_DOWNLOAD_APP;
    }

    private static String encodePath(String path) {
        char[] chars = path.toCharArray();

        boolean needed = false;
        for (char c : chars) {
            if (c == '[' || c == ']') {
                needed = true;
                break;
            }
        }
        if (needed == false) {
            return path;
        }

        StringBuilder sb = new StringBuilder("");
        for (char c : chars) {
            if (c == '[' || c == ']') {
                sb.append('%');
                sb.append(Integer.toHexString(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
